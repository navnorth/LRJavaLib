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
import java.util.ArrayList;

import org.json.*;

/**
 * Result of a learning registry consumption request
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
public class LRResult
{
    private static String resumptionTokenParam = "resumption_token";
    private static String documentsParam = "documents";
    private static String documentParam = "document";
    private static String getRecordParam = "getrecord";
    private static String recordParam = "record";
    private static String resourceDataParam = "resource_data";

    private JSONObject data;
    
    /**
     * Create a result object
     *
     * @param data the JSON data of the result
     */
    public LRResult(JSONObject data)
    {
        this.data = data;
    }
    
    /**
     * Return the JSON data
     *
     * @return JSON data
     */
    public JSONObject getData()
    {
        return data;
    }
    
    /**
     * Returns the resumption token, if it exists
     * 
     * @return resumption token or null
     */
    public String getResumptionToken()
    {
        String resumptionToken = null;
    
        try
        {
            if (data != null && data.has(resumptionTokenParam))
            {
                resumptionToken = data.getString(resumptionTokenParam);
            }
        }
        catch (JSONException e)
        {
            //This is already handled by the enclosing "has" checks
        }
        
        return resumptionToken;
    }
    
    /**
     * Returns resource data from obtain or harvest request
     *
     * @return list of resource data as JSON
     */
    public List<JSONObject> getResourceData()
    {
        List<JSONObject> resources = new ArrayList<JSONObject>();
    
        try
        {
            if (data != null && data.has(documentsParam))
            {
                List<JSONObject> results = getDocuments();
                
                for(JSONObject json : results)
                {
                    if (json.has(documentParam))
                    {
                        JSONObject result = json.getJSONObject(documentParam);
                        results.add(result);
                    }
                }
            }
            else if (data != null && data.has(getRecordParam))
            {
                List<JSONObject> results = getRecords();
                
                for(JSONObject json : results)
                {
                    if (json.has(resourceDataParam))
                    {
                        JSONObject result = json.getJSONObject(resourceDataParam);
                        results.add(result);
                    }
                }
            }
        }
        catch (JSONException e)
        {
            //This is already handled by the enclosing "has" checks
        }
        
        return resources;
    }
    
    /**
     * Returns documents from obtain request
     *
     * @return documents from obtain request
     */
    public List<JSONObject> getDocuments()
    {
        List<JSONObject> documents = new ArrayList<JSONObject>();
        
        try
        {
            if (data != null && data.has(documentsParam))
            {
                JSONArray jsonDocuments = data.getJSONArray(documentsParam);
                
                for(int i = 0; i < jsonDocuments.length(); i++)
                {
                    JSONObject document = jsonDocuments.getJSONObject(i);
                    documents.add(document);
                }
            }
        }
        catch (JSONException e)
        {
            //This is already handled by the enclosing "has" checks
        }
        
        return documents;
    }
    
    /**
     * Returns records from harvest request
     *
     * @return records from harvest request
     */
    public List<JSONObject> getRecords()
    {
        List<JSONObject> records = new ArrayList<JSONObject>();
    
        try
        {
            if (data != null && data.has(getRecordParam))
            {
                JSONObject jsonGetRecord = data.getJSONObject(getRecordParam);
                
                if (jsonGetRecord.has(recordParam))
                {
                    JSONArray jsonRecords = jsonGetRecord.getJSONArray(recordParam);
                    
                    for(int i = 0; i < jsonRecords.length(); i++)
                    {
                        JSONObject record = jsonRecords.getJSONObject(i);
                        records.add(record);
                    }
                }
            }
        }
        catch (JSONException e)
        {
            //This is already handled by the enclosing "has" checks
        }
        
        return records;
    }
}