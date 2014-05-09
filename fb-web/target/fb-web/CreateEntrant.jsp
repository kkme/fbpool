<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.football.entities.*" %>
<%@ page import="com.talient.football.jdbc.JDBCHomes" %>
<%@ page import="com.talient.football.jdbc.JDBCEntrant" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.email.SendMail" %>

<%! Alias alias = null;
    String forwardPage = new String("EntrantCreated.jsp");
%>

<%
if (Properties.getProperty("football.pool.name").equals("NFL Pool")) {
    String adminpw = (String)session.getAttribute("adminpw");
    String password = request.getParameter("password");
    String overridepw =
            Properties.getProperty("football.pool.overridepw", "fboverride");
    if (overridepw.equals(adminpw) || overridepw.equals(password)) {
        session.setAttribute("adminpw", overridepw);
    }
    else {
        throw new Exception("Admin Authorization Failure");
    }
}

String year = request.getParameter("year");
String week = request.getParameter("week");
String entryStr = request.getParameter("entry");

String name = request.getParameter("name");
if (name == null || name.length() == 0) {
    throw new Exception("Name was not entered");
}
Alias alias = JDBCEntrant.findAliasByUsername(name);
if (alias != null) {
    throw new Exception(name + " already exists");
}
String passwd = request.getParameter("password");
if (passwd == null || passwd.length() == 0) {
    throw new Exception("Password was not entered");
}
String email = request.getParameter("email");
if (email == null) {
    email = "";
}
Entrant entrant = new Entrant(name, email, true);
alias = new Alias(entrant, name, passwd);
int rs = JDBCEntrant.store(entrant, alias);
if (rs == 0) {
    throw new Exception("Could not create a new login for " + name);
}

try {
    if (email.indexOf("@") > 0) {
        SendMail sm = new SendMail("", "");
        sm.emailCreatedUpdated(entrant, "Created");
    }
}
catch (Exception e) {
    // Do not generate an error page if email is not working.
}

if (year != null && !year.equals("") && week != null && !week.equals("") &&
    entryStr != null && !entryStr.equals("")) {
    forwardPage = new String("VerifyEntry.jsp");
}
%>

<html>
<head>
<title>Create Entrant</title>
<body>
<jsp:forward page="<%= forwardPage %>" />
</body>
</html>

