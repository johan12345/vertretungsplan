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
<style>
th { background: #000; color: #fff; }
table.mon_list th, td { padding: 8px 4px;}

table.mon_list
{
	color: #000000; 
	width: 100%; 
	font-size: 100%;
	border: 1px;
	border-style:solid;
	border-collapse:collapse;
}

table.mon_head
{
	color: #000000; 
	width: 100%; 
	font-size: 100%;
}

td.info,
th.list,
td.list,
tr.list
{
	border: 1px;
	border-style: solid;
	border-color: black;
	margin: 0px;
	border-collapse:collapse;
	padding: 3px;
}

tr.odd { background: #fad3a6; }
tr.even { background: #fdecd9; }
</style>
</head>
<body>
<%  JSONObject info = new JSONObject(request.getParameter("json"));
	info.put("name", request.getParameter("name"));
	info.put("city", request.getParameter("city")); %>
    
    <p><%= info.toString(2) %></p>
<p>Bitte prüfe die Informationen unten und ergänze fehlende oder falsche Angaben: </p>


<form id="form1" name="form1" method="get" action="step3.jsp">
	<%= VertretungsplanServlet.getTableHeaders(info) %>
  <input type="submit" name="send" id="send" value="Weiter" />
</form>
</body>
</html>