<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.publish.PublishEntry" %>

<%! 
%>

<%
boolean authorized = false;
String adminpw = (String)session.getAttribute("adminpw");
String password = request.getParameter("password");
String overridepw =
        Properties.getProperty("football.pool.overridepw", "fboverride");
if (overridepw.equals(adminpw) || overridepw.equals(password)) {
    authorized = true;
    session.setAttribute("adminpw", overridepw);
}

if (! authorized) {
    throw new Exception("Admin Authorization Failure");
}

String year = request.getParameter("year");
if (year == null) { throw new Exception("Year Parameter not set"); }
String week = request.getParameter("week");
if (week == null) { throw new Exception("Week Parameter not set"); }

String dbUrl = Properties.getProperty("football.database.url");
String database = dbUrl.substring(0, dbUrl.indexOf('?'));

StringBuffer pStr = new StringBuffer();
String pHref = "";
try {
    String path = PublishEntry.publish(Integer.parseInt(year),
                                       Integer.parseInt(week));
    int idx = path.indexOf("email");
    pHref = "/" + path.substring(idx);
    pStr.append("The email entry has been published to ");
}
catch (Exception e) {
    pStr.append(e.getMessage());
}

%>

<HTML>
<HEAD>
<TITLE>
<%= Properties.getProperty("football.pool.name") %> Published Entry</TITLE>

<BODY text=#006666 bgColor=#ffffcc link="#006666" vlink="#006666" alink="#006666">
<P align=center><FONT face="Arial, Helvetica, sans-serif" size=5><B> <%= Properties.getProperty("football.pool.name") %> 
  Published Entry<br>
  for <%= year %></FONT></B> <br>
  <FONT face="Arial, Helvetica, sans-serif" size=2><B><%= database %></B></font> 
</P>
<P align=center><b><font face="Arial, Helvetica, sans-serif" size="3" color="#006666"><%= pStr.toString() %>
  <a href="<%= pHref %>"><%= pHref %></a></font></b> 
</P>
<br>
<TABLE cellSpacing=4 width="80%" align=center border=0>
  <TBODY>
  <TR>
    <TD colSpan=2>
      <DIV align=center><FONT face="Arial, Helvetica, sans-serif" size=+1><A 
      href="javascript:%20history.go(-1)">Go Back</A></FONT></DIV></TD></TR>
  <TR>
    <TD colSpan=2>
      <DIV align=center><FONT face="Arial, Helvetica, sans-serif" size=+1><A 
      href="Admin.jsp"><%= Properties.getProperty("football.pool.name") %> Admin Page</A></FONT></DIV>
    </TD></TR></TBODY></TABLE>
<P>&nbsp;</P></BODY></HTML>
