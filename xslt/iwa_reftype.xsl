<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!--
他の語義への参照を表わす数字・記号を表示するテンプレート
属性値に空白で区切られた複数の参照があるときにも対応する

レベルが違う語義への参照が並ぶ例
  a. 1-1 1-2-1
  b. 1-1-1 2-2
  c. 1-1-3 2-4
  d. 1-2-2 2-3

b,c,dはそのまま表示して問題なし。
a.は例外処理で間に・を入れる → (1)・(2)(ア)
-->

<xsl:template name="repl_sense_number">
  <xsl:choose>
  <xsl:when test="./@reftype">
    <xsl:choose>
    <xsl:when test="./text() = '●'">
      <xsl:call-template name="conv_reftype_to_sense_number">
	<xsl:with-param name="str" select="normalize-space(./@reftype)"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
      <xsl:choose>
      <xsl:when test="./@reftype = '1-1 1-2-1'"> <!-- 例外処理 -->
	(1)・(2)(ア)
      </xsl:when>
      <xsl:otherwise>
	<xsl:call-template name="conv_reftype_to_sense_number">
	  <xsl:with-param name="str" select="normalize-space(./@reftype)"/>
	</xsl:call-template>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:when>
  <xsl:otherwise>
    <xsl:apply-templates/>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="conv_reftype_to_sense_number">
  <xsl:param name="str"/>
  <xsl:choose>
  <xsl:when test="contains($str,' ')">
    <xsl:call-template name="generate_sense_number">
      <xsl:with-param name="val" select="substring-before($str,' ')"/>
    </xsl:call-template>
    <xsl:call-template name="conv_reftype_to_sense_number">
      <xsl:with-param name="str" select="substring-after($str,' ')"/>
    </xsl:call-template>
  </xsl:when>
  <xsl:otherwise>
    <xsl:call-template name="generate_sense_number">
      <xsl:with-param name="val" select="$str"/>
    </xsl:call-template>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="generate_sense_number">
  <xsl:param name="val"/>
  <xsl:choose>
  <xsl:when test="$val = '0-1'">
    <xsl:text>(一)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-2'">
    <xsl:text>(二)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-3'">
    <xsl:text>(三)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-4'">
    <xsl:text>(四)</xsl:text>
  </xsl:when>
<!-- コーパスに出現せず
  <xsl:when test="$val = '0-5'">
    <xsl:text>(五)</xsl:text>
  </xsl:when>
-->
  <xsl:when test="$val = '0-6'">
    <xsl:text>(六)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-1-1'">
    <xsl:text>(一)(1)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-1-2'">
    <xsl:text>(一)(2)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-1-3'">
    <xsl:text>(一)(3)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-1-4-1'">
    <xsl:text>(一)(4)(ア)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-2-1'">
    <xsl:text>(二)(1)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-2-3'">
    <xsl:text>(二)(3)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-2-3-3'">
    <xsl:text>(二)(3)(ウ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-3-2'">
    <xsl:text>(三)(2)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-3-5'">
    <xsl:text>(三)(5)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '0-4-1'">
    <xsl:text>(四)(1)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-1'">
    <xsl:text>(1)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-2'">
    <xsl:text>(2)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-3'">
    <xsl:text>(3)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-4'">
    <xsl:text>(4)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-5'">
    <xsl:text>(5)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-6'">
    <xsl:text>(6)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-7'">
    <xsl:text>(7)</xsl:text>
  </xsl:when>
<!-- コーパスに出現せず
  <xsl:when test="$val = '1-8'">
    <xsl:text>(8)</xsl:text>
  </xsl:when>
-->
  <xsl:when test="$val = '1-9'">
    <xsl:text>(9)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-10'">
    <xsl:text>(10)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-1-1'">
    <xsl:text>(1)(ア)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-1-2'">
    <xsl:text>(1)(イ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-1-3'">
    <xsl:text>(1)(ウ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-1-7'">
    <xsl:text>(1)(キ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-2-1'">
    <xsl:text>(2)(ア)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-2-2'">
    <xsl:text>(2)(イ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-2-3'">
    <xsl:text>(2)(ウ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-3-1'">
    <xsl:text>(3)(ア)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-3-2'">
    <xsl:text>(3)(イ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-3-3'">
    <xsl:text>(3)(ウ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-3-5'">
    <xsl:text>(3)(オ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-4-1'">
    <xsl:text>(4)(ア)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '1-4-2'">
    <xsl:text>(4)(イ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '2-1'">
    <xsl:text>(ア)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '2-2'">
    <xsl:text>(イ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '2-3'">
    <xsl:text>(ウ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '2-4'">
    <xsl:text>(エ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '2-5'">
    <xsl:text>(オ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '2-6'">
    <xsl:text>(カ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '2-7'">
    <xsl:text>(キ)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '2-8'">
    <xsl:text>(ク)</xsl:text>
  </xsl:when>
  <xsl:when test="$val = '2-9'">
    <xsl:text>(ケ)</xsl:text>
  </xsl:when>
  <xsl:otherwise>
    <xsl:text>(?)</xsl:text>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
