<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.johan.vertretungsplan.backend.VertretungsplanServlet" %>
<%@ page import="com.johan.vertretungsplan.objects.Vertretungsplan" %>
<%@ page import="com.johan.vertretungsplan.objects.VertretungsplanTag" %>
<%@ page import="com.johan.vertretungsplan.objects.Vertretung" %>
<%@ page import="com.johan.vertretungsplan.objects.KlassenVertretungsplan" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.util.List" %>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Vertretungsplan</title>
</head>
<body>
<form id="form1" name="form1" method="get" action="step2.jsp">
  <label for="url">Adresse des Vertretungsplans: </label>
  <input type="text" name="url" id="url" />
  <p>
    <label>
      <input type="radio" name="type" value="untis-monitor" id="type_0" />
      Untis Monitor</label>
    <br />
    <label>
      <input type="radio" name="type" value="other" id="type_1" />
      anderes</label>
  </p>
  <input type="submit" name="send" id="send" value="Weiter" />
</form>
</body>
</html>