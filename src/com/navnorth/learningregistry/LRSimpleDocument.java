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

public class LRSimpleDocument extends LREnvelope
{
    private String resourceData;

    public LRSimpleDocument(String resourceData, String resourceLocator, String curator, String owner, String[] tags,
        String payloadPlacement, String payloadPlacementLocator, String[] payloadSchema,
        String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer)
    {
        this.resourceData = LRUtilities.nullifyBadInput(resourceData);
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
}