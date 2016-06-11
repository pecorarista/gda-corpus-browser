package jp.or.gsk.gdacb.search_engine;

/* SuffixArrayによる全文検索を行うクラス
 * - this.text_file はテキストのファイル
 * - this.bin_sa は Suffix Array のファイルで、
 *   「項目データ」を並べたバイナリファイル
 * - 項目データは、テキストファイルにおける1つの位置に対応する。
 *   その位置に関する情報を格納する。項目データの基本的な仕様は以下の通り。
 *		[元のテキストの位置、int、4byte][他の情報...]
 * - このクラスでは、項目データは [元のテキストの位置] だけで構成されるとする。
 *   全文検索は先頭4byteの情報とテキストファイルだけで実現できる。
 * - 項目データは「先頭4byteがテキストにおける位置を表わす」こと以外は自由に設定できる。
 *   項目データとして他の情報を保持するときは、SuffixArrayの下位クラスを定義し、
 *   他の情報を取得するメソッドを定義する。
 * - suffix_array_unit_length で項目データのbyte数を設定する
 *   
 * - consult()メソッドで検索を行う。
 *   検索の結果は cONSULT_{hit_number,hit_offset_start,hit_offset_end,key_byte,key_byte_len} に格納される。
 * - position_at_text(id)メソッドで、検索されたキーのテキストにおける位置を返す。
 *   sa_entry(id)メソッドで、検索されたキーのSuffix Arrayの項目データを返す。
 *   引数 id で何番目の検索結果であるかを指定する。
 * - 用語(変数名)
 *   position: テキストにおける位置(先頭から何byte目か)
 *   offset: Suffix Arrayにおけるオフセット(何番目の項目データか)
 *   id: 検索結果に与えられる識別番号(何番目の検索結果か)
 *       0〜(cONSULT_hit_number-1)までの範囲
 */
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class SuffixArray {
	protected File text_file = null;
	private File suffix_array_file = null;
	protected BinarySuffixArray bin_sa = null;
	int suffix_array_unit_length = 4;

	protected byte[] byte_text = null;	// テキストファイル全体をbyte列として格納する配列
	int byte_text_len = -1;

	private int binary_search_offset_start;
	private int binary_search_offset_end;

	int cONSULT_hit_number;
	private int cONSULT_hit_offset_start;
	private int cONSULT_hit_offset_end;
	private byte[] cONSULT_key_byte;
	private int cONSULT_key_byte_len;
	
	protected int cONSULT_result_posit_at_text = -1;
	
	// Constructor
	SuffixArray(File t,File i){
		this.text_file = t;
		this.suffix_array_file = i;
	}
	SuffixArray(String t,String i){
		this.text_file = new File(t);
		this.suffix_array_file = new File(i);
	}
	
	/**
	 * SuffixArrayにおける1つの項目の長さを設定するメソッド
	 */
	void set_unit_length(int len) {
		this.suffix_array_unit_length = len;
	}

	/**
	 * SuffixArrayをオープンするメソッド
	 * 既にオープンされている場合は何もしない
	 */
	public void open() throws SE_Exception{
		if(byte_text == null) read_merged_file();
		if(bin_sa == null) bin_sa = new BinarySuffixArray(suffix_array_file);
		if(bin_sa.sa_file == null) bin_sa.open_suffix_array();
	}
	/**
	 * SuffixArrayを(強制的に)オープンするメソッド
	 */
	public void reopen() throws SE_Exception {
		read_merged_file();
		if(bin_sa == null) bin_sa = new BinarySuffixArray(suffix_array_file);
		bin_sa.open_suffix_array();
	}
	/**
	 * SuffixArrayを閉じるメソッド
	 */
	public void close() {
		this.byte_text = null;
		this.byte_text_len = -1;
		if(this.bin_sa != null) bin_sa.close();
		this.bin_sa = null;
	}
	
	/**
	 * 直近の consult() による検索のヒット件数を返すメソッド
	 */
	int hit_number(){
		return cONSULT_hit_number;
	}
	/**
	 * 直近の consult() による検索のキーの長さ(byte数)を返す
	 */
	int key_length(){
		return cONSULT_key_byte_len;
	}
	/**
	 * 直近の consult() による検索のid番目の結果のうち、
	 * 元のテキストにおける位置を返すメソッド
	 */
	int position_at_text(int id) throws SE_Exception {
		if(cONSULT_hit_number == -1) return -1;
		set_suffix_array_info(id);
		return this.cONSULT_result_posit_at_text;
	}
	/** 直近の consult() による検索の検索結果を取得するメソッド
	 * @param id	取得する検索結果のID
	 */
	void set_suffix_array_info(int id) throws SE_Exception{
		// if(cONSULT_hit_number == -1) return;  検索でヒットしないときはこのメソッドは呼ばないようにする
		if(id < 0 || id >= cONSULT_hit_number){
			SE_Exception e = new SE_Exception();
			e.printStackTrace();
			e.setMsgJ("引数が不正(SuffixArray#set_suffix_array_info())");
			e.setMsgE("Out of range argument in SuffixArray#set_suffix_array_info()");
			throw e;
		}
		int offset = cONSULT_hit_offset_start + id;
		if(bin_sa.current_offset != offset)	bin_sa.get_entry(offset);
		parse_suffix_array_data();
	}
	/**
	 * Suffix Array の項目データから種々の情報を取得するメソッド
	 * 以下の変数を設定する
	 * 		cONSULT_result_posit_at_text	テキストにおける位置
	 * 項目データをカスタマイズするときは、下位クラスでこのメソッドを Override する
	 */
	protected void parse_suffix_array_data() throws SE_Exception {
		DataInputStream dis = new DataInputStream( new ByteArrayInputStream(bin_sa.current_entry) );
		try {
			this.cONSULT_result_posit_at_text = dis.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			this.cONSULT_result_posit_at_text = -1;
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("サフィックスアレイからの情報取得に失敗しました");
			e2.setMsgE("FATAL ERROR: Fail to parse suffix array entry");
			throw e2;
		}
	}

	/**
	 * 二分探索によって全文検索するメソッド
	 * 以下の変数を設定する
	 *   cONSULT_hit_number	ヒット件数
	 *   cONSULT_hit_start	ヒットしたキーワードの最初のオフセット
	 *   cONSULT_hit_end	ヒットしたキーワードの最後のオフセット
	 *
	 * @param key	検索キー	
	 */
	public void consult(String key) throws SE_Exception {
		if(key == null || key.equals("")){
			cONSULT_hit_number = -1;
			cONSULT_hit_offset_start = -1;
			cONSULT_hit_offset_end = -1;
			return;
		}
		int h;
		try {
			cONSULT_key_byte = key.getBytes("Shift_JIS");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			cONSULT_hit_number = -1;
			cONSULT_hit_offset_start = -1;
			cONSULT_hit_offset_end = -1;
			return;
		}
		
		// 二分探索でキーを検索
		cONSULT_key_byte_len = cONSULT_key_byte.length;
		binary_search_offset_start = 0;
		binary_search_offset_end = bin_sa.size_of_suffix_array - 1;
		int first_hit_offset = find_key_by_binary_search(cONSULT_key_byte,cONSULT_key_byte_len);
		if(first_hit_offset == -1){
//			System.out.println("not hit");
			cONSULT_hit_number = -1;
			cONSULT_hit_offset_start = -1;
			cONSULT_hit_offset_end = -1;
			return;
		}
//		int first_hit_offset_start = binary_search_offset_start;
		int first_hit_offset_end = binary_search_offset_end;
		
		// 同一キーを持つ範囲の先頭(hit_start)を二分探索で求める
		// binary_search_offset_start = first_hit_offset_start;
		binary_search_offset_end = first_hit_offset - 1;
		cONSULT_hit_offset_start = first_hit_offset;
		while(binary_search_offset_start <= binary_search_offset_end){
			h = find_key_by_binary_search(cONSULT_key_byte,cONSULT_key_byte_len);
			if(h == -1) break;
			cONSULT_hit_offset_start = h;
			binary_search_offset_end = h - 1;
		}
		// 同一キーを持つ範囲の末尾(hit_end)を二分探索で求める
		binary_search_offset_start = first_hit_offset + 1; 
		binary_search_offset_end = first_hit_offset_end;
		cONSULT_hit_offset_end = first_hit_offset;
		while(binary_search_offset_start <= binary_search_offset_end){
			h = find_key_by_binary_search(cONSULT_key_byte,cONSULT_key_byte_len);
			if(h == -1) break;
			cONSULT_hit_offset_end = h;
			binary_search_offset_start = h + 1;
		}
		
		cONSULT_hit_number = cONSULT_hit_offset_end - cONSULT_hit_offset_start + 1;
		bin_sa.reset();
		/* debug
		try {
			String x;
			int j,p;
			byte[] tmp = new byte[cONSULT_key_byte.length];
			
			int[] offset_list = new int[4];
			offset_list[0] = cONSULT_hit_offset_start - 1;
			offset_list[1] = cONSULT_hit_offset_start;
			offset_list[2] = cONSULT_hit_offset_end;
			offset_list[3] = cONSULT_hit_offset_end + 1;

			for(int offset: offset_list){
				if(offset < 0) break;
				p = bin_sa.get_posit_at_text(offset);
				for(j=0 ; j < tmp.length ; j++) tmp[j] = byte_text[p+j];
				x = new String(tmp,"Shift_JIS");
				System.out.println("at "+offset+" "+x);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		*/
	}
	/**
	 * キーを二分探索で検索するメソッド
	 * 見つかったときはキーの位置、見つからなかったときは-1を返す
	 */
	private int find_key_by_binary_search(byte[] byte_key,int key_len) throws SE_Exception {
		int c,p;
		while(binary_search_offset_start <= binary_search_offset_end){
			p = (binary_search_offset_start + binary_search_offset_end) / 2;
			c = comp(bin_sa.get_posit_at_text(p),byte_key,key_len);
			//System.out.println("("+binary_search_start+","+p+","+binary_search_end+"): "+c);
			if(c == 0){
				return p;
			}else if(c < 0){
				binary_search_offset_start = p + 1;
			}else{
				binary_search_offset_end = p - 1;
			}
		}
		return -1;
	}
	/**
	 * テキストとキーを比較するメソッド
	 */
	private int comp(int posit,byte[] key,int key_len){
		if(posit + key_len >= byte_text_len) return 1;
		for(int i=0 ; i < key_len ; i++){
			if(byte_text[posit] == key[i]){
				posit++;
				continue;
			}
			// byte の数値を unsigned int として取り扱うために 0xFF との論理和をとる 
			return (byte_text[posit] & 0xFF) - (key[i] & 0xFF);
		}
		return 0;
	}
	
	/**
	 * マージしたテキストファイルを読み込むメソッド
	 */
	private void read_merged_file() throws SE_Exception {
		RandomAccessFile in = null;
		if(! text_file.isFile()){
			byte_text = null;
			byte_text_len = -1;
			SE_Exception e = new SE_Exception();
			e.setMsgJ("検索インデックスが存在しません。\nメニューバーの「Option」→「インデックス作成」を選択し、インデックスを作成して下さい。");
			e.setMsgE("Merged text file `"+text_file.getName()+"' not found");
			throw e;
		}
		try {
			in = new RandomAccessFile(text_file,"r");
			byte_text = new byte[(int) in.length()];
			byte_text_len = byte_text.length;
			int len = in.read(byte_text);
			if (len == -1) {
				SE_Exception e = new SE_Exception();
				e.setMsgJ("インデックスファイル`"+text_file.getName()+"'の読み込みに失敗しました");
				e.setMsgE("Fail to read merged text file `"+text_file.getName()+"'");
				throw e;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("インデックスファイル`"+text_file.getName()+"'の読み込みに失敗しました");
			e2.setMsgE("Fail to read merged text file `"+text_file.getName()+"'");
			throw e2;
		} finally {
			try {
				if (in != null) in.close();
			} catch (IOException e) {}
		}
		//System.out.println("DEBUG: merged text is read (SuffixArray.class)");
	}

	/**
	 * バイナリの Suffix Array を取り扱う内部クラス
	 */
	protected class BinarySuffixArray {
		private File suffix_array_file;
		private RandomAccessFile sa_file = null;
		int size_of_suffix_array = -1;
		// 何番目の項目を読んでいるか?
		int current_offset = -1;
		// current_offset に対応する項目データ
		byte[] current_entry = new byte[suffix_array_unit_length];

		BinarySuffixArray(File f){
			this.suffix_array_file = f;
		}
		
		/**
		 * SuffixArrayのファイルをオープンするメソッド
		 * さらに、インデックスに含まれるエントリの総数を this.size_of_suffix_array にセットする
		 */
		void open_suffix_array() throws SE_Exception {
			try {
				sa_file = new RandomAccessFile(suffix_array_file,"r");
				// RandomAccessFile の length メソッドの戻値は long なので、int に変換する
//				size_of_suffix_array = new Long(sa.length() / suffix_array_unit_length).intValue();
				size_of_suffix_array = ((int)sa_file.length()) / suffix_array_unit_length;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				sa_file = null;
				size_of_suffix_array = -1;
				SE_Exception e2 = new SE_Exception();
				e2.setMsgJ("インデックスファイル`"+suffix_array_file.getName()+"'が存在しません");
				e2.setMsgE("FATAL ERROR: Suffix array file `"+suffix_array_file.getName()+"' is not found");
				throw e2;
			} catch (IOException e) {
				e.printStackTrace();
				sa_file = null;
				size_of_suffix_array = -1;
				SE_Exception e2 = new SE_Exception();
				e2.setMsgJ("インデックスファイル`"+suffix_array_file.getName()+"'のオープンに失敗しました");
				e2.setMsgE("FATAL ERROR: Fail to open suffix array file `"+suffix_array_file.getName()+"'");
				throw e2;
			}
			//System.out.println("DEBUG: suffix array is opened (BinarySuffixArray.class)");
		}
		/**
		 * SuffixArrayのファイルをクローズするメソッド
		 */
		void close(){
			try {
				if(sa_file != null){
					sa_file.close();
					sa_file = null;
					size_of_suffix_array = -1;
				}
			} catch (IOException e) {}
		}
		
		/**
		 * オフセットを引数に取り、それに該当する項目データのテキストにおける位置を返すメソッド
		 * 全文検索時に使用する
		 * @param offset	オフセット
		 * @return			テキストにおける位置
		 */
		private int get_posit_at_text(int offset) throws SE_Exception{
			try {
				sa_file.seek(offset * suffix_array_unit_length);
				return sa_file.readInt();
				//this.posit_at_merged_text = sa_file.readInt();
				//this.file_ID = -1;
				//this.posit_at_gda_file = -1;
				//this.pointer = idx;
			} catch (IOException e) {
				e.printStackTrace();
				SE_Exception e2 = new SE_Exception();
				e2.setMsgJ("インデックスファイル`"+suffix_array_file.getName()+"'のアクセスに失敗しました");
				e2.setMsgE("FATAL ERROR: Fail to access suffix array file `"+suffix_array_file.getName()+"'");
				throw e2;
			}
		}
		/**
		 * Suffix Arrayの項目データを取得するメソッド
		 * 検索結果を取得する際に使用する
		 * @param offset	オフセット
		 */
		private void get_entry(int offset) throws SE_Exception {
			if(this.current_offset == offset) return;
			try {
				sa_file.seek(offset * suffix_array_unit_length);
				sa_file.read(this.current_entry);
				this.current_offset = offset;
			} catch (IOException e) {
				e.printStackTrace();
				this.current_offset = -1;
				SE_Exception e2 = new SE_Exception();
				e2.setMsgJ("インデックスファイル`"+suffix_array_file.getName()+"'のアクセスに失敗しました");
				e2.setMsgE("FATAL ERROR: Fail to access suffix array file `"+suffix_array_file.getName()+"'");
				throw e2;
			}
		}
		void reset(){
			this.current_offset = -1;
		}
	}
	
	public static void main(String[] args) {
		//String key = "と思われる";
		//String key = "によると";
		String key = "";
		SuffixArray sa_test = new SuffixArray("index/mainiti_sa.mer","mai_sa.idx");
		try {
			System.out.println("Start to read index");
			sa_test.open();
			System.out.println("Finish to read index");
			sa_test.consult(key);
			System.out.println("hit number: "+sa_test.hit_number()+" ("+sa_test.cONSULT_hit_offset_start+","+sa_test.cONSULT_hit_offset_end+")");
			int tmp = (sa_test.hit_number() > 10) ? 10 : sa_test.hit_number();
			for(int i=0 ; i < tmp ; i++) System.out.print(i+":"+sa_test.position_at_text(i)+" ");
			System.out.print("\n");
			sa_test.close();
		} catch (SE_Exception e) {
//			e.printStackTrace();
			System.err.println(e.MsgJ);
		}
	}
}
