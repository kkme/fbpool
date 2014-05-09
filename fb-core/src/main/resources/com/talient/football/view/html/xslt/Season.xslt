<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/Season">
<html>
<style>
  body { font-family: Arial, Helvetica, "sans-serif"; }
  TD.ac { font-size: x-small; font-weight: bold; color: black; width: 1%; word-wrap: normal; text-align: center }
  TD.wc { font-size: x-small; font-weight: normal; color: black; width: 1%; word-wrap: normal; text-align: center }
  TD.wac { font-size: x-small; font-weight: bold; color: green; width: 1%; word-wrap: normal; text-align: center }
  TD.wwc { font-size: x-small; font-weight: normal; color: green; width: 1%; word-wrap: normal; text-align: center }
  TD.lac { font-size: x-small; font-weight: bold; color: red; width: 1%; word-wrap: normal; text-align: center }
  TD.lwc { font-size: x-small; font-weight: normal; color: red; width: 1%; word-wrap: normal; text-align: center }
  TD.tac { font-size: x-small; font-weight: bold; color: blue; width: 1%; word-wrap: normal; text-align: center }
  TD.twc { font-size: x-small; font-weight: normal; color: blue; width: 1%; word-wrap: normal; text-align: center }
  TD.pos { font-size: x-small; font-weight: normal; color: black; width: 1%; word-wrap: normal; text-align: center }
  TD.name { font-size: x-small; font-weight: normal; color: black; width: 1%; text-align: right }
  TD.header { font-size: small; font-weight: bold; color: #006666; width: 1%; word-wrap: normal; text-align: center }
  TD.column { font-size: x-small; font-weight: bold; color: #006666; width: 1%; word-wrap: normal; text-align: center }
  TD.score { font-size: x-small; font-weight: normal; color: black; width: 1%; word-wrap: normal; text-align: right }
</style>
<HEAD><TITLE><xsl:value-of select="Year"/>&#160;<xsl:value-of select="Title"/></TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1"></META>
</HEAD>

<body bgcolor="#FFFFCC" text="#000000" link="#000000" vlink="#000000" alink="#000000">
<p align="center"><font face="Arial, Helvetica, sans-serif" size="5"><xsl:value-of select="Year"/>&#160;<xsl:value-of select="Title"/></font><br/>
<b><font face="Arial, Helvetica, sans-serif" size="4">
<xsl:apply-templates select="Leader"/>
<xsl:apply-templates select="Champion"/>
</font></b>
</p>

<table width="1%" border="1" align="center" bordercolor="#0000FF">
  <tr>
    <td>
      <table align="center" width="100%" height="100%" cellpadding="3" cellspacing="0" border="0">
        <tr> 
          <td width="6%" nowrap="true" colspan="8"> 
            <div align="center"><b><font  size="3"><a><xsl:attribute name="href">ytd<xsl:value-of select="Year"/>.htm</xsl:attribute>Year To Date</a></font></b></div>
          </td>
        </tr>
<xsl:apply-templates select="Week"/>
      </table>
    </td>
  </tr>
</table>
<br/>
<br/>
<table border="0" align="center" cellspacing="4">
  <tr nowrap="true"> 
    <td colspan="3"> 
      <div align="center"><font  size="+1"><a href="javascript: history.go(-1)">Go 
        Back</a></font></div>
    </td>
  </tr>
  <tr nowrap="true"> 
    <td colspan="3"> 
      <div align="center"><font size="+1"><a href="/index.htm"><xsl:value-of select="Title"/> Home Page</a></font></div>
    </td>
  </tr>
</table>
<p align="center">&#160;</p>
</body>
</html>
</xsl:template>

<xsl:template match="Leader">
  <xsl:if test="(position() = 1)">
    Leader Through Week <xsl:value-of select="Week"/><br/>
  </xsl:if>
    <xsl:value-of select="Name"/><br/>
</xsl:template>

<xsl:template match="Champion">
  <xsl:if test="(position() = 1)">
    Season Champion<br/>
  </xsl:if>
    <xsl:value-of select="Name"/><br/>
</xsl:template>

<xsl:template match="Week">
  <tr>
    <xsl:if test="(position() mod 2 = 1)">
      <xsl:attribute name="bgcolor">#FFFFFF</xsl:attribute>
    </xsl:if>
    <xsl:if test="(position() mod 2 = 0)">
      <xsl:attribute name="bgcolor">#CCCCCC</xsl:attribute>
    </xsl:if>
    <td width="1%" nowrap="true"> 
      <div align="center"><font size="2" color="#000000"><b>&#160;<a><xsl:attribute name="href">/fb-web/EntryForm.jsp?year=<xsl:value-of select="../Year"/>&amp;week=<xsl:value-of select="WeekNum"/></xsl:attribute><xsl:value-of select="Label"/></a>&#160;</b></font></div>
    </td>
    <xsl:if test="@type = 'CompleteWeek'">
      <td width="1%" nowrap="true"> 
        <div align="center"><font size="2" color="#000000">&#160;<a><xsl:attribute name="href">crosstable<xsl:value-of select="YearWeek"/>.htm</xsl:attribute>Crosstable</a>&#160;</font></div>
      </td>
      <td nowrap="true"> 
        <div align="center"><font size="2" color="#000000">&#160;<a><xsl:attribute name="href">weeklystats<xsl:value-of select="YearWeek"/>.htm</xsl:attribute>Weekly Stats</a>&#160;</font></div>
      </td>
      <td width="1%" nowrap="true"> 
        <div align="center"><font size="2" color="#000000">&#160;<a><xsl:attribute name="href">ytd<xsl:value-of select="YearWeek"/>.htm</xsl:attribute>Year To Date</a>&#160;</font></div>
      </td>
      <td width="1%" nowrap="true"> 
        <div align="center"><font color="#000000" size="2">&#160;<a><xsl:attribute name="href">recap<xsl:value-of select="YearWeek"/>.txt</xsl:attribute>Recap</a>&#160;</font></div>
      </td>
      <td nowrap="true"> 
        <div align="center"><font size="2" color="#000000">&#160;<a><xsl:attribute name="href">results<xsl:value-of select="YearWeek"/>.txt</xsl:attribute>Results</a>&#160;</font></div>
      </td>
      <td nowrap="true"> 
        <div align="center"><font size="2" color="#000000">&#160;<a><xsl:attribute name="href">standings<xsl:value-of select="YearWeek"/>.txt</xsl:attribute>Standings</a>&#160;</font></div>
      </td>
      <td nowrap="true"> 
        <div align="center"><font size="2" color="#000000"><xsl:apply-templates select="Winner"/></font></div>
      </td>
    </xsl:if>
    <xsl:if test="@type = 'WeekInProgress'">
      <td width="1%" nowrap="true"> 
        <div align="center"><font size="2" color="#000000">&#160;<a><xsl:attribute name="href">crosstable<xsl:value-of select="YearWeek"/>.htm</xsl:attribute>Crosstable</a>&#160;</font></div>
      </td>
      <td nowrap="true"> 
        <div align="center"><font size="2" color="#000000">&#160;<a><xsl:attribute name="href">weeklystats<xsl:value-of select="YearWeek"/>.htm</xsl:attribute>Weekly Stats</a>&#160;</font></div>
      </td>
      <td width="1%" nowrap="true">&#160;</td>
      <td width="1%" nowrap="true"> 
        <div align="center"><font color="#000000" size="2">&#160;<a><xsl:attribute name="href">recap<xsl:value-of select="YearWeek"/>.txt</xsl:attribute>Recap</a>&#160;</font></div>
      </td>
      <td nowrap="true"> 
        <div align="center"><font size="2" color="#000000">&#160;<a><xsl:attribute name="href">results<xsl:value-of select="YearWeek"/>.txt</xsl:attribute>Results</a>&#160;</font></div>
      </td>
      <td width="1%" nowrap="true">&#160;</td>
      <td nowrap="true"> 
        <div align="center"><font size="2" color="#000000"><xsl:apply-templates select="Winner"/></font></div>
      </td>
    </xsl:if>
    <xsl:if test="@type = 'FutureWeek'">
      <td align="center" width="1%" nowrap="true" colspan="6"><font size="2" color="#000000">Entry due by <xsl:value-of select="Due"/> (Mountain Time)</font></td>
      <td>&#160;</td>
    </xsl:if>
  </tr>
</xsl:template>

<xsl:template match="Winner">
&#160;<b><xsl:value-of select="."/></b>&#160;<xsl:if test="(position() > 0)"><br/></xsl:if>
</xsl:template>

</xsl:stylesheet>
