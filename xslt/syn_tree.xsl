<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet
 version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" encoding="utf-8" omit-xml-declaration="yes"
 doctype-public="-//WSC//DTD HTML 4.01//EN"
 indent="yes"
/>

<!-- ====================================================================== -->

<!-- 木のルート -->
<xsl:template match="gda">
  <html>
    <head>
      <link rel="stylesheet" type="text/css" href="syn_tree.css"></link>
    </head>
    <body>
      <xsl:for-each select="*">
        <xsl:call-template name="draw_tree">
          <xsl:with-param name="line1">space</xsl:with-param>
          <xsl:with-param name="line2">space</xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
    </body>
  </html>
</xsl:template>

<!-- ====================================================================== -->

<!-- 木の描画 -->
<xsl:template name="draw_tree">
  <!-- Parameters -->
  <xsl:param name="flag_unary">0</xsl:param>
  <xsl:param name="line1">space</xsl:param>
  <xsl:param name="line2">space</xsl:param>
  <!-- Variables -->
  <!-- Body -->
  <xsl:choose>
    <!-- 葉の場合 -->
    <xsl:when test="name() = ''">
      <xsl:call-template name="draw_tree_leaf">
	<xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
	<xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
        <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <!-- sr, srp で reftype がある場合 -->
    <xsl:when test="(name() = 'sr' or name() = 'srp') and ./@reftype">
      <xsl:call-template name="draw_tree_sr_srp">
	<xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
	<xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
        <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <!-- syn="f" の場合 -->
    <xsl:when test="@syn = 'f'">
      <xsl:call-template name="draw_tree_f">
        <xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
        <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
        <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <!-- その他の場合 -->
    <xsl:otherwise>
      <xsl:call-template name="draw_tree_default">
        <xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
        <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
        <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
   
<!-- ====================================================================== -->

<!-- 木の描画 (葉の場合) -->
<xsl:template name="draw_tree_leaf">
  <!-- Parameters -->
  <xsl:param name="flag_unary"></xsl:param>
  <xsl:param name="line1"></xsl:param>
  <xsl:param name="line2"></xsl:param>
  <!-- Variables -->
  <!-- Body -->
  <div align="center">
    <table class="tree">
      <xsl:call-template name="draw_leaf">
	<xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
	<xsl:with-param name="flag_leaf"><xsl:value-of select="1" /></xsl:with-param>
	<xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
	<xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
	<xsl:with-param name="num_dtrs"><xsl:value-of select="0" /></xsl:with-param>
      </xsl:call-template>
    </table>
  </div>
</xsl:template>

<!-- ====================================================================== -->

<!-- 木の描画 (sr か srp の場合) -->
<xsl:template name="draw_tree_sr_srp">
  <!-- Parameters -->
  <xsl:param name="flag_unary"></xsl:param>
  <xsl:param name="line1"></xsl:param>
  <xsl:param name="line2"></xsl:param>
  <!-- Variables -->
  <!-- Body -->
  <div align="center">
    <table class="tree">
      <xsl:call-template name="draw_leaf">
	<xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
	<xsl:with-param name="flag_leaf"><xsl:value-of select="1" /></xsl:with-param>
	<xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
	<xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
	<xsl:with-param name="num_dtrs"><xsl:value-of select="0" /></xsl:with-param>
      </xsl:call-template>
    </table>
  </div>
</xsl:template>

<!-- ====================================================================== -->

<!-- 木の描画 (その他の場合) -->
<xsl:template name="draw_tree_default">
  <!-- Parameters -->
  <xsl:param name="flag_unary"></xsl:param>
  <xsl:param name="line1"></xsl:param>
  <xsl:param name="line2"></xsl:param>
  <!-- Variables -->
  <xsl:variable name="num_dtrs" select="count(node())" />
  <xsl:variable name="flag_pseudo_leaf">
    <xsl:call-template name="check_pseudo_leaf">
    </xsl:call-template>
  </xsl:variable>
  <!-- Body -->
  <table class="tree">
    <xsl:choose>
      <xsl:when test ="$flag_pseudo_leaf = '1'">
	<xsl:call-template name="draw_leaf">
	  <xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
	  <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
	  <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
	  <xsl:with-param name="num_dtrs"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
	  <xsl:with-param name="label"><xsl:value-of select="name()" /></xsl:with-param>
	</xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
	<xsl:call-template name="draw_node">
	  <xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
	  <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
	  <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
	  <xsl:with-param name="num_dtrs"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
	  <xsl:with-param name="label"><xsl:value-of select="name()" /></xsl:with-param>
	</xsl:call-template>
	<tr>
	  <xsl:for-each select="node()">
            <td class="dtr_place">
              <xsl:variable name="dtr_id">
		<xsl:number level="single" count="node()" format="1" />
              </xsl:variable>
              <xsl:choose>
		<!-- 子が 1 個の場合 -->
		<xsl:when test="$dtr_id = 1 and $num_dtrs = 1">
		  <xsl:call-template name="draw_tree">
		    <xsl:with-param name="flag_unary">1</xsl:with-param>
		  </xsl:call-template>
		</xsl:when>
		<!-- 子が複数で最初の子の場合 -->
		<xsl:when test="$dtr_id = 1">
		  <xsl:call-template name="draw_tree">
                    <xsl:with-param name="line1">space</xsl:with-param>
                    <xsl:with-param name="line2">upper_left</xsl:with-param>
		  </xsl:call-template>
		</xsl:when>
		<!-- 子が複数で最後の子の場合 -->
		<xsl:when test="$dtr_id = $num_dtrs">
		  <xsl:call-template name="draw_tree">
                    <xsl:with-param name="line1">upper</xsl:with-param>
                    <xsl:with-param name="line2">left</xsl:with-param>
		  </xsl:call-template>
		</xsl:when>
		<!-- 子が複数で中間の子の場合 -->
		<xsl:otherwise>
		  <xsl:call-template name="draw_tree">
                    <xsl:with-param name="line1">upper</xsl:with-param>
                    <xsl:with-param name="line2">upper_left</xsl:with-param>
		  </xsl:call-template>
		</xsl:otherwise>
              </xsl:choose>
            </td>
	  </xsl:for-each>
	</tr>
      </xsl:otherwise>
    </xsl:choose>
  </table>
</xsl:template>

<!-- ====================================================================== -->

<!-- 木の描画 (syn="f" の場合) -->
<xsl:template name="draw_tree_f">
  <!-- Parameters -->
  <xsl:param name="flag_unary"></xsl:param>
  <xsl:param name="line1"></xsl:param>
  <xsl:param name="line2"></xsl:param>
  <!-- Variables -->
  <xsl:variable name="num_dtrs" select="count(node())" />
  <xsl:variable name="head">
    <xsl:call-template name="search_rightmost_head_p">
      <xsl:with-param name="index"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="has_no_head">
    <xsl:choose>
      <xsl:when test="$head = ''">1</xsl:when>
      <xsl:otherwise>0</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <!-- Body -->
  <xsl:call-template name="draw_tree_f_inversion">
    <xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
    <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
    <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
    <xsl:with-param name="head"><xsl:value-of select="$head" /></xsl:with-param>
    <xsl:with-param name="left_end">1</xsl:with-param>
    <xsl:with-param name="right_end"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
    <xsl:with-param name="flag_top"><xsl:value-of select="1" /></xsl:with-param>
    <xsl:with-param name="num_dtrs"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
    <xsl:with-param name="has_no_head"><xsl:value-of select="$has_no_head" /></xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- ====================================================================== -->

<!-- 右から順に調べ、最初に見つけた主辞のタグに p を付けた文字列を返す -->
<xsl:template name="search_rightmost_head_p">
  <!-- Parameters -->
  <xsl:param name="index"></xsl:param>
  <!-- Variables -->
  <!-- Body -->
  <xsl:choose>
    <!-- 主辞が見つからない場合 (空文字列を返す) -->
    <xsl:when test="$index = 0">
    </xsl:when>
    <!-- 句の場合 -->
    <xsl:when test="substring(name(node()[position()=$index]), string-length(name(node()[position()=$index]))) = 'p'">
      <xsl:call-template name="search_rightmost_head_p">
        <xsl:with-param name="index"><xsl:value-of select="$index - 1" /></xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <!-- テキストノードの場合 (ラベルなし) -->
    <xsl:when test="name(node()[position()=$index]) = ''">
    </xsl:when>
    <!-- leaf の場合 (ラベルなし) (到達せず?) -->
    <xsl:when test="name(node()[position()=$index]) = 'leaf'">
    </xsl:when>
    <!-- 主辞の場合 -->
    <xsl:otherwise>
      <xsl:value-of select="name(node()[position()=$index])" />p
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ====================================================================== -->
  
<!-- 木の描画 (syn="f" の場合: (1) 倒置の処理) -->
<xsl:template name="draw_tree_f_inversion">
  <!-- Parameters -->
  <xsl:param name="flag_unary"></xsl:param>
  <xsl:param name="line1"></xsl:param>
  <xsl:param name="line2"></xsl:param>
  <xsl:param name="head"></xsl:param>
  <xsl:param name="left_end"></xsl:param>
  <xsl:param name="right_end"></xsl:param>
  <xsl:param name="flag_top"></xsl:param>
  <xsl:param name="num_dtrs"></xsl:param>
  <xsl:param name="has_no_head"></xsl:param>
  <!-- Variables -->
  <!-- Body -->
  <xsl:choose>
    <!-- 現在見ている右端ノードが句の場合 (倒置) -->
    <xsl:when test="$has_no_head != '1' and substring(name(node()[position()=$right_end]), string-length(name(node()[position()=$right_end]))) = 'p'">
      <table class="tree">
        <xsl:call-template name="draw_node">
          <xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
          <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
          <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
          <xsl:with-param name="num_dtrs"><xsl:value-of select="2" /></xsl:with-param>
          <xsl:with-param name="label">
            <xsl:choose>
              <xsl:when test="$flag_top = '1'"><xsl:value-of select="name()" /></xsl:when>
              <xsl:otherwise><xsl:value-of select="$head" /></xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="decoration">
            <xsl:choose>
              <xsl:when test="$flag_top != '1'">MIDDLE_INVERSION</xsl:when>
              <xsl:otherwise>INVERSION</xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
        <tr>
          <td class="dtr_place">
            <xsl:call-template name="draw_tree_f_inversion">
              <xsl:with-param name="line1">space</xsl:with-param>
              <xsl:with-param name="line2">upper_left</xsl:with-param>
              <xsl:with-param name="head"><xsl:value-of select="$head" /></xsl:with-param>
              <xsl:with-param name="left_end"><xsl:value-of select="$left_end" /></xsl:with-param>
              <xsl:with-param name="right_end"><xsl:value-of select="$right_end - 1" /></xsl:with-param>
	      <xsl:with-param name="flag_top"><xsl:value-of select="0" /></xsl:with-param>
	      <xsl:with-param name="num_dtrs"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
            </xsl:call-template>
          </td>
          <td class="dtr_place">
            <!-- カレントノードの変更; ループは 1 回 -->
            <xsl:for-each select="node()[position()=$right_end]">
              <xsl:call-template name="draw_tree">
                <xsl:with-param name="line1">upper</xsl:with-param>
                <xsl:with-param name="line2">left</xsl:with-param>
              </xsl:call-template>
            </xsl:for-each>
          </td>
        </tr>
      </table>
    </xsl:when>
    <!-- 現在見ている右端ノードが主辞の場合 -->
    <xsl:otherwise>
      <xsl:call-template name="draw_tree_f_multi_heads">
	<xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
        <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
        <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
        <xsl:with-param name="head"><xsl:value-of select="$head" /></xsl:with-param>
        <xsl:with-param name="left_end"><xsl:value-of select="$left_end" /></xsl:with-param>
        <xsl:with-param name="right_end"><xsl:value-of select="$right_end" /></xsl:with-param>
	<xsl:with-param name="flag_top"><xsl:value-of select="$flag_top" /></xsl:with-param>
	<xsl:with-param name="num_dtrs"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ====================================================================== -->
  
<!-- 木の描画 (syn="f" の場合: (2) 複数主辞の処理) -->
<xsl:template name="draw_tree_f_multi_heads">
  <!-- Parameters -->
  <xsl:param name="flag_unary"></xsl:param>
  <xsl:param name="line1"></xsl:param>
  <xsl:param name="line2"></xsl:param>
  <xsl:param name="head"></xsl:param>
  <xsl:param name="left_end"></xsl:param>
  <xsl:param name="right_end"></xsl:param>
  <xsl:param name="flag_top"></xsl:param>
  <xsl:param name="num_dtrs"></xsl:param>
  <!-- Variables -->
  <xsl:variable name="next_head">
    <xsl:call-template name="search_next_head_index">
      <xsl:with-param name="index"><xsl:value-of select="$right_end - 1" /></xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
  <!-- Body -->
  <xsl:choose>
    <!-- 右端ノード (主辞) の左方向に他の主辞がない場合 -->
    <xsl:when test="$next_head = 0">
      <xsl:call-template name="draw_tree_f_single_head">
	<xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
        <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
        <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
        <xsl:with-param name="head"><xsl:value-of select="$head" /></xsl:with-param>
        <xsl:with-param name="left_end"><xsl:value-of select="$left_end" /></xsl:with-param>
        <xsl:with-param name="right_end"><xsl:value-of select="$right_end" /></xsl:with-param>
	<xsl:with-param name="flag_top"><xsl:value-of select="$flag_top" /></xsl:with-param>
	<xsl:with-param name="num_dtrs"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <!-- 右端ノード (主辞) の左方向に他の主辞がある場合 -->
    <xsl:otherwise>
      <table class="tree">
        <xsl:call-template name="draw_node">
          <xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
          <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
          <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
          <xsl:with-param name="num_dtrs"><xsl:value-of select="2" /></xsl:with-param>
          <xsl:with-param name="label">
            <!-- syn="f" のうちトップノードの場合はそのタグ -->
            <!-- その他の場合は主辞のラベル (ただし NULL+'p' と 'leafp' は表示しない) -->
            <xsl:choose> 
              <xsl:when test="$flag_top = '1'"><xsl:value-of select="name()" /></xsl:when>
              <xsl:when test="$head = 'p'"></xsl:when>
              <xsl:when test="$head = 'leafp'"></xsl:when>
              <xsl:otherwise><xsl:value-of select="$head" /></xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="decoration">
            <!-- 主辞のラベルの場合は装飾 -->
            <xsl:if test="$flag_top != '1'">MIDDLE</xsl:if>
          </xsl:with-param>
        </xsl:call-template>
        <tr>
          <td class="dtr_place">
            <xsl:call-template name="draw_tree_f_multi_heads">
              <xsl:with-param name="line1">space</xsl:with-param>
              <xsl:with-param name="line2">upper_left</xsl:with-param>
              <xsl:with-param name="head"><xsl:value-of select="concat(name(node()[position()=$next_head]), 'p')" /></xsl:with-param>
              <xsl:with-param name="left_end"><xsl:value-of select="$left_end" /></xsl:with-param>
              <xsl:with-param name="right_end"><xsl:value-of select="$next_head" /></xsl:with-param>
	      <xsl:with-param name="flag_top"><xsl:value-of select="0" /></xsl:with-param>
	      <xsl:with-param name="num_dtrs"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
            </xsl:call-template>
          </td>
          <td class="dtr_place">
            <xsl:call-template name="draw_tree_f_single_head">
              <xsl:with-param name="line1">upper</xsl:with-param>
              <xsl:with-param name="line2">left</xsl:with-param>
              <xsl:with-param name="head"><xsl:value-of select="$head" /></xsl:with-param>
              <xsl:with-param name="left_end"><xsl:value-of select="$next_head + 1" /></xsl:with-param>
              <xsl:with-param name="right_end"><xsl:value-of select="$right_end" /></xsl:with-param>
	      <xsl:with-param name="flag_top"><xsl:value-of select="0" /></xsl:with-param>
	      <xsl:with-param name="num_dtrs"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
            </xsl:call-template>
          </td>
        </tr>
      </table>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ====================================================================== -->

<!-- 右から順に調べ、最初に見つけた主辞の位置を返す (なければ 0) -->
<xsl:template name="search_next_head_index">
  <!-- Parameters -->
  <xsl:param name="index"></xsl:param>
  <!-- Variables -->
  <!-- Body -->
  <xsl:choose>
    <xsl:when test="$index = 0">
      0
    </xsl:when>
    <xsl:when test="substring(name(node()[position()=$index]), string-length(name(node()[position()=$index]))) = 'p'">
      <xsl:call-template name="search_next_head_index">
        <xsl:with-param name="index"><xsl:value-of select="$index - 1" /></xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$index" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ====================================================================== -->

<!-- 木の描画 (syn="f" の場合: (3) 単一主辞の処理) -->
<xsl:template name="draw_tree_f_single_head">
  <!-- Parameters -->
  <xsl:param name="flag_unary"></xsl:param>
  <xsl:param name="line1"></xsl:param>
  <xsl:param name="line2"></xsl:param>
  <xsl:param name="head"></xsl:param>
  <xsl:param name="left_end"></xsl:param>
  <xsl:param name="right_end"></xsl:param>
  <xsl:param name="flag_top"></xsl:param>
  <xsl:param name="num_dtrs"></xsl:param>
  <!-- Variables -->
  <!-- Body -->
  <xsl:choose>
    <!-- $left_end が主辞の場合 -->
    <xsl:when test="$left_end = $right_end">
      <xsl:choose>
        <!-- もともと子が 1 個だった場合 -->
        <xsl:when test="$num_dtrs = 1">
	  <table class="tree">
            <xsl:call-template name="draw_node">
              <xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
              <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
              <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
	      <xsl:with-param name="num_dtrs"><xsl:value-of select="count(node())" /></xsl:with-param>
              <xsl:with-param name="label">
		<xsl:choose>
                  <!-- syn="f" のうちトップノードの場合はそのタグ -->
                  <!-- その他の場合は主辞のラベル (ただし NULL+'p' は表示しない) -->
		  <xsl:when test="$flag_top = '1'"><xsl:value-of select="name()" /></xsl:when>
                  <xsl:when test="$head = 'p'"></xsl:when>
		  <xsl:otherwise><xsl:value-of select="$head" /></xsl:otherwise>
		</xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="decoration">
		<!-- 主辞のラベルの場合は装飾 -->
		<xsl:if test="$flag_top != '1'">MIDDLE</xsl:if>
              </xsl:with-param>
            </xsl:call-template>
	    <tr>
              <td class="dtr_place" colspan="2">
                <!-- カレントノードの変更; ループは 1 回 -->
		<xsl:for-each select="node()[position()=$right_end]">
		  <xsl:call-template name="draw_tree">
		    <xsl:with-param name="flag_unary"><xsl:value-of select="1" /></xsl:with-param>
		    <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
		    <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
		  </xsl:call-template>
		</xsl:for-each>
	      </td>
	    </tr>
	  </table>
	</xsl:when>
        <!-- もともと子が 1 個でない通常の場合 -->
	<xsl:otherwise>
          <!-- カレントノードの変更; ループは 1 回 -->
	  <xsl:for-each select="node()[position()=$right_end]">
	    <xsl:call-template name="draw_tree">
	      <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
	      <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
	    </xsl:call-template>
	  </xsl:for-each>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <!-- "$left_end != $right_end"; $left_end が主辞でない場合 -->
    <xsl:otherwise>
      <table class="tree">
        <xsl:call-template name="draw_node">
          <xsl:with-param name="flag_unary"><xsl:value-of select="$flag_unary" /></xsl:with-param>
          <xsl:with-param name="line1"><xsl:value-of select="$line1" /></xsl:with-param>
          <xsl:with-param name="line2"><xsl:value-of select="$line2" /></xsl:with-param>
          <xsl:with-param name="num_dtrs"><xsl:value-of select="2" /></xsl:with-param>
          <xsl:with-param name="label">
            <!-- syn="f" のうちトップノードの場合はそのタグ -->
            <!-- その他の場合は主辞のラベル (ただし NULL+'p' は表示しない) -->
            <xsl:choose>
              <xsl:when test="$flag_top = '1'"><xsl:value-of select="name()" /></xsl:when>
              <xsl:when test="$head = 'p'"></xsl:when>
              <xsl:otherwise><xsl:value-of select="$head" /></xsl:otherwise>
            </xsl:choose>
	  </xsl:with-param>
          <xsl:with-param name="decoration">
            <!-- 主辞のラベルの場合は装飾 -->
	    <xsl:if test="$flag_top != '1'">MIDDLE</xsl:if>
          </xsl:with-param>
        </xsl:call-template>
        <tr>
          <td class="dtr_place">
            <!-- カレントノードの変更; ループは 1 回 -->
            <xsl:for-each select="node()[position()=$left_end]">
              <xsl:call-template name="draw_tree">
                <xsl:with-param name="line1">space</xsl:with-param>
                <xsl:with-param name="line2">upper_left</xsl:with-param>
              </xsl:call-template>
            </xsl:for-each>
          </td>
          <td class="dtr_place">
            <xsl:call-template name="draw_tree_f_single_head">
              <xsl:with-param name="line1">upper</xsl:with-param>
              <xsl:with-param name="line2">left</xsl:with-param>
              <xsl:with-param name="head"><xsl:value-of select="$head" /></xsl:with-param>
              <xsl:with-param name="left_end"><xsl:value-of select="$left_end + 1" /></xsl:with-param>
              <xsl:with-param name="right_end"><xsl:value-of select="$right_end" /></xsl:with-param>
	      <xsl:with-param name="flag_top"><xsl:value-of select="0" /></xsl:with-param>
	      <xsl:with-param name="num_dtrs"><xsl:value-of select="$num_dtrs" /></xsl:with-param>
            </xsl:call-template>
          </td>
        </tr>
      </table>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ====================================================================== -->

<!-- ノードの描画 -->
<xsl:template name="draw_node">
  <!-- Parameters -->
  <xsl:param name="flag_unary"></xsl:param>
  <xsl:param name="flag_leaf"></xsl:param>
  <xsl:param name="line1"></xsl:param>
  <xsl:param name="line2"></xsl:param>
  <xsl:param name="num_dtrs"></xsl:param>
  <xsl:param name="label"></xsl:param>
  <xsl:param name="decoration"></xsl:param>
  <!-- Variables -->
  <!-- Body -->
  <tr>
    <td class="node_place">
      <xsl:attribute name="colspan"><xsl:value-of select="$num_dtrs" /></xsl:attribute>
      <table class="node" style="width: 100%">
	<xsl:if test="$flag_unary != 1"> <!-- ノードの上側の線を引く; unary の場合は必要なし -->
          <tr>
            <td class="node_place" style="width: 50%">
              <xsl:attribute name="class"><xsl:value-of select="$line1" /></xsl:attribute>
            </td>
            <td class="node_place" style="width: 50%">
              <xsl:attribute name="class"><xsl:value-of select="$line2" /></xsl:attribute>
            </td>
          </tr>
	</xsl:if>
        <tr>
          <td colspan="2">
            <xsl:attribute name="class">
              <xsl:choose>
                <xsl:when test="$flag_leaf = '1'">terminal</xsl:when>
                <xsl:otherwise>label</xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
            <div align="center">
              <xsl:choose>
		<xsl:when test="$decoration = 'MIDDLE'">
		  <span class="virtual"><xsl:value-of select="$label"/></span>
                </xsl:when>
		<xsl:when test="$decoration = 'INVERSION'">
		  <span class="inversion"><xsl:value-of select="$label"/></span>
                </xsl:when>
		<xsl:when test="$decoration = 'MIDDLE_INVERSION'">
		  <span class="vir_inver"><xsl:value-of select="$label"/></span>
                </xsl:when>
		<xsl:when test="@syn = 'c'">
		  <span class="coordination"><xsl:value-of select="$label"/></span>
                </xsl:when>
		<xsl:when test="@syn = 'a'">
		  <span class="apposition"><xsl:value-of select="$label"/></span>
                </xsl:when>
		<xsl:otherwise>
		    <xsl:value-of select="$label"/>
		</xsl:otherwise>
              </xsl:choose>
            </div>
          </td>
        </tr>
        <xsl:if test="$flag_leaf != '1'">
          <tr> <!-- ノードの下側の線を引く -->
            <td style="width: 50%" class="space" />
            <td style="width: 50%" class="left" />
          </tr>
	</xsl:if>
      </table>
    </td>
  </tr>
</xsl:template>

<!-- ====================================================================== -->

<!-- 葉の描画 -->
<xsl:template name="draw_leaf">
  <!-- Parameters -->
  <xsl:param name="flag_unary"></xsl:param>
  <xsl:param name="flag_leaf"></xsl:param>
  <xsl:param name="line1"></xsl:param>
  <xsl:param name="line2"></xsl:param>
  <xsl:param name="num_dtrs"></xsl:param>
  <!-- Variables -->
  <!-- Body -->
  <tr>
    <td class="node_place">
      <xsl:attribute name="colspan"><xsl:value-of select="$num_dtrs" /></xsl:attribute>
      <table class="node" style="width: 100%">
	<xsl:if test="$flag_unary != 1"> <!-- ノードの上側の線を引く; unary の場合は必要なし -->
          <tr>
            <td class="node_place" style="width: 50%">
              <xsl:attribute name="class"><xsl:value-of select="$line1" /></xsl:attribute>
            </td>
            <td class="node_place" style="width: 50%">
              <xsl:attribute name="class"><xsl:value-of select="$line2" /></xsl:attribute>
            </td>
          </tr>
	</xsl:if>
        <tr>
          <td colspan="2" class="terminal">
            <div align="center">
	      <xsl:choose>
		<xsl:when test="count(node()) > 0"> <!-- leaf, sr, srp -->
		  <xsl:call-template name="repl_sense_number">
		  </xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:value-of select="."/>
		</xsl:otherwise>
	      </xsl:choose>
            </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</xsl:template>

<!-- ====================================================================== -->

<!-- leaf, sr, srp タグのどれかが付いた表示上の「葉」かどうか返す -->
<xsl:template name="check_pseudo_leaf">
  <!-- Parameters -->
  <!-- Variables -->
  <!-- Body -->
  <xsl:choose>
    <xsl:when test="name() = 'leaf' or name() = 'sr' or name() = 'srp'">1</xsl:when>
    <xsl:otherwise>0</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ====================================================================== -->

<!-- target を強調表示 -->
<xsl:template match="target">
  <!-- Parameters -->
  <!-- Variables -->
  <!-- Body -->
  <span class="target"><xsl:value-of select="text()" /></span>
</xsl:template>

<!-- ====================================================================== -->
<!-- ====================================================================== -->

<xsl:template name="repl_sense_number" match="sr">
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

<!-- ====================================================================== -->

</xsl:stylesheet>
