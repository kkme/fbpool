<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/YearToDate">
<html>
<style>
  body { font-family: Arial, Helvetica, "sans-serif"; font-size: x-small }
  TD.title { font-size: x-small; font-weight: bold; color: #006666; width: 1%; text-align: center }
  TD.right { font-size: x-small; font-weight: normal; color: black; width: 1%; text-align: right; word-wrap: normal }
  TD.center { font-size: x-small; font-weight: normal; color: black; width: 1%; text-align: center }
  TD.sep { font-size: x-small; font-weight: normal; color: black; width: 5; text-align: center; background-color: #999999 }
  TD.desc { font-size: x-small; font-weight: bold; width: 5%; text-align: center; color: #006666 }
  TD.awardname { font-size: x-small; width: 1%; text-align: left }
  TD.award { font-size: x-small; width: 1%; text-align: right }
</style>
<head>
  <title><xsl:value-of select="Title"/> Year To Date</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
</head>
<body bgcolor="#FFFFCC" text="black">
  <p align="center"><b><font size="+2" color="#006666">
    <xsl:value-of select="Title"/>&#160;<xsl:value-of select="Year"/>&#160;<xsl:value-of select="WeekName"/><br/>
    Year To Date</font></b><br/>
  </p>
  <TABLE borderColor="#0000ff" width="1%" align="center" border="1">
    <TBODY>
    <TR>
      <TD>
        <TABLE height="100%" cellSpacing="0" cellPadding="3" width="100%" align="center" border="0">
          <TBODY>
          <TR>
            <TD noWrap="true" width="6%" colSpan="16"><DIV align="center"><B><FONT size="3" color="#006666">
              Year To Date Scores</FONT></B></DIV>
            </TD>
          </TR>
          <TR bgcolor="#00cccc">
            <TD class="title">&#160;</TD>
            <TD class="title">Name</TD>
            <TD width="5" bgColor="#009999"></TD>
            <TD class="title">Weeks<BR/>Played</TD>
            <TD class="title">&#160;Weeks&#160;<BR/>First</TD>
            <xsl:if test="//Title='NFL Pool'">
              <TD class="title">&#160;Weeks&#160;<BR/>Last</TD>
            </xsl:if>
            <TD width="5" bgColor="#009999"></TD>
            <TD class="title">&#160;Wins&#160;</TD>
            <TD class="title">&#160;Losses&#160;</TD>
            <xsl:if test="//Title='NFL Pool'">
              <TD class="title">&#160;Ties&#160;</TD>
            </xsl:if>
            <TD width="5" bgColor="#009999"></TD>
            <TD class="title">&#160;Total&#160;<BR/>Pts</TD>
            <TD class="title">&#160;Worst&#160;<BR/>Week</TD>
            <TD class="title">2nd<BR/>&#160;Worst&#160;</TD>
            <TD class="title">Adj.<BR/>&#160;Total&#160;</TD>
          </TR>
          <xsl:apply-templates select="Entrant"/>
          </TBODY>
        </TABLE>
      </TD>
    </TR>
    </TBODY>
  </TABLE>
  <br/>

<xsl:if test="//Title='NFL Pool'">
<table width="50%" border="1" align="center" bordercolor="#0000FF">
  <tr> 
    <td> 
      <table align="center" width="100%" height="100%" cellpadding="3" cellspacing="0" border="0">
        <tr> 
          <td width="6%" colspan="3"> 
            <div align="center"><b><font size="3" color="#006666">
              Individual Awards</font></b>
            </div>
          </td>
        </tr>
        <tr bgcolor="#00cccc"> 
          <td class="desc" colspan="3">Highest weekly score for the year</td>
        </tr>
        <xsl:apply-templates select="HighestWeeklyScore"/>
        <tr bgcolor="#00cccc"> 
          <td class="desc" colspan="3">Biggest winning margin of the year</td>
        </tr>
        <xsl:apply-templates select="BiggestWinningMargin"/>
        <tr bgcolor="#00cccc"> 
          <td class="desc" colspan="3">Closest runnerup score for the year</td>
        </tr>
        <xsl:apply-templates select="ClosestRunnerup"/>
        <tr bgcolor="#00cccc"> 
          <td class="desc" colspan="3">Lowest weekly score for the year</td>
        </tr>
        <xsl:apply-templates select="LowestWeeklyScore"/>
        <tr bgcolor="#00cccc"> 
          <td class="desc" colspan="3">Lowest weekly score for the year that did not earn a free entry the following week</td>
        </tr>
        <xsl:apply-templates select="LowestScoreWithoutFreeEntry"/>
        <xsl:apply-templates select="HighestAdjPointsWithoutWin"/>
        <tr bgcolor="#00cccc"> 
          <td class="desc" colspan="3">Highest season point total without dropping any individual weekly scores</td>
        </tr>
        <xsl:apply-templates select="HighestGrossPoints"/>
        <xsl:apply-templates select="LowestPointTotal"/>
        <xsl:apply-templates select="FourthPlace"/>
        <tr bgcolor="#00cccc"> 
          <td class="desc" colspan="3">Random Entry Selection</td>
        </tr>
        <xsl:apply-templates select="RandomEntry"/>
      </table>
    </td>
  </tr>
</table>
<br/>
</xsl:if>

  <div align="center"><font size="+1"><a href="javascript: history.go(-1)">Go Back</a><br/>
  <a><xsl:attribute name="href"><xsl:value-of select="Url"/></xsl:attribute>
  <xsl:value-of select="Title"/> Home Page</a></font></div>
</body>
</html>
</xsl:template>

<xsl:template match="Entrant">
  <tr> 
    <xsl:if test="(position() mod 2 = 1)">
      <xsl:attribute name="bgcolor">#FFFFFF</xsl:attribute>
    </xsl:if>
    <xsl:if test="(position() mod 2 = 0)">
      <xsl:attribute name="bgcolor">#CCCCCC</xsl:attribute>
    </xsl:if>
    <td class="center"><xsl:value-of select="Rank"/></td>
    <td nowrap="true" class="right"><xsl:value-of select="Name"/></td>
    <td class="sep">&#160;</td>
    <td class="center"><xsl:value-of select="WeeksPlayed"/></td>
    <td class="center"><xsl:value-of select="WeeksWon"/></td>
    <xsl:if test="//Title='NFL Pool'">
        <td class="center"><xsl:value-of select="WeeksLast"/></td>
    </xsl:if>
    <td class="sep">&#160;</td>
    <td class="center"><xsl:value-of select="Wins"/></td>
    <td class="center"><xsl:value-of select="Losses"/></td>
    <xsl:if test="//Title='NFL Pool'">
      <td class="center"><xsl:value-of select="Ties"/></td>
    </xsl:if>
    <td class="sep">&#160;</td>
    <td class="right">&#160;<xsl:value-of select="TotalPts"/></td>
    <td class="right"><xsl:value-of select="WorstWeek"/></td>
    <td class="right"><xsl:value-of select="SecondWorst"/></td>
    <td class="right"><b>&#160;<xsl:value-of select="AdjTotal"/></b></td>
  </tr>
</xsl:template>

<xsl:template match="HighestWeeklyScore">
        <tr bgcolor="#FFFFFF"> 
          <td class="awardname" nowrap="true"><xsl:value-of select="Name"/></td>
          <td class="award" nowrap="true">Week <xsl:value-of select="Week"/></td>
          <td class="award"><xsl:value-of select="Score"/></td>
        </tr>
</xsl:template>

<xsl:template match="BiggestWinningMargin">
        <tr bgcolor="#FFFFFF"> 
          <td class="awardname" nowrap="true"><xsl:value-of select="Name"/></td>
          <td class="award" nowrap="true">Week <xsl:value-of select="Week"/></td>
          <td class="award"><xsl:value-of select="Score"/></td>
        </tr>
</xsl:template>

<xsl:template match="ClosestRunnerup">
        <tr bgcolor="#FFFFFF"> 
          <td class="awardname" nowrap="true"><xsl:value-of select="Name"/></td>
          <td class="award" nowrap="true">Week <xsl:value-of select="Week"/></td>
          <td class="award"><xsl:value-of select="Score"/></td>
        </tr>
</xsl:template>

<xsl:template match="LowestWeeklyScore">
        <tr bgcolor="#FFFFFF"> 
          <td class="awardname" nowrap="true"><xsl:value-of select="Name"/></td>
          <td class="award" nowrap="true">Week <xsl:value-of select="Week"/></td>
          <td class="award"><xsl:value-of select="Score"/></td>
        </tr>
</xsl:template>

<xsl:template match="LowestScoreWithoutFreeEntry">
        <tr bgcolor="#FFFFFF"> 
          <td class="awardname" nowrap="true"><xsl:value-of select="Name"/></td>
          <td class="award" nowrap="true">Week <xsl:value-of select="Week"/></td>
          <td class="award"><xsl:value-of select="Score"/></td>
        </tr>
</xsl:template>

<xsl:template match="HighestAdjPointsWithoutWin">
    <xsl:if test="(position() = 1)">
        <tr bgcolor="#00cccc"> 
          <td class="desc" colspan="3">Highest adjusted point total among those who did not win an individual week</td>
        </tr>
    </xsl:if>
        <tr bgcolor="#FFFFFF"> 
          <td class="awardname" nowrap="true"><xsl:value-of select="Name"/></td>
          <td class="award">&#160;</td>
          <td class="award"><xsl:value-of select="Score"/></td>
        </tr>
</xsl:template>

<xsl:template match="HighestGrossPoints">
        <tr bgcolor="#FFFFFF"> 
          <td class="awardname" nowrap="true"><xsl:value-of select="Name"/></td>
          <td class="award">&#160;</td>
          <td class="award"><xsl:value-of select="Score"/></td>
        </tr>
</xsl:template>

<xsl:template match="LowestPointTotal">
    <xsl:if test="(position() = 1)">
        <tr bgcolor="#00cccc"> 
          <td class="desc" colspan="3">Lowest adjusted point total that played a minimum of 15 weeks</td>
        </tr>
    </xsl:if>
        <tr bgcolor="#FFFFFF"> 
          <td class="awardname" nowrap="true"><xsl:value-of select="Name"/></td>
          <td class="award">&#160;</td>
          <td class="award"><xsl:value-of select="Score"/></td>
        </tr>
</xsl:template>

<xsl:template match="FourthPlace">
    <xsl:if test="(position() = 1)">
        <tr bgcolor="#00cccc"> 
          <td class="desc" colspan="3">Fourth place in the adjusted season total</td>
        </tr>
    </xsl:if>
        <tr bgcolor="#FFFFFF"> 
          <td class="awardname" nowrap="true"><xsl:value-of select="Name"/></td>
          <td class="award">&#160;</td>
          <td class="award"><xsl:value-of select="Score"/></td>
        </tr>
</xsl:template>

<xsl:template match="RandomEntry">
        <tr bgcolor="#FFFFFF"> 
          <td class="awardname" nowrap="true"><xsl:value-of select="Name"/></td>
          <td class="award" nowrap="true">Week <xsl:value-of select="Week"/></td>
          <td class="award"><xsl:value-of select="Score"/></td>
        </tr>
</xsl:template>

</xsl:stylesheet>
