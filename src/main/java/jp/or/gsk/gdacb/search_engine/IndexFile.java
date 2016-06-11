package jp.or.gsk.gdacb.search_engine;

/* 仕様
 * - consult(キーワード)で検索を行い、結果をcONSULT_key_file_infoに保存する
 *   cONSULT_key_file_info は *.idx における位置のキーワードのヒット件数を保持する
 * - hit_number, fileID(id), position(id), length(id) で、
 *   直近の consult による結果
 *   (ヒット件数ならびにid番目のキーワードのファイルID、位置、キーワード長)を返す
 *   これらは呼び出される度に *.idx にアクセスして情報を取得する
 * - *.idx から情報を取得する際にはファイルID、位置、キーワード長を同時に取得し、
 *   cONSULT_IoKI_cache にキャッシュとして保存する
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.HashMap;

class IndexFile {
	private static final int index_unit_length = 7;

	private String index_file_name,keyword_file_name;
	private HashMap<String,KeyFileInfo> index_of_keyword = null;
	private RandomAccessFile index_fp;
	//	cONSULT_* は consultメソッドの結果を保持するフィールド
	private KeyFileInfo cONSULT_key_file_info = null;
	private IndexOfKeywordInstance cONSULT_IoKI_cache = null;
	// private IndexOfKeywordInstance[] cONSULT_IoKI_list = null;
	
	// constructor
	IndexFile (String key_file,String idx_file) {
		this.keyword_file_name = key_file;
		this.index_file_name = idx_file;
		this.index_of_keyword = null;
		this.index_fp = null;
		this.cONSULT_key_file_info = null;
		this.cONSULT_IoKI_cache = new IndexOfKeywordInstance();
		cONSULT_IoKI_cache.set_ID(-1);
	}
	/* destructor  (上位クラスの finalize をオーバーライドしてしまう) 
	@Override protected void finalize () {
		try {
			index_fp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	
	void open () throws SE_Exception {
		try {
			if(this.index_of_keyword == null) read_keyword_file();
			if(this.index_fp == null) open_index_file();
		} catch (SE_Exception e) {
			throw e;
		}
	}
	void reopen () throws SE_Exception {
		read_keyword_file();
		open_index_file();
	}
	void close() {
		this.index_of_keyword = null;
		try {
			if(index_fp != null) {
				index_fp.close();
				index_fp = null;
			}
		} catch (IOException e) {}
	}
	
	void consult (String k) throws SE_Exception {
		if(index_of_keyword == null) {
			SE_Exception e = new SE_Exception();
			e.setMsgJ("キーワードファイル(*.key)が読み込まれていません");
			e.setMsgE("Index file (*.key) is not read");
			throw e;
		}
		if(index_fp == null) {
			SE_Exception e = new SE_Exception();
			e.setMsgJ("インデックスファイル(*.idx)が読み込まれていません");
			e.setMsgE("Index file (*.idx) is not opened");
			throw e;
		}			

		cONSULT_key_file_info = index_of_keyword.get(k);
		cONSULT_IoKI_cache.set_ID(-1);
	}
	
	// 直近の consult による結果を返すメソッド(ヒット件数、ファイルID、位置、キーワード長)
	int hit_number () {
		if(cONSULT_key_file_info == null) return -1;
		return cONSULT_key_file_info.keyword_num;
	}
	int fileID(int id) throws SE_Exception {
		if(cONSULT_key_file_info == null) return -1;
		if(cONSULT_IoKI_cache.id != id)
			getIndexOfKeywordInstance(id);
		return cONSULT_IoKI_cache.file_ID;
	}
	int position(int id) throws SE_Exception {
		if(cONSULT_key_file_info == null) return -1;
		if(cONSULT_IoKI_cache.id != id)
			getIndexOfKeywordInstance(id);
		return cONSULT_IoKI_cache.position;
	}
	int length(int id) throws SE_Exception {
		if(cONSULT_key_file_info == null) return 0;
		if(cONSULT_IoKI_cache.id != id)
			getIndexOfKeywordInstance(id);
		return cONSULT_IoKI_cache.length;
	}

	// キーワードファイル(*.key)を読み込み、ハッシュを作成するメソッド
	private void read_keyword_file() throws SE_Exception {
		BufferedReader in = null;
		String s = null;
		String[] line, info;

		index_of_keyword = new HashMap<String,KeyFileInfo>();
		File f = new File(keyword_file_name);
		if(! f.isFile()) {
			SE_Exception e = new SE_Exception();
			e.setMsgJ("キーワードファイル`"+f.getName()+"'が存在しません");
			e.setMsgE("FATAL ERROR: Keyword file `"+f.getName()+"' does not exist");
			throw e;
		}
		try {
			//in = new BufferedReader(new FileReader(keyword_file_name));
			in = new BufferedReader( new InputStreamReader( new FileInputStream(keyword_file_name), "UTF-8") ); 
			while( (s=in.readLine()) != null ) {
				line = s.split("\t");
				info = line[1].split(":");
				index_of_keyword.put(line[0],new KeyFileInfo(info[0],info[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("キーワードファイル`"+f.getName()+"'の読み込みに失敗しました");
			e2.setMsgE("FATAL ERROR: Fail to read keyword file `"+f.getName()+"'");
			throw e2;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("キーワードファイル`"+f.getName()+"'のフォーマットが不正です");
			e2.setMsgE("FATAL ERROR: Illegal format in keyword file `"+f.getName()+"'");
			throw e2;
		} finally {
			try {
				if (in != null) in.close();
			} catch (IOException e) {}
		}
		//System.out.println("DEBUG: keyword file is read (IndexFile.class)");
	}
	// インデックスファイル *.idx をオープンするメソッド
	private void open_index_file() throws SE_Exception {
		File f = new File(index_file_name);

		if(! f.isFile()) {
			SE_Exception e = new SE_Exception();
			e.setMsgJ("インデックスファイル`"+f.getName()+"'が存在しません");
			e.setMsgE("FATAL ERROR: Index file `"+f.getName()+"' does not exist");
			throw e;
		}
		try {
			index_fp = new RandomAccessFile(index_file_name,"r");
		} catch (IOException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("インデックスファイル`"+f.getName()+"'のオープンに失敗しました");
			e2.setMsgE("FATAL ERROR: Fail to open index file `"+f.getName()+"'");
			throw e2;
		}
		//System.out.println("DEBUG: index file is opened (IndexFile.class)");
	}
	// キーワードの情報を *.idx から取得するメソッド
	private void getIndexOfKeywordInstance(int id) throws SE_Exception{
		if(cONSULT_key_file_info == null) return;
		if(id < 0 || id >= cONSULT_key_file_info.keyword_num){
			SE_Exception e = new SE_Exception();
			e.setMsgJ("インデックスファイルへの不正なアクセス");
			e.setMsgE("Out of range in Index#getIndexOfKeywrodInstance()");
			throw e;
		}
		try {
			index_fp.seek(cONSULT_key_file_info.position+id*index_unit_length);
			cONSULT_IoKI_cache.set_fileID( index_fp.readUnsignedShort() );
			cONSULT_IoKI_cache.set_position( index_fp.readInt() );
			cONSULT_IoKI_cache.set_length( (int) index_fp.readByte() );
//			System.out.println(cONSULT_IoKI_cache.file_ID+" "+cONSULT_IoKI_cache.position+" "+cONSULT_IoKI_cache.length);
		} catch (IOException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("インデックスファイル(*.idx)のアクセスに失敗しました");
			e2.setMsgE("FATAL ERROR: Fail to seek in index file");
			throw e2;
		}
	}

	
	// internal class
	private class KeyFileInfo {
		int position, keyword_num;

		// constructor
		KeyFileInfo(String p,String n) {
			this.position = Integer.parseInt(p);
			this.keyword_num = Integer.parseInt(n);
		}
	}

	private class IndexOfKeywordInstance {
		int id, file_ID, position, length;

		// constructor
//		IndexOfKeywordInstance() {
//      }
		void set_ID(int i){
			this.id = i;
		}
		void set_fileID (int f){
			this.file_ID = f;
		}
		void set_position (int p){
			this.position = p;
		}
		void set_length (int l){
			this.length = l;
		}
	}
}
