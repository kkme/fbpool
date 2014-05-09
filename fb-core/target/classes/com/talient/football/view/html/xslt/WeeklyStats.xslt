<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/WeeklyStats">
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
<HEAD><TITLE><xsl:value-of select="Title"/>&#160;<xsl:value-of select="Year"/> Weekly Stats for <xsl:value-of select="WeekName"/></TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1"></META>
</HEAD>
<BODY text="#006666" bgColor="#ffffcc">
<P align="center"><FONT size="5"><xsl:value-of select="Title"/>&#160;<xsl:value-of select="Year"/>&#160;<xsl:value-of select="WeekName"/><BR/>
  Weekly Stats</FONT><BR/>
</P>
<TABLE borderColor="#0000ff" width="1%" align="center" border="1">
  <TBODY>
  <TR>
    <TD> 
      <table width="1%" border="0" cellspacing="0" cellpadding="2" align="center">
        <tr> 
          <td> 
            <table height="100%" cellspacing="0" cellpadding="3" width="100%" align="center" border="0">
            <tbody> 
              <tr> 
                <td class="header" noWrap="true" colspan="7">Favorites</td>
              </tr>
              <tr bgcolor="#00cccc"> 
                <td class="column" noWrap="true">&#160;Picked&#160;</td>
                <td class="column" noWrap="true">&#160;Favorite&#160;</td>
                <td class="column" noWrap="true">&#160;Weight&#160;</td>
                <td class="column" noWrap="true">&#160;Picked&#160;</td>
                <td class="column" noWrap="true">&#160;Underdog&#160;</td>
                <td class="column" noWrap="true">&#160;Weight&#160;</td>
              </tr>
              <xsl:apply-templates select="Favorites"/>
              </tbody> 
            </table>
          </td>
          <td>&#160;</td>
          <td> 
            <table height="100%" cellspacing="0" cellpadding="3" width="100%" align="center" border="0">
              <tbody> 
              <tr> 
                <td class="header" noWrap="true" colspan="2">Consensus</td>
              </tr>
              <tr bgcolor="#00cccc"> 
                <td class="column" noWrap="true">&#160;Team&#160;</td>
                <td class="column" noWrap="true">&#160;Weight&#160;</td>
              </tr>
              <xsl:apply-templates select="Consensus"/>
              </tbody> 
            </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TBODY>
</TABLE>
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
<TABLE borderColor="#0000ff" width="1%" align="center" border="1">
  <TBODY>
  <TR>
    <TD>
      <TABLE height="100%" cellSpacing="0" cellPadding="3" width="100%" align="center" border="0">
        <TBODY> 
        <TR> 
          <TD noWrap="true">&#160;</TD>
          <xsl:if test="//Title='NFL Pool'">
            <TD class="header" noWrap="true" colSpan="4"> 
              Picks Against<BR/>
              Consensus
            </TD>
          </xsl:if>
          <xsl:if test="//Title='CFPOOL'">
            <TD class="header" noWrap="true" colSpan="3"> 
              Picks Against<BR/>
              Consensus
            </TD>
          </xsl:if>
          <TD noWrap="true">&#160;</TD>
          <TD class="header" noWrap="true" colSpan="4"> 
            Win/Loss<BR/>
            Records
          </TD>
        </TR>
        <TR bgcolor="#00cccc"> 
          <TD class="column" noWrap="true">Name&#160;</TD>
          <TD class="column" noWrap="true">&#160;Against&#160;</TD>
          <TD class="column" noWrap="true">&#160;Won&#160;</TD>
          <TD class="column" noWrap="true">&#160;Lost&#160;</TD>
          <xsl:if test="//Title='NFL Pool'">
            <TD class="column" noWrap="true">&#160;Tied&#160;</TD>
          </xsl:if>
          <TD noWrap="true" width="1%" bgcolor="#009999">&#160;</TD>
          <TD class="column" noWrap="true">&#160;Won&#160;</TD>
          <TD class="column" noWrap="true">&#160;Lost&#160;</TD>
          <xsl:if test="//Title='NFL Pool'">
            <TD class="column" noWrap="true">&#160;Tied&#160;</TD>
          </xsl:if>
          <TD class="column" noWrap="true">&#160;Pts&#160;</TD>
        </TR>
        <xsl:apply-templates select="Entrants"/>
        </TBODY>
      </TABLE>
    </TD></TR></TBODY></TABLE><BR/><BR/>
<TABLE cellSpacing="4" align="center" border="0">
  <TBODY>
  <TR nowrap="true">
    <TD colSpan="3">
      <DIV align="center"><FONT size="+1"><A 
      href="javascript:%20history.go(-1)">Go Back</A></FONT></DIV></TD></TR>
  <TR nowrap="true">
    <TD colSpan="3">
      <DIV align="center"><FONT size="+1"><A 
      href="/"><xsl:value-of select="Title"/> Home Page</A></FONT></DIV>
    </TD>
  </TR>
  </TBODY>
</TABLE>
<P align="center">&#160;</P>
</BODY>
</html>
</xsl:template>

<xsl:template match="Favorites">
  <tr> 
    <xsl:if test="(position() mod 2 = 1)">
      <xsl:attribute name="bgcolor">#FFFFFF</xsl:attribute>
    </xsl:if>
    <xsl:if test="(position() mod 2 = 0)">
      <xsl:attribute name="bgcolor">#CCCCCC</xsl:attribute>
    </xsl:if>
    <td noWrap="true">
      <xsl:attribute name="class">
        <xsl:value-of select="FavoriteState"/>
      </xsl:attribute>
      <xsl:value-of select="FavoritePicked"/>&#160;
    </td>
    <td noWrap="true">
      <xsl:attribute name="class">
        <xsl:value-of select="FavoriteState"/>
      </xsl:attribute>
      <xsl:value-of select="Favorite"/>
    </td>
    <td noWrap="true">
      <xsl:attribute name="class">
        <xsl:value-of select="FavoriteState"/>
      </xsl:attribute>
      <xsl:value-of select="FavoriteAverage"/>
    </td>
    <td noWrap="true">
      <xsl:attribute name="class">
        <xsl:value-of select="UnderdogState"/>
      </xsl:attribute>
      <xsl:value-of select="UnderdogPicked"/>&#160;
    </td>
    <td noWrap="true">
      <xsl:attribute name="class">
        <xsl:value-of select="UnderdogState"/>
      </xsl:attribute>
      <xsl:value-of select="Underdog"/>
    </td>
    <td noWrap="true">
      <xsl:attribute name="class">
        <xsl:value-of select="UnderdogState"/>
      </xsl:attribute>
      <xsl:value-of select="UnderdogAverage"/>
    </td>
  </tr>
</xsl:template>

<xsl:template match="Consensus">
  <tr> 
    <xsl:if test="(position() mod 2 = 1)">
      <xsl:attribute name="bgcolor">#FFFFFF</xsl:attribute>
    </xsl:if>
    <xsl:if test="(position() mod 2 = 0)">
      <xsl:attribute name="bgcolor">#CCCCCC</xsl:attribute>
    </xsl:if>
    <td noWrap="true">
      <xsl:attribute name="class">
        <xsl:value-of select="State"/>
      </xsl:attribute>
      <xsl:value-of select="Team"/>&#160;
    </td>
    <td noWrap="true">
      <xsl:attribute name="class">
        <xsl:value-of select="State"/>
      </xsl:attribute>
      <xsl:value-of select="Weight"/>
    </td>
  </tr>
</xsl:template>

<xsl:template match="Entrants">
  <tr> 
    <xsl:if test="(position() mod 2 = 1)">
      <xsl:attribute name="bgcolor">#FFFFFF</xsl:attribute>
    </xsl:if>
    <xsl:if test="(position() mod 2 = 0)">
      <xsl:attribute name="bgcolor">#CCCCCC</xsl:attribute>
    </xsl:if>
    <td noWrap="true">
      <xsl:attribute name="class">name</xsl:attribute>
      <xsl:value-of select="Name"/>&#160;
    </td>
    <td noWrap="true">
      <xsl:attribute name="class">wc</xsl:attribute>
      <xsl:value-of select="AgainstConsensus"/>&#160;
    </td>
    <td noWrap="true">
      <xsl:attribute name="class">wc</xsl:attribute>
      <xsl:value-of select="ConsensusWins"/>&#160;
    </td>
    <td noWrap="true">
      <xsl:attribute name="class">wc</xsl:attribute>
      <xsl:value-of select="ConsensusLosses"/>&#160;
    </td>
    <xsl:if test="//Title='NFL Pool'">
    <td noWrap="true">
      <xsl:attribute name="class">wc</xsl:attribute>
      <xsl:value-of select="ConsensusTies"/>&#160;
    </td>
    </xsl:if>
    <TD noWrap="true" width="1%" bgColor="#999999">&#160;</TD>
    <td noWrap="true">
      <xsl:attribute name="class">wc</xsl:attribute>
      <xsl:value-of select="Wins"/>&#160;
    </td>
    <td noWrap="true">
      <xsl:attribute name="class">wc</xsl:attribute>
      <xsl:value-of select="Losses"/>&#160;
    </td>
    <xsl:if test="//Title='NFL Pool'">
    <td noWrap="true">
      <xsl:attribute name="class">wc</xsl:attribute>
      <xsl:value-of select="Ties"/>&#160;
    </td>
    </xsl:if>
    <td noWrap="true" align="right">
      <xsl:attribute name="class">score</xsl:attribute>
      <xsl:value-of select="Score"/>&#160;
    </td>
  </tr>
</xsl:template>

</xsl:stylesheet>
