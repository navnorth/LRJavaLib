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
 * @version 0.1
 * @since 2011-11-17
 * @author Todd Brown / Navigation North
 *      <br>
 *      Copyright © 2011 Navigation North Learning Solutions LLC
 *      <br>
 *      Licensed under the Apache License, Version 2.0 (the "License"); See LICENSE
 *      and README.md files distributed with this work for additional information
 *      regarding copyright ownership.
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
	 * Creates the exporter object with the specified details
	 *
	 * @param batchSize the number of items to submit per batch to the Learning Registry node
	 * @param url location of the Learning Registry node to use for export
	*/
    public LRExporter(int batchSize, String url)
    {
		this.batchSize = batchSize;
		this.url = url;
		this.publishAuthUser = null;
		this.publishAuthPassword = null;
    }
	
	/**
	 * Creates the exporter object with the specified details
	 * This version of the constructor sets the exporter up to use SSL
	 *
	 * @param batchSize the number of items to submit per batch to the Learning Registry node
	 * @param url location of the Learning Registry node to use for export
	 * @param publishAuthUser user value for SSL
	 * @param publishAuthPassword password value for SSL
	*/
    public LRExporter(int batchSize, String url, String publishAuthUser, String publishAuthPassword)
    {
		this.batchSize = batchSize;
		this.url = url;
		this.publishAuthUser = publishAuthUser;
		this.publishAuthPassword = publishAuthPassword;
    }

	/**
	 * Attempt to configure the exporter with the values used in the constructor
	 * This must be called before an exporter can be used and after any setting of configuration values
	 *
	 * @throws LRException
	 */
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
	 * Adds an envelope to the exporter
	 *
	 * @param envelope envelope to add to the exporter
	 * @throws LRException NOT_CONFIGURED
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

	/**
	 * Sets the batchSize value
	 * Must call "configure" on exporter after setting this
	 *
	 * @param batchSize value
	 */
	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
		configured = false;
	}
	
	/**
	 * Get the batchSize value
	 *
	 * @return batchSize value
	 */
	public int getBatchSize()
	{
		return batchSize;
	}
	
	/**
	 * Sets the url value
	 * Must call "configure" on exporter after setting this
	 *
	 * @param url value
	 */
	public void setUrl(String url)
	{
		this.url = url;
		configured = false;
	}
	
	/**
	 * Get the url value
	 *
	 * @return url value
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * Sets the publishAuthUser value
	 * Must call "configure" on exporter after setting this
	 *
	 * @param publishAuthUser value
	 */
	public void setPublishAuthUser(String publishAuthUser)
	{
		this.publishAuthUser = publishAuthUser;
		configured = false;
	}
	
	/**
	 * Get the publishAuthUser value
	 *
	 * @return publishAuthUser value
	 */
	public String getPublishAuthUser()
	{
		return publishAuthUser;
	}
	
	/**
	 * Sets the publishAuthPassword value
	 * Must call "configure" on exporter after setting this
	 *
	 * @param publishAuthPassword value
	 */
	public void setPublishAuthPassword(String publishAuthPassword)
	{
		this.publishAuthPassword = publishAuthPassword;
		configured = false;
	}
	
	/**
	 * Get the publishAuthPassword value
	 *
	 * @return publishAuthPassword value
	 */
	public String getPublishAuthPassword()
	{
		return publishAuthPassword;
	}
}
