<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.entities.*" %>
<%@ page import="com.talient.football.jdbc.JDBCEntrant" %>
<%@ page import="com.talient.football.email.SendMail" %>

<%
String realname = request.getParameter("realname");
if (realname == null || realname.length() == 0) {
    throw new Exception("Name was not entered");
}
String email = request.getParameter("email");
if (email == null) {
    throw new Exception("Invalid email value");
}
try {
    if (email.indexOf("@") > 0) {
        SendMail sm = new SendMail(request.getParameter("subject"), request.getParameter("comments"));
        sm.send(email);
    }
}
catch (Exception e) {
    // Do not generate an error page if email is not working.
}

%>

<html>
<head>
<title><%= Properties.getProperty("football.pool.name") %> Message Sent</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>


<body bgcolor="#FFFFCC" text="#006666">
<p align="center"><font face="Arial, Helvetica, sans-serif"><b><font size="5">
Message sent from <%= realname %> <br>
  <br><font size="3">
<%
if (email.indexOf("@") > 0) {
    out.print("Your comments have been sent.");
}
%></font></font></b></font></p>

<table width="80%" border="0" align="center" cellspacing="4">
  <tr> 
    <td colspan="2"> 
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+1"><a href="javascript: history.go(-1)">Go 
        Back</a></font></div>
    </td>
  </tr>
  <tr> 
    <td colspan="2"> 
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+1"><a href="/">
      <%= Properties.getProperty("football.pool.name") %> 
        Home Page</a></font></div>
    </td>
  </tr>
</table>
<p>&nbsp;</p>
</body>
</html>
