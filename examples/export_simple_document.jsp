<%@ page import="java.util.*,com.navnorth.learningregistry.*" %>

<%
// Setup exporter
int batchSize = 100;
String nodeHost = "sandbox.learningregistry.org";
LRExporter exporterLR = new LRExporter(batchSize, nodeHost);

// Configure exporter
try
{
exporterLR.configure();
}
catch (LRException e)
{
return;
}

// Setup signer
String publicKeyLocation = "publicly accessible url of public key";
String privateKey = "location on disk of private key";
String passPhrase = "pass phrase";
LRSigner signerLR = new LRSigner(publicKeyLocation, privateKey, passPhrase);

// Setup envelope parameters
// These values will usually remain the same for all envelopes of the same schema and type
String[] payloadSchema = new String[1];
payloadSchema[0] = "NSDL DC 1.02.020";
String resourceDataType = "metadata";
String payloadSchemaURL = "url of schema description";
String payloadPlacement = "inline";

// Setup data parameters
// These values depend on the data being drawn on
String rData = "resource data";
String rUrl = "url of resource";
String curator = "Curator";
String provider = "Provider";
String keys[] = new String[2];
keys[0] = "example key one";
keys[1] = "example key two";
String submitter = "Submitter";
String submitterType = "agent";
String submissionTOS = "Submitter TOS";
String submissionAttribution = "Submitter Attribution";
String signer = "Signer";

// Build resource envelope
// In a production environment, you would likely put many envelopes into the exporter before sending the data
LREnvelope doc = new LRSimpleDocument(rData, resourceDataType, rUrl, curator, provider, keys, payloadPlacement, payloadSchemaURL, payloadSchema, submitter, submitterType, submissionTOS, submissionAttribution, signer);

// Sign envelope
try
{
doc = signerLR.sign(doc);
}
catch(LRException E)
{
return;
}

// Add envelope to exporter
exporterLR.addDocument(doc);

// Send data and get responses
List<LRResponse> responses;
try
{
responses = exporterLR.sendData();
}
catch(LRException e)
{
return;
}

// Parse responses
out.print("<h1>Export Results</h1>");
for (LRResponse res : responses)
{
out.print("<h2>Batch Results</h2>");
out.print("Status Code: " + res.getStatusCode());
out.print("<br/>");
out.print("Status Reason: " + res.getStatusReason());
out.print("<br/>");
out.print("Batch Success: " + res.getBatchSuccess());
out.print("<br/>");
out.print("Batch Response: " + res.getBatchResponse());
out.print("<br/>");
out.print("<br/>");
out.print("<h3>Exported Resources</h3>");

for(String id : res.getResourceSuccess())
{
out.print("Id: " + id);
out.print("<br/>");
}

out.print("<br/>");
out.print("<h3>Export Errors</h3>");

for(String message : res.getResourceFailure())
{
out.print("Error: " + message);
out.print("<br/>");
}
}
%>
