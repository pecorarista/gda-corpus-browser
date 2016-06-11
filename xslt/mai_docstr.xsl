<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- 文書整形(毎日) -->

<xsl:template match="byline">
  <xsl:choose>
  <!-- 最初に出現する h のみ、「記事情報」を挿入する -->
  <xsl:when test="preceding::byline">
    <xsl:apply-templates />
  </xsl:when>
  <xsl:otherwise>
    <font color="blue">記事情報</font><br/>
    <xsl:apply-templates />
    <br/>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="h">
  <xsl:choose>
  <!-- 最初に出現する h のみ、「タイトル」を挿入する -->
  <xsl:when test="preceding::h">
    <xsl:apply-templates />
  </xsl:when>
  <xsl:otherwise>
    <font color="blue">タイトル</font><br/>
    <xsl:apply-templates />
    <br/>
    <font color="blue">本文</font> <br/>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
