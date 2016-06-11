<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:import href="iwa_reftype.xsl"/>

<!-- 文書整形(岩波) -->

<xsl:template match="sense">
  <blockquote class="sense">
    <table class="sense"><tr>
<!--
    <td class="senseid">
      <xsl:value-of select="substring-after(./@senseid,'.')"/>
    </td>
-->
    <xsl:element name="td">
      <xsl:choose>
      <xsl:when test="./@type = 'new'">
	<xsl:attribute name="class"><xsl:text>senseid_new</xsl:text></xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
	<xsl:attribute name="class"><xsl:text>senseid</xsl:text></xsl:attribute>
      </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="substring-after(./@senseid,'.')"/>
    </xsl:element>
    <td><xsl:apply-templates /></td>
    </tr></table>
  </blockquote>
</xsl:template>

<!-- hd(見出し)を強調する -->
<xsl:template match="hd">
  <span class="headword">
  <xsl:apply-templates />
  </span>
  <xsl:text>　</xsl:text>
</xsl:template>

<!-- pos(品詞)を〔〕で囲む -->
<xsl:template match="pos">
  <xsl:text>〔</xsl:text>
  <xsl:apply-templates />
  <xsl:text>〕</xsl:text>
</xsl:template>

<!-- orth(正書表記)を【】で囲む -->
<xsl:template match="orth">
  <xsl:text>【</xsl:text>
  <xsl:apply-templates />
  <xsl:text>】</xsl:text>
</xsl:template>

<!-- hst(歴史的仮名遣い)を( )で囲む -->
<xsl:template match="hst">
  <xsl:text>(</xsl:text>
  <xsl:apply-templates />
  <xsl:text>)</xsl:text>
</xsl:template>

<!-- old(旧字体)を[ ]で囲む -->
<xsl:template match="old">
  <xsl:text>[</xsl:text>
  <xsl:apply-templates />
  <xsl:text>]</xsl:text>
</xsl:template>

<!-- prn, tprn, prns(読み)については、一番外側のみ( )で囲む -->
<xsl:template match="prn">
  <xsl:call-template name="printing"/>
</xsl:template>
<xsl:template match="tprn">
  <xsl:call-template name="printing"/>
</xsl:template>
<xsl:template match="prns">
  <xsl:call-template name="printing"/>
</xsl:template>

<!-- usg(使用の範囲や分野)を〔〕で囲む -->
<xsl:template match="usg">
  <xsl:choose>
  <xsl:when test="not(starts-with(.//text(),'〔'))">
    <xsl:text>〔</xsl:text>
    <xsl:apply-templates />
    <xsl:text>〕</xsl:text>
  </xsl:when>
  <xsl:otherwise>
    <xsl:apply-templates />
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- gram(文法的な解説)を《》で囲む -->
<xsl:template match="gram">
  <xsl:choose>
  <xsl:when test="not(starts-with(.//text(),'《'))">
    <xsl:text>《</xsl:text>
    <xsl:apply-templates />
    <xsl:text>》</xsl:text>
  </xsl:when>
  <xsl:otherwise>
    <xsl:apply-templates />
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- sr, srp(他の語義の参照)については記号を補完する -->
<xsl:template match="sr">
  <xsl:call-template name="repl_sense_number"/>
</xsl:template>
<xsl:template match="srp">
  <xsl:call-template name="repl_sense_number"/>
</xsl:template>


<!-- 読みに( )をつけるテンプレート -->
<xsl:template name="printing">
  <xsl:choose>
  <xsl:when test="ancestor::prn or ancestor::tprn or ancestor::prns">
    <xsl:apply-templates />
  </xsl:when>
  <xsl:otherwise>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates />
    <xsl:text>)</xsl:text>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
