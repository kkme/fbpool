<%@ page isErrorPage="true" contentType="text/html" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="java.io.PrintWriter" %>

<%
    System.out.println(Properties.getProperty("football.pool.name") +
                       " Error.jsp: " + exception.getMessage());
%>
<html>
<head>
<title><%= Properties.getProperty("football.pool.name") %> Error Message</title>
</head>

<body bgcolor="#FFFFCC" text="#006666">
<p align="center"><font face="Arial, Helvetica, sans-serif" size="5"><b>
<%= Properties.getProperty("football.pool.name") %> Error </b></font></p>
<p align="center"><font face="Arial, Helvetica, sans-serif" size="4"><b>Could 
  not process your request for the following reason:</b></font></p>
<table width="75%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr bgcolor="#00CCCC"> 
    <td colspan="2"> 
      <div align="center"> 
        <p><font face="Arial, Helvetica, sans-serif" size="5" color="#000000">
            <%= exception.getMessage() %>
            </font><font face="Arial, Helvetica, sans-serif" size="+2"> 
          <br>
          </font></p>
      </div>
    </td>
  </tr>
  <tr bgcolor="#009999"> 
    <td nowrap colspan="2"> 
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="4" color="#000000">If 
        you feel you should not have gotten this error<br>
        please e-mail this page to <a href="mailto:<%= Properties.getProperty("football.pool.email") %>"><%= Properties.getProperty("football.pool.email") %></a></font></div>
    </td>
  </tr>
  <tr bgcolor="#FFFFCC"> 
    <td nowrap>&nbsp;</td>
    <td nowrap>&nbsp;</td>
  </tr>
  <tr bgcolor="#FFFFCC"> 
    <td nowrap colspan="2"> 
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+2"><a href="javascript: history.go(-1)"><font size="5">Go 
        Back</font></a></font> </div>
      <div align="right"><font face="Arial, Helvetica, sans-serif" size="+2"></font></div>
    </td>
  </tr>
</table>
<p align="center">&nbsp;</p>
<!--
<% PrintWriter pout = new PrintWriter(out);
   exception.printStackTrace(pout);
%>
-->
</body>
</html>
