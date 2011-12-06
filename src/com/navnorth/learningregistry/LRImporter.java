package com.navnorth.learningregistry;

import java.util.Map;
import java.util.HashMap;

import java.net.URL;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.*;

/**
 * Importer of data from a Learning Registry node
 *
 * @author Navigation North
 * @version 1.0
 * @since 2011-12-05
*/
public class LRImporter
{
	// LR function paths
	private static String harvestPath = "/harvest/getrecord";
	private static String obtainPath = "/obtain/getrecord";
	
	// LR parameters
	private static String byResourceIDParam = "by_resource_ID";
	private static String requestIDParam = "request_ID";
	private static String byDocIDParam = "by_doc_ID";
	private static String idsOnlyParam = "ids_only";
	private static String resumptionTokenParam = "resumption_token";
	
	// LR string values of boolean values
	private static String booleanTrueString = "true";
	private static String booleanFalseString = "false";
	
	private Boolean useSSL;
	private String nodeURL;
	
	/**
	 * Creates the importer object
	 *
	 * @param nodeURL the URL of the learning registry node
	 * @param useSSL specify if this importer should use SSL
	 */
	public LRImporter(String nodeURL, Boolean useSSL)
	{
		this.useSSL = useSSL;
		this.nodeURL = nodeURL;
	}
	
	/**
	 * Obtain the path used for an obtain request
	 * 
	 * @param requestID the "request_ID" parameter for the request
	 * @param byResourceID the "by_resource_ID" parameter for the request
	 * @param byDocID the "by_doc_ID" parameter for the request
	 * @param idsOnly the "ids_only" parameter for the request
	 * @param resumptionToken the "resumption_token" parameter for the request
	 * @return the string of the path for an obtain request
	 */
	private String getObtainRequestPath(String requestID, Boolean byResourceID, Boolean byDocID, Boolean idsOnly, String resumptionToken)
	{
		String path = nodeURL + harvestPath;
		
		if (resumptionToken != null)
		{
			path += "?" + resumptionTokenParam + "=" + resumptionToken;
			return path;
		}
		
		if (requestID != null)
		{
			path += "?" + requestIDParam + "=" + requestID;
		}
		else
		{
			// error
			return null;
		}
		
		if (byResourceID)
		{
			path += "&" + byResourceIDParam + "=" + booleanTrueString;
		}
		else
		{
			path += "&" + byResourceIDParam + "=" + booleanFalseString;
		}
		
		if (byDocID)
		{
			path += "&" + byDocIDParam + "=" + booleanTrueString;
		}
		else
		{
			path += "&" + byDocIDParam + "=" + booleanFalseString;
		}
		
		if (idsOnly)
		{
			path += "&" + idsOnlyParam + "=" + booleanTrueString;
		}
		else
		{
			path += "&" + idsOnlyParam + "=" + booleanFalseString;
		}
		
		return path;
	}
	
	/**
	 * Obtain the path used for a harvest request
	 * 
	 * @param requestID the "request_ID" parameter for the request
	 * @param byResourceID the "by_resource_ID" parameter for the request
	 * @param byDocID the "by_doc_ID" parameter for the request
	 * @return the string of the path for a harvest request
	 */
	private String getHarvestRequestPath(String requestID, Boolean byResourceID, Boolean byDocID)
	{
		String path = nodeURL + obtainPath;
		
		if (requestID != null)
		{
			path += "?" + requestIDParam + "=" + requestID;
		}
		else
		{
			// error
			return null;
		}
		
		if (byResourceID)
		{
			path += "&" + byResourceIDParam + "=" + booleanTrueString;
		}
		else
		{
			path += "&" + byResourceIDParam + "=" + booleanFalseString;
		}
		
		if (byDocID)
		{
			path += "&" + byDocIDParam + "=" + booleanTrueString;
		}
		else
		{
			path += "&" + byDocIDParam + "=" + booleanFalseString;
		}
		
		return path;
	}
	
	/**
	 * Get a result from an obtain request
	 *
	 * @param resumptionToken the "resumption_token" value to use for this request
	 * @return the result from this request
	 */
	public LRResult getObtainJSONData(String resumptionToken)
	{
		return getObtainJSONData(null, null, null, null, resumptionToken);
	}
	
	/**
	 * Get a result from an obtain request
	 *
	 * @param requestID the "request_id" value to use for this request
	 * @param byResourceID the "by_resource_id" value to use for this request
	 * @param byDocID the "by_doc_id" value to use for this request
	 * @param idsOnly the "ids_only" value to use for this request
	 * @return the result from this request
	 */
	public LRResult getObtainJSONData(String requestID, Boolean byResourceID, Boolean byDocID, Boolean idsOnly)
	{
		return getObtainJSONData(requestID, byResourceID, byDocID, idsOnly, null);
	}
	
	/**
	 * Get a result from an obtain request
	 * If the resumption token is not null, it will override the other parameters for ths request
	 *
	 * @param requestID the "request_id" value to use for this request
	 * @param byResourceID the "by_resource_id" value to use for this request
	 * @param byDocID the "by_doc_id" value to use for this request
	 * @param idsOnly the "ids_only" value to use for this request
	 * @param resumptionToken the "resumption_token" value to use for this request
	 * @return the result from this request
	 */
	private LRResult getObtainJSONData(String requestID, Boolean byResourceID, Boolean byDocID, Boolean idsOnly, String resumptionToken)
	{
		String path = getObtainRequestPath(requestID, byResourceID, byDocID, idsOnly, resumptionToken);
		
		JSONObject json = null;
		
		if (useSSL)
		{
			json = getJSONFromPathSSL(path);
		}
		else
		{
			json = getJSONFromPath(path);
		}
		
		return new LRResult(json);
	}
	
	/**
	 * Get a result from a harvest request
	 *
	 * @param requestID the "request_id" value to use for this request
	 * @param byResourceID the "by_resource_id" value to use for this request
	 * @param byDocID the "by_doc_id" value to use for this request
	 * @return the result from this request
	 */
	public LRResult getHarvestJSONData(String requestID, Boolean byResourceID, Boolean byDocID)
	{
		String path = getHarvestRequestPath(requestID, byResourceID, byDocID);
		
		JSONObject json = null;
		
		if (useSSL)
		{
			json = getJSONFromPathSSL(path);
		}
		else
		{
			json = getJSONFromPath(path);
		}
		
		return new LRResult(json);	
	}
		
	/**
	 * Get the useSSL value
	 *
	 * @return useSSL value
	 */
	public Boolean getUseSSL()
	{
		return useSSL;
	}
	
	/**
	 * Set the useSSL value
	 *
	 * @param useSSL value
	 */
	public void setUseSSL(Boolean useSSL)
	{
		this.useSSL = useSSL;
	}
	
	/**
	 * Get the nodeURL value
	 *
	 * @return nodeURL value
	 */
	public String getNodeURL()
	{
		return nodeURL;
	}
	
	/**
	 * Set the nodeURL value
	 *
	 * @param nodeURL value
	 */
	public void setNodeURL(String nodeURL)
	{
		this.nodeURL = nodeURL;
	}
	
	/**
	 * Get the data from the specified path as a JSONObject, using SSL
	 * 
	 * @param path the path to use for this request
	 * @return the JSON from the request
	 */
	private JSONObject getJSONFromPathSSL(String path)
	{
		JSONObject json = null;
		
		try
		{
			String jsonTxt = LRClient.executeJsonGet("https://" + path);
			json = new JSONObject(jsonTxt);
		}
		catch(Exception e)
		{
		}

		return json;
	}

	/**
	 * Get the data from the specified path as a JSONObject
	 * 
	 * @param path the path to use for this request
	 * @return the JSON from the request
	 */
	private JSONObject getJSONFromPath(String path)
	{
		JSONObject json = null;
		
		try
		{
            URL importURL = new URL("http://" + path);
									
			InputStream is = importURL.openStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			is.close();
			
			String jsonTxt = sb.toString();

			json = new JSONObject(jsonTxt);
		}
		catch(Exception e)
		{
		}

		return json;
	}
}