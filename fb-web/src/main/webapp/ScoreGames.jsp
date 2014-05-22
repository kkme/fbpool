<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.entities.WeeklySchedule" %>
<%@ page import="com.talient.football.entities.Game" %>


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
if (games.size() == 0) {
    throw new Exception("No games found for week " + week + ", " + year);
}

SimpleDateFormat df = new SimpleDateFormat("EEE. h:mma");

%>
<html>
<head>
<title><%= Properties.getProperty("football.pool.name") %> Score Games</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFCC" text="#006666">
<form method="post" action="ProcessScoreGames.jsp">
  <table width="1%" border="1" bordercolor="#0000FF" align="center">
    <tr>
      <td>
        <table width="1%" align="center">
          <tr bgcolor="#FFFFCC"> 
            <td colspan="4" width="1%"> 
              <div align="center"><font face="Arial, Helvetica, sans-serif"><b><font size="4">Score 
                Games<br>
                for <%= year %> Week <%= week %></font></b></font><br>
                <FONT face="Arial, Helvetica, sans-serif" size=2><B>
                <%= database %></B></font></div>
            </td>
          </tr>
<% ListIterator giter = games.listIterator();
   String color = null;
   int num = 0;
   while (giter.hasNext()) {
       Game game = (Game)giter.next();
       num++;
       if (num % 2 == 0) {
           color = "#00CCCC";
       }
       else {
           color = "#00FFFF";
       }
       String clock = " " + (game.getGameClock() / 60) + ":" +
           (((game.getGameClock() % 60) < 10) ? "0" : "") +
           (game.getGameClock() % 60);                                  
       String startTime = df.format(game.getStartTime());
%>
          <tr bgcolor="<%= color %>"> 
            <td nowrap width="1%"> 
              <p align="right"><font color="#000000" face="Arial, Helvetica, sans-serif" size="3"><b><%= game.getVisitor() %><br>
                at <%= game.getHome() %></b></font></p>
            </td>
            <td width="1%" nowrap> <font face="Arial, Helvetica, sans-serif" size="3"> 
              <input type="text" name="visitor<%= num %>Score" size="2" maxlength="2" value="<%= game.getVisitorScore() %>">
              <br>
              <input type="text" name="home<%= num %>Score" maxlength="2" size="2" value="<%= game.getHomeScore() %>">
              </font></td>
            <td width="1%" nowrap> 
              <div align="right"><font color="#000000" face="Arial, Helvetica, sans-serif" size="3"><b>Qtr:</b></font> 
                <font face="Arial, Helvetica, sans-serif" size="3"> 
                <select name="g<%= num %>Quarter" size="1">
                  <option value="0" <%= (game.getQuarter()==0)?"selected":"" %>> </option>
                  <option value="1" <%= (game.getQuarter()==1)?"selected":"" %>>1st</option>
                  <option value="2" <%= (game.getQuarter()==2)?"selected":"" %>>2nd</option>
                  <option value="3" <%= (game.getQuarter()==3)?"selected":"" %>>3rd</option>
                  <option value="4" <%= (game.getQuarter()==4)?"selected":"" %>>4th</option>
                  <option value="5" <%= (game.getQuarter()==5)?"selected":"" %>>OT</option>
                </select>
                <br>
                <font color="#000000"><b>Clock: </b></font></font> <b><font face="Arial, Helvetica, sans-serif" size="3"> 
                </font></b><font face="Arial, Helvetica, sans-serif" size="3">
<input type="text" name="g<%= num %>Clock" maxlength="5" size="5" value="<%= clock %>">
                </font></div>
            </td>
            <td width="1%" nowrap> 
              <div align="right"><font color="#000000" face="Arial, Helvetica, sans-serif" size="3"><b>State:</b></font> 
                <font face="Arial, Helvetica, sans-serif" size="3"> 
                <select name="g<%= num %>State" size="1">
                  <option value="0" <%= (game.getGameState()==0)?"selected":"" %>>INPROGRESS</option>
                  <option value="1" <%= (game.getGameState()==1)?"selected":"" %>>FINAL</option>
                  <option value="2" <%= (game.getGameState()==2)?"selected":"" %>>VALIDATED</option>
                  <option value="3" <%= (game.getGameState()==3)?"selected":"" %>>DROPPED</option>
                </select>
                <br>
                <font color="#000000"><b><%= startTime %></b></font> </font></div>
            </td>
          </tr>
<% } %>
          <tr bgcolor="#FFFFCC"> 
            <td nowrap width="4%" colspan="4"> 
              <div align="center"> 
                <input type="hidden" name="year" value="<%= year %>">
                <input type="hidden" name="week" value="<%= week %>">
                <input type="submit" name="Submit" value="Update Scores">
              </div>
            </td>
          </tr>
        </table>
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
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+1"><a href="Admin.jsp"><%= Properties.getProperty("football.pool.name") %> Admin Page</a></font></div>
    </td>
  </tr>
</table>
<p>&nbsp;</p>
</body>
</html>
