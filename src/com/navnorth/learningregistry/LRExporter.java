package com.navnorth.learningregistry;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.net.URL;
import java.net.MalformedURLException;

import org.json.*;

import org.apache.commons.io.IOUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.SignatureException;

import org.ardverk.coding.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.openpgp.*;

/**
 * Exporter of data to a Learning Registry node
 *
 * @author Navigation North
 * @version 1.0
 * @since 2011-11-17
*/
public class LRExporter
{
	// Date format in which to encode date strings
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	// Static strings
	private static final String docVersion = "0.23.0";
	private static final String docType = "resource_data";
	private static final String signingMethod = "LR-PGP.1.0";
	private static final String publishUrlPrefix = "http://";
	private static final String publishSSLUrlPrefix = "https://";
	private static final String publishUrlPath = "/publish";
	private static final String pgpRegex = ".*-----BEGIN PGP PRIVATE KEY BLOCK-----.*-----END PGP PRIVATE KEY BLOCK-----.*";
	
	// Cofiguration variables
	private int batchSize;
	private String url;
	private String publishAuthUser;
	private String publishAuthPassword;

	private String publicKeyLocation;
	private String privateKey;
	private String passPhrase;
	
	// Booleans to track if configuration is complete
	private boolean configured = false;

	// Collection of encoded documents to be sent
	private List<Object> docs = new ArrayList<Object>();
    
	/**
	 * Creates the exporter object and adds the security provider
	*/
    public LRExporter(int batchSize, String url, String passPhrase, String publicKeyLocation, String privateKey)
    {
		Security.addProvider(new BouncyCastleProvider());
		
		this.batchSize = batchSize;
		this.url = url;
		this.passPhrase = passPhrase;
		this.publicKeyLocation = publicKeyLocation;
		this.privateKey = privateKey;
		this.publishAuthUser = null;
		this.publishAuthPassword = null;
    }
	
	/**
	 * Creates the exporter object and adds the security provider
	*/
    public LRExporter(int batchSize, String url, String passPhrase, String publicKeyLocation, String privateKey, String publishAuthUser, String publishAuthPassword)
    {
		Security.addProvider(new BouncyCastleProvider());
		
		this.batchSize = batchSize;
		this.url = url;
		this.passPhrase = passPhrase;
		this.publicKeyLocation = publicKeyLocation;
		this.privateKey = privateKey;
		this.publishAuthUser = publishAuthUser;
		this.publishAuthPassword = publishAuthPassword;
    }

	public void configureLearningRegistryExporter() throws LRException
	{
		// Trim or nullify strings
		url = LRUtilities.nullifyBadInput(url);
		passPhrase = LRUtilities.nullifyBadInput(passPhrase);
		publicKeyLocation = LRUtilities.nullifyBadInput(publicKeyLocation);
		privateKey = LRUtilities.nullifyBadInput(privateKey);
		publishAuthUser = LRUtilities.nullifyBadInput(publishAuthUser);
		publishAuthPassword = LRUtilities.nullifyBadInput(publishAuthPassword);
	
		// Throw an exception if any of the required fields are null
		if (url == null || passPhrase == null || publicKeyLocation == null || privateKey == null)
		{
			throw new LRException(LRException.NULL_FIELD);
		}
		
		// Throw an error if the batch size is zero
		if (batchSize == 0)
		{
			throw new LRException(LRException.BATCH_ZERO);
		}
	
		this.batchSize = batchSize;
		
		// If both authorization values are not present, use the non-SSL url
		if (publishAuthUser == null || publishAuthPassword == null)
		{
			this.url = publishUrlPrefix + url + publishUrlPath;
		}
		// Otherwise, use the SSL url
		else
		{
			this.url = publishSSLUrlPrefix + url + publishUrlPath;
		}
		
		this.publicKeyLocation = publicKeyLocation;
		
		this.publishAuthUser = publishAuthUser;
		this.publishAuthPassword = publishAuthPassword;
		
		this.passPhrase = passPhrase;
		this.privateKey = privateKey;
		
		this.configured = true;
	}
	
	/**
	 * Converts the local location or text of a private key into an input stream
	 * 
	 * @param privateKey The local location or text of the private key for the digital signature
	 * @return Private key stream
	 * @throws LRException NO_KEY_STREAM if the private key cannot be turned into a stream
	*/
	private InputStream getPrivateKeyStream(String privateKey) throws LRException
	{
		try
		{
			// If the private key matches the form of a private key string, treat it as such
			if (privateKey.matches(pgpRegex))
			{
				return new ByteArrayInputStream(privateKey.getBytes());
			}
			// Otherwise, treat it as a file location on the local disk
			else
			{
				return new FileInputStream(new File(privateKey));
			}
		}
		catch (IOException e)
		{
			throw new LRException(LRException.NO_KEY_STREAM);
		}
	}

	/**
	 * Adds a document to the exporter
	 *
	 * This version should be used when including the optional fields
	 *
	 * @param resourceLocator Locator of the document being added (should be the original url)
	 * @param resourceData Data to be added for this document (String or Map<String, Object>)
	 * @param curator Curator of this document
	 * @param owner Owner of this document
	 * @param tags Key values to add to this document
	 * @throws LRException NO_DATA, NO_LOCATOR, NOT_DETAILED, NOT_CONFIGURED, BENCODE_FAILED, SIGNING_FAILED, NO_KEY, NO_KEY_STREAM
	*/
	public void addDocument(LREnvelope envelope) throws LRException
	{
		// Throws errors if required values are null
		if(envelope.getResourceData() == null)
		{
			throw new LRException(LRException.NO_DATA);
		}
		
		if(envelope.getResourceLocator() == null)
		{
			throw new LRException(LRException.NO_LOCATOR);
		}
	
		if(!configured)
		{
			throw new LRException(LRException.NOT_CONFIGURED);
		}
		
		// Build canonical document for signing
		Map<String, Object> doc = new HashMap<String, Object>();
		LRUtilities.put(doc, "doc_type", docType);
		LRUtilities.put(doc, "doc_version", docVersion);
		LRUtilities.put(doc, "resource_data_type", envelope.getResourceDataType());
		
		// The version of the document used for signing must use a string instead of a boolean for "active"
		LRUtilities.put(doc, "active", "true");
		
		Map<String, Object> docId = new HashMap<String, Object>();
		LRUtilities.put(docId, "submitter_type", envelope.getSubmitterType());
		LRUtilities.put(docId, "submitter", envelope.getSubmitter());
		LRUtilities.put(docId, "curator", envelope.getCurator());
		LRUtilities.put(docId, "owner", envelope.getOwner());
		LRUtilities.put(docId, "signer", envelope.getSigner());
		LRUtilities.put(doc, "identity", docId);
		Map<String, Object> docTOS = new HashMap<String, Object>();
		LRUtilities.put(docTOS, "submission_TOS", envelope.getSubmissionTOS());
		LRUtilities.put(docTOS, "submission_attribution", envelope.getSubmissionAttribution());
		LRUtilities.put(doc, "TOS", docTOS);
		LRUtilities.put(doc, "resource_locator", envelope.getResourceLocator());
		LRUtilities.put(doc, "payload_placement", envelope.getPayloadPlacement());
		LRUtilities.put(doc, "payload_schema", envelope.getPayloadSchema());
		LRUtilities.put(doc, "payload_schema_locator", envelope.getPayloadSchemaLocator());
		LRUtilities.put(doc, "keys", envelope.getTags());
		LRUtilities.put(doc, "resource_data", envelope.getResourceData());
		
		// Bencode the document
		String bencodedMessage = bencode(doc);
		
		// Clear sign the bencoded document
		String clearSignedMessage = signEnvelopeData(bencodedMessage);
		
		// Finish the actual document to be sent
		LRUtilities.put(doc, "active", true);
		Map<String, Object> sig = new HashMap<String, Object>();
		String[] keys = {publicKeyLocation};
		LRUtilities.put(sig, "key_location", keys);
		LRUtilities.put(sig, "signing_method", signingMethod);
		LRUtilities.put(sig, "signature", clearSignedMessage);
		LRUtilities.put(doc, "digital_signature", sig);
		
		docs.add(doc);
	}
	
	/**
	 * Sends documents to the node defined in configuration
	 *
	 * @return List of LRResponse packages for each batch of documents sent to the node
	 * @throws LRException NOT_CONFIGURED, NO_DOCUMENTS, NO_RESPONSE, INVALID_RESPONSE, JSON_FAILED
	*/
	public List<LRResponse> sendData() throws LRException
	{
		// Throw an error if configuration has not been performed
		if(!configured)
		{
			throw new LRException(LRException.NOT_CONFIGURED);
		}
		
		// Throw an error if no documents have been added for submission
		if (docs.size() == 0)
		{
			throw new LRException(LRException.NO_DOCUMENTS);
		}
		
		List<LRResponse> responses = new ArrayList<LRResponse>();
		
		JSONObject jsonObjSend = new JSONObject();

		// Figure out how many batches need to be sent
		int batches = (int)Math.ceil((float)docs.size() / batchSize);
		
		// Send each batch and add the response to our return value
		for (int i = 0; i < batches; i++)
		{
			int startIndex = i * batchSize;
			int endIndex = startIndex + batchSize;
			if (endIndex > docs.size())
			{
				endIndex = docs.size();
			}
		
			List<Object> batchDoc = docs.subList(startIndex, endIndex);
			
			// Add this batch of documents to the batch parent document
			try
			{
				jsonObjSend.put("documents", batchDoc);
			}
			catch (JSONException e)
			{
				throw new LRException(LRException.JSON_FAILED);
			}

			String jsonString = jsonObjSend.toString();

			HttpResponse response;
			
			String jsonError = "";
			
			StringEntity se;
			
			// Convert this batch into a string for submission
			try
			{
				se = new StringEntity(jsonString);
			}
			catch (UnsupportedEncodingException e)
			{
				throw new LRException(LRException.JSON_FAILED);
			}
			
			// Send the string to the node
			try
			{
				response = LRClient.executeJsonPost(url, se, publishAuthUser, publishAuthPassword);
			}
			catch (Exception e)
			{
				throw new LRException(LRException.NO_RESPONSE);
			}

			LRResponse responsePackage = null;
			
			// Get the response from the node
			if (response != null)
			{
				try
				{
					InputStream is = response.getEntity().getContent();
					jsonError = IOUtils.toString(is, "UTF-8");
					responsePackage = new LRResponse(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
				}
				catch (IOException e)
				{
					throw new LRException(LRException.INVALID_RESPONSE);
				}
			}

			// Decode the response and prepare results for return
			if (responsePackage != null)
			{
				try
				{
					JSONObject jsonObjRes = new JSONObject(jsonError);
					
					boolean batchSuccess = false;
					String batchError = "No error reported";
					if (jsonObjRes.has("OK"))
					{
						batchSuccess = jsonObjRes.getBoolean("OK");
					}
					if (jsonObjRes.has("error"))
					{
						batchError = jsonObjRes.getString("error");
					}
					
					responsePackage.setBatchResponse(batchError, batchSuccess);

					if (batchSuccess)
					{
						JSONArray jarry = jsonObjRes.getJSONArray("document_results");
						
						for(int j = 0; j < jarry.length(); j++)
						{
							JSONObject job = jarry.getJSONObject(j);
							
							String error = "";
							String id = "";
							boolean ok = false;
							
							if (job.has("OK"))
								ok = job.getBoolean("OK");

							if (ok)
							{
								if (job.has("doc_ID"))
								{
									id = job.getString("doc_ID");
								}
								
								responsePackage.addResourceSuccess(id);
							}
							else
							{
								if (job.has("error"))
								{
									error = job.getString("error");
								}
								
								responsePackage.addResourceFailure(error);
							}
						}
					}
				}
				catch (JSONException e)
				{
					//Return response package anyway, since it already has the basic information we need
				}
				responses.add(responsePackage);
			}
		}
		
		return responses;
	}
	
	/**
	 * Bencodes document
	 *
	 * @param doc Document to be bencoded
	 * @return Bencoded string of the provided document
	 * @throws LRException BENCODE_FAILED if document cannot be bencoded
	*/
	private String bencode(Map<String, Object> doc) throws LRException
	{
		String text = "";
		String encodedString = "";
		
		// Bencode the provided document
		
		try
		{
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			BencodingOutputStream bencoder = new BencodingOutputStream(s);
			bencoder.writeMap(doc);
			bencoder.flush();
			encodedString = s.toString();
			s.close();

			// Hash the bencoded document
			MessageDigest md;
			
			md = MessageDigest.getInstance("SHA-256");
				
			md.update(encodedString.getBytes());
			byte[] mdbytes = md.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i=0; i<mdbytes.length; i++)
			{
				String hex = Integer.toHexString(0xFF & mdbytes[i]);
				if (hex.length() == 1)
				{
					hexString.append('0');
				}
				hexString.append(hex);
			}
			text = hexString.toString();
		}
		catch (Exception e)
		{
			throw new LRException(LRException.BENCODE_FAILED);
		}
		
		return text;
	}
	
	/**
	 * Encodes the provided message with the private key and pass phrase set in configuration
	 *
	 * @param message Message to encode
	 * @return Encoded message
	 * @throws LRException SIGNING_FAILED if the document cannot be signed, NO_KEY if the key cannot be obtained
	*/
	private String signEnvelopeData(String message) throws LRException
	{
		// Get an InputStream for the private key
		InputStream privateKeyStream = getPrivateKeyStream(privateKey);
		
		// Get an OutputStream for the result
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		ArmoredOutputStream aOut = new ArmoredOutputStream(result);
		
		// Get the pass phrase
		char[] privateKeyPassword = passPhrase.toCharArray();
		
		try
		{
			// Get the private key from the InputStream
			PGPSecretKey sk = readSecretKey(privateKeyStream);
			PGPPrivateKey pk = sk.extractPrivateKey(privateKeyPassword, "BC");
			PGPSignatureGenerator pgp = new PGPSignatureGenerator(sk
					.getPublicKey().getAlgorithm(), PGPUtil.SHA256, "BC");
			PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
			
			// Clear sign the message		
			java.util.Iterator it = sk.getPublicKey().getUserIDs();
			if (it.hasNext()) {
				spGen.setSignerUserID(false, (String) it.next());
				pgp.setHashedSubpackets(spGen.generate());
			}			
			aOut.beginClearText(PGPUtil.SHA256);
			pgp.initSign(PGPSignature.CANONICAL_TEXT_DOCUMENT, pk);
			byte[] msg = message.getBytes();
			pgp.update(msg,0,msg.length);
			aOut.write(msg,0,msg.length);
			BCPGOutputStream bOut = new BCPGOutputStream(aOut);
			aOut.endClearText();
			pgp.generate().encode(bOut);
			String strResult = result.toString("utf8");
			return strResult;
		}
		catch (Exception e)
		{
			throw new LRException(LRException.SIGNING_FAILED);
		}
		finally
		{
			try
			{
				if (privateKeyStream != null) {
					privateKeyStream.close();
				}
				aOut.close();
				result.close();
			}
			catch (IOException e)
			{
				//Could not close the streams
			}
		}
	}
	
	/**
	 * Reads private key from the provided InputStream
	 *
	 * @param input InputStream of the private key
	 * @return PGPSecretKey
	 * @throws LRException NO_KEY error if the key cannot be obtained from the input stream
	*/
	private PGPSecretKey readSecretKey(InputStream input) throws LRException
	{
		PGPSecretKeyRingCollection pgpSec;
		
		try
		{
			pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(input));
		}
		catch (Exception e)
		{
			throw new LRException(LRException.NO_KEY);
		}

		java.util.Iterator keyRingIter = pgpSec.getKeyRings();
		while (keyRingIter.hasNext()) {
			PGPSecretKeyRing keyRing = (PGPSecretKeyRing) keyRingIter.next();

			java.util.Iterator keyIter = keyRing.getSecretKeys();
			while (keyIter.hasNext()) {
				PGPSecretKey key = (PGPSecretKey) keyIter.next();

				if (key.isSigningKey()) {
					return key;
				}
			}
		}

		throw new LRException(LRException.NO_KEY);
	}
}
