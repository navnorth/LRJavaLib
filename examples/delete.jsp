<%@ page import="java.util.*,com.navnorth.learningregistry.*" %>

<%
// default node and signing values
String nodeDomain = (request.getParameter("nodeDomain") != null) ? request.getParameter("nodeDomain") : "lrtest02.learningregistry.org";
String publicKeyLocation = (request.getParameter("publicKeyLocation") != null) ? request.getParameter("publicKeyLocation") : "http://keyserver.pgp.com/vkd/DownloadKey.event?keyid=0x8E155268359114B4";
String privateKey = (request.getParameter("privateKey") != null) ? request.getParameter("privateKey") : "";
String passPhrase = (request.getParameter("passPhrase") != null) ? request.getParameter("passPhrase") : "";

String publishUsername = (request.getParameter("publishUsername") != null) ? request.getParameter("publishUsername") : "";
String publishPassword = (request.getParameter("publishPassword") != null) ? request.getParameter("publishPassword") : "";
Boolean ssl = (request.getParameter("ssl") != null) ? true : false;

// Docs to Replace
String[] replaces = (request.getParameter("replaces") != null) ? request.getParameter("replaces").split(",", -1) : new String[] {""};
String replacesString = "";
for (int i = 0; i < replaces.length; i++)
{
    replacesString += replaces[i];
    if (i < replaces.length - 1)
    {
        replacesString += ",";
    }
}

// Identity data
String signer = (request.getParameter("signer") != null) ? request.getParameter("signer") : "";
String submitter = (request.getParameter("submitter") != null) ? request.getParameter("submitter") : "Navigation North";
String submitterType = (request.getParameter("submitterType") != null) ? request.getParameter("submitterType") : "agent";
String submissionTOS = (request.getParameter("submissionTOS") != null) ? request.getParameter("submissionTOS") : "http://creativecommons.org/licenses/by/3.0/";
String submissionAttribution = (request.getParameter("submissionAttribution") != null) ? request.getParameter("submissionAttribution") : "Copyright 2011 Navigation North: CC-BY-3.0";


// if form submitted.
if (request.getParameter("deleteNow") != null && request.getParameter("deleteNow").length() > 0)
{
    // List of exceptions for display
    List<LRException> exceptions = new ArrayList<LRException>();

    // Setup signer
    LRSigner signerLR = new LRSigner(publicKeyLocation, privateKey, passPhrase);

    // Setup exporter
    int batchSize = 1;
    LRExporter exporterLR = new LRExporter(batchSize, nodeDomain, publishUsername, publishPassword, ssl);

    // Configure exporter
    try {
        exporterLR.configure();
    }
    catch (LRException e) {
        exceptions.add(e);
    }

    // Build resource envelope, with just the "replaces" ID(s)

    LRDelete deleteDoc = null;

    try {
        deleteDoc = new LRDelete(submitter, submitterType, submissionTOS, submissionAttribution, signer, replaces);
    }
    catch (LRException e) {
        exceptions.add(e);
    }

    // sign the doc
    if (privateKey.length() > 0 && passPhrase.length() > 0 && publicKeyLocation.length() > 0)
    {
        try {
            deleteDoc = signerLR.sign(deleteDoc);
        }
        catch (LRException e) {
            exceptions.add(e);
        }
    }

    //out.print("<pre>"+resourceData+"</pre>");

    out.print("<div style=\"background-color:#98afc7;margin:10px;padding:10px\"><h1>Delete Results</h2>");

    if (exceptions.size() > 0)
    {
        // Output exceptions
        out.print("<h2>Exceptions</h2>");
        for(LRException e : exceptions)
        {
            out.print(e.getMessage());
        }
        out.print("<h2>No Delete Docs were Published</h2>");
    }
    else
    {
        // Add envelope to exporter
        exporterLR.addDocument(deleteDoc);

        // Send data and get responses
        List<LRResponse> responses = null;
        try {
            responses = exporterLR.sendData();
        }
        catch(LRException e) {
            exceptions.add(e);
        }

        // Parse responses
        if (responses != null)
        {
            for (LRResponse res : responses)
            {
                out.print("<h2>Batch Results</h2>");
                out.print("Status Code: " + res.getStatusCode() + "<br/>");
                out.print("Status Reason: " + res.getStatusReason() + "<br/>");
                out.print("Batch Success: " + res.getBatchSuccess() + "<br/>");
                out.print("Batch Response: " + res.getBatchResponse() + "<br/><br/>");

                out.print("<h3>Published Delete Tombstone(s)</h3>");
                for(String id : res.getResourceSuccess())
                {
                    out.print("Id: <a href=\"http://" + nodeDomain + "/obtain?by_doc_ID=T&request_ID=" + id + "\" target=_\"blank\">" + id + "</a><br/>");
                }

                if (!res.getResourceFailure().isEmpty())
                {
                    out.print("<br/>");
                    out.print("<h3>Publish Errors</h3>");

                    for(String message : res.getResourceFailure())
                    {
                        out.print("Error: " + message);
                        out.print("<br/>");
                    }
                }
            }
        }
    }
    out.print("</div><hr />");
}

%>



<form method="post" action="delete.jsp">
    <b>Node domain:</b> <input type="text" name="nodeDomain" value="<%= nodeDomain %>" size="60" /><br />
    <hr />

    <b>Doc IDs to Replace (comma-delimited):</b><br />
    <textarea name="replaces" rows="5" cols="60"><%= replacesString %></textarea><br />

    <hr />
    <b>Signer:</b> <input type="text" name="signer" value="<%= signer %>" size="60" /><br />
    <b>Submitter:</b> <input type="text" name="submitter" value="<%= submitter %>" size="60" /><br />
    <b>Submitter Type:</b> <input type="text" name="submitterType" value="<%= submitterType %>" size="60" /><br />
    <b>Submission TOS:</b> <input type="text" name="submissionTOS" value="<%= submissionTOS %>" size="60" /><br />
    <b>Submission Attribution:</b> <input type="text" name="submissionAttribution" value="<%= submissionAttribution %>" size="60" /><br />

    <hr />
    <b>Publish Username:</b> <input type="text" name="publishUsername" value="<%= publishUsername %>" size="60" /><br />
    <b>Publish Password:</b> <input type="text" name="publishPassword" value="<%= publishPassword %>" size="60" /><br />
    <b>SSL:</b> <input type="checkbox" name="ssl" <% if (ssl) out.print("checked=''true''"); %> /><br />

    <hr />
    <b>Public Key Location:</b> <input type="text" name="publicKeyLocation" value="<%= publicKeyLocation %>" size="60" /><br />
    <b>Pass Phrase:</b> <input type="text" name="passPhrase" value="<%= passPhrase %>" size="60" /><br />
    <b>Private Key:</b> <br />
    <textarea name="privateKey" rows="5" cols="60"><%= privateKey %></textarea><br />

    <input type="submit" name="deleteNow" value="Publish Delete Doc to Node" />
</form>


