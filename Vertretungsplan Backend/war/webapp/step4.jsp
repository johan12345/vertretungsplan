<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.johan.vertretungsplan.backend.VertretungsplanServlet" %>
<%@ page import="com.johan.vertretungsplan.objects.Vertretungsplan" %>
<%@ page import="com.johan.vertretungsplan.objects.VertretungsplanTag" %>
<%@ page import="com.johan.vertretungsplan.objects.Vertretung" %>
<%@ page import="com.johan.vertretungsplan.objects.KlassenVertretungsplan" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.util.List" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONArray" %>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Vertretungsplan</title>
<style>

</style>
</head>
<body>

<%  JSONObject info = new JSONObject(request.getParameter("json"));
	JSONObject data = info.getJSONObject("data");
	data.put("classes", new JSONArray("[" + request.getParameter("classes") + "]"));
	JSONArray columns = new JSONArray();
	int i = 0;
	while(request.getParameter("column_" + i) != null) {
		columns.put(request.getParameter("column_" + i));
		i++;
	}
	data.put("columns", columns); %>
    
    <%= info.toString(2) %>
<!-- <p>Bitte prüfe die Informationen unten und ergänze fehlende oder falsche Angaben: </p>


<form id="form1" name="form1" method="post" action="step4.jsp">
	<input type="hidden" name="json" id="json" value='' />
	
  <label for="classes">Liste ALLER Klassen (durch Kommas getrennt, bitte fehlende ergänzen!)</label><br/>
    <textarea name="classes" id="classes" cols="45" rows="10"></textarea><br/>
  <input type="submit" name="send" id="send" value="Weiter" />
</form> -->
</body>
</html>