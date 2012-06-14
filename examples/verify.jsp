<%@ page import="java.util.*,com.navnorth.learningregistry.*" %>

<%
String signature = (request.getParameter("signature") != null) ? request.getParameter("signature") : ""; 
String publicKey = (request.getParameter("publicKey") != null) ? request.getParameter("publicKey") : ""; 
String message = (request.getParameter("message") != null) ? request.getParameter("message") : "";

if (request.getParameter("verify") != null && request.getParameter("verify").length() > 0) 
{
	boolean result = LRVerify.VerifyStrings(signature, message, publicKey);
	out.print("Verified: " + result);
}

%>



<form method="post" action="verify.jsp">

	<b>Public Key:</b> <br />
    <textarea name="publicKey" rows="15" cols="100"><%= publicKey %></textarea><br />

	<b>Signature:</b> <br />
    <textarea name="signature" rows="15" cols="100"><%= signature %></textarea><br />

	<b>Message:</b> <br />
    <textarea name="message" rows="15" cols="100"><%= message %></textarea><br />
	
    <input type="submit" name="verify" value="Verify" />
</form>


