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

/**
 * Response from the send request in the Learning Registery Exporter
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
public class LRResponse
{
    private int statusCode;
    private String statusReason;
    private String batchResponse;
    private boolean batchSuccess;
    private List<String> resourceSuccess = new ArrayList<String>();
    private List<String> resourceFailure = new ArrayList<String>();
    
    /**
     * Create a response
     *
     * @param statusCode The code returned by the Learning Registry node
     * @param statusReason The reason for an error returned by the Learning Registry node
     */
    public LRResponse(int statusCode, String statusReason)
    {
        this.statusCode = statusCode;
        this.statusReason = statusReason;
        this.batchResponse = "";
        this.batchSuccess = false;
    }
    
    /**
     * Add a success report to the response
     *
     * @param id Identifier of a resource successfully added to the Learning Registry node
     */
    public void addResourceSuccess(String id)
    {
        resourceSuccess.add(id);
    }
    
    /**
     * Add a failure report to the response
     *
     * @param error Error message of a resource that could not be added to the Learning Registry node
     */
    public void addResourceFailure(String error)
    {
        resourceFailure.add(error);
    }
    
    /**
     * Set the response to this batch of documents as a whole
     *
     * @param batchResponse Response message for this batch
     * @param batchSuccess True if this batch was added successfully
     */
    public void setBatchResponse(String batchResponse, boolean batchSuccess)
    {
        this.batchResponse = batchResponse;
        this.batchSuccess = batchSuccess;
    }
    
    /**
     * Get the batch response
     *
     * @return Batch response message
     */
    public String getBatchResponse()
    {
        return batchResponse;
    }
    
    /**
     * Get the batch success
     *
     * @return True if the batch was successfully added or else false
     */
    public boolean getBatchSuccess()
    {
        return batchSuccess;
    }
    
    /**
     * Get the status code returned from the node
     *
     * @return Status code from node
     */
    public int getStatusCode()
    {
        return statusCode;
    }
    
    /**
     * Get the message from the node
     *
     * @return Status message from node
     */
    public String getStatusReason()
    {
        return statusReason;
    }
    
    /**
     * Get a list of ids of all the successful resource adds in this batch
     *
     * @return List of ids of successfully added resources
     */
    public List<String> getResourceSuccess()
    {
        return resourceSuccess;
    }
    
    /**
     * Get a list of error messages of all the resources that were not successfully added in this batch
     *
     * @return List of error messages of unsuccessfully added resources
     */
    public List<String> getResourceFailure()
    {
        return resourceFailure;
    }
}