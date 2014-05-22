<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>

<%
String name = request.getParameter("name");
String target = request.getParameter("target");
if (target == null) {
    target = "/";
}
Cookie[] cookies = request.getCookies();
if (cookies != null && cookies.length > 0) {
    if (name == null) {
        for (int i=0; i<cookies.length; i++) {
            if (cookies[i].getName().equals("username")) {
                name = cookies[i].getValue();
                break;
            }
        }
    }
}
if (name == null) { name = ""; }
if (name.length() > 1 && name.startsWith("\"") && name.endsWith("\"")) {
    name = name.substring(1, name.length() - 1);
}
%>

<html>
<head>
<title><%= Properties.getProperty("football.pool.name") %> Sign-In</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFCC" text="#006666">
<p align="center"><font face="Arial, Helvetica, sans-serif"><b><font size="5">
  <%= Properties.getProperty("football.pool.name") %> Sign-In</font></b></font>
</p>
<form method="POST" action="VerifyEntrant.jsp" name="SignIn">
  <table width="200" border="0" align="center" cellspacing="0">
    <tr> 
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
    </tr>
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Name:</font></b> 
        </div>
      </td>
      <td nowrap> 
        <input type="text" name="name" value="<%= name %>">
      </td>
    </tr>
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Password:</font></b></div>
      </td>
      <td nowrap> 
        <input type="password" name="password">
      </td>
    </tr>
    <tr> 
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
    </tr>
    <tr> 
      <td nowrap colspan="2"> 
        <div align="center">
          <input type="submit" name="Submit" value="Sign-In">
          <input type="hidden" name="target" value="<%= target %>">
        </div>
      </td>
    </tr>
  </table>
</form>
<table width="80%" border="0" align="center" cellspacing="4">
  <tr> 
    <td colspan="2"> 
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+1"><a href="javascript: history.go(-1)">Go 
        Back</a></font></div>
    </td>
  </tr>
  <tr> 
    <td colspan="2"> 
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+1"><a href="/"><%= Properties.getProperty("football.pool.name") %> 
        Home Page</a></font></div>
    </td>
  </tr>
</table>
<p>&nbsp;</p>
</body>
</html>
