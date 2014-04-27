<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.johan.vertretungsplan.backend.VertretungsplanServlet" %>
<%@ page import="com.johan.vertretungsplan.objects.Vertretungsplan" %>
<%@ page import="com.johan.vertretungsplan.objects.VertretungsplanTag" %>
<%@ page import="com.johan.vertretungsplan.objects.Vertretung" %>
<%@ page import="com.johan.vertretungsplan.objects.KlassenVertretungsplan" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.util.List" %>
<%@ page import="org.json.JSONObject" %>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Vertretungsplan</title>
</head>
<body>
<% JSONObject info = VertretungsplanServlet.createMonitorPlan(request.getParameter("url")); %>
<p>Bitte prüfe die Informationen unten und ergänze fehlende oder falsche Angaben: </p>
<form id="form1" name="form1" method="get" action="step3.jsp">
  <label for="name">Schulname</label>
  <input type="text" name="name" id="name" value="<%= info.getString("name") %>"><br/>
  <label for="name">Ort</label>
  <input type="text" name="name" id="name" value="<%= info.getString("city") %>"><br/>
  <input type="submit" name="send" id="send" value="Weiter" />
</form>
</body>
</html>