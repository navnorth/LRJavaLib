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

/**
 * Base envelope class from which Learning Registry exportable types are derived
 *
 * @version 0.1
 * @since 2011-12-08
 * @author Todd Brown / Navigation North
 *      <br>
 *      Copyright © 2011 Navigation North Learning Solutions LLC
 *      <br>
 *      Licensed under the Apache License, Version 2.0 (the "License"); See LICENSE
 *      and README.md files distributed with this work for additional information
 *      regarding copyright ownership.
 */
public class LRSimpleDocument extends LREnvelope
{
    /**
     * Create a new simple documet with specified details
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
    public LRSimpleDocument(String resourceData, String resourceDataType, String resourceLocator, String curator, String owner, String[] tags,
        String payloadPlacement, String payloadSchemaLocator, String[] payloadSchema,
        String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer)
    {
        this.resourceData = LRUtilities.nullifyBadInput(LRUtilities.cleanResourceData(resourceData));
        this.resourceDataType = LRUtilities.nullifyBadInput(resourceDataType);
        this.resourceLocator = LRUtilities.nullifyBadInput(resourceLocator);
        this.curator = LRUtilities.nullifyBadInput(curator);
        this.owner = LRUtilities.nullifyBadInput(owner);
        this.tags = LRUtilities.nullifyBadInput(tags);
        this.payloadPlacement = LRUtilities.nullifyBadInput(payloadPlacement);
        this.payloadSchemaLocator = LRUtilities.nullifyBadInput(payloadSchemaLocator);
        this.payloadSchema = LRUtilities.nullifyBadInput(payloadSchema);
        this.submissionTOS = LRUtilities.nullifyBadInput(submissionTOS);
        this.submissionAttribution = LRUtilities.nullifyBadInput(submissionAttribution);
        this.submitterType = LRUtilities.nullifyBadInput(submitterType);
        this.submitter = LRUtilities.nullifyBadInput(submitter);
        this.signer = LRUtilities.nullifyBadInput(signer);
    }
    
    public Object getResourceData()
    {
        return resourceData;
    }
}