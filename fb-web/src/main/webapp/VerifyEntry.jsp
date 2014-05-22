<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="com.talient.football.entities.*" %>
<%@ page import="com.talient.football.jdbc.JDBCHomes" %>
<%@ page import="com.talient.football.jdbc.JDBCEntrant" %>
<%@ page import="com.talient.football.jdbc.JDBCEntry" %>
<%@ page import="com.talient.football.jdbc.JDBCTeam" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.email.SendMail" %>

<%! WeeklySchedule schedule = null;
    Alias alias = null;
    Entrant entrant = null;
    Entry entry = null;
%>

<%
String year = request.getParameter("year");
if (year == null) { throw new Exception("Invalid Year Parameter"); }
String week = request.getParameter("week");
if (week == null) { throw new Exception("Invalid Week Parameter"); }
String name = request.getParameter("name");
if (name == null || name.length() == 0) {
    throw new Exception("Name was not entered");
}
String passwd = request.getParameter("password");
if (passwd == null || passwd.length() == 0) {
    throw new Exception("Password was not entered");
}
String entryStr = request.getParameter("entry");
if (entryStr == null || entryStr.length() == 0) {
    throw new Exception("There were no picks specified");
}
alias = JDBCEntrant.findAliasByUsername(name);
entrant = JDBCEntrant.findByUsername(name);
boolean override =
         passwd.equals(Properties.getProperty("football.pool.overridepw",
                                              "fboverride"));
if (alias == null || entrant == null) {
    throw new Exception("Unknown Entrant " + name);
}
else if (! alias.getPassword().equals(passwd) && ! override) { 
    throw new Exception("Invalid Password for " + name);
}
schedule = WeeklySchedule.getHome().findByYearWeek(
            Integer.parseInt(year), Integer.parseInt(week));
if (schedule == null || schedule.size() <= 0) { 
    throw new Exception("Game schedule is not available for " + year +
                        " Week " + week);
}
Date now = new Date();
Timestamp entryTime = new Timestamp(now.getTime());
Date gameTime = schedule.getStartTime();
if (gameTime == null) {
    throw new Exception("Failed to retrieve the game time for the first game" +
                        " of week " + week + ", " + year);
}
if (! override && name.startsWith("~")) {
    override = true;
}
if (entryTime.after(gameTime) && ! override) {
    throw new Exception("Entry cannot be accepted after Kick-Off of the first" +
                        " game of the week.");
}

entry = new Entry(schedule, entrant);
int start = 0;
boolean last = false;
while (! last) {
    int idx = entryStr.indexOf("!", start);
    if (idx == -1) {
        idx = entryStr.length();
        last = true;
    }
    Team team = JDBCTeam.findByLongName(entryStr.substring(start,idx));
    if (team == null) {
        throw new Exception("Team (" + entryStr.substring(start,idx) +
                            ") is an invalid team name");
    }
    else {
        entry.add(team);
    }
    start = idx + 1;
}
entry.getScore();
JDBCEntry.store(entry);

Cookie nameCookie = new Cookie("username", name);
nameCookie.setMaxAge(300*24*60*60);
response.addCookie(nameCookie);
Cookie entryCookie = new Cookie("week"+year+week, entryStr);
entryCookie.setMaxAge(300*24*60*60);
response.addCookie(entryCookie);
session.setAttribute("username", name);

try {
    String emailentry = request.getParameter("emailentry");
    if (emailentry != null && emailentry.equals("1")) {
        SendMail sm = new SendMail("", "");
        sm.emailEntry(entry);
    }
}
catch (Exception e) {
    // Do not generate an error page if email is not working.
}
%>
<html>
<head>
<title><%= Properties.getProperty("football.pool.name") %> Valid Entry Result</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src="scripts/cookielib.js"></script>
<script src="scripts/nflpool.js"></script>

</head>

<body bgcolor="#FFFFCC">

<% if (request.getParameter("email") == null) { %>
<p align="center"><font face="Arial, Helvetica, sans-serif" size="+2" color="#006666"><%= Properties.getProperty("football.pool.name") %> Week 
  <%= week %> entry for<br>
  <%= name %>
  <br>has been validated.</font></p>
<%
   }
   else {
%>
<p align="center"><font face="Arial, Helvetica, sans-serif" size="+2" color="#006666">
  Login name "<%= name %>" has been created<br>
  and an entry for Week <%= week %><br>
  has been validated.</font><br><br>
  <font face="Arial, Helvetica, sans-serif" color="#006666">
  Visit the <a href="<%= response.encodeURL("MemberServices.jsp") %>">Member Services</a> page to update your profile.</font></p>
<% } %>
<table width="1%" border="1" align="center" bordercolor="#0000FF">
  <tr>
    <td>
      <table width="100%" border="0" align="center" cellpadding="2" cellspacing="0">
        <tr> 
          <td width="15%" nowrap colspan="3"> 
            <div align="center"><font face="Arial, Helvetica, sans-serif" size="+2" color="#006666"><b>Picks</b></font></div>
          </td>
          <td width="5%" nowrap> 
            <div align="center"><font face="Arial, Helvetica, sans-serif" size="+2" color="#006666"><b>Pts</b></font></div>
          </td>
        </tr>
<% String color = null;
   int weights[] = schedule.getWeights();
   NumberFormat f = schedule.getNumberFormat();
   for (int i=0; i<entry.size(); i++) {
       Team team = entry.get(i);
       if (i % 2 == 0) {
           color = "#00FFFF";
       }
       else {
           color = "#00CCCC";
       }
%>
        <tr bgcolor="<%= color %>"> 
          <td width="1%" nowrap> 
            <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3"><%= team %></font></b></div>
          </td>
          <td width="1%" nowrap> 
            <div align="center"><font face="Arial, Helvetica, sans-serif" size="3">&nbsp;over&nbsp;</font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="left"><font face="Arial, Helvetica, sans-serif" size="3"><%= schedule.getOpponent(team) %></font></div>
          </td>
          <td width="1%" nowrap> 
            <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">&nbsp;<%= f.format(weights[i]) %></font></b></div>
          </td>
        </tr>
<%
   } 
%>
      </table>
    </td>
  </tr>
</table>
<table width="50%" border="0" align="center">
  <tr> 
    <td>
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+1"><a href="/"><%= Properties.getProperty("football.pool.name") %> 
        Home Page</a></font></div>
    </td>
  </tr>
</table>
<p align="center">&nbsp;</p>
</body>
</html>
