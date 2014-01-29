/**
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navnorth.learningregistry;

import com.navnorth.learningregistry.util.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import org.ardverk.coding.*;

/**
 * Signer for Learning Registry envelopes
 *
 * @version 0.1.1
 * @since 2011-12-08
 * @author Todd Brown / Navigation North
 *      <br>
 *      Copyright © 2011 Navigation North Learning Solutions LLC
 *      <br>
 *      Licensed under the Apache License, Version 2.0 (the "License"); See LICENSE
 *      and README.md files distributed with this work for additional information
 *      regarding copyright ownership.
 */
public class LRSigner
{
    private static final String pgpRegex = "(?s).*-----BEGIN PGP PRIVATE KEY BLOCK-----.*-----END PGP PRIVATE KEY BLOCK-----.*";
    private static final String signingMethod = "LR-PGP.1.0";
    private static final String nullLiteral = "null";

    private String publicKeyLocation;
    private String privateKey;
    private String passPhrase;
    
    /**
     * Creates a signer, using specified key values
     *
     * @param publicKeyLocation location of the public key to be included in signed envelopes
     * @param privateKey local location or raw value of private key to be used for encoding
     * @param passPhrase pass phrase for signing with the included private key
     */
    public LRSigner(String publicKeyLocation, String privateKey, String passPhrase)
    {
        Security.addProvider(new BouncyCastleProvider());
    
        this.publicKeyLocation = publicKeyLocation.replaceAll("&amp;", "&");
        this.privateKey = privateKey;
        this.passPhrase = passPhrase;
        
        passPhrase = StringUtil.nullifyBadInput(passPhrase);
        publicKeyLocation = StringUtil.nullifyBadInput(publicKeyLocation);
        privateKey = StringUtil.nullifyBadInput(privateKey);
    }
    
    /**
     * Sign the specified envelope with this signer
     *
     * @param envelope envelope to be signed
     * @return signed envelope
     * @throws LRException
     */
    public LREnvelope sign(LREnvelope envelope) throws LRException
    {
        // Bencode the document
        String bencodedMessage = bencode(envelope.getSignableData());
        
        // Clear sign the bencoded document
        String clearSignedMessage = signEnvelopeData(bencodedMessage);
        
        envelope.addSigningData(signingMethod, publicKeyLocation, clearSignedMessage);
        
        return envelope;
    }

    /**
     * Sign the specified activity (envelope) with this signer
     *
     * @param envelope envelope to be signed
     * @return signed envelope
     * @throws LRException
     */
    public LRActivity sign(LRActivity envelope) throws LRException
    {
        // Bencode the document
        String bencodedMessage = bencode(envelope.getSignableData());
        
        // Clear sign the bencoded document
        String clearSignedMessage = signEnvelopeData(bencodedMessage);
        
        envelope.addSigningData(signingMethod, publicKeyLocation, clearSignedMessage);
        
        return envelope;
    }

    /**
     * Normalizes document as LRSignature Python module does
     * - nulls converted to string literal "null"
     * - booleans converted to string literals "true" or "false"
     * - numeric values in lists are dropped
     * - nested maps/JSON documents are normalized
     * @param doc Document to normalize
     */
    private Map<String, Object> normalizeMap(Map<String, Object> doc) {
    	
    	final Map<String, Object> result = new LinkedHashMap<String, Object>();
    	
    	for (String key : doc.keySet()) {
    		Object value = doc.get(key);
    		
    		if (value == null) {
    			result.put(key, nullLiteral);
    		} else if (value instanceof Boolean) {
    			result.put(key, ((Boolean) value).toString());
    		} else if (value instanceof List<?>) {
    			result.put(key, normalizeList((List<Object>) value));
    		} else if (value instanceof Map<?, ?>) {
    			result.put(key, normalizeMap((Map<String, Object>) value));
    		} else {
    			result.put(key, value);
    		}
    	}
    	
    	return result;
    }
    
    /**
     * Helper for map normalization; inspects list and returns
     * a replacement list that has been normalized.
     * @param list
     * @return Normalized list for encoding/hashing/signing
     */
    private List<Object> normalizeList(List<Object> list) {
    	List<Object> result = new ArrayList<Object>();
    	for (Object o : list) {
    		if (o == null) {
    			result.add(nullLiteral);
    		} else if (o instanceof Boolean) {
    			result.add(((Boolean) o).toString());
    		} else if (o instanceof List<?>) {
    			result.add(normalizeList((List<Object>) o));
    		} else if (o instanceof Map<?, ?>) {
    			result.add(normalizeMap((Map<String, Object>) o));
    		} else if (!(o instanceof Number)) {
    			result.add(o);
    		}
    	}
    	return result;
    }
    
    
    /**
     * Bencodes document
     *
     * @param doc Document to be bencoded
     * @return Bencoded string of the provided document
     * @throws LRException BENCODE_FAILED if document cannot be bencoded
    */
    private String bencode(final Map<String, Object> doc) throws LRException
    {
        String text = "";
        String encodedString = "";
        
        // normalize the document
        final Map<String, Object> normalizedDoc = normalizeMap(doc);
        
        // Bencode the normalized document
        try
        {
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            BencodingOutputStream bencoder = new BencodingOutputStream(s);
            bencoder.writeMap(normalizedDoc);
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
        // Throw an exception if any of the required fields are null
        if (passPhrase == null || publicKeyLocation == null || privateKey == null)
        {
            throw new LRException(LRException.NULL_FIELD);
        }
    
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
            PGPPrivateKey pk = sk.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(privateKeyPassword));
            PGPSignatureGenerator sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(sk.getPublicKey().getAlgorithm(), PGPUtil.SHA256).setProvider("BC"));
            PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
            
            // Clear sign the message        
            java.util.Iterator it = sk.getPublicKey().getUserIDs();
            if (it.hasNext()) {
                spGen.setSignerUserID(false, (String) it.next());
                sGen.setHashedSubpackets(spGen.generate());
            }
            aOut.beginClearText(PGPUtil.SHA256);
            sGen.init(PGPSignature.CANONICAL_TEXT_DOCUMENT, pk);
            byte[] msg = message.getBytes();
            sGen.update(msg,0,msg.length);
            aOut.write(msg,0,msg.length);
            BCPGOutputStream bOut = new BCPGOutputStream(aOut);
            aOut.endClearText();
            sGen.generate().encode(bOut);
            aOut.close();
            
            String strResult = result.toString("utf8");
            
            // for whatever reason, bouncycastle is failing to put a linebreak before "-----BEGIN PGP SIGNATURE"
            strResult = strResult.replaceAll("([a-z0-9])-----BEGIN PGP SIGNATURE-----", "$1\n-----BEGIN PGP SIGNATURE-----");
            
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
}