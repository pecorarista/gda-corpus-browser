package jp.or.gsk.gdacb.search_engine;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.TreeSet;

public abstract class SA_Metadata extends SuffixArray {
	private int cONSULT_result_fileID = -1;
	protected int number_of_gda_files = -1;
	protected int[] line_index = null;

	SA_Metadata(File text_file, File sa_file) {
		super(text_file, sa_file);
		this.suffix_array_unit_length = 6;
	}
	SA_Metadata(String text_file_name,String sa_file_name){
		super(text_file_name, sa_file_name);
		this.suffix_array_unit_length = 6;
	}

	/**
	 * 行のインデックス(line_index)を作成するメソッド
	 *   line_index[i] -> i行目の先頭の位置 (0 <= i < ファイルの行数)
	 */
	public void create_line_index () throws SE_Exception {
		if(byte_text == null){
			SE_Exception e = new SE_Exception();
			e.setMsgE("ERROR: text has not been read in SA_Metadata#create_line_index()");
			throw e;
		}
		line_index = new int[number_of_gda_files];
		int line_no = 0;
		line_index[0] = 0;
		for(int i=0 ; i < byte_text.length ; i++){
			if(byte_text[i] == 10){	// == '\n'
				line_no++;
				if(line_no >= line_index.length) continue;
				line_index[line_no] = i + 1;
			}
		}
		if(line_no != line_index.length){
			SE_Exception e = new SE_Exception();
			e.setMsgJ("インデックスファイル`"+text_file.getName()+"'の内容が不正です");
			e.setMsgE("Line number mismatch in `"+text_file.getName()+"'");
			throw e;
		}
	}
	/**
	 * 以下の変数を設定する
	 * 		cONSULT_result_posit_at_text	 テキストにおける位置
	 * 		cONSULT_result_fileID			 対応するGDAファイルのID
	 * このメソッドは SuffixArray#set_suffix_array_info の中で呼び出される
	 */
	@Override
	protected void parse_suffix_array_data() throws SE_Exception {
		DataInputStream dis = new DataInputStream( new ByteArrayInputStream(bin_sa.current_entry) );
		try {
			this.cONSULT_result_posit_at_text = dis.readInt();
			this.cONSULT_result_fileID = dis.readUnsignedShort();
		} catch (IOException e) {
			e.printStackTrace();
			this.cONSULT_result_posit_at_text = -1;
			this.cONSULT_result_fileID = -1;
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
	 * 直近の consult() による検索でヒットしたキーワードの集合に対し、
	 * 対応する file ID(GDAファイルのID) を保持した TreeMap を返すメソッド
	 * - 重複する file ID はマージされる
	 * - file ID は昇順にソートされる 
	 */
	public TreeSet<Integer> fileID_set() throws SE_Exception {
		TreeSet<Integer> file_ID_set = new TreeSet<Integer>();
		if(cONSULT_hit_number == -1) return null;
		for(int i=0 ; i < cONSULT_hit_number ; i++){
			file_ID_set.add(fileID(i));
		}
		return file_ID_set;
	}
	/**
	 * fileID_set() とほぼ同じだが、エントリIDと完全一致したケースだけを選択する 
	 */
	public TreeSet<Integer> fileID_set_entryID_exact_match() throws SE_Exception {
		int posit, file_id, key_len;
		TreeSet<Integer> file_ID_set = new TreeSet<Integer>();
		if(cONSULT_hit_number == -1) return null;
		key_len = key_length();
		for(int i=0 ; i < cONSULT_hit_number ; i++){
			posit = position_at_text(i);
			file_id = fileID(i);
			if(line_index[file_id] == posit &&
			   byte_text[posit+key_len] == 9){	// == '\t'
				file_ID_set.add(file_id);
			}
		}
		return file_ID_set;
	}
	/**
	 * 全てのファイルIDのセットを返す
	 */
	public TreeSet<Integer> all_fileID () throws SE_Exception {
		TreeSet<Integer> file_ID_set = new TreeSet<Integer>();
		for(int i=0 ; i < number_of_gda_files ; i++){
			file_ID_set.add(i);
		}
		return file_ID_set;
	}
	public String extract_metadata (int file_id) throws SE_Exception {
		int len;
		if(file_id < 0 || file_id >= line_index.length){
			SE_Exception e = new SE_Exception();
			e.setMsgE("ERROR: illegal argment in SA_Metadata#extract_metadata()");
			throw e;
		}
		// 行の長さを得る。ただし、改行コードは除く。
		if(file_id == line_index.length-1){
			len = byte_text.length - line_index[file_id] - 1;
		}else{
			len = line_index[file_id+1] - line_index[file_id] -1 ;
		}
		byte[] line_byte = new byte[len];
		int j = 0;
		for(int i=line_index[file_id] ; i < byte_text.length ; i++){
			if(byte_text[i] == 10) break;	// == '\n'
			line_byte[j] = byte_text[i];
			j++;
		}
		try {
			return new String(line_byte,"Shift_JIS");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgE("ERROR: Unsupported Encoding in SA_Metadata#extract_metadata");
			throw e2;
		}
	}	
}
