<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/Crosstable">
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
  TD.score { font-size: x-small; font-weight: normal; color: black; width: 1%; word-wrap: normal; text-align: center }
  TD.weight { font-size: x-small; font-weight: normal; color: #000099; width: 1%; word-wrap: normal; text-align: center }
</style>
<head>
  <title><xsl:value-of select="Title"/> Crosstable for <xsl:value-of select="WeekName"/></title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
</head>
<body bgcolor="#FFFFCC" text="#006666">
  <p align="center"><b><font size="+2"><xsl:value-of select="Title"/>&#160;<xsl:value-of select="Year"/>&#160;<xsl:value-of select="WeekName"/><br/>
    Crosstable</font></b><br/>
    <xsl:if test="//Title='CFPOOL'">
      <xsl:choose>
        <xsl:when test="//Winner=''">
          Top entry does not reflect <xsl:value-of select="Bonus"/> bonus.
        </xsl:when>
        <xsl:otherwise>
          Entry for <b><xsl:value-of select="Winner"/></b> reflects <xsl:value-of select="Bonus"/> bonus.
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </p>
  <table width="100%" border="1" align="center" bordercolor="#0000FF">
    <tr>
      <td>
        <table width="100%" border="0" align="center" cellpadding="3" cellspacing="0" bgcolor="#FFFFCC">
          <xsl:apply-templates select="Weights"/>
          <xsl:apply-templates select="Entry"/>
        </table>
      </td>
    </tr>
  </table>
  <table width="1%" border="0" align="center" cellspacing="4">
    <tr> 
      <td nowrap="true" class="wc">Black&#160;<font color="#006666">-&#160;Pick&#160;Same&#160;As&#160;Consensus</font></td>
      <td nowrap="true" width="20">&#160;</td>
      <td nowrap="true" class="ac">Bold&#160;Black&#160;<font color="#006666">-&#160;Pick&#160;Against&#160;Consensus</font></td>
    </tr>
    <tr> 
      <td nowrap="true" class="wwc">Green&#160;<font color="#006666">-&#160;Win&#160;Same&#160;As&#160;Consensus</font></td>
      <td nowrap="true" width="20">&#160;</td>
      <td nowrap="true" class="wac">Bold&#160;Green&#160;<font color="#006666">-&#160;Win&#160;Against&#160;Consensus</font></td>
    </tr>
    <tr> 
      <td nowrap="true" class="lwc">Red&#160;<font color="#006666">-&#160;Loss&#160;Same&#160;As&#160;Consensus</font></td>
      <td nowrap="true" width="20">&#160;</td>
      <td nowrap="true" class="lac">Bold&#160;Red&#160;<font color="#006666">-&#160;Loss&#160;Against&#160;Consensus</font></td>
    </tr>
<xsl:if test="//Title='NFL Pool'">
    <tr> 
      <td nowrap="true" class="twc">Blue&#160;<font color="#006666">-&#160;Tie&#160;Same&#160;As&#160;Consensus</font></td>
      <td nowrap="true" width="20">&#160;</td>
      <td nowrap="true" class="tac">Bold&#160;Blue&#160;<font color="#006666">-&#160;Tie&#160;Against&#160;Consensus</font></td>
    </tr>
</xsl:if>
  </table>
  <br/>
  <table width="1%" border="1" align="center" bordercolor="#0000FF">
    <tr>
      <td>
        <table width="100%" border="0" align="center" cellpadding="3" cellspacing="0" bgcolor="#FFFFCC">
          <tr bgcolor="#00CCCC"> 
            <td colspan="6" nowrap="true"> 
              <div align="center"><font size="+1" color="#000099"><b>Scoreboard</b></font></div>
            </td>
          </tr>
          <xsl:apply-templates select="Game"/>
        </table>
      </td>
    </tr>
  </table>
  <br/>
  <div align="center"><font size="+1"><a href="javascript: history.go(-1)">Go Back</a><br/>
  <a><xsl:attribute name="href"><xsl:value-of select="Url"/></xsl:attribute>
  <xsl:value-of select="Title"/> Home Page</a></font></div>
</body>
</html>
</xsl:template>

<xsl:template match="Weights">
  <tr bgcolor="#00CCCC"> 
    <td class="weight"> &#160;</td>
    <td class="weight"><b>Name</b></td>
    <xsl:for-each select="Weight">
      <td class="weight"><xsl:value-of select="."/></td>
    </xsl:for-each>
    <td class="weight"><b>Total</b></td>
  </tr>
</xsl:template>

<xsl:template match="Entry">
  <tr> 
    <xsl:if test="(position() mod 2 = 1)">
      <xsl:attribute name="bgcolor">#FFFFFF</xsl:attribute>
    </xsl:if>
    <xsl:if test="(position() mod 2 = 0)">
      <xsl:attribute name="bgcolor">#CCCCCC</xsl:attribute>
    </xsl:if>
    <td class="pos"><xsl:value-of select="Rank"/></td>
    <td class="name" nowrap="true"><xsl:value-of select="Name"/></td>
    <xsl:for-each select="Team">
      <td>
        <xsl:attribute name="class"><xsl:value-of select="State"/></xsl:attribute>
        <xsl:value-of select="TeamName"/>
      </td>
    </xsl:for-each>
    <td><xsl:attribute name="class">score</xsl:attribute><xsl:value-of select="Score"/></td>
  </tr>
</xsl:template>

<xsl:template match="Game">
  <tr> 
    <xsl:if test="(position() mod 2 = 1)">
      <xsl:attribute name="bgcolor">#FFFFFF</xsl:attribute>
    </xsl:if>
    <xsl:if test="(position() mod 2 = 0)">
      <xsl:attribute name="bgcolor">#CCCCCC</xsl:attribute>
    </xsl:if>
    <td width="1%" nowrap="true"> 
      <xsl:if test="//Title='NFL Pool'">
        <div align="left"><font size="-1" color="black"><xsl:value-of select="Time"/> &#160;</font></div>
      </xsl:if>
      <xsl:if test="//Title='CFPOOL'">
        <div align="left"><font size="x-small" color="black">&#160;</font></div>
      </xsl:if>
    </td>
    <td width="1%" nowrap="true"> 
      <xsl:attribute name="class"><xsl:value-of select="Vstate"/></xsl:attribute>
      <xsl:if test="//Title='NFL Pool'">
        <div align="right"><xsl:value-of select="Vscore"/>&#160;
        <xsl:value-of select="Vname"/></div>
      </xsl:if>
      <xsl:if test="//Title='CFPOOL'">
        <div align="right"><xsl:value-of select="Vscore"/>&#160;
        <xsl:value-of select="Vname"/>&#160;(<xsl:value-of select="Vabbrev"/>)</div>
      </xsl:if>
    </td>
    <td width="1%" nowrap="true"> 
      <div align="center"><font size="-1" color="black">&#160;at&#160;</font></div>
    </td>
    <td width="1%" nowrap="true"> 
      <xsl:attribute name="class"><xsl:value-of select="Hstate"/></xsl:attribute>
      <xsl:if test="//Title='NFL Pool'">
        <div align="left"><xsl:value-of select="Hscore"/>&#160;
        <xsl:value-of select="Hname"/></div>
      </xsl:if>
      <xsl:if test="//Title='CFPOOL'">
        <div align="left"><xsl:value-of select="Hscore"/>&#160;
        <xsl:value-of select="Hname"/>&#160;(<xsl:value-of select="Habbrev"/>)</div>
      </xsl:if>
    </td>
    <td width="1%" nowrap="true"> 
      <div align="right"><font size="-1" color="black"> &#160;<xsl:value-of select="State"/></font></div>
    </td>
  </tr>
</xsl:template>

</xsl:stylesheet>
