<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="com.talient.util.Properties" %>

<%
String name = request.getParameter("name");
if (name == null || name.length() == 0) {
    throw new Exception("Name was not entered");
}
%>

<html>
<head>
<title>Invitation Only</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFCC" text="#006666">
<p align="center"><font face="Arial, Helvetica, sans-serif" size="5"><b>
The name &quot;<%= name %>&quot; was not found.</b></font></p>
<p align="center"><font face="Arial, Helvetica, sans-serif" size="5"><b><font size="4">If you think you have mistyped your name,<br>
go back and make the correction and resubmit your entry.<br><br>
Participation in the <%= Properties.getProperty("football.pool.name") %> is by invitation only.<br>
  <br>
  This pool is not open to the general public.<br>
  If you weren't directed here by someone in the pool<br>
  we cannot accept your entries. Sorry :-(</font></b></font></p>
<p align="center"><font size="4" face="Arial, Helvetica, sans-serif"><a href="javascript: history.go(-1)">Go 
  Back</a></font></p>
<p>&nbsp; </p>
</body>
</html>
