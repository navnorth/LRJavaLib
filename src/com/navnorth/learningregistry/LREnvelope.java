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

import com.navnorth.learningregistry.util.MapUtil;

import java.util.Map;
import java.util.Map;
import java.util.HashMap;

/**
 * Envelope for data to export to a learning registry node
 *
 * @version 0.1.1
 * @since 2011-12-06
 * @author Todd Brown / Navigation North
 *      <br>
 *      Copyright © 2011 Navigation North Learning Solutions LLC
 *      <br>
 *      Licensed under the Apache License, Version 2.0 (the "License"); See LICENSE
 *      and README.md files distributed with this work for additional information
 *      regarding copyright ownership.
 */
public abstract class LREnvelope
{
    private static final String docVersion = "0.23.0";
    private static final String docType = "resource_data";
    private static final String active = "true";
    
    private static final String docTypeField = "doc_type";
    private static final String docVersionField = "doc_version";
    private static final String activeField = "active";
    private static final String resourceDataTypeField = "resource_data_type";
    private static final String submitterTypeField = "submitter_type";
    private static final String submitterField = "submitter";
    private static final String curatorField = "curator";
    private static final String ownerField = "owner";
    private static final String signerField = "signer";
    private static final String identityField = "identity";
    private static final String submissionTOSField = "submission_TOS";
    private static final String submissionAttributionField = "submission_attribution";
    private static final String TOSField = "TOS";
    private static final String resourceLocatorField = "resource_locator";
    private static final String payloadPlacementField = "payload_placement";
    private static final String payloadSchemaField = "payload_schema";
    private static final String payloadSchemaLocatorField = "payload_schema_locator";
    private static final String keysField = "keys";
    private static final String resourceDataField = "resource_data";
    
    private static final String keyLocationField = "key_location";
    private static final String signingMethodField = "signing_method";
    private static final String signatureField = "signature";
    private static final String digitalSignatureField = "digital_signature";
    
    protected String resourceLocator;
    protected String resourceDataType;
    protected Object resourceData;
    
    protected String payloadPlacement;
    protected String payloadSchemaLocator;
    protected String[] payloadSchema;
    
    protected String curator;
    protected String owner;
    protected String[] tags;
    
    protected String submissionTOS;
    protected String submissionAttribution;
    protected String submitterType;
    protected String submitter;
    protected String signer;
    
    protected Boolean signed = false;
    
    protected String publicKeyLocation;
    protected String signingMethod;
    protected String clearSignedMessage;

    protected abstract Object getResourceData();
    
    /**
     * Builds and returns a map of the envelope data, suitable for signing with the included signer
     *
     * @return map of envelope data, suitable for signing
     */
    protected Map<String, Object> getSignableData()
    {
        Map<String, Object> doc = new HashMap<String, Object>();
        
        MapUtil.put(doc, docTypeField, docType);
        MapUtil.put(doc, docVersionField, docVersion);
        MapUtil.put(doc, activeField, active);
        MapUtil.put(doc, resourceDataTypeField, resourceDataType);
        Map<String, Object> docId = new HashMap<String, Object>();
        MapUtil.put(docId, submitterTypeField, submitterType);
        MapUtil.put(docId, submitterField, submitter);
        MapUtil.put(docId, curatorField, curator);
        MapUtil.put(docId, ownerField, owner);
        MapUtil.put(docId, signerField, signer);
        MapUtil.put(doc, identityField, docId);
        Map<String, Object> docTOS = new HashMap<String, Object>();
        MapUtil.put(docTOS, submissionTOSField, submissionTOS);
        MapUtil.put(docTOS, submissionAttributionField, submissionAttribution);
        MapUtil.put(doc, TOSField, docTOS);
        MapUtil.put(doc, resourceLocatorField, resourceLocator);
        MapUtil.put(doc, payloadPlacementField, payloadPlacement);
        MapUtil.put(doc, payloadSchemaField, payloadSchema);
        MapUtil.put(doc, payloadSchemaLocatorField, payloadSchemaLocator);
        MapUtil.put(doc, keysField, tags);
        MapUtil.put(doc, resourceDataField, getResourceData());
        
        return doc;
    }
    
    /**
     * Builds and returns a map of the envelope data including any signing data, suitable for sending to a Learning Registry node
     *
     * @return map of the envelope data, including signing data
     */
    protected Map<String, Object> getSendableData()
    {
        Map<String, Object> doc = new HashMap<String, Object>();
    
        MapUtil.put(doc, docTypeField, docType);
        MapUtil.put(doc, docVersionField, docVersion);
        MapUtil.put(doc, activeField, true);
        MapUtil.put(doc, resourceDataTypeField, resourceDataType);
        Map<String, Object> docId = new HashMap<String, Object>();
        MapUtil.put(docId, submitterTypeField, submitterType);
        MapUtil.put(docId, submitterField, submitter);
        MapUtil.put(docId, curatorField, curator);
        MapUtil.put(docId, ownerField, owner);
        MapUtil.put(docId, signerField, signer);
        MapUtil.put(doc, identityField, docId);
        Map<String, Object> docTOS = new HashMap<String, Object>();
        MapUtil.put(docTOS, submissionTOSField, submissionTOS);
        MapUtil.put(docTOS, submissionAttributionField, submissionAttribution);
        MapUtil.put(doc, TOSField, docTOS);
        MapUtil.put(doc, resourceLocatorField, resourceLocator);
        MapUtil.put(doc, payloadPlacementField, payloadPlacement);
        MapUtil.put(doc, payloadSchemaField, payloadSchema);
        MapUtil.put(doc, payloadSchemaLocatorField, payloadSchemaLocator);
        MapUtil.put(doc, keysField, tags);
        MapUtil.put(doc, resourceDataField, getResourceData());
        
        if (signed)
        {
            Map<String, Object> sig = new HashMap<String, Object>();
            String[] keys = {publicKeyLocation};
            MapUtil.put(sig, "key_location", keys);
            MapUtil.put(sig, "signing_method", signingMethod);
            MapUtil.put(sig, "signature", clearSignedMessage);
            MapUtil.put(doc, "digital_signature", sig);
        }
        
        return doc;
    }
    
    /**
     * Adds signing data to the envelope
     *
     * @param signingMethod method used for signing this envelope
     * @param publicKeyLocation location of the public key for the signature on this envelope
     * @param clearSignedMessage clear signed message created for signing this envelope
     */
    public void addSigningData(String signingMethod, String publicKeyLocation, String clearSignedMessage)
    {
        this.signingMethod = signingMethod;
        this.publicKeyLocation = publicKeyLocation;
        this.clearSignedMessage = clearSignedMessage;
        this.signed = true;
    }
}