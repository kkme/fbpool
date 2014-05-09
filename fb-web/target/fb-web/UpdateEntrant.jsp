<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.entities.*" %>
<%@ page import="com.talient.football.jdbc.JDBCEntrant" %>
<%@ page import="com.talient.football.email.SendMail" %>

<%
String name = (String)session.getAttribute("username");
if (name == null) {
    throw new Exception("You must Sign-In before you can modify your profile");
}
name = request.getParameter("name");
if (name == null || name.length() == 0) {
    throw new Exception("Name was not entered");
}
String password = request.getParameter("password");
if (password == null || password.length() == 0) {
    throw new Exception("Password was not entered");
}
String email = request.getParameter("email");
if (email == null) {
    throw new Exception("Invalid email value");
}
Alias alias = JDBCEntrant.findAliasByUsername(name);
if (alias == null) {
    throw new Exception("Unknown Name: " + name);
}
alias.setPassword(password);
Entrant entrant = alias.getEntrant();
entrant.setContactEmail(email);
if (request.getParameter("noemail") == null) { entrant.setActive(true); }
else { entrant.setActive(false); }
if (request.getParameter("entry") == null) { entrant.setWeeklyEntry(false); }
else { entrant.setWeeklyEntry(true); }
if (request.getParameter("recap") == null) { entrant.setGameOrderedRecap(false); }
else { entrant.setGameOrderedRecap(true); }
if (request.getParameter("results") == null) { entrant.setWeeklyResult(false); }
else { entrant.setWeeklyResult(true); }
if (request.getParameter("standings") == null) { entrant.setStandings(false); }
else { entrant.setStandings(true); }
if (request.getParameter("late") == null) { entrant.setNotifyLate(false); }
else { entrant.setNotifyLate(true); }
if (request.getParameter("medium") == null) { entrant.setNotifyMedium(false); }
else { entrant.setNotifyMedium(true); }
if (request.getParameter("early") == null) { entrant.setNotifyEarly(false); }
else { entrant.setNotifyEarly(true); }

if (JDBCEntrant.store(entrant, alias) < 1) {
    throw new Exception("Could not store user profile for " + name);
}
try {
    if (entrant.getActive() && email.indexOf("@") > 0) {
        SendMail sm = new SendMail("", "");
        sm.emailCreatedUpdated(entrant, "Updated");
    }
}
catch (Exception e) {
    // Do not generate an error page if email is not working.
}

String active = entrant.getActive()?"":"checked";
String entry = entrant.getWeeklyEntry()?"checked":"";
String recap = entrant.getGameOrderedRecap()?"checked":"";
String results = entrant.getWeeklyResult()?"checked":"";
String standings = entrant.getStandings()?"checked":"";
String early = entrant.getNotifyEarly()?"checked":"";
String medium = entrant.getNotifyMedium()?"checked":"";
String late = entrant.getNotifyLate()?"checked":"";
%>

<html>
<head>
<title><%= Properties.getProperty("football.pool.name") %> Profile Updated</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<script language="JavaScript">
function emailClick(box) {
    if (box.checked == true) {
        box.checked = false;
    }
    else {
        box.checked = true;
    }
}
</script>

<body bgcolor="#FFFFCC" text="#006666">
<p align="center"><font face="Arial, Helvetica, sans-serif"><b><font size="5">
<%= name %> Profile Updated<br>
  <br><font size="3">
<%
if (entrant.getActive() && email.indexOf("@") > 0) {
    out.print("An email verifying your updated profile has been sent.");
}
%></font></font></b></font></p>
<form method="POST" action="UpdateProfile.jsp" name="Profile">
  <table width="200" border="0" align="center" cellspacing="0">
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Name:</font></b></div>
      </td>
      <td nowrap>&nbsp;</td>
      <td nowrap> <font face="Arial, Helvetica, sans-serif" size="3">
        <%= name %></font>
      </td>
    </tr>
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Password:</font></b></div>
      </td>
      <td nowrap>&nbsp;</td>
      <td nowrap> <font face="Arial, Helvetica, sans-serif" size="3"><%= password %></font></td>
    </tr>
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Email:</font></b></div>
      </td>
      <td nowrap>&nbsp;</td>
      <td nowrap> <font face="Arial, Helvetica, sans-serif" size="3"><%= email %></font></td>
    </tr>
    <tr> 
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="noemail" value="1" <%= active %> onClick="emailClick(document.Profile.noemail)" >
        No email</font></td>
      <td nowrap width="30">&nbsp;</td>
      <td nowrap> <font face="Arial, Helvetica, sans-serif" size="3">
        Turn off all email notifications.</font></td>
    </tr>
    <tr> 
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
    </tr>
    <tr> 
      <td nowrap colspan="3" bgcolor="#00FFFF"> 
        <div align="center"><font size="3" face="Arial, Helvetica, sans-serif"><b><font size="4">Email 
          Reports</font></b></font></div>
      </td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="entry" value="1" <%= entry %> onClick="emailClick(document.Profile.entry)" >
        Entry</font></td>
      <td nowrap width="30">&nbsp;</td>
      <td nowrap> <font face="Arial, Helvetica, sans-serif" size="3">Weekly entry 
        when games have been selected.</font></td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="recap" value="1" <%= recap %> onClick="emailClick(document.Profile.recap)" >
        Recap </font></td>
      <td nowrap>&nbsp;</td>
      <td nowrap> <font face="Arial, Helvetica, sans-serif" size="3">Recap of 
        everyone's picks before the games are played.</font></td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="results" value="1" <%= results %> onClick="emailClick(document.Profile.results)" >
        Results</font></td>
      <td nowrap>&nbsp;</td>
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3">Results for 
        the week after all the games have been played.</font></td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="standings" value="1" <%= standings %> onClick="emailClick(document.Profile.standings)" >
        Standings</font></td>
      <td nowrap>&nbsp;</td>
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3">Year to date 
        standings after the current week has been scored.</font></td>
    </tr>
    <tr>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
    </tr>
    <tr> 
      <td nowrap colspan="3" bgcolor="#00FFFF"> 
        <div align="center"><font size="3" face="Arial, Helvetica, sans-serif"><b><font size="4">Reminder 
          Notices</font></b></font></div>
      </td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="late" value="1" <%= late %> onClick="emailClick(document.Profile.late)" >
        6 hours</font></td>
      <td rowspan="3">&nbsp;</td>
      <td rowspan="3"><font face="Arial, Helvetica, sans-serif" size="3">
        Send a reminder notice if no entry has been received from you this far in advance of the entry deadline.</font></td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="medium" value="1" <%= medium %> onClick="emailClick(document.Profile.medium)" >
        24 hours</font></td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="early" value="1" <%= early %> onClick="emailClick(document.Profile.early)" >
        72 hours</font></td>
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
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+1"><a href="/">
      <%= Properties.getProperty("football.pool.name") %> 
        Home Page</a></font></div>
    </td>
  </tr>
</table>
<p>&nbsp;</p>
</body>
</html>
