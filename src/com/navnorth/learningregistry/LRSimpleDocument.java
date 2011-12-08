package com.navnorth.learningregistry;

public class LRSimpleDocument extends LREnvelope
{
	public LRSimpleDocument(String resourceData, String resourceDataType, String resourceLocator, String curator, String owner, String[] tags,
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