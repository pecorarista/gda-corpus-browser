package jp.or.gsk.gdacb.search_engine;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class GDA_File {
	protected abstract void set_mor_info (String[] s,int p,int l);
	protected abstract int get_leftmost_boundary_for_left_kwic (int p);
	protected abstract String xsl_file_name(String s);
	protected abstract void output_metadata(DataOutputStream o) throws SE_Exception;
	protected abstract int[] get_range_of_sentence(int p);
	
	private int mor_info_posit = 0;
	String mor_POS = null;
	String mor_CONJ = null;
	String mor_BASE = null;
	String mor_YOMI = null;
	String mor_SENSE = null;
	byte[] byte_text = null;
	private String input_file_name;
	private RandomAccessFile in = null;
	
	private int left_kwic_length = 40;	// default 
	private int right_kwic_length = 40;	// default
	
	private static String target_open_tag = "<target>";
	private static String target_close_tag = "</target>";
	private static String leaf_open_tag = "<leaf>";
	private static String leaf_close_tag = "</leaf>";
	
	// constructor
	GDA_File (String file) {
		this.input_file_name = file;
	}
	
	// GDAファイルをまるごと読み込むメソッド
	void read_all() throws SE_Exception {
		try {
			in = new RandomAccessFile(input_file_name,"r");
			byte_text = new byte[(int) in.length()];
			int len = in.read(byte_text);
			if (len == -1) {
				SE_Exception e = new SE_Exception();
				File f = new File(input_file_name);
				e.setMsgJ("GDAファイル`"+f.getName()+"'の読み込みに失敗しました");
				e.setMsgE("Fail to read GDA file `"+f.getName()+"'");
				throw e;
			}
			in.close();
		} catch (IOException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			File f = new File(input_file_name);
			e2.setMsgJ("GDAファイル`"+f.getName()+"'の読み込みに失敗しました");
			e2.setMsgE("Fail to read GDA file `"+f.getName()+"'");
			throw e2;
		} finally {
			try {
				if (in != null) in.close();
			} catch (IOException e) {}
		}
	}
	/**
	 * GDAファイル名を返すメソッド
	 */
	String filename () {
		return input_file_name;
	}
	/**
	 * ファイル名からエントリIDを抽出するメソッド
	 */
	String entry_id () {
		Pattern p = Pattern.compile("([^\\/\\.]+)\\.[^\\.]+$");
		Matcher m = p.matcher(this.input_file_name);
		if(m.find()){
			return m.group(1);
		}else{
			return null;
		}
	}
	// 左右の幅をセットするメソッド、引数は文字数
	void kwic_length (int char_length) {
		left_kwic_length = char_length * 2;
		right_kwic_length = char_length * 2;
	}
	// 左の幅をセットするメソッド、引数は文字数
	void kwic_left_length (int char_length) {
		left_kwic_length = char_length * 2;
	}
	// 右の幅をセットするメソッド、引数は文字数
	void kwic_right_length (int char_length) {
		right_kwic_length = char_length * 2;
	}
	
	// 左文脈を返すメソッド
	String generate_left_kwic (int posit) {
		//byte left_byte = 0;
		
		if(left_kwic_length == 0) return "";

		int left_most_boundary = get_leftmost_boundary_for_left_kwic(posit);
		boolean inside_tag_flag = false;
		byte[] byte_buf = new byte[left_kwic_length];
		int j = left_kwic_length - 1;
		for(int i=posit-1 ; i >= left_most_boundary ; ) {
			if(inside_tag_flag){
				if(byte_text[i] == 60){  // == '<'
					inside_tag_flag = false;
				}
				i--;
			}else{
				if(byte_text[i] == 62){  // == '>'
					inside_tag_flag = true;
					i--;
				}else if(byte_text[i] == 10){	// == '\n'
					i--;
				}else if(i > 0 && isShiftJIS(byte_text[i-1])){	// 2byte文字のとき
					if(j >= 1){
						byte_buf[j] = byte_text[i];
						j--; i--;
						byte_buf[j] = byte_text[i];
						j--; i--;
						if (j < 0) break;
					}else{
						break;
					}
				}else{	// 1byte文字のとき
					byte_buf[j] = byte_text[i];
					j--; i--;
					if(j < 0) break;
//					if (j < 0){
//						// 左文脈の直左にあるバイト文字を記憶する
//						left_byte = (i > 0) ? byte_text[i-1] : 0;
//						break;
//					}
				}
			}
		}
		// 左文脈の先頭が2byte文字の境界にあたるかをチェックする
		// SJISの2byte目には任意のコードが出現するので、この方式はうまくいかない
//		if(j < 0 && isShiftJIS(left_byte)){
//			byte_buf[0] = 32;	// white space
//		}
		while(j >= 0){
			byte_buf[j] = 32;	// white space
			j--;
		}
		try {
			return new String(byte_buf,"Shift_JIS");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
	}
	// 右文脈を返すメソッド(第1引数の位置から作成する)
	String generate_right_kwic (int posit) {
		if(right_kwic_length == 0) return "";
		boolean inside_tag_flag = false;
		byte[] byte_buf = new byte[right_kwic_length];
		int j = 0;
		for(int i=posit ; i < byte_text.length ; ) {
			if(inside_tag_flag){
				if(byte_text[i] == 62){  // == '>'
					inside_tag_flag = false;
				}
				i++;
			}else{
				if(byte_text[i] == 60){  // == '<'
					inside_tag_flag = true;
					i++;
				}else if(byte_text[i] == 10){	// == '\n'
					i++;
				}else if(isShiftJIS(byte_text[i])){	// 2byte文字のとき
					if(j <= byte_buf.length - 2){
						byte_buf[j] = byte_text[i];
						j++; i++;
						byte_buf[j] = byte_text[i];
						j++; i++;
						if (j >= right_kwic_length) break;
					}else{
						break;
					}
				}else{	// 1byte文字のとき
					byte_buf[j] = byte_text[i];
					j++; i++;
					if (j >= right_kwic_length) break;
				}
			}
		}
		// 右文脈の末尾が2byte文字の境界にあたるかをチェックする
		// SJISの2byte目には任意のコードが出現するので、この方式はうまくいかない
//		if(j >= right_kwic_length &&
//		   isShiftJIS(byte_buf[right_kwic_length-2]) &&
//		   isShiftJIS(byte_buf[right_kwic_length-1])){
//			System.out.println((byte_buf[right_kwic_length-2]&0xFF)+" "+(byte_buf[right_kwic_length-1]&0xFF));
//			byte_buf[right_kwic_length-1] = 32;	// white space		
//		}
		while(j < right_kwic_length){
			byte_buf[j] = 32;	// white space
			j++;
		}
		try {
			return new String(byte_buf,"Shift_JIS");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
	}
	// 右文脈を返すメソッド(第2引数の数だけ文字を読み飛ばす)
	String generate_right_kwic (int posit,int len) {
		int i;
		
		if(right_kwic_length == 0) return "";
		boolean inside_tag_flag = false;
		for(i=posit ; i < byte_text.length ; i++) {
			if(inside_tag_flag){
				if(byte_text[i] == 62){  // == '>'
					inside_tag_flag = false;
				}
			}else{
				if(byte_text[i] == 60){  // == '<'
					inside_tag_flag = true;
				}else{
					len--;
					if(len == 0) break;
				}
			}
		}
		if(len == 0){
			return generate_right_kwic(i+1);
		}else{
			return "";
		}
	}

	/**
	 * キーワードを返すメソッド
	 * @param posit		キーワードのテキストにおける位置
	 * @param len		キーワードの長さ
	 * @return			キーワード
	 */
	String extract_key (int posit, int len) {
//		return get_substring(posit, posit+len-1);
		boolean inside_tag_flag = false;
		byte[] key_byte = new byte[len];
		int p = 0;
		for(int i=posit ; i < byte_text.length ; i++){
			if(inside_tag_flag){
				if(byte_text[i] == 62){	// == >
					inside_tag_flag = false;
				}
			}else{
				if(byte_text[i] == 60){	// == <
					inside_tag_flag = true;
				}else{
					key_byte[p] = byte_text[i];
					p++;
					if(p >= len) break;
				}
			}
		}
		try {
			return new String(key_byte,"Shift_JIS");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}	
		
	}
	/**
	 * 品詞を返すメソッド
	 * @param posit		キーワードのテキストにおける位置
	 * @param len		キーワードの長さ
	 * @return			品詞
	 */
	String pos (int posit,int len) {
		if (posit != mor_info_posit) {
			extract_mor_info(posit,len);
		}
		return mor_POS;
	}
	/**
	 * 活用形を返すメソッド
	 * @param posit		キーワードのテキストにおける位置
	 * @param len		キーワードの長さ
	 * @return			活用形
	 */
	String conj (int posit,int len) {
		if (posit != mor_info_posit) {
			extract_mor_info(posit,len);
		}
		return mor_CONJ;
	}
	/**
	 * 基本形を返すメソッド
	 * @param posit		キーワードのテキストにおける位置
	 * @param len		キーワードの長さ
	 * @return			基本形
	 */
	String base (int posit,int len) {
		if (posit != mor_info_posit) {
			extract_mor_info(posit,len);
		}
		return mor_BASE;
	}
	/**
	 * 読みを返すメソッド
	 * @param posit		キーワードのテキストにおける位置
	 * @param len		キーワードの長さ
	 * @return			基本形
	 */
	String yomi (int posit,int len) {
		if (posit != mor_info_posit) {
			extract_mor_info(posit,len);
		}
		return mor_YOMI;
	}
	/**
	 * 語義を返すメソッド
	 * @param posit		キーワードのテキストにおける位置
	 * @param len		キーワードの長さ
	 * @return			語義
	 */
	String sense (int posit,int len) {
		if (posit != mor_info_posit) {
			extract_mor_info(posit,len);
		}
		return mor_SENSE;
	}
	// Methods below will be overridden by GDA_File_IWA
	String extract_midasi () { return null; };
	String extract_sense_def (String s) { return null; };

	/**
	 * positの位置にある単語の出現形を返すメソッド
	 * positが単語の途中にある場合にも対応する
	 * @param posit		テキストの位置
	 * @return			単語の出現形
	 */
	String extract_word(int posit){
		int i, start=0, end=0;
		for(i=posit-1 ; i >= 0 ; i--){
			if(byte_text[i] == 62){	// == '>'
				start = i + 1;
				break;
			}
		}
		for(i=posit ; i < byte_text.length ; i++){
			if(byte_text[i] == 60){	// == '<'
				end = i - 1;
				break;
			}
		}
		return get_substring(start,end);
	}
	/**
	 * positの位置にある文(<su></su>で囲まれた文字列)の範囲を返すメソッド
	 * @param posit		テキストにおける位置
	 * @return			[開始位置,終了位置] という配列
	 */
	int[] get_range_of_su_tagged_sentence (int posit){
		// ToDo このアルゴリズムで本当に大丈夫か?
		// (以下の説明は後方に</su>タグを探す場合。前方に<su>を探す場合も同様)
		// 開始地点の右にある<su>と</su>の位置を近いものからそれぞれ順番に求める
		// <su>の入れ子があるときは、前者の位置は後者の位置よりも小さい
		// このときは、次に近い<su>と</su>の位置をそれぞれ求める
		// 入れ子が解消されたときは、前者の位置は後者よりも大きくなる
		int start, end, k1, k2, p1, p2;
		Byte_Comparer_BM bcBM_close_su_tag = new Byte_Comparer_BM("</su>");
		
		// 開始位置(<su>タグの位置)を探す
		start = -1;
		k1 = posit;		// <su>の探索開始位置
		k2 = posit;		// </su>の探索開始位置
		// p1: k1より左にあって最も近い<su>の位置
		// p2: k2より左にあって最も近い</su>の位置
		while(k1 >= 0){
			p1 = search_open_su_tag_backward(k1);
			p2 = bcBM_close_su_tag.search_backward(byte_text, k2);
			if(p1 == -1) return null;
			if(p2 == -1 || p2 < p1){
				start = p1;
				break;
			}
			k1 = p1 - 1;
			k2 = p2 - 1;
		}
		if(start == -1) return null;

		// 終了位置(</su>タグの位置)を探す
		end = -1;
		k1 = start+1;	// <su>の探索開始位置 
		k2 = start+1;	// </su>の探索開始位置 
		// p1: k1より右にあって最も近い<su>の位置
		// p2: k2より右にあって最も近い</su>の位置
		while(k2 < byte_text.length){
			p1 = search_open_su_tag_forward(k1);
			p2 = bcBM_close_su_tag.search_forward(byte_text, k2);
			if(p2 == -1) return null;
			if(p1 == -1 || p2 < p1){
				end = p2 + bcBM_close_su_tag.pat_length-1;
				break;
			}
			k1 = p1 + 3;
			k2 = p2 + bcBM_close_su_tag.pat_length;
		}
		if(end == -1) return null;
		
		int[] range = new int[2];
		range[0] = start;
		range[1] = end;
		return range;
	}
	/**
	 * positの位置にある文を文字列として返すメソッド
	 */
	String extract_sentence (int posit){
		int[] su_range = get_range_of_sentence(posit);
		if(su_range == null) return null;
		return get_substring(su_range[0],su_range[1]);
	}

	/**
	 * GDAファイルをコピーするメソッド
	 * - xslファイルのパスを埋め込む
	 * - 検索キーワードを<target>というタグで囲む
	 */
	public void output_xml_all(File out_file,int posit,int length,boolean br_sent_flag,Boolean sjis_js_flag) throws SE_Exception, IOException {
		int p, xsl_posit;
		String s;
		BufferedOutputStream out = null;
		TagPositMap target_tag_map = null;
		
		try {
			if(byte_text == null) read_all();
		} catch (SE_Exception e) {
			throw e;
		}
		// xslのパスを挿入する位置を得る
		xsl_posit = -1;
		for(p=0 ; p < byte_text.length ; p++){
			if(byte_text[p] == 62){	// == '>'
				xsl_posit = p + 1;
				break;
			}
		}
		if(xsl_posit == -1 || xsl_posit >= byte_text.length){
			SE_Exception e = new SE_Exception();
			e.setMsgJ("GDAファイルの中にタグがありません");
			e.setMsgE("ERROR: no tag in the file");
			throw e;
		}
		// <target>, </target> を挿入する位置を得る
		if(posit != -1){
			target_tag_map = get_tag_posit_map(posit,length,false);
			if(target_tag_map == null || xsl_posit >= target_tag_map.start_posit()){
				SE_Exception e = new SE_Exception();
				e.setMsgJ("検索キーが見つかりません");
				e.setMsgE("ERROR: fail to find search keyword in GDA file");
				throw e;
			}
		}else{
			target_tag_map = null;
		}
		// ファイルを出力する
		try {
			out = new BufferedOutputStream( new FileOutputStream(out_file) );
			
			for(p=0 ; p < xsl_posit ; p++){
				out.write(byte_text[p]);
			}

			s = "<?xml-stylesheet href=\""; 
			out.write( s.getBytes("Shift_JIS") );

			if(sjis_js_flag){
				if(br_sent_flag){
					s = xsl_file_name("_bs");
				}else{
					s = xsl_file_name("_ns");
				}
			}else{
				if(br_sent_flag){
					s = xsl_file_name("_bu");
				}else{
					s = xsl_file_name("_nu");
				}
			}
			out.write( s.getBytes("Shift_JIS") );

			s = "\" type=\"text/xsl\" ?>";
			out.write( s.getBytes("Shift_JIS") );

			if(posit != -1){
				for(Integer k: target_tag_map.keySet()){
					for( ; p < k ; p++){
						out.write(byte_text[p]);
					}
					out.write(target_tag_map.get(k).toString().getBytes("Shift_JIS"));
				}
			}
			for( ; p < byte_text.length ; p++){
				out.write(byte_text[p]);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {}
		}
	}
	/**
	 * GDAファイルから構文木を取り出し、ファイルに出力するメソッド
	 * - xslファイルのパスを埋め込む
	 * - 検索キーワードを<target>というタグで囲む
	 * 
	 * @param out_file	出力ファイル
	 * @param posit		検索キーワードの開始位置
	 * @param length	検索キーワードの長さ
	 * @return 出力に成功したか(falseは、検索キーワードを囲む<su></su>が見つからなかったとき)
	 */
	public boolean output_xml_tree (File out_file,int posit,int length) throws SE_Exception, IOException {
		int p;
		String s;
		BufferedOutputStream out = null;
		int[] sentence_range = null;
		TagPositMap insert_tag_map;
		
		try {
			if(byte_text == null) read_all();
		} catch (SE_Exception e) {
			throw e;
		}

		sentence_range = get_range_of_sentence(posit);
		if(sentence_range == null) return false;
		insert_tag_map = get_tag_posit_map(posit,length,true);
		if(insert_tag_map == null){
			System.err.println("ERROR: no target word found");
		}else{
			if(sentence_range[0] > insert_tag_map.start_posit() ||
			   sentence_range[1] < insert_tag_map.end_posit()){
				insert_tag_map = null;
				System.err.println("ERROR: target word crosses over for a sentence");
			}
		}

		// ファイルを出力する
		try {
			out = new BufferedOutputStream( new FileOutputStream(out_file) );
			
			s = "<?xml version=\"1.0\" encoding=\"Shift_JIS\"?><?xml-stylesheet href=\"syn_tree.xsl\" type=\"text/xsl\" ?>\n<gda>\n";
			out.write( s.getBytes("Shift_JIS") );

			// ToDo デバッグ情報の出力
			//s = "<!-- " + input_file_name + "," + posit + "," + length + " -->\n";
			//out.write( s.getBytes("Shift_JIS") );
			
			//if(sentence_range == null){
			//	s = "<ERROR>構文木が見つかりませんでした</ERROR>";
			//	out.write( s.getBytes("Shift_JIS") );
			//}else{
				if(insert_tag_map == null){
					for(p=sentence_range[0] ; p <= sentence_range[1] ; p++){
						out.write(byte_text[p]);
					}
				}else{
					p = sentence_range[0];
					for(Integer k: insert_tag_map.keySet()){
						for( ; p < k ; p++)	out.write(byte_text[p]);
						out.write(insert_tag_map.get(k).toString().getBytes("Shift_JIS"));
					}
					for( ; p <= sentence_range[1] ; p++) out.write(byte_text[p]);
				}
			//}

			s = "\n</gda>\n";
			out.write( s.getBytes("Shift_JIS") );
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {}
		}
		return true;
	}
	/**
	 * タグを取り除いたテキストを取り出し、ファイル out に書き込むメソッド
	 */
	void output_text(DataOutputStream out) throws SE_Exception{
		boolean tag_flag = false;
		int i;
		try {
			for(i=0 ; i < byte_text.length ; i++){
				if(tag_flag){
					if(byte_text[i] == 62){		// >
						tag_flag = false;
					}
				}else{
					if(byte_text[i] == 60){		// <
						tag_flag = true;
					}else{
						out.writeByte(byte_text[i]);
					}
				}
			}
			// ファイルの最後に 0 を記入
			byte b = 0;
			out.write(b);
		} catch (IOException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgE("CODE: GDA_File::output_text");
			throw e2;
		}
	}
	/**
	 * 部分文字列を返すメソッド
	 */
	String get_substring (int start,int end) {
		int len = end - start + 1;
		if ( start < 0 || byte_text.length < end || len <= 0 ) {
			return null;
		}
		byte [] word_byte = new byte[len];
		for(int i=0 ; i < len ; i++) {
			word_byte[i] = byte_text[start+i];
		}
		try {
			return new String(word_byte,"Shift_JIS");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
	}
	/**
	 * 前方方向に <su> タグを探索し、その開始位置を返すメソッド
	 * suで始まるタグは <su> の他に <sup>, <subp> があることに留意する
	 */
	int search_open_su_tag_forward (int posit){
		// suで始まるタグは <su> の他に <sup>, <subp> があることに留意する
		Byte_Comparer_BM bm_open_su_tag = new Byte_Comparer_BM("<su");
		int su_posit = -1;
		int k = posit;
		while(k < byte_text.length){
			su_posit = bm_open_su_tag.search_forward(byte_text, k);
			if(su_posit== -1) return -1;
			if(byte_text[su_posit+bm_open_su_tag.pat_length] == 32 ||  // == ' '
			   byte_text[su_posit+bm_open_su_tag.pat_length] == 62){	// == '>'
				return su_posit;
			}else{
				k = su_posit + 1;
			}
		}
		return -1;
	}
	/**
	 * 後方方向に <su> タグを探索し、その開始位置を返すメソッド
	 * suで始まるタグは <su> の他に <sup>, <subp> があることに留意する
	 */
	int search_open_su_tag_backward (int posit){
		Byte_Comparer_BM bm_open_su_tag = new Byte_Comparer_BM("<su");
		int su_posit = -1;
		int k = posit;
		while(k >= 0){
			su_posit = bm_open_su_tag.search_backward(byte_text, k);
			if(su_posit== -1) return -1;
			if(byte_text[su_posit+bm_open_su_tag.pat_length] == 32 ||  // == ' '
			   byte_text[su_posit+bm_open_su_tag.pat_length] == 62){	// == '>'
				return su_posit;
			}else{
				k = su_posit - 1;
			}
		}
		return -1;
	}
	/**
	 * <mor>タグの属性から形態素情報(品詞、活用形、基本形、読み、語義)を取り出し、
	 * 内部変数に格納するメソッド
	 * さらに、内部変数に保持されている単語のオフセットを mor_info_posit に記録する
	 * 形態素情報の取り出しに失敗したとき、語義がない、などは空列を格納する
	 */
	private void extract_mor_info (int posit,int len) {
		Pattern p_mph = Pattern.compile("mph=\"[^\"]+\"");
		Pattern p_sem = Pattern.compile("sem=\"[^\"]+\"");

		String tag = extract_preceding_tag(posit);
//        System.out.println("DEBUG: extract_mor_info is executed");
		if (tag == null) {
			mor_POS = "";
			mor_CONJ = "";
			mor_BASE = "";
			mor_YOMI = "";
			mor_SENSE = "";
			mor_info_posit = posit;
			return;
		}
		
		// mph属性から品詞、活用形、基本形、読みを取り出す
		Matcher m1 = p_mph.matcher(tag);
		if (m1.find()) {
			// mph_attr に mph="..." の中身を代入
			String mph_attr = tag.substring(m1.start()+5,m1.end()-1);
			Pattern p = Pattern.compile(";");
			String[] mor = p.split(mph_attr);
			set_mor_info(mor,posit,len); // implemented in Subclass
		}else{
			mor_POS = "";
			mor_CONJ = "";
			mor_BASE = "";
			mor_YOMI = "";
		}

		// sem属性から語義を取り出す
		Matcher m2 = p_sem.matcher(tag);
		if (m2.find()) {
			// sem_attr に sem="..." の中身を代入
			String sem_attr = tag.substring(m2.start()+5,m2.end()-1);
			mor_SENSE = sem_attr;
		}else{
			mor_SENSE = "";
		}
		
		mor_info_posit = posit;
		return;
	}
	/**
	 * posit の直前にあるタグを文字列にして返すメソッド
	 */
	private String extract_preceding_tag(int posit) {
		int start = -1;
		int end = -1;
		int i;
		for(i=posit-1 ; i >= 0 ; i--){
			if (byte_text[i] == 62) {	// == '>'
				end = i;
				break;
			}
		}
		if(end == -1) return null;
		for(i=end-1 ; i >= 0 ; i--) {
			if (byte_text[i] == 60) {	// == '<'
				start = i;
				break;
			}
		}
		if(start == -1)	return null;
		
		return get_substring(start,end);
	}
	/**
	 * GDAファイルに以下のタグを挿入するための情報を得る
	 *   target タグ: 検索キーワードを囲むタグ
	 *   leaf タグ: 検索キーワードを含む葉ノードを囲むタグ
	 *   
	 * @param posit		検索キーワードの先頭の位置
	 * @param length	検索キーワードの長さ
	 * @param add_leaf_tag_flag		leaf タグを挿入するか否か
	 * @return			TagPositMap(private class)
	 * 					キー=位置、値=挿入するタグの文字列、とするTreeMap
	 */
	private TagPositMap get_tag_posit_map(int posit,int length,boolean add_leaf_tag_flag){
		int p, posit_in_kw, kw_start, kw_end;
		TagPositMap map = new TagPositMap();
	
		p = posit;
		posit_in_kw = 0;  // 処理済みの検索キーワードの長さ
		kw_start = posit; // 現在のキーワード断片の開始位置
		kw_end = posit;	  // 現在のキーワード断片の終了位置
		while(posit_in_kw < length){
			if(p >= byte_text.length) return null;	// ERROR
			if(byte_text[p] == 60){		// == '<'
				// target, leaf タグを挿入する
				insert_target_tag(map,kw_start,kw_end);
				if(add_leaf_tag_flag){
					if(! insert_leaf_tag(map,kw_start,kw_end)) return null;
				}
				// 次の断片を探す
				while(p < byte_text.length){
					p++;
					if(byte_text[p] == 62){			// == '>'
						if(p < byte_text.length - 1 &&
						   byte_text[p+1] == 60){ 	// == '<'
							p++;
						}else{
							break;
						}
					}
				}
				p++;
				kw_start = p;
				kw_end = p;
			}else{
				p++;
				posit_in_kw++;
				kw_end++;
			}
		}
		if(p >= byte_text.length) return null;	// ERROR
		// target, leaf タグを挿入する
		insert_target_tag(map,kw_start,kw_end);
		if(add_leaf_tag_flag){
			if(! insert_leaf_tag(map,kw_start,kw_end)) return null;
		}
		return map;
	}
	/**
	 * TagPositMap に target タグを挿入するメソッド
	 */
	private void insert_target_tag (TagPositMap map,int start,int end){
		map.put_entry(start, target_open_tag, true);
		map.put_entry(end, target_close_tag, false);
	}
	/**
	 * TagPositMap に leaf タグを挿入するメソッド
	 * 戻値は挿入に成功したか否か
	 */
	private boolean insert_leaf_tag (TagPositMap map,int start,int end){
		int i;
		// 検索キーワードを囲む葉に leaf タグを挿入
		i = start;
		while(i >= 0){
			if(byte_text[i] == 62){	// == '>'
				map.put_entry(i+1, leaf_open_tag, true);
				break;
			}
			i--;
		}
		if(i < 0) return false;	// ERROR
		i = end;
		while(i < byte_text.length){
			if(byte_text[i] == 60){	// == '<'
				map.put_entry(i, leaf_close_tag, false);
				break;
			}
			i++;
		}
		if(i >= byte_text.length) return false;	// ERROR
		return true;
	}
	/**
	 * Shift_JISの2byte文字の1文字目に該当するかを判定するメソッド
	 */
	private boolean isShiftJIS(byte b){
		int i = b & 0xFF;
		if((i >= 0x81 && i <= 0x9F)||(i >= 0xE0 && i <= 0xEF)) return true;
		return false;
	}
	
	// internal class
	protected class Byte_Comparer {
		private byte[] byte_pattern;
		protected int pat_length = -1;
		
		Byte_Comparer (String pattern){
			try {
				this.byte_pattern = pattern.getBytes("Shift_JIS");
				this.pat_length = byte_pattern.length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				throw new IllegalStateException(e.getMessage());
			}
		}
		
		int search_forward (byte[] b,int start) {
			if(start < 0) return -1;
			for(int i=start ; i <= b.length - this.pat_length ; i++){
				if(match_body(b,i)) return i;
			}
			return -1;
		}
		int search_backward (byte[] b,int start) {
			if(start > b.length - this.pat_length) return -1;
			for(int i=start ; i >= 0 ; i--){
				if(match_body(b,i)) return i;
			}
			return -1;
		}
		boolean match (byte[] b,int p) {
			if(p < 0) return false;
			if(b.length <= p + this.pat_length) return false;
			return match_body(b,p);
		}
		// 位置pでパターンとマッチするかを調べる
		// インデックスp+パターンの長さが配列の範囲を超えないことを確認してから呼び出すこと
		private boolean match_body (byte[] b,int p) {
			for(int i=0 ; i < this.pat_length ; i++) {
				if(b[p+i] != byte_pattern[i])
					return false;
			}
			return true;
		}
	}
	
	// Boyer-Moore algorithm によるバイト列の検索
	protected class Byte_Comparer_BM {
		private byte[] byte_pattern;
		protected int pat_length;
		HashMap<Byte,Integer> BM_fw_table, BM_bw_table;
		
		// constructor
		Byte_Comparer_BM (String pattern){
			try {
				byte_pattern = pattern.getBytes("Shift_JIS");
				this.pat_length = byte_pattern.length;
				BM_fw_table = make_BM_fw_table(byte_pattern);
				BM_bw_table = make_BM_bw_table(byte_pattern);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				throw new IllegalStateException(e.getMessage());
			}
		}
		
		/* バックスライド(移動量が負)になったとき、次のポインタは1ではなく2ずらすことができる
		   1文字ずれればパターンにマッチする状況下ではバックスライドは発生しないため、
		   1文字ずらしての検査はしなくてもよい。
		   参考: http://fussy.web.fc2.com/algo/algo7-4.htm */

		int search_forward (byte[] b,int start) {
			int i,p,move;
			
//			if (b.length < start + pat_length) return -1;
			p = start + pat_length - 1;
			while( p < b.length ) {
				move = -1;
				for(i=0 ; i < pat_length ; i++){ 
					if(b[p-i] != byte_pattern[pat_length-i-1]){
						move = BM_fw_table.containsKey(b[p-i]) ? BM_fw_table.get(b[p-i]) : pat_length;
						move -= i;	// i = 今照合しているのはパターンの末尾から何文字目か?
						if(move < 0) move = 2;	// バックスライド
//						System.out.println("p="+p+"("+b[p]+"<=>"+byte_pattern[i]+"), i="+i+", move="+move);
						p += move;
						break;
					}
				}
				if(move == -1) return (p - pat_length + 1);	// マッチ
			}
			return -1;
		}
		int search_backward (byte[] b,int start) {
			int i,p,move;
			
			p = (b.length < start + pat_length) ? b.length - pat_length : start;
			while( p >= 0 ) {
				move = -1;
				for(i=0 ; i < pat_length ; i++){ 
					if(b[p+i] != byte_pattern[i]){
						move = BM_bw_table.containsKey(b[p+i]) ? BM_bw_table.get(b[p+i]) : pat_length;
						move -= i;	// i = 今照合しているのはパターンの先頭から何文字目か?
						if(move < 0) move = 2;	// バックスライド
//						System.out.println(b[p]+"<=>"+byte_pattern[i]+", p="+p+", i="+i+", move="+move);
						p -= move;
						break;
					}
				}
				if(move == -1) return p;	// マッチ
			}
			return -1;
		}

		private HashMap<Byte,Integer> make_BM_fw_table (byte[] pat) {
			int move = pat_length - 1;
			HashMap<Byte,Integer> tbl = new HashMap<Byte,Integer>();
			for(int i=0 ; i < pat_length ; i++) {
				tbl.put(pat[i],move);
//				System.out.println(pat[i] + " " + move);
				move--;
			}
			return tbl;
		}
		private HashMap<Byte,Integer> make_BM_bw_table (byte[] pat) {
			int move = pat_length - 1;
			HashMap<Byte,Integer> tbl = new HashMap<Byte,Integer>();
			for(int i=pat_length-1 ; i >= 0 ; i--) {
				tbl.put(pat[i],move);
//				System.out.println(pat[i] + " " + move);
				move--;
			}
			return tbl;
		}
	}

	/** テキスト中の位置をキー、挿入するタグの文字列を値とする TreeMap を保持するクラス
	 *  GDAファイルや構文木を表示する際に target や leaf タグを挿入するために使う
	 */
	private class TagPositMap extends TreeMap<Integer,StringBuilder>{
		private static final long serialVersionUID = 1L;

		// 位置positにタグ文字列tagを挿入する
		// 葉に近いタグから順に挿入することを仮定している
		private void put_entry(int posit,String tag,boolean open_tag_flag){
			if(this.containsKey(posit)){
				StringBuilder sb = this.get(posit);
				if(open_tag_flag){
					sb.insert(0, tag);
				}else{
					sb.append(tag);
				}
				this.put(posit, sb);
			}else{
				this.put(posit, new StringBuilder(tag));
			}
		}

		private int start_posit(){
			return this.firstKey();
		}
		private int end_posit(){
			return this.lastKey();
		}
	}
	
	/**
     * @param args
     */
    public static void main(String[] args) {
    	//GDA_File_IWA gda = new GDA_File_IWA("../data/iwa_final/dict16/08/30095.dict");
    	//GDA_File_MAI gda = new GDA_File_MAI("../data/mai_final/a01-2/00015950.gda");
    	//GDA_File_IWA gda = new GDA_File_IWA("../data/iwa_final/dict01/01/00007.dict");
    	GDA_File_IWA gda = new GDA_File_IWA("../data/iwa_final/dict01/01/00001.dict");

    	File o = new File("tmp.xml");
    	try {
    		// gda.output_xml_tree(o, 747, 4);
    		// gda.output_xml_tree(o, 10062, 14);
    		//gda.output_xml_tree(o, 2572, 10);
    		gda.output_xml_tree(o, 903, 10);
    	} catch (SE_Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("tmp.xml is outputed");
    }
}
