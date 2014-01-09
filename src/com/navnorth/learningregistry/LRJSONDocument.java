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

import java.io.IOException;
import java.util.HashMap;
import org.json.*;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Base envelope class from which Learning Registry exportable types are derived
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
public class LRJSONDocument extends LREnvelope
{
    /**
     * Create a new simple document with specified details
     *
     * @param resourceData value for "resource_data"
     * @param resourceDataType value for "resource_data_type"
     * @param resourceLocator value for "resource_locator"
     * @param curator value for "curator"
     * @param owner value for "owner"
     * @param tags value for "keys"
     * @param payloadPlacement value for "payload_placement"
     * @param payloadSchemaLocator value for "payload_schema_locator"
     * @param payloadSchema value for "payload_schema"
     * @param submitter value for "submitter"
     * @param submitterType value for "submitter_type"
     * @param submissionTOS value for "submission_TOS"
     * @param submissionAttribution value for "submission_attribution"
     * @param signer value for "signer"
     */
    public LRJSONDocument(JSONObject resourceData, String resourceDataType, String resourceLocator, String curator, String owner, String[] tags,
        String payloadPlacement, String payloadSchemaLocator, String[] payloadSchema,
        String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer) throws LRException
    {
        initProperties(this, resourceData, resourceDataType, resourceLocator, curator, owner, tags, payloadPlacement,
    		payloadSchemaLocator, payloadSchema, submitter, submitterType, submissionTOS,
    		submissionAttribution, signer, null);
    }
    
    /**
     * Create a new simple document with specified details
     *
     * @param resourceData value for "resource_data"
     * @param resourceDataType value for "resource_data_type"
     * @param resourceLocator value for "resource_locator"
     * @param curator value for "curator"
     * @param owner value for "owner"
     * @param tags value for "keys"
     * @param payloadPlacement value for "payload_placement"
     * @param payloadSchemaLocator value for "payload_schema_locator"
     * @param payloadSchema value for "payload_schema"
     * @param submitter value for "submitter"
     * @param submitterType value for "submitter_type"
     * @param submissionTOS value for "submission_TOS"
     * @param submissionAttribution value for "submission_attribution"
     * @param signer value for "signer"
     */
    public LRJSONDocument(String resourceData, String resourceDataType, String resourceLocator, String curator, String owner, String[] tags,
        String payloadPlacement, String payloadSchemaLocator, String[] payloadSchema,
        String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer) throws LRException
    {
        try
        {
            JSONObject resourceJSON = new JSONObject(resourceData);
            initProperties(this, resourceJSON, resourceDataType, resourceLocator, curator, owner, tags, payloadPlacement,
            		payloadSchemaLocator, payloadSchema, submitter, submitterType, submissionTOS,
            		submissionAttribution, signer, null);
        }
        catch (JSONException e)
        {
            throw new LRException(LRException.INVALID_JSON);
        }
    }
    
    /**
     * Create a new simple document with specified details
     *
     * @param resourceData value for "resource_data"
     * @param resourceDataType value for "resource_data_type"
     * @param resourceLocator value for "resource_locator"
     * @param curator value for "curator"
     * @param owner value for "owner"
     * @param tags value for "keys"
     * @param payloadPlacement value for "payload_placement"
     * @param payloadSchemaLocator value for "payload_schema_locator"
     * @param payloadSchema value for "payload_schema"
     * @param submitter value for "submitter"
     * @param submitterType value for "submitter_type"
     * @param submissionTOS value for "submission_TOS"
     * @param submissionAttribution value for "submission_attribution"
     * @param signer value for "signer"
     * @param replaces array of document IDs to be replaced by this document
     */
    public LRJSONDocument(JSONObject resourceData, String resourceDataType, String resourceLocator, String curator, String owner, String[] tags,
        String payloadPlacement, String payloadSchemaLocator, String[] payloadSchema,
        String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer,
        String[] replaces) throws LRException
    {
        initProperties(this, resourceData, resourceDataType, resourceLocator, curator, owner, tags, payloadPlacement,
    		payloadSchemaLocator, payloadSchema, submitter, submitterType, submissionTOS,
    		submissionAttribution, signer, replaces);
    }
    
    protected void initProperties(LRJSONDocument document, JSONObject resourceData, String resourceDataType, String resourceLocator, String curator, String owner, String[] tags,
            String payloadPlacement, String payloadSchemaLocator, String[] payloadSchema,
            String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer,
            String[] replaces)  throws LRException
    {
    	try 
    	{
	        document.resourceData = new ObjectMapper().readValue(resourceData.toString(), HashMap.class);
	        document.resourceDataType = StringUtil.nullifyBadInput(resourceDataType);
	        document.resourceLocator = StringUtil.nullifyBadInput(resourceLocator);
	        document.curator = StringUtil.nullifyBadInput(curator);
	        document.owner = StringUtil.nullifyBadInput(owner);
	        document.tags = StringUtil.removeDuplicates(tags);
	        document.payloadPlacement = StringUtil.nullifyBadInput(payloadPlacement);
	        document.payloadSchemaLocator = StringUtil.nullifyBadInput(payloadSchemaLocator);
	        document.payloadSchema = StringUtil.nullifyBadInput(payloadSchema);
	        document.submissionTOS = StringUtil.nullifyBadInput(submissionTOS);
	        document.submissionAttribution = StringUtil.nullifyBadInput(submissionAttribution);
	        document.submitterType = StringUtil.nullifyBadInput(submitterType);
	        document.submitter = StringUtil.nullifyBadInput(submitter);
	        document.signer = StringUtil.nullifyBadInput(signer);
	        document.replaces = StringUtil.removeDuplicates(replaces);
    	}
        catch (IOException e)
        {
            throw new LRException(LRException.INVALID_JSON);
        }
    }
    
    public Object getResourceData()
    {
        return resourceData;
    }
}