<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.entities.WeeklySchedule" %>
<%@ page import="com.talient.football.entities.Game" %>
<%@ page import="com.talient.football.jdbc.JDBCGame" %>
<%@ page import="com.talient.football.publish.PublishCrosstable" %>
<%@ page import="com.talient.football.publish.PublishWeeklyStats" %>
<%@ page import="com.talient.football.publish.PublishResults" %>

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

WeeklySchedule schedule =
    WeeklySchedule.getHome().findByYearWeek(Integer.parseInt(year),
                                            Integer.parseInt(week));
List games = schedule.orderedGames();
ListIterator giter = games.listIterator();
int num = 0;
while (giter.hasNext()) {
    Game game = (Game)giter.next();
    num++;
    String vScore = request.getParameter("visitor" + num + "Score");
    if (vScore == null) {
        throw new Exception("Missing visitor" + num + "Score Parameter");
    }
    game.setVisitorScore(Integer.parseInt(vScore));
    String hScore = request.getParameter("home" + num + "Score");
    if (hScore == null) {
        throw new Exception("Missing home" + num + "Score Parameter");
    }
    game.setHomeScore(Integer.parseInt(hScore));
    String quarter = request.getParameter("g" + num + "Quarter");
    if (quarter == null) {
        throw new Exception("Missing g" + num + "Quarter Parameter");
    }
    game.setQuarter(Integer.parseInt(quarter));
    String clock = request.getParameter("g" + num + "Clock");
    if (clock == null) {
        throw new Exception("Missing g" + num + "Clock Parameter");
    }
    game.setGameClock(clock);
    String state = request.getParameter("g" + num + "State");
    if (state == null) {
        throw new Exception("Missing g" + num + "State Parameter");
    }
    game.setGameState(Integer.parseInt(state));
    JDBCGame.store(game);
}

StringBuffer ctStr = new StringBuffer();
String ctHref = "";
try {
    String path = PublishCrosstable.publish(Integer.parseInt(year), 
                                            Integer.parseInt(week));
    int idx = path.indexOf(year);
    ctHref = "/" + path.substring(idx);
    ctStr.append("The Crosstable has been published to ");
}
catch (Exception e) {
    ctStr.append(e.getMessage());
}

StringBuffer wsStr = new StringBuffer();
String wsHref = "";
try {
    String path = PublishWeeklyStats.publish(Integer.parseInt(year), 
                                             Integer.parseInt(week));
    int idx = path.indexOf(year);
    wsHref = "/" + path.substring(idx);
    wsStr.append("The Weekly Stats has been published to ");
}
catch (Exception e) {
    wsStr.append(e.getMessage());
}

StringBuffer rStr = new StringBuffer();
String rHref = "";
try {
    String path = PublishResults.publish(Integer.parseInt(year), 
                                        Integer.parseInt(week));
    int idx = path.indexOf(year);
    rHref = "/" + path.substring(idx);
    rStr.append("The Result has been published to ");
}
catch (Exception e) {
    rStr.append(e.getMessage());
}


%>

<HTML>
<HEAD>
<TITLE>
<%= Properties.getProperty("football.pool.name") %> Games Scored</TITLE>

<BODY text=#006666 bgColor=#ffffcc link="#006666" vlink="#006666" alink="#006666">
<P align=center><FONT face="Arial, Helvetica, sans-serif" size=5><B> <%= Properties.getProperty("football.pool.name") %> 
  Games Scored<br>
  Week <%= week %>, <%= year %> </FONT></B> <br>
  <FONT face="Arial, Helvetica, sans-serif" size=2><B><%= database %></B></font> 
</P>
<P align=center><b><font face="Arial, Helvetica, sans-serif" size="3" color="#006666"><%= ctStr.toString() %>
  <a href="<%= ctHref %>"><%= ctHref %></a></font></b> 
</P>
</P>
<P align=center><b><font face="Arial, Helvetica, sans-serif" size="3" color="#006666"><%= wsStr.toString() %>
  <a href="<%= wsHref %>"><%= wsHref %></a></font></b> 
</P>
<P align=center><b><font face="Arial, Helvetica, sans-serif" size="3"><%= rStr.toString() %>
  <a href="<%= rHref %>"><%= rHref %></a></font></b></P>
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
