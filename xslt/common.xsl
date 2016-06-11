<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- 毎日、岩波、共通 -->

<xsl:output method="html" encoding="utf-8" omit-xml-declaration="yes"
  doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
  doctype-system="http://www.w3.org/TR/html4/loose.dtd"
  indent="no" />

<xsl:template match="/">
  <html>
    <head>
      <!--
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      -->
      <meta http-equiv="Content-Style-Type" content="text/css" />
      <link rel="stylesheet" href="gda_corpus.css" type="text/css" />
      <!--
      <script type="text/javascript" src="gda.js"></script>
      -->
      <xsl:element name="script">
	<xsl:attribute name="type">
	  <xsl:text>text/javascript</xsl:text>
	</xsl:attribute>
	<xsl:attribute name="src">
	  <xsl:value-of select="$js_file"/>
	</xsl:attribute>
      </xsl:element>
      <title>GDA Corpus</title>
    </head>

    <body onLoad="formLoad('floating_popup_menu');hide_invalid_markers()">
      <xsl:apply-templates />
      <div id="floating_popup_menu" style="position:absolute;"></div>
      <div id="deictic_area" style="position:absolute;"></div>
    </body>
  </html>
</xsl:template>

<xsl:template match="target">
  <a name="target" class="target_word"><xsl:value-of select="."/></a>
</xsl:template>

</xsl:stylesheet>
