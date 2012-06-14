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
 * @version 0.1.1
 * @since 2011-11-17
 * @author Todd Brown / Navigation North
 *      <br>
 *      Copyright © 2011 Navigation North Learning Solutions LLC
 *      <br>
 *      Licensed under the Apache License, Version 2.0 (the "License"); See LICENSE
 *      and README.md files distributed with this work for additional information
 *      regarding copyright ownership.
 */
public class LRImporter
{
    // LR function paths
    // TODO : get service URLs from the node itself
    private static String harvestPath = "/harvest/getrecord";
    private static String obtainPath = "/obtain";
    
    // LR parameters
    private static String byResourceIDParam = "by_resource_ID";
    private static String requestIDParam = "request_ID";
    private static String byDocIDParam = "by_doc_ID";
    private static String idsOnlyParam = "ids_only";
    private static String resumptionTokenParam = "resumption_token";
    
    // LR string values of boolean values
    private static String booleanTrueString = "true";
    private static String booleanFalseString = "false";
    
    private String nodeHost;
    private String importProtocol = "http";
    
    /**
     * Creates the importer object
     *
     * @param nodeHost the IP/domain of the learning registry node
     * @param importProtocol which protocol to import over (typically "http" or "https")
     */
    public LRImporter(String nodeHost, String importProtocol)
    {
        this.nodeHost = nodeHost;
        this.importProtocol = importProtocol;
    }

    /**
     * Creates the importer object
     *  !!! Will be deprecated in future releases in favor of "protocol" parameter !!!
     *
     * @param nodeHost the IP/domain of the learning registry node
     * @param useSSL specify if this importer should use SSL
     */
    public LRImporter(String nodeHost, Boolean useSSL)
    {
        this.nodeHost = nodeHost;
        if (useSSL) 
        {
            this.importProtocol = "https";
        } 
        else
        {
            this.importProtocol = "http";
        }
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
        String path = obtainPath;
        
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
        String path = harvestPath;
        
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
    public LRResult getObtainJSONData(String resumptionToken) throws LRException
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
    public LRResult getObtainJSONData(String requestID, Boolean byResourceID, Boolean byDocID, Boolean idsOnly) throws LRException
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
    private LRResult getObtainJSONData(String requestID, Boolean byResourceID, Boolean byDocID, Boolean idsOnly, String resumptionToken) throws LRException
    {
        String path = getObtainRequestPath(requestID, byResourceID, byDocID, idsOnly, resumptionToken);
                
        JSONObject json = getJSONFromPath(path);
        
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
    public LRResult getHarvestJSONData(String requestID, Boolean byResourceID, Boolean byDocID) throws LRException
    {
        String path = getHarvestRequestPath(requestID, byResourceID, byDocID);
        
        JSONObject json = getJSONFromPath(path);
        
        return new LRResult(json);
    }
        
    /**
     * Get the importProtocol value
     *
     * @return importProtocol value
     */
    public String getImportProtocol()
    {
        return importProtocol;
    }
    
    /**
     * Set the importProtocol value
     *
     * @param importProtocol value
     */
    public void setImportProtocol(String importProtocol)
    {
        this.importProtocol = importProtocol;
    }
    
    /**
     * Get the nodeHost value
     *
     * @return nodeHost value
     */
    public String getNodeHost()
    {
        return nodeHost;
    }
    
    /**
     * Set the nodeHost value
     *
     * @param nodeHost value
     */
    public void setNodeHost(String nodeHost)
    {
        this.nodeHost = nodeHost;
    }
    
    /**
     * Get the data from the specified path as a JSONObject
     * 
     * @param path the path to use for this request
     * @return the JSON from the request
     */
    private JSONObject getJSONFromPath(String path) throws LRException
    {
        JSONObject json = null;
        String jsonTxt = null;
        
        try
        {
            jsonTxt = LRClient.executeJsonGet(importProtocol + "://" + nodeHost + path);
        }
        catch(Exception e)
        {
            throw new LRException(LRException.IMPORT_FAILED);
            //e.printStackTrace();
        }
        
        try
        {
            json = new JSONObject(jsonTxt);
        }
        catch(JSONException e)
        {
            throw new LRException(LRException.JSON_IMPORT_FAILED);
            //e.printStackTrace();
        }

        return json;
    }
}