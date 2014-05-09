<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="com.talient.football.reports.WeeklyStats" %>
<%@ page import="com.talient.football.reports.Crosstable" %>
<%@ page import="com.talient.football.entities.Game" %>
<%@ page import="com.talient.football.entities.Team" %>
<%@ page import="com.talient.football.entities.Entrant" %>
<%@ page import="com.talient.football.jdbc.JDBCCrosstable" %>
<%@ page import="com.talient.util.Properties" %>

<%
String year = request.getParameter("year");
if (year == null) { throw new Exception("Invalid Year Parameter"); }
String week = request.getParameter("week");
if (week == null) { throw new Exception("Invalid Week Parameter"); }
Crosstable ct = JDBCCrosstable.findByYearWeek(Integer.parseInt(year),
                                              Integer.parseInt(week));
if (ct.size() == 0) {
    throw new Exception("There are no entries for " + year + ", Week " + week);
}
WeeklyStats ws = new WeeklyStats(ct);

Cookie[] cookies = request.getCookies();
String name = "";
if (cookies != null && cookies.length > 0) {
    for (int i=0; i<cookies.length; i++) {
        if (cookies[i].getName().equals("username")) {
            name = cookies[i].getValue();
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
<title><%= Properties.getProperty("football.pool.name") %> Weekly Stats</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFCC" text="#006666">
<p align="center"><font face="Arial, Helvetica, sans-serif" size="5">
  <%= Properties.getProperty("football.pool.name") %>  2002 Week <%= week %><br>
  Weekly Stats</font>
</p>
<br>
<table width="1%" border="1" align="center" bordercolor="#0000FF">
  <tr>
    <td>
      <table align="center" width="100%" height="100%" cellpadding="3" cellspacing="0" border="0">
        <tr> 
          <td width="6%" nowrap colspan="7"> 
            <div align="center"><b><font face="Arial, Helvetica, sans-serif" size="3">Favorites</font></b></div>
          </td>
        </tr>
        <tr> 
          <td width="3%" nowrap> 
            <div align="center"><b><font face="Arial, Helvetica, sans-serif" size="2">&nbsp;Picked&nbsp;</font></b></div>
          </td>
          <td width="3%" nowrap> 
            <div align="center"><b><font face="Arial, Helvetica, sans-serif" size="2">&nbsp;Favorite&nbsp;</font></b></div>
          </td>
          <td width="3%" nowrap> 
            <div align="center"><b><font face="Arial, Helvetica, sans-serif" size="2">&nbsp;Weight&nbsp;</font></b></div>
          </td>
          <td width="20" nowrap>&nbsp;</td>
          <td width="3%" nowrap> 
            <div align="center"><b><font face="Arial, Helvetica, sans-serif" size="2">&nbsp;Picked&nbsp;</font></b></div>
          </td>
          <td width="3%" nowrap> 
            <div align="center"><b><font face="Arial, Helvetica, sans-serif" size="2">&nbsp;Underdog&nbsp;</font></b></div>
          </td>
          <td width="3%" nowrap> 
            <div align="center"><b><font face="Arial, Helvetica, sans-serif" size="2">&nbsp;Weight&nbsp;</font></b></div>
          </td>
        </tr>

<% ListIterator iter = ws.getFavorites().listIterator();
   String color = null;
   int num = 0;
   while (iter.hasNext()) {
       Game game = (Game)iter.next();
       num++;
       if (num % 2 == 0) {
           color = "#00CCCC";
       }
       else {
           color = "#00FFFF";
       }
       Team fav = null;
       Team ud = null;
       if (ws.getTimesPicked(game.getHome()) >
           ws.getTimesPicked(game.getVisitor())) {
           fav = game.getHome();
           ud = game.getVisitor();
       }
       else if (ws.getTimesPicked(game.getHome()) <
                ws.getTimesPicked(game.getVisitor())) {
           ud = game.getHome();
           fav = game.getVisitor();
       }
       else if (ws.getTotalPoints(game.getHome()) >=
                ws.getTotalPoints(game.getVisitor())) {
           fav = game.getHome();
           ud = game.getVisitor();
       }
       else if (ws.getTotalPoints(game.getHome()) <
                ws.getTotalPoints(game.getVisitor())) {
           ud = game.getHome();
           fav = game.getVisitor();
       }
       double fav_avg = (double)ws.getTotalPoints(fav) / ws.getTimesPicked(fav);
       double ud_avg = 0.0;
       if (ws.getTimesPicked(ud) > 0) {
           ud_avg = (double)ws.getTotalPoints(ud) / ws.getTimesPicked(ud);
       }
%>

        <tr bgcolor="<%= color %>"> 
          <td width="1%" nowrap> 
            <div align="right"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><%= ws.getTimesPicked(fav) %>&nbsp;</font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="left"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><%= fav %></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="right"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><%= ws.getAvgFormat().format(fav_avg) %></font></div>
          </td>
          <td width="20" nowrap> 
            <div align="center"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000">&nbsp;&nbsp;</font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="right"><font color="#000000" face="Arial, Helvetica, sans-serif" size="2"><%= ws.getTimesPicked(ud) %>&nbsp;</font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="left"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><%= ud %></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="right"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000">&nbsp;<%= ws.getAvgFormat().format(ud_avg) %></font></div>
          </td>
        </tr>
<% } %>
      </table>
    </td>
  </tr>
</table>
<br>
<table width="1%" border="1" align="center" bordercolor="#0000FF">
  <tr> 
    <td> 
      <table align="center" width="100%" height="100%" cellpadding="3" cellspacing="0" border="0">
        <tr> 
          <td nowrap>&nbsp;</td>
          <td nowrap colspan="4"> 
            <div align="center"><b><font face="Arial, Helvetica, sans-serif" size="3">Picks Against<br>
            Consensus</font></b><b><font face="Arial, Helvetica, sans-serif" size="3"></font></b></div>
          </td>
          <td nowrap>&nbsp;</td>
          <td colspan="4" nowrap> 
            <div align="center"><b><font face="Arial, Helvetica, sans-serif" size="3">Win/Loss<br>
              Records</font></b></div>
          </td>
        </tr>
        <tr> 
          <td width="1%" nowrap> 
            <div align="center"><font size="2"><b><font face="Arial, Helvetica, sans-serif">Name&nbsp;</font></b></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="center"><font size="2"><b><font face="Arial, Helvetica, sans-serif">&nbsp;Against&nbsp;</font></b></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="center"><font size="2"><b><font face="Arial, Helvetica, sans-serif">&nbsp;Won&nbsp;</font></b></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="center"><font size="2"><b><font face="Arial, Helvetica, sans-serif">&nbsp;Lost&nbsp;</font></b></font></div>
          </td>
<% if (Properties.getProperty("football.pool.name").equals("NFL Pool")) { %>
          <td width="1%" nowrap> 
            <div align="center"><font size="2"><b><font face="Arial, Helvetica, sans-serif">&nbsp;Tied&nbsp;</font></b></font></div>
          </td>
<% } %>
          <td width="1%" nowrap>&nbsp;</td>
          <td width="1%" nowrap> 
            <div align="center"><font size="2"><b><font face="Arial, Helvetica, sans-serif">&nbsp;Won&nbsp;</font></b></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="center"><font size="2"><b><font face="Arial, Helvetica, sans-serif">&nbsp;Lost&nbsp;</font></b></font></div>
          </td>
<% if (Properties.getProperty("football.pool.name").equals("NFL Pool")) { %>
          <td width="1%" nowrap> 
            <div align="center"><font size="2"><b><font face="Arial, Helvetica, sans-serif">&nbsp;Tied&nbsp;</font></b></font></div>
          </td>
<% } %>
          <td width="1%" nowrap> 
            <div align="center"><font size="2"><b><font face="Arial, Helvetica, sans-serif">&nbsp;Pts&nbsp;</font></b></font></div>
          </td>
        </tr>

<% iter = ws.getWinLossRecord().listIterator();
   NumberFormat nf = ws.getSchedule().getNumberFormat();
   color = null;
   num = 0;
   while (iter.hasNext()) {
       Entrant entrant = (Entrant)iter.next();
       num++;
       if (entrant.getUsername().equals(name)) {
           color = "#CCCCCC";
       }
       else if (num % 2 == 0) {
           color = "#00CCCC";
       }
       else {
           color = "#00FFFF";
       }
%>
        <tr bgcolor="<%= color %>"> 
          <td width="1%" nowrap> 
            <div align="left"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><%= entrant %></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="right"><font color="#000000" face="Arial, Helvetica, sans-serif" size="2">&nbsp;<%= ws.getAgainstConsensus(entrant) %></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="right"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000">&nbsp;<%= ws.getAgainstWins(entrant) %></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="right"><font color="#000000" face="Arial, Helvetica, sans-serif" size="2">&nbsp;<%= ws.getAgainstLosses(entrant) %></font></div>
          </td>
<% if (Properties.getProperty("football.pool.name").equals("NFL Pool")) { %>
          <td width="1%" nowrap> 
            <div align="right"><font color="#000000" face="Arial, Helvetica, sans-serif" size="2">&nbsp;<%= ws.getAgainstTies(entrant) %>&nbsp;</font></div>
          </td>
<% } %>
          <td width="1%" nowrap bgcolor="#009999">&nbsp;</td>
          <td width="1%" nowrap> 
            <div align="right"><font color="#000000" face="Arial, Helvetica, sans-serif" size="2">&nbsp;<%= ws.getWins(entrant) %></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="right"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000">&nbsp;<%= ws.getLosses(entrant) %></font></div>
          </td>
<% if (Properties.getProperty("football.pool.name").equals("NFL Pool")) { %>
          <td width="1%" nowrap> 
            <div align="right"><font color="#000000" face="Arial, Helvetica, sans-serif" size="2">&nbsp;<%= ws.getTies(entrant) %>&nbsp;</font></div>
          </td>
<% } %>
          <td width="1%" nowrap> 
            <div align="right"><font color="#000000" face="Arial, Helvetica, sans-serif" size="2">&nbsp;<%= nf.format(ws.getScore(entrant)) %></font></div>
          </td>
        </tr>
<% } %>
      </table>
    </td>
  </tr>
</table>
<br>
<br>
<table border="0" align="center" cellspacing="4">
  <tr nowrap> 
    <td colspan="3"> 
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+1"><a href="javascript: history.go(-1)">Go 
        Back</a></font></div>
    </td>
  </tr>
  <tr nowrap> 
    <td colspan="3"> 
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+1"><a href="/"><%= Properties.getProperty("football.pool.name") %> Home Page</a></font></div>
    </td>
  </tr>
</table>
<p align="center">&nbsp;</p>
</body>
</html>
