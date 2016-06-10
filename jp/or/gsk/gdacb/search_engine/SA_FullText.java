package jp.or.gsk.gdacb.search_engine;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

class SA_FullText extends SuffixArray {
	private int cONSULT_result_fileID = -1;
	private int cONSULT_result_posit_at_gda_file = -1;
	
	SA_FullText(File text_file,File sa_file){
		super(text_file,sa_file);
		this.suffix_array_unit_length = 10;
	}
	SA_FullText(String text_file_name,String sa_file_name){
		super(text_file_name,sa_file_name);
		this.suffix_array_unit_length = 10;
	}
	/**
	 * 以下の変数を設定する
	 * 		cONSULT_result_posit_at_text	 テキストにおける位置
	 * 		cONSULT_result_fileID			 元のGDAファイルのID
	 * 		cONSULT_result_posit_at_gda_file 元のGDAファイルにおける位置
	 * このメソッドは SuffixArray#set_suffix_array_info の中で呼び出される
	 */
	@Override
	protected void parse_suffix_array_data() throws SE_Exception {
		DataInputStream dis = new DataInputStream( new ByteArrayInputStream(bin_sa.current_entry) );
		try {
			this.cONSULT_result_posit_at_text = dis.readInt();
			this.cONSULT_result_fileID = dis.readUnsignedShort();
			this.cONSULT_result_posit_at_gda_file = dis.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			this.cONSULT_result_posit_at_text = -1;
			this.cONSULT_result_fileID = -1;
			this.cONSULT_result_posit_at_gda_file = -1;
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("サフィックスアレイからの情報取得に失敗しました");
			e2.setMsgE("FATAL ERROR: Fail to parse suffix array entry");
			throw e2;
		}
	}
	/**
	 * 直近の consult() による検索のid番目の結果のうち、
	 * 元のGDAファイルのIDを返すメソッド
	 */
	int fileID (int id) throws SE_Exception {
		if(cONSULT_hit_number == -1) return -1;
		set_suffix_array_info(id);
		return this.cONSULT_result_fileID;
	}
	/**
	 * 直近の consult() による検索のid番目の結果のうち、
	 * 元のGDAファイルにおける位置を返すメソッド
	 */
	int position_at_gda_file (int id) throws SE_Exception {
		if(cONSULT_hit_number == -1) return -1;
		set_suffix_array_info(id);
		return this.cONSULT_result_posit_at_gda_file;
	}
	
	public static void main(String[] args) {
		//String key = "と思われる";
		String key = "によると";
		SA_FullText sa_test = new SA_FullText("index/mainiti_sa.mer","index/mainiti_sa.idx");
		try {
			System.out.println("Start to read index");
			sa_test.open();
			System.out.println("Finish to read index");
			sa_test.consult(key);
			System.out.println("hit number: "+sa_test.hit_number());
			int tmp = (sa_test.hit_number() > 10) ? 10 : sa_test.hashCode();
			for(int i=0 ; i < tmp ; i++) System.out.print(i+":"+sa_test.position_at_text(i)+":"+sa_test.fileID(i)+":"+sa_test.position_at_gda_file(i)+" ");
			System.out.print("\n");
			sa_test.close();
		} catch (SE_Exception e) {
//			e.printStackTrace();
			System.err.println(e.MsgJ);
		}
	}
}
