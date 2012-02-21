<%@ page import="org.json.*,java.util.*,com.navnorth.learningregistry.*" %>

<%

// Setup importer
String node = "sandbox.learningregistry.org";
LRImporter importerLR = new LRImporter(node, false);

// Make harvest query based on a given resource url
String id = "http://www.google.com";
LRResult result = importerLR.getHarvestJSONData(id, true, false);

// Get data from result
// This is raw JSON data from the Learning Registry node
JSONObject json = result.getData();

try
{
    out.print(json.toString());
}
    catch(Exception e)
{
    return;
}

%>
