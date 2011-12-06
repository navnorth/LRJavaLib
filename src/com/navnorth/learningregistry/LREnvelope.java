package com.navnorth.learningregistry;

/**
 * Envelope for data to export to a learning registry node
 *
 * @author Navigation North
 * @version 1.0
 * @since 2011-12-06
*/
public class LREnvelope
{
	private String resourceLocator;
	private String resourceDataType;
	private Object resourceData;
	
	private String payloadPlacement;
	private String payloadSchemaLocator;
	private String[] payloadSchema;
	
	private String curator;
	private String owner;
	private String[] tags;
	
	private String submissionTOS;
	private String submissionAttribution;
	private String submitterType;
	private String submitter;
	private String signer;
	
	public LREnvelope(Object resourceData, String resourceDataType, String resourceLocator, String curator, String owner, String[] tags,
		String payloadPlacement, String payloadSchemaLocator, String[] payloadSchema,
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
	
	public Object getResourceData()
	{
		return resourceData;
	}
	
	public void setResourceData(Object resourceData)
	{
		this.resourceData = LRUtilities.nullifyBadInput(resourceData);
	}
	
	public String getResourceLocator()
	{
		return resourceLocator;
	}
	
	public void setResourceLocator(String resourceLocator)
	{
		this.resourceLocator = LRUtilities.nullifyBadInput(resourceLocator);
	}
	
	public String getResourceDataType()
	{
		return resourceDataType;
	}
	
	public void setResourceDataType(String resourceLocator)
	{
		this.resourceDataType = LRUtilities.nullifyBadInput(resourceDataType);
	}
	
	public String getCurator()
	{
		return curator;
	}
	
	public void setCurator(String curator)
	{
		this.curator = LRUtilities.nullifyBadInput(curator);
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public void setOwner(String owner)
	{
		this.owner = LRUtilities.nullifyBadInput(owner);
	}
	
	public String[] getTags()
	{
		return tags;
	}
	
	public void setTags(String[] tags)
	{
		this.tags = LRUtilities.nullifyBadInput(tags);
	}

	public String getPayloadPlacement()
	{
		return payloadPlacement;
	}
	
	public void setPayloadPlacement(String payloadPlacement)
	{
		this.payloadPlacement = LRUtilities.nullifyBadInput(payloadPlacement);
	}
	
	public String getPayloadSchemaLocator()
	{
		return payloadSchemaLocator;
	}
	
	public void setPayloadSchemaLocator(String payloadSchemaLocator)
	{
		this.payloadSchemaLocator = LRUtilities.nullifyBadInput(payloadSchemaLocator);
	}

	public String[] getPayloadSchema()
	{
		return payloadSchema;
	}
	
	public void setPayloadSchema(String[] payloadSchema)
	{
		this.payloadSchema = LRUtilities.nullifyBadInput(payloadSchema);
	}
	
	public String getSubmissionTOS()
	{
		return submissionTOS;
	}
	
	public void setSubmissionTOS(String submissionTOS)
	{
		this.submissionTOS = LRUtilities.nullifyBadInput(submissionTOS);
	}
	
	public String getSubmissionAttribution()
	{
		return submissionAttribution;
	}
	
	public void setSubmissionAttribution(String submissionAttribution)
	{
		this.submissionAttribution = LRUtilities.nullifyBadInput(submissionAttribution);
	}
	
	public String getSubmitterType()
	{
		return submitterType;
	}
	
	public void setSubmitterType(String submitterType)
	{
		this.submitterType = LRUtilities.nullifyBadInput(submitterType);
	}
		
	public String getSubmitter()
	{
		return submitter;
	}
	
	public void setSubmitter(String submitter)
	{
		this.submitter = LRUtilities.nullifyBadInput(submitter);
	}
	
	public String getSigner()
	{
		return signer;
	}
	
	public void setSigner(String signer)
	{
		this.signer = LRUtilities.nullifyBadInput(signer);
	}
	
}