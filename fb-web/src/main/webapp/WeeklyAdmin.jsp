<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>

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

%>

<HTML>
<HEAD>
<TITLE>
<%= Properties.getProperty("football.pool.name") %> Weekly Administration
</TITLE>

<BODY text=#006666 bgColor=#ffffcc link="#006666" vlink="#006666" alink="#006666">
<P align=center><FONT face="Arial, Helvetica, sans-serif" size=5><B>
<%= Properties.getProperty("football.pool.name") %> Weekly Administration<br>
Week <%= week %>, <%= year %></FONT></B><br>
<FONT face="Arial, Helvetica, sans-serif" size=2><B><%= database %></B></font>
</P>
<div align="center">
<FONT face="Arial, Helvetica, sans-serif" size=3>
<b>
<a href="ScoreGames.jsp?year=<%= year %>&week=<%= week %>">Score Games</a>
<font size=1><a href="ScoreGamesPDA.jsp?year=<%= year %>&week=<%= week %>">PDA</a></font>
<br>
<a href="PersistResults.jsp?year=<%= year %>&week=<%= week %>">Persist Results</a>
<hr width="75%">
<a href="PublishEntry.jsp?year=<%= year %>&week=<%= week %>">Publish Entry</a>
<br>
<a href="PublishRecap.jsp?year=<%= year %>&week=<%= week %>">Publish Recap</a>
<br>
<a href="PublishSeason.jsp?year=<%= year %>">Publish Season</a>
<br>
<a href="NewEntrant.jsp">Create New Entrant</a>
<hr width="75%">
<a href="EmailEntry.jsp?year=<%= year %>&week=<%= week %>">Email Entry</a>
<br>
<a href="EmailRecap.jsp?year=<%= year %>&week=<%= week %>">Email Recap</a>
<br>
<a href="EmailResults.jsp?year=<%= year %>&week=<%= week %>">Email Results</a>
<br>
<a href="EmailStandings.jsp?year=<%= year %>&week=<%= week %>">Email Standings</a>
<% if (Properties.getProperty("football.pool.name").equals("CFPOOL")) { %>
<br>
<a href="http://groups.google.com/groups?q=cfpool+group:rec.sport.football.college&hl=en&lr=&ie=UTF-8&scoring=d">RSFC News Group</a>
</b></font>
<% } %>
</div>
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
