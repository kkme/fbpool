<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>

<%
String name = request.getParameter("name");
if (name == null) { name = ""; };
String password = request.getParameter("password");
if (password == null) { password = ""; };
%>

<html>
<head>
<title><%= Properties.getProperty("football.pool.name") %> Create Login</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFCC" text="#006666">
<p align="center"><font face="Arial, Helvetica, sans-serif"><b><font size="5">
  <%= Properties.getProperty("football.pool.name") %> Create Login</font></b></font>
</p>
<form method="POST" action="CreateEntrant.jsp" name="SignIn">
  <table width="200" border="0" align="center" cellspacing="0">
    <tr> 
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
    </tr>
<% if (request.getParameter("entry") != null) { %>
    <tr align="center">
      <td colspan="2">
      <font face="Arial, Helvetica, sans-serif" size="4"><b>The Name <font color="#CC3333">&quot;<%= name %>&quot;</font> was not found.<br>
        <br>
        If this is your first entry in the football pool<br>
        click on the &quot;Create Login&quot; button.<br>
        <font color="#990000">Please, only create a new user if you are sure 
        you<br>
        don't already have a username.</font></b></font><br><br>
      <hr><br>
      <input type="hidden" name="name" value="<%= name %>" >
      <input type="hidden" name="year" value="<%= request.getParameter("year") %>" >
      <input type="hidden" name="week" value="<%= request.getParameter("week") %>" >
      <input type="hidden" name="entry" value="<%= request.getParameter("entry") %>" >
      <input type="hidden" name="emailentry" value="<%= request.getParameter("emailentry") %>" >
      </td>
    </tr>
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Name:</font></b> 
        </div>
      </td>
      <td nowrap> 
        <font face="Arial, Helvetica, sans-serif" size="3"><%= name %></font>
      </td>
    </tr>
<% }
   else {
%>
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Name:</font></b> 
        </div>
      </td>
      <td nowrap> 
        <input type="text" name="name">
      </td>
    </tr>
<% } %>
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Password:</font></b></div>
      </td>
      <td nowrap> 
        <input type="text" name="password" value="<%= password %>" >
      </td>
    </tr>
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Email:</font></b></div>
      </td>
      <td nowrap> 
        <input type="text" name="email" size=50> (optional)
      </td>
    </tr>
    <tr> 
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
    </tr>
    <tr> 
      <td nowrap colspan="2"> 
        <div align="center">
          <input type="submit" name="Submit" value="Create Login">
        </div>
      </td>
    </tr>
    <tr> 
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
    </tr>
<% if (request.getParameter("entry") != null) { %>
    <tr align="center">
      <td colspan=2>
        <hr>
        <p><font face="Arial, Helvetica, sans-serif" size="4"><b> The other 
          possibility is that <br>
          you have mistyped your name.<br>
          If so, go back and make the correction<br>
          and resubmit your entry.</b></font> </p>
      </td>
    </tr>
<% } %>
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
