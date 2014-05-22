<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.talient.football.entities.*" %>
<%@ page import="com.talient.football.jdbc.JDBCEntrant" %>
<%@ page import="com.talient.util.Properties" %>

<%! Alias alias = null;
    String forwardPage = null;
%>

<%
String name = request.getParameter("name");
if (name == null || name.length() == 0) {
    throw new Exception("Name was not entered");
}
String passwd = request.getParameter("password");
if (passwd == null || passwd.length() == 0) {
    throw new Exception("Password was not entered");
}
forwardPage = request.getParameter("target");
if (forwardPage == null || forwardPage.length() == 0) {
    forwardPage = "/index.htm";
}

alias = JDBCEntrant.findAliasByUsername(name);
if (alias == null) {
    throw new Exception("Username does not exist");
}
else if (! alias.getPassword().equals(passwd)) {
    throw new Exception("Invalid Password for " + name);
}

session.setAttribute("username", name);
Cookie usernameCookie = new Cookie("username", name);
usernameCookie.setMaxAge(300*24*60*60);
response.addCookie(usernameCookie);

%>

<html>
<head>
<title>VerifyEntrant</title>
<body>
<jsp:forward page="<%= forwardPage %>" />
</body>
</html>

