<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- This file is automatically generated. Don't edit it. -->


<!--
  use は見出しを参照することを、
  mention は語の用法を表わす。(いまいち理解しきれていない)

  これらは特殊な用法なので出力しないようにする。
  表示方法は後で考える
-->

<xsl:template match="*">
  <xsl:call-template name="attr_handler"/>
</xsl:template>

<xsl:template name="attr_handler">
  <xsl:choose>
<!--  <xsl:when test="($show_mor_info_flag and (./@mph or ./@sem)) or ./@id -->
  <xsl:when test="./@id or ./@mph or ./@sem
or (./@eq and not(starts-with(./@eq,'use')) and not(./@eq = 'mention'))
or (./@eq.mt and not(starts-with(./@eq.mt,'use')) and not(./@eq.mt = 'mention'))
or (./@arg and not(starts-with(./@arg,'use')) and not(./@arg = 'mention'))
or (./@mod and not(starts-with(./@mod,'use')) and not(./@mod = 'mention'))
or (./@sbj and not(starts-with(./@sbj,'use')) and not(./@sbj = 'mention'))
or (./@obj and not(starts-with(./@obj,'use')) and not(./@obj = 'mention'))
or (./@obj.rcp and not(starts-with(./@obj.rcp,'use')) and not(./@obj.rcp = 'mention'))
or (./@obj.uba and not(starts-with(./@obj.uba,'use')) and not(./@obj.uba = 'mention'))
or (./@obj.rpt and not(starts-with(./@obj.rpt,'use')) and not(./@obj.rpt = 'mention'))
or (./@obj.ql and not(starts-with(./@obj.ql,'use')) and not(./@obj.ql = 'mention'))
or (./@obj.msr and not(starts-with(./@obj.msr,'use')) and not(./@obj.msr = 'mention'))
or (./@iob and not(starts-with(./@iob,'use')) and not(./@iob = 'mention'))
or (./@ctl.obj and not(starts-with(./@ctl.obj,'use')) and not(./@ctl.obj = 'mention'))
or (./@ctl.agt and not(starts-with(./@ctl.agt,'use')) and not(./@ctl.agt = 'mention'))
or (./@ctl.sbj and not(starts-with(./@ctl.sbj,'use')) and not(./@ctl.sbj = 'mention'))
or (./@ctl.exp and not(starts-with(./@ctl.exp,'use')) and not(./@ctl.exp = 'mention'))
or (./@ctl.iob and not(starts-with(./@ctl.iob,'use')) and not(./@ctl.iob = 'mention'))
or (./@plg and not(starts-with(./@plg,'use')) and not(./@plg = 'mention'))
or (./@agt and not(starts-with(./@agt,'use')) and not(./@agt = 'mention'))
or (./@agt.rcp and not(starts-with(./@agt.rcp,'use')) and not(./@agt.rcp = 'mention'))
or (./@agt.rpt and not(starts-with(./@agt.rpt,'use')) and not(./@agt.rpt = 'mention'))
or (./@cap and not(starts-with(./@cap,'use')) and not(./@cap = 'mention'))
or (./@aen and not(starts-with(./@aen,'use')) and not(./@aen = 'mention'))
or (./@aen.rcp and not(starts-with(./@aen.rcp,'use')) and not(./@aen.rcp = 'mention'))
or (./@rpt and not(starts-with(./@rpt,'use')) and not(./@rpt = 'mention'))
or (./@rcp and not(starts-with(./@rcp,'use')) and not(./@rcp = 'mention'))
or (./@src and not(starts-with(./@src,'use')) and not(./@src = 'mention'))
or (./@gol and not(starts-with(./@gol,'use')) and not(./@gol = 'mention'))
or (./@gol.ql and not(starts-with(./@gol.ql,'use')) and not(./@gol.ql = 'mention'))
or (./@res and not(starts-with(./@res,'use')) and not(./@res = 'mention'))
or (./@mat and not(starts-with(./@mat,'use')) and not(./@mat = 'mention'))
or (./@exp and not(starts-with(./@exp,'use')) and not(./@exp = 'mention'))
or (./@exp.uba and not(starts-with(./@exp.uba,'use')) and not(./@exp.uba = 'mention'))
or (./@pos and not(starts-with(./@pos,'use')) and not(./@pos = 'mention'))
or (./@ela and not(starts-with(./@ela,'use')) and not(./@ela = 'mention'))
or (./@eg and not(starts-with(./@eg,'use')) and not(./@eg = 'mention'))
or (./@cnt and not(starts-with(./@cnt,'use')) and not(./@cnt = 'mention'))
or (./@cau and not(starts-with(./@cau,'use')) and not(./@cau = 'mention'))
or (./@pur and not(starts-with(./@pur,'use')) and not(./@pur = 'mention'))
or (./@sub and not(starts-with(./@sub,'use')) and not(./@sub = 'mention'))
or (./@sup and not(starts-with(./@sup,'use')) and not(./@sup = 'mention'))
or (./@tmx and not(starts-with(./@tmx,'use')) and not(./@tmx = 'mention'))
or (./@tim and not(starts-with(./@tim,'use')) and not(./@tim = 'mention'))
or (./@spx and not(starts-with(./@spx,'use')) and not(./@spx = 'mention'))
or (./@loc and not(starts-with(./@loc,'use')) and not(./@loc = 'mention'))
or (./@ilc and not(starts-with(./@ilc,'use')) and not(./@ilc = 'mention'))
or (./@via and not(starts-with(./@via,'use')) and not(./@via = 'mention'))
or (./@dir and not(starts-with(./@dir,'use')) and not(./@dir = 'mention'))
or (./@opp and not(starts-with(./@opp,'use')) and not(./@opp = 'mention'))
or (./@int and not(starts-with(./@int,'use')) and not(./@int = 'mention'))
or (./@fin and not(starts-with(./@fin,'use')) and not(./@fin = 'mention'))
or (./@sit and not(starts-with(./@sit,'use')) and not(./@sit = 'mention'))
or (./@sit.uba and not(starts-with(./@sit.uba,'use')) and not(./@sit.uba = 'mention'))
or (./@in and not(starts-with(./@in,'use')) and not(./@in = 'mention'))
or (./@ni and not(starts-with(./@ni,'use')) and not(./@ni = 'mention'))
or (./@ccm and not(starts-with(./@ccm,'use')) and not(./@ccm = 'mention'))
or (./@cmp and not(starts-with(./@cmp,'use')) and not(./@cmp = 'mention'))
or (./@bas and not(starts-with(./@bas,'use')) and not(./@bas = 'mention'))
or (./@cev and not(starts-with(./@cev,'use')) and not(./@cev = 'mention'))
or (./@mns and not(starts-with(./@mns,'use')) and not(./@mns = 'mention'))
or (./@mob and not(starts-with(./@mob,'use')) and not(./@mob = 'mention'))
or (./@ql and not(starts-with(./@ql,'use')) and not(./@ql = 'mention'))
or (./@sbm and not(starts-with(./@sbm,'use')) and not(./@sbm = 'mention'))
or (./@rpl and not(starts-with(./@rpl,'use')) and not(./@rpl = 'mention'))
or (./@utr and not(starts-with(./@utr,'use')) and not(./@utr = 'mention'))
or (./@smr and not(starts-with(./@smr,'use')) and not(./@smr = 'mention'))
">
    <xsl:element name="span">
      <!-- id属性を表示 -->
      <xsl:if test="./@id">
	<xsl:attribute name="id"><xsl:value-of select="./@id"/></xsl:attribute>
      </xsl:if>
      <!-- class属性を表示(値は元の属性と組を全て連結した文字列) -->
<!--  <xsl:if test="($show_mor_info_flag and (./@mph or ./@sem)) -->
      <xsl:if test="./@mph or ./@sem
or (./@eq and not(starts-with(./@eq,'use')) and not(./@eq = 'mention'))
or (./@eq.mt and not(starts-with(./@eq.mt,'use')) and not(./@eq.mt = 'mention'))
or (./@arg and not(starts-with(./@arg,'use')) and not(./@arg = 'mention'))
or (./@mod and not(starts-with(./@mod,'use')) and not(./@mod = 'mention'))
or (./@sbj and not(starts-with(./@sbj,'use')) and not(./@sbj = 'mention'))
or (./@obj and not(starts-with(./@obj,'use')) and not(./@obj = 'mention'))
or (./@obj.rcp and not(starts-with(./@obj.rcp,'use')) and not(./@obj.rcp = 'mention'))
or (./@obj.uba and not(starts-with(./@obj.uba,'use')) and not(./@obj.uba = 'mention'))
or (./@obj.rpt and not(starts-with(./@obj.rpt,'use')) and not(./@obj.rpt = 'mention'))
or (./@obj.ql and not(starts-with(./@obj.ql,'use')) and not(./@obj.ql = 'mention'))
or (./@obj.msr and not(starts-with(./@obj.msr,'use')) and not(./@obj.msr = 'mention'))
or (./@iob and not(starts-with(./@iob,'use')) and not(./@iob = 'mention'))
or (./@ctl.obj and not(starts-with(./@ctl.obj,'use')) and not(./@ctl.obj = 'mention'))
or (./@ctl.agt and not(starts-with(./@ctl.agt,'use')) and not(./@ctl.agt = 'mention'))
or (./@ctl.sbj and not(starts-with(./@ctl.sbj,'use')) and not(./@ctl.sbj = 'mention'))
or (./@ctl.exp and not(starts-with(./@ctl.exp,'use')) and not(./@ctl.exp = 'mention'))
or (./@ctl.iob and not(starts-with(./@ctl.iob,'use')) and not(./@ctl.iob = 'mention'))
or (./@plg and not(starts-with(./@plg,'use')) and not(./@plg = 'mention'))
or (./@agt and not(starts-with(./@agt,'use')) and not(./@agt = 'mention'))
or (./@agt.rcp and not(starts-with(./@agt.rcp,'use')) and not(./@agt.rcp = 'mention'))
or (./@agt.rpt and not(starts-with(./@agt.rpt,'use')) and not(./@agt.rpt = 'mention'))
or (./@cap and not(starts-with(./@cap,'use')) and not(./@cap = 'mention'))
or (./@aen and not(starts-with(./@aen,'use')) and not(./@aen = 'mention'))
or (./@aen.rcp and not(starts-with(./@aen.rcp,'use')) and not(./@aen.rcp = 'mention'))
or (./@rpt and not(starts-with(./@rpt,'use')) and not(./@rpt = 'mention'))
or (./@rcp and not(starts-with(./@rcp,'use')) and not(./@rcp = 'mention'))
or (./@src and not(starts-with(./@src,'use')) and not(./@src = 'mention'))
or (./@gol and not(starts-with(./@gol,'use')) and not(./@gol = 'mention'))
or (./@gol.ql and not(starts-with(./@gol.ql,'use')) and not(./@gol.ql = 'mention'))
or (./@res and not(starts-with(./@res,'use')) and not(./@res = 'mention'))
or (./@mat and not(starts-with(./@mat,'use')) and not(./@mat = 'mention'))
or (./@exp and not(starts-with(./@exp,'use')) and not(./@exp = 'mention'))
or (./@exp.uba and not(starts-with(./@exp.uba,'use')) and not(./@exp.uba = 'mention'))
or (./@pos and not(starts-with(./@pos,'use')) and not(./@pos = 'mention'))
or (./@ela and not(starts-with(./@ela,'use')) and not(./@ela = 'mention'))
or (./@eg and not(starts-with(./@eg,'use')) and not(./@eg = 'mention'))
or (./@cnt and not(starts-with(./@cnt,'use')) and not(./@cnt = 'mention'))
or (./@cau and not(starts-with(./@cau,'use')) and not(./@cau = 'mention'))
or (./@pur and not(starts-with(./@pur,'use')) and not(./@pur = 'mention'))
or (./@sub and not(starts-with(./@sub,'use')) and not(./@sub = 'mention'))
or (./@sup and not(starts-with(./@sup,'use')) and not(./@sup = 'mention'))
or (./@tmx and not(starts-with(./@tmx,'use')) and not(./@tmx = 'mention'))
or (./@tim and not(starts-with(./@tim,'use')) and not(./@tim = 'mention'))
or (./@spx and not(starts-with(./@spx,'use')) and not(./@spx = 'mention'))
or (./@loc and not(starts-with(./@loc,'use')) and not(./@loc = 'mention'))
or (./@ilc and not(starts-with(./@ilc,'use')) and not(./@ilc = 'mention'))
or (./@via and not(starts-with(./@via,'use')) and not(./@via = 'mention'))
or (./@dir and not(starts-with(./@dir,'use')) and not(./@dir = 'mention'))
or (./@opp and not(starts-with(./@opp,'use')) and not(./@opp = 'mention'))
or (./@int and not(starts-with(./@int,'use')) and not(./@int = 'mention'))
or (./@fin and not(starts-with(./@fin,'use')) and not(./@fin = 'mention'))
or (./@sit and not(starts-with(./@sit,'use')) and not(./@sit = 'mention'))
or (./@sit.uba and not(starts-with(./@sit.uba,'use')) and not(./@sit.uba = 'mention'))
or (./@in and not(starts-with(./@in,'use')) and not(./@in = 'mention'))
or (./@ni and not(starts-with(./@ni,'use')) and not(./@ni = 'mention'))
or (./@ccm and not(starts-with(./@ccm,'use')) and not(./@ccm = 'mention'))
or (./@cmp and not(starts-with(./@cmp,'use')) and not(./@cmp = 'mention'))
or (./@bas and not(starts-with(./@bas,'use')) and not(./@bas = 'mention'))
or (./@cev and not(starts-with(./@cev,'use')) and not(./@cev = 'mention'))
or (./@mns and not(starts-with(./@mns,'use')) and not(./@mns = 'mention'))
or (./@mob and not(starts-with(./@mob,'use')) and not(./@mob = 'mention'))
or (./@ql and not(starts-with(./@ql,'use')) and not(./@ql = 'mention'))
or (./@sbm and not(starts-with(./@sbm,'use')) and not(./@sbm = 'mention'))
or (./@rpl and not(starts-with(./@rpl,'use')) and not(./@rpl = 'mention'))
or (./@utr and not(starts-with(./@utr,'use')) and not(./@utr = 'mention'))
or (./@smr and not(starts-with(./@smr,'use')) and not(./@smr = 'mention'))
">
	<xsl:attribute name="class">
	  <xsl:call-template name="make_attr_list"/>
	</xsl:attribute>
      </xsl:if>
      <!-- 形態素情報を表示するための JavaScript コードを表示 -->
      <!-- <xsl:if test="$show_mor_info_flag"> -->
	<xsl:if test="./@mph or ./@sem">
	  <xsl:attribute name="onmouseup">
	    <xsl:text>ck1(event)</xsl:text>
	  </xsl:attribute>
	  <!--
	  <xsl:attribute name="style">
	    <xsl:text>white-space:nowrap;</xsl:text>
	  </xsl:attribute>
	  -->
	</xsl:if>
      <!-- </xsl:if> -->
      <xsl:choose>
      <!-- eq属性を表わすマーク(●)を表示 -->
      <xsl:when test="false()
or (./@eq and not(starts-with(./@eq,'use')) and not(./@eq = 'mention'))
or (./@eq.mt and not(starts-with(./@eq.mt,'use')) and not(./@eq.mt = 'mention'))
">
	<span class="marker_eq" onmouseup="ck2(this)">●</span>
      </xsl:when>
      <!-- id属性を表わすマーク(■)を表示
	   id="id..." のときは単語のIDなので表示しない
      <xsl:when test="./@id and not(starts-with(./@id,'id'))">
      -->
      <xsl:when test="./@id">
	<span class="marker_id" onmouseup="ck2(this)">■</span>
      </xsl:when>
      </xsl:choose>
      <!-- 関係の属性を表わすマーク(▼)を表示 -->
      <xsl:if test="false()
or (./@arg and not(starts-with(./@arg,'use')) and not(./@arg = 'mention'))
or (./@mod and not(starts-with(./@mod,'use')) and not(./@mod = 'mention'))
or (./@sbj and not(starts-with(./@sbj,'use')) and not(./@sbj = 'mention'))
or (./@obj and not(starts-with(./@obj,'use')) and not(./@obj = 'mention'))
or (./@obj.rcp and not(starts-with(./@obj.rcp,'use')) and not(./@obj.rcp = 'mention'))
or (./@obj.uba and not(starts-with(./@obj.uba,'use')) and not(./@obj.uba = 'mention'))
or (./@obj.rpt and not(starts-with(./@obj.rpt,'use')) and not(./@obj.rpt = 'mention'))
or (./@obj.ql and not(starts-with(./@obj.ql,'use')) and not(./@obj.ql = 'mention'))
or (./@obj.msr and not(starts-with(./@obj.msr,'use')) and not(./@obj.msr = 'mention'))
or (./@iob and not(starts-with(./@iob,'use')) and not(./@iob = 'mention'))
or (./@ctl.obj and not(starts-with(./@ctl.obj,'use')) and not(./@ctl.obj = 'mention'))
or (./@ctl.agt and not(starts-with(./@ctl.agt,'use')) and not(./@ctl.agt = 'mention'))
or (./@ctl.sbj and not(starts-with(./@ctl.sbj,'use')) and not(./@ctl.sbj = 'mention'))
or (./@ctl.exp and not(starts-with(./@ctl.exp,'use')) and not(./@ctl.exp = 'mention'))
or (./@ctl.iob and not(starts-with(./@ctl.iob,'use')) and not(./@ctl.iob = 'mention'))
or (./@plg and not(starts-with(./@plg,'use')) and not(./@plg = 'mention'))
or (./@agt and not(starts-with(./@agt,'use')) and not(./@agt = 'mention'))
or (./@agt.rcp and not(starts-with(./@agt.rcp,'use')) and not(./@agt.rcp = 'mention'))
or (./@agt.rpt and not(starts-with(./@agt.rpt,'use')) and not(./@agt.rpt = 'mention'))
or (./@cap and not(starts-with(./@cap,'use')) and not(./@cap = 'mention'))
or (./@aen and not(starts-with(./@aen,'use')) and not(./@aen = 'mention'))
or (./@aen.rcp and not(starts-with(./@aen.rcp,'use')) and not(./@aen.rcp = 'mention'))
or (./@rpt and not(starts-with(./@rpt,'use')) and not(./@rpt = 'mention'))
or (./@rcp and not(starts-with(./@rcp,'use')) and not(./@rcp = 'mention'))
or (./@src and not(starts-with(./@src,'use')) and not(./@src = 'mention'))
or (./@gol and not(starts-with(./@gol,'use')) and not(./@gol = 'mention'))
or (./@gol.ql and not(starts-with(./@gol.ql,'use')) and not(./@gol.ql = 'mention'))
or (./@res and not(starts-with(./@res,'use')) and not(./@res = 'mention'))
or (./@mat and not(starts-with(./@mat,'use')) and not(./@mat = 'mention'))
or (./@exp and not(starts-with(./@exp,'use')) and not(./@exp = 'mention'))
or (./@exp.uba and not(starts-with(./@exp.uba,'use')) and not(./@exp.uba = 'mention'))
or (./@pos and not(starts-with(./@pos,'use')) and not(./@pos = 'mention'))
or (./@ela and not(starts-with(./@ela,'use')) and not(./@ela = 'mention'))
or (./@eg and not(starts-with(./@eg,'use')) and not(./@eg = 'mention'))
or (./@cnt and not(starts-with(./@cnt,'use')) and not(./@cnt = 'mention'))
or (./@cau and not(starts-with(./@cau,'use')) and not(./@cau = 'mention'))
or (./@pur and not(starts-with(./@pur,'use')) and not(./@pur = 'mention'))
or (./@sub and not(starts-with(./@sub,'use')) and not(./@sub = 'mention'))
or (./@sup and not(starts-with(./@sup,'use')) and not(./@sup = 'mention'))
or (./@tmx and not(starts-with(./@tmx,'use')) and not(./@tmx = 'mention'))
or (./@tim and not(starts-with(./@tim,'use')) and not(./@tim = 'mention'))
or (./@spx and not(starts-with(./@spx,'use')) and not(./@spx = 'mention'))
or (./@loc and not(starts-with(./@loc,'use')) and not(./@loc = 'mention'))
or (./@ilc and not(starts-with(./@ilc,'use')) and not(./@ilc = 'mention'))
or (./@via and not(starts-with(./@via,'use')) and not(./@via = 'mention'))
or (./@dir and not(starts-with(./@dir,'use')) and not(./@dir = 'mention'))
or (./@opp and not(starts-with(./@opp,'use')) and not(./@opp = 'mention'))
or (./@int and not(starts-with(./@int,'use')) and not(./@int = 'mention'))
or (./@fin and not(starts-with(./@fin,'use')) and not(./@fin = 'mention'))
or (./@sit and not(starts-with(./@sit,'use')) and not(./@sit = 'mention'))
or (./@sit.uba and not(starts-with(./@sit.uba,'use')) and not(./@sit.uba = 'mention'))
or (./@in and not(starts-with(./@in,'use')) and not(./@in = 'mention'))
or (./@ni and not(starts-with(./@ni,'use')) and not(./@ni = 'mention'))
or (./@ccm and not(starts-with(./@ccm,'use')) and not(./@ccm = 'mention'))
or (./@cmp and not(starts-with(./@cmp,'use')) and not(./@cmp = 'mention'))
or (./@bas and not(starts-with(./@bas,'use')) and not(./@bas = 'mention'))
or (./@cev and not(starts-with(./@cev,'use')) and not(./@cev = 'mention'))
or (./@mns and not(starts-with(./@mns,'use')) and not(./@mns = 'mention'))
or (./@mob and not(starts-with(./@mob,'use')) and not(./@mob = 'mention'))
or (./@ql and not(starts-with(./@ql,'use')) and not(./@ql = 'mention'))
or (./@sbm and not(starts-with(./@sbm,'use')) and not(./@sbm = 'mention'))
or (./@rpl and not(starts-with(./@rpl,'use')) and not(./@rpl = 'mention'))
or (./@utr and not(starts-with(./@utr,'use')) and not(./@utr = 'mention'))
or (./@smr and not(starts-with(./@smr,'use')) and not(./@smr = 'mention'))
">
	<span class="marker_rel" onmouseup="ck3(event)">▼</span>
      </xsl:if>
      <xsl:apply-templates />
    </xsl:element>
  </xsl:when>
  <xsl:otherwise>
    <xsl:apply-templates />
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="make_attr_list">
  <xsl:for-each select="./@*">
    <xsl:choose>
<!--
    <xsl:when test="not(starts-with(.,'use')) and not(. = 'mention') and
		    (($show_mor_info_flag and (name(.) = 'mph' or name(.) = 'sem'))
-->
    <xsl:when test="not(starts-with(.,'use')) and not(. = 'mention') and
		    (name(.) = 'mph' or name(.) = 'sem'
or name(.) = 'arg'
or name(.) = 'mod'
or name(.) = 'sbj'
or name(.) = 'obj'
or name(.) = 'obj.rcp'
or name(.) = 'obj.uba'
or name(.) = 'obj.rpt'
or name(.) = 'obj.ql'
or name(.) = 'obj.msr'
or name(.) = 'iob'
or name(.) = 'ctl.obj'
or name(.) = 'ctl.agt'
or name(.) = 'ctl.sbj'
or name(.) = 'ctl.exp'
or name(.) = 'ctl.iob'
or name(.) = 'plg'
or name(.) = 'agt'
or name(.) = 'agt.rcp'
or name(.) = 'agt.rpt'
or name(.) = 'cap'
or name(.) = 'aen'
or name(.) = 'aen.rcp'
or name(.) = 'rpt'
or name(.) = 'rcp'
or name(.) = 'src'
or name(.) = 'gol'
or name(.) = 'gol.ql'
or name(.) = 'res'
or name(.) = 'mat'
or name(.) = 'exp'
or name(.) = 'exp.uba'
or name(.) = 'pos'
or name(.) = 'ela'
or name(.) = 'eg'
or name(.) = 'cnt'
or name(.) = 'cau'
or name(.) = 'pur'
or name(.) = 'sub'
or name(.) = 'sup'
or name(.) = 'tmx'
or name(.) = 'tim'
or name(.) = 'spx'
or name(.) = 'loc'
or name(.) = 'ilc'
or name(.) = 'via'
or name(.) = 'dir'
or name(.) = 'opp'
or name(.) = 'int'
or name(.) = 'fin'
or name(.) = 'sit'
or name(.) = 'sit.uba'
or name(.) = 'in'
or name(.) = 'ni'
or name(.) = 'ccm'
or name(.) = 'cmp'
or name(.) = 'bas'
or name(.) = 'cev'
or name(.) = 'mns'
or name(.) = 'mob'
or name(.) = 'ql'
or name(.) = 'sbm'
or name(.) = 'rpl'
or name(.) = 'utr'
or name(.) = 'smr'
)">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
      <xsl:value-of select="."/>
      <xsl:text>||</xsl:text>
    </xsl:when>
    <xsl:when test="not(starts-with(.,'use')) and not(. = 'mention') and (false()
or name(.) = 'eq'
or name(.) = 'eq.mt'
)">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
      <xsl:value-of select="."/>
<!-- obsolete
     'use'で始まるeq属性値の末尾に senseid を付与し、
     共参照の範囲を同一語義内に限定するときの処理
      <xsl:if test="starts-with(.,'use')">
	<xsl:text>.</xsl:text>
	<xsl:value-of select="ancestor::sense[1]/@senseid"/>
      </xsl:if>
-->
      <xsl:text>||</xsl:text>
    </xsl:when>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>