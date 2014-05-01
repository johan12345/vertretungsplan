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

<p>Die Informationen für deine Schule wurden erstellt. Du kannst sie jetzt abschicken.</p>
<p>Wenn ich die Daten erhalten habe, werde ich sie testen und schnellstmöglich in die App einbauen. Bitte beachte, dass ich als Student und freiwilliger Programmierer dieser App häufig wenig Zeit dafür habe und es dementsprechend manchmal etwas länger dauern kann, bis deine Schule hinzugefügt wird.</p>


<form id="form1" name="form1" method="post" action="sendmail">
	<input type="hidden" name="json" id="json" value='<%= info.toString()%>' />
	<label for="email">Dein Name (für Rückfragen): </label>
	<input type="text" name="name" id="name"><br/>
    <label for="email">Deine E-Mail-Adresse (für Rückfragen): </label>
	<input type="text" name="email" id="email"><br/>
<input type="submit" name="send" id="send" value="Senden" />
</form>
</body>
</html>