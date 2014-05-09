<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.entities.*" %>
<%@ page import="com.talient.football.jdbc.JDBCEntrant" %>

<%
String name = (String)session.getAttribute("username");
if (name == null) {
    throw new Exception("You must Sign-In before you can modify your profile");
}
Alias alias = JDBCEntrant.findAliasByUsername(name);
if (alias == null) {
    throw new Exception("Unknown Name: " + name);
}
Entrant entrant = alias.getEntrant();
String active = entrant.getActive()?"":"checked";
String entry = entrant.getWeeklyEntry()?"checked":"";
String recap = entrant.getGameOrderedRecap()?"checked":"";
String results = entrant.getWeeklyResult()?"checked":"";
String standings = entrant.getStandings()?"checked":"";
String early = entrant.getNotifyEarly()?"checked":"";
String medium = entrant.getNotifyMedium()?"checked":"";
String late = entrant.getNotifyLate()?"checked":"";
String email = entrant.getContactEmail();
if (email == null) { email = ""; }
String password = alias.getPassword();
if (password == null) { password = ""; }
%>

<html>
<head>
<title><%= Properties.getProperty("football.pool.name") %> Member Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<script language="JavaScript">

function noemailClick() {
    if (document.Profile.noemail.checked == true) {
        document.Profile.entry.checked = false;
        document.Profile.recap.checked = false;
        document.Profile.results.checked = false;
        document.Profile.standings.checked = false;
        document.Profile.late.checked = false;
        document.Profile.medium.checked = false;
        document.Profile.early.checked = false;
    }
}

function emailClick(box) {
    if (box.checked == true) {
        document.Profile.noemail.checked = false;
    }
}
</script>

<body bgcolor="#FFFFCC" text="#006666">
<p align="center"><font face="Arial, Helvetica, sans-serif"><b><font size="5">
<%= Properties.getProperty("football.pool.name") %> Member Services<br>
  <br>
  <font size="3">If you are not <%= name %> <a href="SignIn.jsp?target=MemberServices.jsp">click here</a> to Sign-In</font></font></b></font></p>
<form method="POST" action="<%= response.encodeURL("UpdateEntrant.jsp") %>" name="Profile">
  <table width="200" border="0" align="center" cellspacing="0">
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Name:</font></b></div>
      </td>
      <td nowrap>&nbsp;</td>
      <td nowrap> <font face="Arial, Helvetica, sans-serif" size="3"><%= name %></font></td>
    </tr>
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Password:</font></b></div>
      </td>
      <td nowrap>&nbsp;</td>
      <td nowrap> 
        <input type="hidden" name="name" value="<%= name %>" >
        <input type="text" name="password" value="<%= password %>" >
      </td>
    </tr>
    <tr> 
      <td nowrap> 
        <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">Email:</font></b></div>
      </td>
      <td nowrap>&nbsp;</td>
      <td nowrap> 
        <input type="text" name="email" size="50" value="<%= email %>" >
      </td>
    </tr>
    <tr> 
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="noemail" value="1" <%= active %> onClick="noemailClick()" >
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
        <input type="checkbox" name="entry" value="1" <%= entry %>
         onClick="emailClick(document.Profile.entry)" >
        Entry</font></td>
      <td nowrap width="30">&nbsp;</td>
      <td nowrap> <font face="Arial, Helvetica, sans-serif" size="3">Weekly
        entry when games have been selected.</font>
        <font face="Arial, Helvetica, sans-serif" size="1">(<a href="/2002/email0201.txt">example</a>)</font>
      </td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="recap" value="1" <%= recap %> onClick="emailClick(document.Profile.recap)" >
        Recap </font></td>
      <td nowrap>&nbsp;</td>
      <td nowrap> <font face="Arial, Helvetica, sans-serif" size="3">Recap of 
        everyone's picks before the games are played.</font>
        <font face="Arial, Helvetica, sans-serif" size="1">(<a href="/2002/recap0201.txt">example</a>)</font>
      </td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="results" value="1" <%= results %> onClick="emailClick(document.Profile.results)" >
        Results</font>
      </td>
      <td nowrap>&nbsp;</td>
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3">Results for 
        the week after all the games have been played.</font>
        <font face="Arial, Helvetica, sans-serif" size="1">(<a href="/2002/results0201.txt">example</a>)</font>
      </td>
    </tr>
    <tr> 
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3"> 
        <input type="checkbox" name="standings" value="1" <%= standings %> onClick="emailClick(document.Profile.standings)" >
        Standings</font></td>
      <td nowrap>&nbsp;</td>
      <td nowrap><font face="Arial, Helvetica, sans-serif" size="3">Year to
        date standings after the current week has been scored.</font>
        <font face="Arial, Helvetica, sans-serif" size="1">(<a href="/2002/standings0201.txt">example</a>)</font>
      </td>
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
    <tr> 
      <td nowrap colspan="3"> 
        <br>
        <div align="center"> 
          <input type="submit" name="Submit" value="Update">
        </div>
      </td>
    </tr>
    <tr> 
      <td colspan="3"><font face="Arial, Helvetica, sans-serif" size="3">
        <br><b>Wondering why you are not receiving <%= Properties.getProperty("football.pool.name") %> emails?</b>  Some ISPs may block <%= Properties.getProperty("football.pool.name") %> messages.  Look for a confirmation message to your address if you update your information and have any emails enabled.
        </font>
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
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="+1"><a href="/">
      <%= Properties.getProperty("football.pool.name") %> 
        Home Page</a></font></div>
    </td>
  </tr>
</table>
<p>&nbsp;</p>
</body>
</html>
