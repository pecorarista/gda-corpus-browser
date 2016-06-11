<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- XSL file for News GDA corpus
     insert new line for each sentence
     use `gda_sjis.js' as java script
-->

<xsl:import href="mai_attr.xsl"/>
<xsl:import href="mai_docstr.xsl"/>
<xsl:import href="mai_br_sent.xsl"/>
<xsl:import href="common.xsl"/>

<xsl:variable name="js_file">gda_sjis.js</xsl:variable>

<!--
  形態素情報(mph属性とsem属性)を表示するか否かを切り替えるための変数
  結局、形態素情報は常に表示することにした
  場合分けの処理は全部コメントアウトしたが、処理時間はあまり変わらないようだ
<xsl:variable name="show_mor_info_flag" select="true()"/>
<xsl:variable name="show_mor_info_flag" select="false()"/>
-->

</xsl:stylesheet>
