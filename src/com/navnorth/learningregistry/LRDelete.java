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

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;


/**
 * Utility class for building a delete document for the Learning Registry Exporter
 *
 * @version 0.1.0
 * @since 2014-04-10
 * @author joe hobson / Navigation North
 *      <br>
 *      Copyright © 2014 Navigation North Learning Solutions LLC
 *      <br>
 *      Licensed under the Apache License, Version 2.0 (the "License"); See LICENSE
 *      and README.md files distributed with this work for additional information
 *      regarding copyright ownership.
 */
public class LRDelete extends LREnvelope
{
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static final String payloadPlacementValue = "none";

    String[] replaces;

    /**
     * Basic constructor to create a new delete doc, with no doc IDs specified
     *
     * @param resourceLocator value for "resource_locator"
     * @param submitter value for "submitter"
     * @param submitterType value for "submitter_type"
     * @param submissionTOS value for "submission_TOS"
     * @param submissionAttribution value for "submission_attribution"
     * @param signer value for "signer"
     */
    public LRDelete(String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer)
    {
        this.resourceLocator = StringUtil.nullifyBadInput(resourceLocator);
        this.payloadPlacement = payloadPlacementValue;
        this.submissionTOS = StringUtil.nullifyBadInput(submissionTOS);
        this.submissionAttribution = StringUtil.nullifyBadInput(submissionAttribution);
        this.submitterType = StringUtil.nullifyBadInput(submitterType);
        this.submitter = StringUtil.nullifyBadInput(submitter);
        this.signer = StringUtil.nullifyBadInput(signer);
        this.replaces = StringUtil.nullifyBadInput(replaces);
    }

    /**
     * Create a new delete doc with a single doc ID to delete
     *
     * @param resourceLocator value for "resource_locator"
     * @param submitter value for "submitter"
     * @param submitterType value for "submitter_type"
     * @param submissionTOS value for "submission_TOS"
     * @param submissionAttribution value for "submission_attribution"
     * @param signer value for "signer"

    public LRDelete(String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer, String replaces)
    {
        this.resourceLocator = StringUtil.nullifyBadInput(resourceLocator);
        this.curator = null;
        this.owner = null;
        this.payloadPlacement = payloadPlacementValue;
        this.payloadSchemaLocator = null;
        this.submissionTOS = StringUtil.nullifyBadInput(submissionTOS);
        this.submissionAttribution = StringUtil.nullifyBadInput(submissionAttribution);
        this.submitterType = StringUtil.nullifyBadInput(submitterType);
        this.submitter = StringUtil.nullifyBadInput(submitter);
        this.signer = StringUtil.nullifyBadInput(signer);
        this.replaces = StringUtil.nullifyBadInput(new String[] replaces);
    }
    */

    /**
     * Create a new delete doc with an array of doc IDs to delete
     *
     * @param resourceLocator value for "resource_locator"
     * @param submitter value for "submitter"
     * @param submitterType value for "submitter_type"
     * @param submissionTOS value for "submission_TOS"
     * @param submissionAttribution value for "submission_attribution"
     * @param signer value for "signer"
     */
    public LRDelete(String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer, String[] replaces)
    {
        this.resourceLocator = StringUtil.nullifyBadInput(resourceLocator);
        this.curator = null;
        this.owner = null;
        this.payloadPlacement = payloadPlacementValue;
        this.payloadSchemaLocator = null;
        this.submissionTOS = StringUtil.nullifyBadInput(submissionTOS);
        this.submissionAttribution = StringUtil.nullifyBadInput(submissionAttribution);
        this.submitterType = StringUtil.nullifyBadInput(submitterType);
        this.submitter = StringUtil.nullifyBadInput(submitter);
        this.signer = StringUtil.nullifyBadInput(signer);
        this.replaces = StringUtil.nullifyBadInput(replaces);
    }

    public Object getResourceData()
    {
        return new HashMap<String, Object>();
    }

}
