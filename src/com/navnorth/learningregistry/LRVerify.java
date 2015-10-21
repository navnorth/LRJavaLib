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

import java.util.Iterator;

import java.io.IOException;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.SignatureException;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.bcpg.BCPGInputStream;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

/**
 * Utilities for verifying signed messages
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
public class LRVerify
{
	/**
     * Converts input strings to input streams for the main verify function
     *
     * @param signature String of the signature
     * @param message String of the message
     * @param publicKey String of the public key
	 * @return true if signing is verified, false if not
	 * @throws LRException NULL_FIELD if any field is null, INPUT_STREAM_FAILED if any field cannot be converted to an input stream
     */
	public static boolean VerifyStrings(String signature, String message, String publicKey) throws LRException
	{
		// Check that none of the inputs are null
		if (signature == null || message == null || publicKey == null)
		{
			throw new LRException(LRException.NULL_FIELD);
		}
	
		// Convert all inputs into input streams
		InputStream isSignature = null;
		InputStream isMessage = null;
		InputStream isPublicKey = null;
		
		try
		{
			isSignature = new ByteArrayInputStream(signature.getBytes());
			isMessage = new ByteArrayInputStream(message.getBytes());
			isPublicKey = new ByteArrayInputStream(publicKey.getBytes());
		}
		catch (Exception e)
		{
			throw new LRException(LRException.INPUT_STREAM_FAILED);
		}
		
		// Feed the input streams into the primary verify function
		return Verify(isSignature, isMessage, isPublicKey);
	}

	/**
	 * Verfies that the provided message and signature using the public key
	 *
	 * @param isSignature InputStream of the signature
	 * @param isMessage InputStream of the message
	 * @param isPublicKey InputStream of the public key
	 * @throws LRException
	 */
    private static boolean Verify(InputStream isSignature, InputStream isMessage, InputStream isPublicKey) throws LRException
    {
		// Get the public key ring collection from the public key input stream
		PGPPublicKeyRingCollection pgpRings = null;
		
		try
		{
			 pgpRings = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(isPublicKey));
		}
		catch (Exception e)
		{
			throw new LRException(LRException.INVALID_PUBLIC_KEY);
		}
	
		// Add the Bouncy Castle security provider
		Security.addProvider(new BouncyCastleProvider());
		
		// Build an output stream from the message for verification
		boolean verify = false;
		int ch;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ArmoredInputStream aIn  = null;
		
		try
		{
			aIn = new ArmoredInputStream(isMessage);
			// We are making no effort to clean the input for this example
			// If this turns into a fully-featured verification utility in a future version, this will need to be handled
			while ((ch = aIn.read()) >= 0 && aIn.isClearText())
			{
				bOut.write((byte)ch);
			}
			
			bOut.close();
		}
		catch (Exception e)
		{
			throw new LRException(LRException.MESSAGE_INVALID);
		}
		
		// Build an object factory from the signature input stream and try to get an object out of it
		Object o = null;
		try
		{
			PGPObjectFactory pgpFact = new PGPObjectFactory(PGPUtil.getDecoderStream(isSignature));
			o = pgpFact.nextObject();
		}
		catch (Exception e)
		{
			throw new LRException(LRException.SIGNATURE_INVALID);
		}
			
		// Check if the object we fetched is a signature list and if it is, get the signature and use it to verfiy
		try
		{
			if (o instanceof PGPSignatureList)
			{
				PGPSignatureList list = (PGPSignatureList)o;
				if (list.size() > 0)
				{
					PGPSignature sig = list.get(0);
					
					PGPPublicKey publicKey = pgpRings.getPublicKey(sig.getKeyID());
					sig.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), publicKey);
					
					sig.update(bOut.toByteArray());
					verify = sig.verify();
				}
			}
		}
		catch (Exception e)
		{
			throw new LRException(LRException.SIGNATURE_NOT_FOUND);
		}
		
		return verify;
	}
}