<%@ page contentType="text/html" errorPage="Error.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.talient.util.Properties" %>
<%@ page import="com.talient.football.entities.WeeklySchedule" %>

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
    session.setMaxInactiveInterval(60*60*12);
}

ArrayList years = new ArrayList();
Calendar now = Calendar.getInstance();
int year = now.get(Calendar.YEAR);
int findYears = 2;
while (findYears > 0) {
    WeeklySchedule schedule = WeeklySchedule.getHome().findByYearWeek(year, 1);
    if (schedule.size() > 0) {
        years.add(new Integer(year));
    }
    else {
        findYears--;
    }
    year--;
    if (year < 1980) {
        break;
    }
}
int weeks = 16;
if (Properties.getProperty("football.pool.name", "CFPOOL").equals("CFPOOL")) {
    weeks = 16;
}
else {
    weeks = 17;
}

%>

<HTML>
<HEAD>
<TITLE>
<%= Properties.getProperty("football.pool.name") %> Administration
</TITLE>

<script language="JavaScript">
function adminSubmit(form) {
    form.year.value = document.Common.year.value; 
    form.week.value = document.Common.week.value; 
    form.password.value = document.Common.password.value; 
    form.submit();
}
</script>

<BODY text=#006666 bgColor=#ffffcc>
<P align=center><FONT face="Arial, Helvetica, sans-serif" size=5><B>
<%= Properties.getProperty("football.pool.name") %> Administration </FONT></B>
</P>
<form name="Common" method="post" action="WeeklyAdmin.jsp">
<% if (! authorized) { %>
  <font face="Arial, Helvetica, sans-serif" size="3">
  <div align="center"><b>Admin Password: 
    <input type="password" name="password">
    <br>
    <br>
    </b></div>
  </font>
<% } %>
  <div align="center">
<br>
<table cellspacing=0 width=200 align=center border=0>
      <tbody> 
      <tr> 
        <td noWrap width="1%"><b><font face="Arial, Helvetica, sans-serif" size="3">Season: 
          <select name=year>
<% for (int i=0; i<years.size(); i++) {
       int yr = ((Integer)years.get(i)).intValue();
%>
            <option value=<%= yr %> ><%= yr %></option>
<% } %>
          </select>
          </font></b></td>
        <td noWrap width=20>&nbsp;</td>
        <td noWrap width="1%"> 
          <div align=right><b><font face="Arial, Helvetica, sans-serif" size="3">Week: 
            <select name=week>
<% for (int i=weeks; i>0; i--) { %>
              <option value=<%= i %> ><%= i %></option>
<% } %>
            </select>
            </font></b></div>
        </td>
        <td>&nbsp;&nbsp;&nbsp;</td>
        <td>
          <input type=submit value="Weekly Admin" name=Submit>
        </td>
      </tr>
      </tbody> 
    </table>
  </div>
</form>
<DIV align=center>
<br>
</DIV>
<TABLE cellSpacing=4 width="80%" align=center border=0>
  <TBODY>
  <TR>
    <TD colSpan=2>
      <DIV align=center><FONT face="Arial, Helvetica, sans-serif" size=+1><A 
      href="javascript:%20history.go(-1)">Go Back</A></FONT></DIV></TD></TR>
  <TR>
    <TD colSpan=2>
      <DIV align=center><FONT face="Arial, Helvetica, sans-serif" size=+1><A 
      href="/"><%= Properties.getProperty("football.pool.name") %> Home Page</A></FONT></DIV>
    </TD></TR></TBODY></TABLE>
<P>&nbsp;</P></BODY></HTML>
