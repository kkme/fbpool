<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="com.talient.util.Properties" %>

<%
String name = request.getParameter("name");
if (name == null || name.length() == 0) {
    throw new Exception("Name was not entered");
}
session.setAttribute("username", name);
%>

<html>
<head>
<title><%= Properties.getProperty("football.pool.name") %> Login Created</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFCC" text="#006666">
<p align="center"><font face="Arial, Helvetica, sans-serif" size="5"><b>
<%= name %> Login Created</b></font></p>
<p align="center"><font face="Arial, Helvetica, sans-serif" size="5"><b>You may 
  now submit <%= Properties.getProperty("football.pool.name") %> entries.</b></font></p>
<p align="center"><font face="Arial, Helvetica, sans-serif" size="3"><b> 
  Visit the <a href="<%= response.encodeURL("MemberServices.jsp") %>" >Member Services</a> page to update
  your profile.</b></font></p>
<p align="center"><font size="4" face="Arial, Helvetica, sans-serif"><a href="/"><%= Properties.getProperty("football.pool.name") %>
  Home Page</a></font></p>
<p>&nbsp; </p>
</body>
</html>
