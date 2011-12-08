package com.navnorth.learningregistry;

import java.util.Map;
import java.util.HashMap;

/**
 * Envelope for data to export to a learning registry node
 *
 * @author Navigation North
 * @version 1.0
 * @since 2011-12-06
*/
public abstract class LREnvelope
{
	private static final String docVersion = "0.23.0";
	private static final String docType = "resource_data";

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

	public Map<String, Object> getSignableData()
	{
		Map<String, Object> doc = new HashMap<String, Object>();
		LRUtilities.put(doc, "doc_type", docType);
		LRUtilities.put(doc, "doc_version", docVersion);
		LRUtilities.put(doc, "active", "true");
		LRUtilities.put(doc, "resource_data_type", resourceDataType);
		Map<String, Object> docId = new HashMap<String, Object>();
		LRUtilities.put(docId, "submitter_type", submitterType);
		LRUtilities.put(docId, "submitter", submitter);
		LRUtilities.put(docId, "curator", curator);
		LRUtilities.put(docId, "owner", owner);
		LRUtilities.put(docId, "signer", signer);
		LRUtilities.put(doc, "identity", docId);
		Map<String, Object> docTOS = new HashMap<String, Object>();
		LRUtilities.put(docTOS, "submission_TOS", submissionTOS);
		LRUtilities.put(docTOS, "submission_attribution", submissionAttribution);
		LRUtilities.put(doc, "TOS", docTOS);
		LRUtilities.put(doc, "resource_locator", resourceLocator);
		LRUtilities.put(doc, "payload_placement", payloadPlacement);
		LRUtilities.put(doc, "payload_schema", payloadSchema);
		LRUtilities.put(doc, "payload_schema_locator", payloadSchemaLocator);
		LRUtilities.put(doc, "keys", tags);
		LRUtilities.put(doc, "resource_data", resourceData);
		
		return doc;
	}
	
	public Map<String, Object> getSendableData()
	{
		Map<String, Object> doc = new HashMap<String, Object>();
		LRUtilities.put(doc, "doc_type", docType);
		LRUtilities.put(doc, "doc_version", docVersion);
		LRUtilities.put(doc, "active", true);
		LRUtilities.put(doc, "resource_data_type", resourceDataType);
		Map<String, Object> docId = new HashMap<String, Object>();
		LRUtilities.put(docId, "submitter_type", submitterType);
		LRUtilities.put(docId, "submitter", submitter);
		LRUtilities.put(docId, "curator", curator);
		LRUtilities.put(docId, "owner", owner);
		LRUtilities.put(docId, "signer", signer);
		LRUtilities.put(doc, "identity", docId);
		Map<String, Object> docTOS = new HashMap<String, Object>();
		LRUtilities.put(docTOS, "submission_TOS", submissionTOS);
		LRUtilities.put(docTOS, "submission_attribution", submissionAttribution);
		LRUtilities.put(doc, "TOS", docTOS);
		LRUtilities.put(doc, "resource_locator", resourceLocator);
		LRUtilities.put(doc, "payload_placement", payloadPlacement);
		LRUtilities.put(doc, "payload_schema", payloadSchema);
		LRUtilities.put(doc, "payload_schema_locator", payloadSchemaLocator);
		LRUtilities.put(doc, "keys", tags);
		LRUtilities.put(doc, "resource_data", resourceData);
		
		if (signed)
		{
			Map<String, Object> sig = new HashMap<String, Object>();
			String[] keys = {publicKeyLocation};
			LRUtilities.put(sig, "key_location", keys);
			LRUtilities.put(sig, "signing_method", signingMethod);
			LRUtilities.put(sig, "signature", clearSignedMessage);
			LRUtilities.put(doc, "digital_signature", sig);
		}
		
		return doc;
	}
	
	public void addSigningData(String signingMethod, String publicKeyLocation, String clearSignedMessage)
	{
		this.signingMethod = signingMethod;
		this.publicKeyLocation = publicKeyLocation;
		this.clearSignedMessage = clearSignedMessage;
		this.signed = true;
	}
}