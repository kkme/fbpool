<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.entities.*" %>
<%@ page import="com.talient.football.jdbc.JDBCEntrant" %>

<%
String username = (String)session.getAttribute("username");
if (username == null) {
    username = "";
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
        for (int i=0; i<cookies.length; i++) {
            if (cookies[i].getName().equals("username")) {
                username = cookies[i].getValue();
                if (username == null) { username = ""; }
                if (username.length() > 1 &&
                    username.startsWith("\"") && username.endsWith("\"")) {
                    username = username.substring(1, username.length() - 1);
                }
                break;
            }
        }
    }
}
%>
<html>
<head>
<title><%= Properties.getProperty("football.pool.name") %> Contact Us</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFCC" text="#006666" link="#006666" vlink="#006666" alink="#006666">
<p align="center"><font size=7><b><i><font face="Times New Roman, Times, serif"><%= Properties.getProperty("football.pool.name") %></font></i></b></font><br>
<p align="center"><font face="Arial, Helvetica, sans-serif"><b><font size="4">Contact 
  Us </font></b></font></p>
<table width="75%" border="0" align="center">
  <tr bordercolor="#006666" bgcolor="#3300FF"> 
    <td colspan="2" bgcolor="#006666"> 
      <div align="center"><font face="Arial, Helvetica, sans-serif" size="3"><b><font color="#FFFFFF">E-MAIL</font></b></font></div>
    </td>
  </tr>
  <tr> 
    <td colspan="2"> 
      <div align="center"> 
        <p><font face="Arial, Helvetica, sans-serif"><font size="4">This page 
          is provided as an alternative to e-mailing us via the normal address.
          Due to the large volume of spam messages, we are unable wade through 
          the inbox to find valid emails. Use of this page is currently
          the best and quickest way of contacting us.</font></font></p>
        <form method="POST" action="<%= response.encodeURL("SendEmail.jsp") %>" name="ContactUs">
          <table width="100%" border="0">
            <tr> 
              <td width="30%" nowrap> 
                <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">* 
                  Moniker:</font></b></div>
              </td>
              <td width="50%"><font size="3"> 
                <input type="text" name="realname" value="<%= username %>" size="40">
                </font></td>
            </tr>
            <tr> 
              <td width="30%" nowrap> 
                <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">* 
                  E-Mail Address:</font></b></div>
              </td>
              <td width="50%"><font size="3"> 
                <input type="text" name="email" size="40">
                </font></td>
            </tr>
            <tr> 
              <td width="30%" nowrap> 
                <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="3">* 
                  Subject:</font></b></div>
              </td>
              <td width="50%"><font size="3"> 
                <input type="text" name="subject" size="40">
                </font></td>
            </tr>
            <tr> 
              <td width="30%" valign="top" nowrap> 
                <div align="right"> 
                  <p><b><font face="Arial, Helvetica, sans-serif" size="3">Message:</font></b></p>
                  <p>&nbsp;</p>
                  <p><font size="3"> 
                    <input type="hidden" name="redirect" value="emailThankYou.htm">
                    <input type="hidden" name="required" value="realname,email,subject">
                    </font></p>
                </div>
              </td>
              <td width="50%"><font size="3"> 
                <textarea name="comments" cols="40" rows="8"></textarea>
                </font></td>
            </tr>
            <tr> 
              <td width="30%" valign="top" nowrap>&nbsp;</td>
              <td width="50%"> 
                <div align="left"><font size="3"> 
                  <input type="submit" name="Submit" value="Send E-mail">
                  </font></div>
              </td>
            </tr>
            <tr> 
              <td colspan="2" nowrap> 
                <div align="center"><font size="2" face="Arial, Helvetica, sans-serif"><b>* 
                  Required Fields</b></font></div>
              </td>
            </tr>
          </table>
        </form>
      </div>
    </td>
  </tr>
</table>
</body>
</html>
