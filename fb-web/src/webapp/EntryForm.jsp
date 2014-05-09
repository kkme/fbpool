<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.talient.football.entities.*" %>
<%@ page import="com.talient.football.jdbc.JDBCEntrant" %>
<%@ page import="com.talient.football.jdbc.JDBCTeam" %>
<%@ page import="com.talient.util.Properties" %>

<%! WeeklySchedule schedule;
    SimpleDateFormat dueDF = new SimpleDateFormat("EEEE, MMM. d, h:mma");
    List games;
    Game game1;
    ListIterator giter;
    String year;
    String week;
%>

<%
year = request.getParameter("year");
if (year == null) { throw new Exception("Invalid Year Parameter"); }
week = request.getParameter("week");
if (week == null) { throw new Exception("Invalid Week Parameter"); }
schedule = WeeklySchedule.getHome().findByYearWeek(
            Integer.parseInt(year), Integer.parseInt(week));
if (schedule == null || schedule.size() <= 0) { 
    throw new Exception("Game schedule is not available for " + year +
                        " Week " + week);
}
games = schedule.orderedGames();
Collections.shuffle(games);
game1 = (Game)games.get(0);

String weekName = schedule.getLabel();

String name = "";
String entryStr = "";
String verifyChecked = "";
boolean verifyCookie = false;
Entry entry = null;
Cookie[] cookies = request.getCookies();
if (cookies != null && cookies.length > 0) {
    for (int i=0; i<cookies.length; i++) {
        if (cookies[i].getName().equals("username")) {
            name = cookies[i].getValue();
            if (name == null) { name = ""; }
            if (name.length() > 1 &&
                name.startsWith("\"") && name.endsWith("\"")) {
                name = name.substring(1, name.length() - 1);
            }
getServletConfig().getServletContext().log("Name Cookie=" + name);
        }
        if (cookies[i].getName().equals("emailentry")) {
            if (cookies[i].getValue().equals("1")) {
                verifyChecked = "checked";
            }
            verifyCookie = true;
        }
        if (cookies[i].getName().equals("week"+year+week)) {
            try {
                entryStr = cookies[i].getValue();
                if (entryStr == null) { entryStr = ""; }
                if (entryStr.length() > 1 &&
                    entryStr.startsWith("\"") &&
                    entryStr.endsWith("\"")) {
                    entryStr = entryStr.substring(1, entryStr.length() - 1);
                }

                Entrant entrant = JDBCEntrant.findByUsername("~Concensus");
                entry = new Entry(schedule, entrant);
                int start = 0;
                boolean last = false;
                while (! last) {
                    int idx = entryStr.indexOf("!", start);
                    if (idx == -1) {
                        idx = entryStr.length();
                        last = true;
                    }
                    Team team =
                        JDBCTeam.findByLongName(entryStr.substring(start,idx));
                    if (team == null) {
                        entry = null;
                        break;
                    }
                    else {
                        entry.add(team);
                    }
                    start = idx + 1;
                }
                if (entry != null) {
                    entry.getScore();
                }
            }
            catch (Exception e) {
                entry = null;
            }
        }
    }
}
if (! verifyCookie) {
    verifyChecked = "checked";
}
if (entry == null) {
    Entrant entrant = JDBCEntrant.findByUsername("~Concensus");
    entry = new Entry(schedule, entrant);
    giter = games.listIterator();
    while (giter.hasNext()) {
        Game game = (Game)giter.next();
        entry.add(game.getHome());
    }
    StringBuffer sb = new StringBuffer();
    for (int j=0; j<entry.size(); j++) {
        if (j !=0 ) { 
            sb.append("!");
        }
        sb.append(entry.get(j));
    }
    entryStr = sb.toString();
}
getServletConfig().getServletContext().log("Name Cookie=" + name);
getServletConfig().getServletContext().log("Entry Cookie=" + entryStr);

%>

<html>
<head>
<title>
  <%= Properties.getProperty("football.pool.name") %> <%= weekName %>
</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src="scripts/cookielib.js"></script>
<script src="scripts/nflpool.js"></script>
<script language="JavaScript">

var games = new blankArray(<%= schedule.size() %>);

function loadPicks() {
<%
for (int i=0; i<schedule.size(); i++) {
    out.print("    games[" + i + "] = document.NFLPool.game" + (i+1) + ";\n");
}
%>

    restorePicks("<%= entryStr %>");
    if (navigator.appName.indexOf("Netscape") >= 0) {
        window.resizeBy(-1,-1);
    }
}


function checkOrder() {
    if (unordered == 1) {
        alert("WARNING!\nYou have not changed the order of your picks.\nOrder your picks on the Entry Form by selecting the team you want to move in the pick list and clicking on the up and down arrows.");
    }
}

function viewEntry() {
    document.NFLPoolViewEntry.name.value = document.NFLPool.name.value;
    document.NFLPoolViewEntry.password.value = document.NFLPool.password.value; 
    document.NFLPoolViewEntry.year.value = document.NFLPool.year.value; 
    document.NFLPoolViewEntry.week.value = document.NFLPool.week.value; 
    document.NFLPoolViewEntry.submit();
}
</script>
<script language="JavaScript">
<!--
function MM_callJS(jsStr) { //v2.0
  return eval(jsStr)
}
//-->
</script>
</head>

<body onLoad="loadPicks()" bgcolor="#FFFFCC" text="#006666">
<form method="post" action="SubmitEntry.jsp" name="NFLPool">
  <table width="54%" border="0" cellpadding="2" cellspacing="0" align="center">
    <tr bgcolor="#FFFFCC"> 
      <td width="100%" nowrap colspan="6"> 
        <div align="center"><b><font size="+2" face="Arial, Helvetica, sans-serif"><%= Properties.getProperty("football.pool.name") %> <%= weekName %></font></b></div>
      </td>
    </tr>
    <tr bgcolor="#FFFFCC"> 
      <td width="100%" nowrap colspan="6"> 
        <div align="center"><font face="Arial, Helvetica, sans-serif">Entry due 
          by <%= dueDF.format(schedule.getStartTime()) %> (Mountain Time)</font></div>
      </td>
    </tr>
    <tr bgcolor="#FFFFCC"> 
      <td width="100%" colspan="6"> 
        <div align="center"><font face="Arial, Helvetica, sans-serif" color="red" size="2">
<% if (Properties.getProperty("football.pool.name").equals("CFPOOL")) { %>
          If this is your first time using the Online Entry Form, fill in the form (make up a password) and select your picks.  The next page will prompt you to add your login.
<% } %>
        </font></div>
      </td>
    </tr>
<%
try {
    FileReader file = new FileReader(
        Properties.getProperty("football.pool.DocumentRoot") + 
        "/entry" + year + "_" + week + ".msg");
    char buf[] = new char[4096];
    int cnt = file.read(buf);
    while (cnt != -1) {
        out.write(buf);
        cnt = file.read(buf);
    }
    file.close();
}
catch (Exception e) {
    // If there is no additional text then ignore the exception.
}
getServletConfig().getServletContext().log("Name Cookie=" + name);
getServletConfig().getServletContext().log("Entry Cookie=" + entryStr);

%>
    <tr bgcolor="#FFFFCC"> 
      <td width="100%" nowrap colspan="6"> 
        <div align="center"><font face="Arial, Helvetica, sans-serif"><b><font size="+1">Name:</font><font size="+2"> 
          </font></b> 
<%
getServletConfig().getServletContext().log("Name Cookie=" + name);
%>
          <input type="text" name="name" value="<%= name %>">
          <b><font size="+1">Password:</font></b> 
          <input type="password" name="password">
          </font></div>
      </td>
    </tr>
    <tr bgcolor="#FFFFCC"> 
      <td width="100%" nowrap colspan="6"> 
        <div align="center"> 
          <p>
            <font face="Arial, Helvetica, sans-serif" size="+1">
            <input type="checkbox" name="emailentry" value="1" <%= verifyChecked %> > Send email verification
            </font>
          </p>
        </div>
      </td>
    </tr>
    <tr bgcolor="#00FFFF"> 
      <td width="100%" nowrap colspan="6"> 
        <table width="100%" border="2" bordercolor="#6666FF">
          <tr> 
            <td> 
              <table width="100%" border="0" cellpadding="2" cellspacing="0" align="center">
                <tr> 
                  <td bgcolor="#00FFFF" nowrap>&nbsp;</td>
                  <td nowrap bgcolor="#00FFFF"> 
                    <div align="right"> <font face="Arial, Helvetica, sans-serif" size="2" color="#000000"> 
                      <input type="radio" name="game1" value="<%=game1.getVisitor() %>" onClick="MM_callJS('changePick(document.NFLPool.game1, document.NFLPool.picks)')">
                      <%= game1.getVisitor() %></font></div>
                  </td>
                  <td nowrap bgcolor="#00FFFF"> 
                    <div align="center"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000">at</font></div>
                  </td>
                  <td nowrap bgcolor="#00FFFF"> <font face="Arial, Helvetica, sans-serif" size="2" color="#000000"> 
                    <input type="radio" name="game1" value="<%=game1.getHome() %>" checked onClick="MM_callJS('changePick(document.NFLPool.game1, document.NFLPool.picks)')">
                    <%=game1.getHome() %></font></td>
                  <td width="25" rowspan="<%= schedule.size() %>" bgcolor="#009999">&nbsp;</td>
                  <td width="140" rowspan="<%= schedule.size() %>" bgcolor="#009999"> 
                    <div align="center"> 
                      <p><font face="Arial, Helvetica, sans-serif"><b><font size="+1" color="#80FFFF">Picks</font></b></font></p>
                      <p> <font face="Arial, Helvetica, sans-serif"> 
                        <select name="picks" size="<%= schedule.size() %>">
<%
   for (int j=0; j<entry.size(); j++) {
%>                <option><%= entry.get(j).toString() %></option>
<%
   } 
%>
                        </select>
                        </font></p>
                      <p> <font face="Arial, Helvetica, sans-serif"> 
                        <input type="submit" name="Submit2" value="Submit"
            onClick="document.NFLPool.entry.value = 
<%
for (int i=0; i<schedule.size()-1; i++) {
%>                document.NFLPool.picks.options[<%= i %>].value + '!' +
<%
}
%>                document.NFLPool.picks.options[<%= schedule.size()-1 %>].value;
                  checkOrder();"
          >
                        </font></p>
                    </div>
                  </td>
                  <td rowspan="<%= schedule.size() %>" bgcolor="#009999" width="45" align="right"> 
                    <p align="center"><font face="Arial, Helvetica, sans-serif"><a href="javascript: MM_callJS('moveUp(document.NFLPool.picks)')"><img src="images/cyuarrw.gif" border=0 ></a></font></p>
                    <p align="center"><font face="Arial, Helvetica, sans-serif"><a href="javascript: MM_callJS('moveDown(document.NFLPool.picks)')"><img src="images/cydarrw.gif" border=0 ></a></font></p>
                  </td>
                </tr>
<% giter = games.listIterator();
   String color = null;
   giter.next();
   int num = 1;
   while (giter.hasNext()) {
       Game game = (Game)giter.next();
       num++;
       if (num % 2 == 0) {
           color = "#00CCCC";
       }
       else {
           color = "#00FFFF";
       }
%>
                <tr> 
                  <td nowrap bgcolor="<%= color %>">&nbsp;</td>
                  <td nowrap bgcolor="<%= color %>"> 
                    <div align="right"> <font face="Arial, Helvetica, sans-serif" size="2" color="#000000"> 
                      <input type="radio" name="game<%= num %>" value="<%= game.getVisitor() %>" onClick="MM_callJS('changePick(document.NFLPool.game<%= num %>, document.NFLPool.picks)')">
                      <%= game.getVisitor() %></font></div>
                  </td>
                  <td nowrap bgcolor="<%= color %>"> 
                    <div align="center"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000">at</font></div>
                  </td>
                  <td nowrap bgcolor="<%= color %>"> <font face="Arial, Helvetica, sans-serif" size="2" color="#000000"> 
                    <input type="radio" name="game<%= num %>" value="<%= game.getHome() %>" checked onClick="MM_callJS('changePick(document.NFLPool.game<%= num %>, document.NFLPool.picks)')">
                    <%= game.getHome() %></font></td>
                </tr>
<%
   } 
%>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <input type="hidden" name="entry">
  <input type="hidden" name="year" value="<%= schedule.getYear() %>">
  <input type="hidden" name="week" value="<%= schedule.getWeek() %>">
</form>
        <div align="center"><font face="Arial, Helvetica, sans-serif"><a href="/"><b><%= Properties.getProperty("football.pool.name") %>
          Home Page</b></a></font></div>
<form method="post" action="ViewEntry.jsp" name="NFLPoolViewEntry">
  <div align="center">
    <input type="hidden" name="name">
    <input type="hidden" name="password">
    <input type="hidden" name="year">
    <input type="hidden" name="week">
    <font face="Arial, Helvetica, sans-serif" size="3"><b>
    <a href="javascript:viewEntry()">View Existing Entry</a>
    </b></font>
  </div>
</form>
<p>&nbsp;</p>
</body>
</html>
