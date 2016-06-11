package jp.or.gsk.gdacb.search_engine;

import java.io.DataOutputStream;
import java.io.IOException;

public class GDA_File_MAI extends GDA_File {
	public GDA_File_MAI(String file) {
		super(file);
	}

	@Override
	protected void set_mor_info (String [] mor,int posit,int len) {
		mor_POS = mor[1];
		mor_CONJ = mor[2];
		mor_BASE = mor[3].equals("") ? extract_word(posit) : mor[3];
		mor_YOMI = mor[4];
	}
	
	/**
	 * 左文脈の文字列を作成するときの探索範囲を求める
	 * 毎日の場合、<byline></byline>で囲まれるメタ情報は左文脈に入れないことにし、
	 * </byteline> の末尾を探索範囲とする
	 */
	@Override
	protected int get_leftmost_boundary_for_left_kwic (int posit) {
		Byte_Comparer_BM bm_close_byline_tag = new Byte_Comparer_BM("</byline>");
		int start_posit = bm_close_byline_tag.search_forward(byte_text,0);
		if(start_posit == -1) return 0;
		// 文字列検索で <byline></byline> で囲まれた文字列がヒットし、
		// </byline> が自分自身の後に来ることがある
		// そのときは探索の開始位置を 0 (ファイルの先頭) にする
		if(start_posit >= posit) return 0;
		return start_posit + bm_close_byline_tag.pat_length;
	}
	/**
	 * GDAファイルからメタデータ(新聞記事のタイトル)を取り出し、
	 * ファイル out に出力するメソッド
	 */
	@Override
	protected void output_metadata(DataOutputStream out) throws SE_Exception {
		int i;
		boolean tag_flag, extract_metadata_flag = false;
		Byte_Comparer bc_close_h_tag = new Byte_Comparer("</h>");
		
		try {
			out.write( entry_id().getBytes() );
			out.write('\t');
			
			int posit = 0;
			int start_posit = search_open_h_tag(posit);
			while(start_posit != -1){
				if(extract_metadata_flag) out.write(' '); // <h>タグの間のセパレータ
				tag_flag = true;
				for(i=start_posit+1 ; i < byte_text.length ; i++){
					if(tag_flag){
						if(byte_text[i] == 62){		// == '>'
							tag_flag = false;
						}
					}else{
						if(byte_text[i] == 60){		// == '<'
							if(bc_close_h_tag.match(byte_text, i)) break;
							tag_flag = true;
						}else{
							out.write(byte_text[i]);
							extract_metadata_flag = true;
						}
					}
				}
				posit = i + bc_close_h_tag.pat_length;
				start_posit = search_open_h_tag(posit);
			}
			if(extract_metadata_flag){
				out.write('\n');
				return;
			}
		
			// <h>タグがないとき、
			// 最初の</byline>の直後から、「＝」または</su>タグの直前までを返す
			Byte_Comparer_BM bm_close_byline_tag = new Byte_Comparer_BM("</byline>");
			Byte_Comparer bc_close_su_tag = new Byte_Comparer("</su>");
			start_posit = bm_close_byline_tag.search_forward(byte_text, 0);
			if(start_posit == -1) {
				out.write('\n');
				return;
			}
		
			tag_flag = false;
			for(i=start_posit+bm_close_byline_tag.pat_length ; i < byte_text.length ; i++){
				if(tag_flag){
					if(byte_text[i] == 62){		// == '>'
						tag_flag = false;
					}
				}else{
					if(byte_text[i] == 60){		// == '<'
						if(bc_close_su_tag.match(byte_text, i)) break;
						tag_flag = true;
					//}else if(byte_text[i] == 129 && byte_text[i+1] == 129){	// == '＝' (shift JIS)
					}else if(byte_text[i] == -127 && byte_text[i+1] == -127){	// 比較の際にbyteからsigned intへの変換が行われることに対応
						break;
					}else{
						out.write(byte_text[i]);
					}
				}
			}
			out.write('\n');
			return;
		} catch (IOException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgE("CODE: GDA_File::output_metadata");
			throw e2;
		}
	}
	/**
	 * <h>タグを検索するメソッド
	 */
	private int search_open_h_tag(int start_posit){
		int posit1, posit2;
		Byte_Comparer_BM bm_open_h_tag1 = new Byte_Comparer_BM("<h>");
		Byte_Comparer_BM bm_open_h_tag2 = new Byte_Comparer_BM("<h ");

		posit1 = bm_open_h_tag1.search_forward(byte_text, start_posit);
		posit2 = bm_open_h_tag2.search_forward(byte_text, start_posit);
		if(posit1 == -1){
			if(posit2 == -1){
				return -1;
			}else{
				return posit2;
			}
		}else{
			if(posit2 == -1){
				return posit1;
			}else{
				return (posit1 > posit2) ? posit2 : posit1;
			}
		}

	}
	/**
	 * XMLファイルをdumpするときに挿入するxslファイル名を返すメソッド
	 */
	@Override
	protected String xsl_file_name(String s){
		return "mainiti"+s+".xsl";
	}

	/**
	 * positの位置にある文の範囲を返すメソッド
	 * @param posit		テキストにおける位置
	 * @return			[開始位置,終了位置] という配列
	 */
	@Override
	protected int[] get_range_of_sentence (int posit) {
		int[] range = null;
		
		range = get_range_of_su_tagged_sentence(posit);
		if(range != null) return range;
		// 毎日の場合、<h></h>タグで囲まれた文字列も検索する
		// <h>には入れ子はないと仮定する
		Byte_Comparer_BM bcBM_open_h_tag = new Byte_Comparer_BM("<h>"); 
		Byte_Comparer_BM bcBM_close_h_tag = new Byte_Comparer_BM("</h>");
		int start = bcBM_open_h_tag.search_backward(byte_text, posit);
		if(start == -1) return null;
		int end = bcBM_close_h_tag.search_forward(byte_text, posit);
		if(end == -1) return null;
		range = new int[2];
		range[0] = start;
		range[1] = end+bcBM_close_h_tag.pat_length-1;
		return range;
	}
}
