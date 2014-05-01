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

table.mon_list .header-selectors {
	border:none;
}

/* Speech bubble */
table.mon_list .header-selectors th {
  position:relative;
  padding:15px;
  margin:1em 0 3em;
  color:#000;
  border-width:0;
  background:#338BFF; /* default background for browsers without gradient support */
  /* css3 */
  -webkit-border-radius:10px;
  -moz-border-radius:10px;
  border-radius:10px;
  bottom:16px;
}

/* creates triangle */
table.mon_list .header-selectors th:after {
  content:"";
  position:absolute;
  bottom:-15px; /* value = - border-top-width - border-bottom-width */
  left:50px; /* controls horizontal position */
  border-width:15px 15px 0; /* vary these values to change the angle of the vertex */
  border-style:solid;
  border-color:#338BFF transparent;
  /* reduce the damage in FF3.0 */
  display:block;
  width:0;
}
</style>
</head>
<body>

<%  JSONObject info = new JSONObject(request.getParameter("json"));
	info.put("name", request.getParameter("name"));
	info.put("city", request.getParameter("city")); %>
    
<p>Bitte prüfe die Informationen unten und ergänze fehlende oder falsche Angaben: </p>


<form id="form1" name="form1" method="post" action="step4.jsp">
	<input type="hidden" name="json" id="json" value='<%= info.toString() %>' />
	<%= VertretungsplanServlet.getTableHeaders(info) %>
  <label for="classes">Liste ALLER Klassen (durch Kommas getrennt, bitte fehlende ergänzen!)</label><br/>
    <textarea name="classes" id="classes" cols="45" rows="10"><%= VertretungsplanServlet.classesList(info) %></textarea><br/>
  <input type="submit" name="send" id="send" value="Weiter" />
</form>
</body>
</html>