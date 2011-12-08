package com.navnorth.learningregistry;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

/**
 * Exporter of data to a Learning Registry node
 *
 * @author Navigation North
 * @version 1.0
 * @since 2011-11-17
*/
public class LRExporter
{
	// Static strings
	private static final String publishUrlPrefix = "http://";
	private static final String publishSSLUrlPrefix = "https://";
	private static final String publishUrlPath = "/publish";
	
	// Cofiguration variables
	private int batchSize;
	private String url;
	private String publishAuthUser;
	private String publishAuthPassword;
	
	// Booleans to track if configuration is complete
	private boolean configured = false;

	// Collection of encoded documents to be sent
	private List<Object> docs = new ArrayList<Object>();
	
	/**
	 * Creates the exporter object and adds the security provider
	*/
    public LRExporter(int batchSize, String url)
    {
		this.batchSize = batchSize;
		this.url = url;
		this.publishAuthUser = null;
		this.publishAuthPassword = null;
    }
	
	/**
	 * Creates the exporter object and adds the security provider
	*/
    public LRExporter(int batchSize, String url, String publishAuthUser, String publishAuthPassword)
    {
		this.batchSize = batchSize;
		this.url = url;
		this.publishAuthUser = publishAuthUser;
		this.publishAuthPassword = publishAuthPassword;
    }

	public void configure() throws LRException
	{
		// Trim or nullify strings
		url = LRUtilities.nullifyBadInput(url);
		publishAuthUser = LRUtilities.nullifyBadInput(publishAuthUser);
		publishAuthPassword = LRUtilities.nullifyBadInput(publishAuthPassword);
	
		// Throw an exception if any of the required fields are null
		if (url == null)
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
		
		this.publishAuthUser = publishAuthUser;
		this.publishAuthPassword = publishAuthPassword;
		
		this.configured = true;
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
		if(!configured)
		{
			throw new LRException(LRException.NOT_CONFIGURED);
		}
		
		docs.add(envelope.getSendableData());
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

	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
		configured = false;
	}
	
	public int getBatchSize()
	{
		return batchSize;
	}
	
	public void setUrl(String url)
	{
		this.url = url;
		configured = false;
	}
	
	public String getUrl()
	{
		return url;
	}

	public void setPublishAuthUser(String publishAuthUser)
	{
		this.publishAuthUser = publishAuthUser;
		configured = false;
	}
	
	public String getPublishAuthUser()
	{
		return publishAuthUser;
	}
	
	public void setPublishAuthPassword(String publishAuthPassword)
	{
		this.publishAuthPassword = publishAuthPassword;
		configured = false;
	}
	
	public String getPublishAuthPassword()
	{
		return publishAuthPassword;
	}
}
