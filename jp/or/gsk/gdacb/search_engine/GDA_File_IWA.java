package jp.or.gsk.gdacb.search_engine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class GDA_File_IWA extends GDA_File {
	public GDA_File_IWA(String file) {
		super(file);
	}

	@Override
	protected void set_mor_info (String [] mor,int posit,int len) {
		mor_POS = mor[0];
		mor_CONJ = mor[1];
		mor_BASE = mor[2].equals("") ? extract_word(posit) : mor[2];
		mor_YOMI = mor[3];
	}

	// 辞書見出しを返すメソッド
	@Override
	protected String extract_midasi () {
		byte[] byte_midasi = extract_midasi_byte();
		if(byte_midasi == null) return "NA";
		try {
			return new String(byte_midasi,"Shift_JIS");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	private byte[] extract_midasi_byte() { 
		int start,end,p1,p2;
		boolean white_space_flag = false;
		byte[] byte_midasi;
//		byte_comparer bc1 = new byte_comparer("<entry");
//		byte_comparer bc2 = new byte_comparer("entry=\"");
		Byte_Comparer_BM bc_BM1 = new Byte_Comparer_BM("<entry");
		Byte_Comparer_BM bc_BM2 = new Byte_Comparer_BM("entry=\"");

		start = -1;
//		p1 = bc1.search_forward(byte_text, 0);
		p1 = bc_BM1.search_forward(byte_text,0);
		if(p1 != -1){
//			p2 = bc2.search_forward(byte_text, p1+bc1.length);
			p2 = bc_BM2.search_forward(byte_text, p1+bc_BM2.pat_length);
			if(p2 != -1) {
				start = p2 + bc_BM2.pat_length;
			}
		}
		if(start == -1) return null;

		end = -1;
		for(p1=start+1 ; p1 < byte_text.length ; p1++) {
			if(byte_text[p1] == 34) {	// == "
				end = p1 - 1;
				break;
			}else if(byte_text[p1] == 32) {	// == ' '
				white_space_flag = true;
			}else if(byte_text[p1] == 62) { // == >
				break;
			}else if(byte_text[p1] == 60) { // == <
				break;
			}
		}
		if(end == -1) return null;

		if(white_space_flag) {
			// "かな見出し 漢字表記 ..."  という文字列を
			// "かな見出し[漢字表記,...]" に変換する
			byte_midasi = new byte[end-start+2];
			boolean flag = false;
			for(p1=0 ; p1 < byte_midasi.length-1 ; p1++) {
				if(byte_text[start+p1] == 32) {	// == ' '
					if(flag){
						// 2番目以降のスペースは , に置換
						byte_midasi[p1] = 44;	// ','
					}else{
						// 最初のスペースは [ に置換
						byte_midasi[p1] = 91;	// '['
						flag = true;
					}
				}else{
					byte_midasi[p1] = byte_text[start+p1];
				}
			}
			byte_midasi[byte_midasi.length-1] = 93;	// ']'
			return byte_midasi;
		}else{
			byte_midasi = new byte[end-start+1];
			for(int i=0 ; i < byte_midasi.length ; i++) {
				byte_midasi[i] = byte_text[start+i];
			}
			return byte_midasi;
		}
	}
	
	/**
	 * 辞書の語釈文を返すメソッド
	 *   語義IDがGDAファイルの中に見つからないとき null を返す
	 *   語釈文が見つからないとき "" を返す   
	 */
	@Override
	protected String extract_sense_def (String sense_id) {
		ArrayList<Byte> byte_def = new ArrayList<Byte>(500);
		int start, p, p2;
				
		Byte_Comparer_BM bc_BM1 = new Byte_Comparer_BM("senseid=\""+sense_id+"\"");
		Byte_Comparer bc1 = new Byte_Comparer("<sense");
		Byte_Comparer bc2 = new Byte_Comparer("</sense>");
		
		boolean in_tag_flag = true;
		boolean close_sense_tag_found = false;
		start = bc_BM1.search_forward(byte_text, 0);
		if(start == -1) return null;
		
		p = start + 1;
		while(p < byte_text.length){
			if(in_tag_flag){
				if(byte_text[p] == 62) {	// == '>'
					in_tag_flag = false;
				}
				p++;
			}else{
				if(byte_text[p] == 60) {	// == '<'
					in_tag_flag = true;
					// <sense...>タグがあるとき、対応する</sense>まで読み飛ばす
					if(bc1.match(byte_text,p)){
						p2 = get_end_posit_of_close_sense_tag(byte_text,p+bc1.pat_length,bc1,bc2);
						if(p2 == -1) break;
						in_tag_flag = false;
						p = p2 + 1;
					}else if(bc2.match(byte_text,p)){
						close_sense_tag_found = true;
						break;
					}
				}else{
					byte_def.add(byte_text[p]);
					p++;
				}
			}
		}
		if(close_sense_tag_found){
			// Listを配列に変換するときは普通は toArray() メソッドを使うが、
			// byte, int などのプリミティブ型ではこのメソッドは使えない
			// 以下のように地道にやるしかない
			byte[] b_array = new byte[byte_def.size()];
			for(int i=0 ; i < byte_def.size() ; i++) b_array[i] = byte_def.get(i);
			
			try {
				return new String(b_array,"Shift_JIS");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return "";
		}
	}
	/**
	 * </sense>の位置を返すメソッド
	 * senseタグの入れ子も考慮し、対応する
	 * @param b			テキスト
	 * @param start		探索開始位置
	 * @param bc_open	<sense>にマッチする Byte_Comparer
	 * @param bc_close	</sense>にマッチする Byte_Comparer
	 * @return
	 */
	private int get_end_posit_of_close_sense_tag(byte[] b,int start,Byte_Comparer bc_open,Byte_Comparer bc_close){
		int sense_tag_stack = 1;
		int p = start;
		while(p < b.length){
			if(b[p] == 60){		// == '<'
				if(bc_open.match(b,p)){
					sense_tag_stack++;
					p += bc_open.pat_length;
				}else if(bc_close.match(b,p)){
					sense_tag_stack--;
					if(sense_tag_stack == 0) break;
					p += bc_close.pat_length;
				}else{
					p++;
				}
			}else{
				p++;
			}
		}
		int end = p + bc_close.pat_length - 1;
		if(end >= b.length) return -1;
		return end;
	}

	/**
	 * 左文脈の文字列を作成するときの探索範囲を求める
	 * 岩波の場合、<su></su>で囲まれていない見出しや品詞などの情報は左文脈に入れないことにし、
	 * 最初に出現する <su> までを探索範囲とする
	 */
	@Override
	protected int get_leftmost_boundary_for_left_kwic (int posit) {
		// 最初に出現する <su> を見つける
		int start_posit = search_open_su_tag_forward(0);
		if(start_posit == -1) return 0;
		// 文字列検索で <su></su> で囲まれていない文字列がヒットし、
		// 最初に出現する <su> が自分自身の後に来ることがある
		// そのときは探索の開始位置を 0 (ファイルの先頭) にする
		if(start_posit >= posit) return 0;
		return start_posit;
	}
	/**
	 * GDAファイルのメタデータ(辞書の見出し)を取り出し、
	 * ファイル out に出力するメソッド
	 */
	@Override
	protected void output_metadata(DataOutputStream out) throws SE_Exception {
		try {
			out.write( entry_id().getBytes() );
			out.write('\t');

			byte[] midasi_byte = extract_midasi_byte();
			if(midasi_byte == null){
				out.write('\n');
				return;
			}
			for(int i=0 ; i < midasi_byte.length ; i++){
				out.write(midasi_byte[i]);
			}
			out.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgE("CODE: GDA_File::output_metadata");
			throw e2;
		}
	}
	/**
	 * XMLファイルをdumpするときに挿入するxslファイル名を返すメソッド
	 */
	@Override
	protected String xsl_file_name(String s){
		return "iwanami"+s+".xsl";
	}

	/**
	 * positの位置にある文の範囲を返すメソッド
	 * @param posit		テキストにおける位置
	 * @return			[開始位置,終了位置] という配列
	 */
	@Override
	protected int[] get_range_of_sentence (int posit) {
		return get_range_of_su_tagged_sentence(posit);
	}
}
