<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.entities.WeeklySchedule" %>
<%@ page import="com.talient.football.entities.Game" %>
<%@ page import="com.talient.football.jdbc.JDBCGame" %>
<%@ page import="com.talient.football.util.results.PersistWeeklyResults" %>
<%@ page import="com.talient.football.publish.PublishStandings" %>
<%@ page import="com.talient.football.publish.PublishYearToDate" %>

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

PersistWeeklyResults pwr = new PersistWeeklyResults(Integer.parseInt(year),
                                                    Integer.parseInt(week));

StringBuffer ytdStr = new StringBuffer();
String ytdHref = "";
String ytd2Href = "";
try {
    String path = PublishYearToDate.publish(Integer.parseInt(year), 
                                            Integer.parseInt(week));
    int idx = path.indexOf(year);
    ytdHref = "/" + path.substring(idx);
    ytdStr.append("The YearToDate has been published to ");
    String path2 = PublishYearToDate.publish(Integer.parseInt(year), 0);
    idx = path2.indexOf(year);
    ytd2Href = "/" + path2.substring(idx);
}
catch (Exception e) {
    ytdStr.append(e.getMessage());
}

StringBuffer sStr = new StringBuffer();
String sHref = "";
try {
    String path = PublishStandings.publish(Integer.parseInt(year), 
                                           Integer.parseInt(week));
    int idx = path.indexOf(year);
    sHref = "/" + path.substring(idx);
    sStr.append("The Standings has been published to ");
}
catch (Exception e) {
    sStr.append(e.getMessage());
}


%>

<HTML>
<HEAD>
<TITLE>
<%= Properties.getProperty("football.pool.name") %> Persisted Results</TITLE>

<BODY text=#006666 bgColor=#ffffcc link="#006666" vlink="#006666" alink="#006666">
<P align=center><FONT face="Arial, Helvetica, sans-serif" size=5><B> <%= Properties.getProperty("football.pool.name") %> 
  Persisted Results<br>
  Week <%= week %>, <%= year %> </FONT></B> <br>
  <FONT face="Arial, Helvetica, sans-serif" size=2><B><%= database %></B></font> 
</P>
<P align=center><b><font face="Arial, Helvetica, sans-serif" size="3" color="#006666"><%= ytdStr.toString() %>
  <a href="<%= ytdHref %>"><%= ytdHref %></a> and
  <a href="<%= ytd2Href %>"><%= ytd2Href %></a></font></b> 
</P>
<P align=center><b><font face="Arial, Helvetica, sans-serif" size="3"><%= sStr.toString() %>
  <a href="<%= sHref %>"><%= sHref %></a></font></b></P>
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
