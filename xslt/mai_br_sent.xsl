<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- <su>で改行する -->

<xsl:template match="su">
  <xsl:choose>
  <!-- su の入れ子があるとき、一番外側の su のみ改行を入れる -->
  <xsl:when test="ancestor::su">  
    <!-- <xsl:apply-templates /> -->
    <xsl:call-template name="attr_handler"/>
  </xsl:when>
  <xsl:otherwise>
    <!-- <xsl:apply-templates /> -->
    <xsl:call-template name="attr_handler"/>
    <br/>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
