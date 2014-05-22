<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.talient.football.entities.*" %>
<%@ page import="com.talient.football.jdbc.JDBCHomes" %>
<%@ page import="com.talient.football.jdbc.JDBCEntrant" %>
<%@ page import="com.talient.util.Properties" %>

<%! Alias alias = null;
    String forwardPage = null;
%>

<%
String year = request.getParameter("year");
if (year == null) { throw new Exception("Invalid Year Parameter"); }
String week = request.getParameter("week");
if (week == null) { throw new Exception("Invalid Week Parameter"); }
String entry = request.getParameter("entry");
if (entry != null && entry.length() != 0) {
    Cookie entryCookie = new Cookie("week"+year+week, entry);
    entryCookie.setMaxAge(300*24*60*60);
    response.addCookie(entryCookie);
}
String name = request.getParameter("name");
if (name == null || name.length() == 0) {
    throw new Exception("Name was not entered");
}
Cookie nameCookie = new Cookie("username", name);
nameCookie.setMaxAge(300*24*60*60);
response.addCookie(nameCookie);
String emailentry = request.getParameter("emailentry");
if (emailentry != null && emailentry.equals("1")) {
    Cookie emailentryCookie = new Cookie("emailentry", emailentry);
    emailentryCookie.setMaxAge(300*24*60*60);
    response.addCookie(emailentryCookie);
}
else {
    Cookie emailentryCookie = new Cookie("emailentry", "0");
    emailentryCookie.setMaxAge(300*24*60*60);
    response.addCookie(emailentryCookie);
}
String passwd = request.getParameter("password");
if (passwd == null || passwd.length() == 0) {
    throw new Exception("Password was not entered");
}
alias = JDBCEntrant.findAliasByUsername(name);
WeeklySchedule schedule = WeeklySchedule.getHome().findByYearWeek(
            Integer.parseInt(year), Integer.parseInt(week));
if (schedule == null || schedule.size() <= 0) { 
    throw new Exception("Game schedule is not available for " + year +
                        " Week " + week);
}
if (alias == null) {
    if (Properties.getProperty("football.pool.name").equals("CFPOOL")) {
        forwardPage = new String("NewEntrant.jsp");
    }
    else {
        forwardPage = new String("InvitationOnly.jsp");
    }
}
else {
    forwardPage = new String("VerifyEntry.jsp");
}
%>

<html>
<head>
<title> SumbitEntry </title>
<body>
<jsp:forward page="<%= forwardPage %>" />
</body>
</html>

