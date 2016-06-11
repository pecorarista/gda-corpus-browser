package jp.or.gsk.gdacb.search_engine;

import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.E_KeywordType;
import java.io.File;

public abstract class SearchEngine {
//	protected abstract void open_index_files (String s) throws SE_Exception;
//	protected abstract void read_file_list(String s) throws SE_Exception;
	protected abstract GDA_File create_GDA(String s);
	protected abstract RetrievedKeyword[] create_kw_list(int i);
	protected abstract RetrievedKeyword create_kw();

	private String GDA_Base_DIR = null;
	FilelistFile filelist = null;
	IndexFile hw_idx = null;
	IndexFile bs_idx = null;
	SA_FullText sa = null;
	private GDA_File gda = null;
	private int hit_number;
	//private int left_kwic_char_length = 20;
	//private int right_kwic_char_length = 20;
	// corpus_type はサブクラス(*_MAI,*_IWA)のコンストラクタ内で設定される
	protected E_Corpus corpus_type;
	
	/**
	 * constructor
	 * @param dir	GDAファイルのあるディレクトリ	
	 */
	public SearchEngine (String dir) throws SE_Exception {
		/* インデックスファイルの置き場所はカスタマイズできないので、
		 * インデックスファイルが存在しないなどのエラーは、
		 * GDAファイルのエラーよりも深刻である。
		 * そのため、まず最初にインデックスを作り、インデックス関係のエラーを
		 * GDAファイルのエラーよりも先に表示させる
		 * 
		 * → 方針変更: インデックスは必要なときに読み込むようにする
		 */
		
		if(dir == null || dir.equals("")) {
			this.GDA_Base_DIR = File.separator;

			SE_Exception e = new SE_Exception();
			e.setMsgJ("CODE: not specified");	// GUIでメッセージを変更する
			e.setMsgE("GDA directory is not specified");
			throw e;
		}
		if(! dir.substring(dir.length()-1,dir.length()).equals( System.getProperty("file.separator") )){
			this.GDA_Base_DIR = dir + File.separator;
		}else{
			this.GDA_Base_DIR = dir;
		}
		File f = new File(GDA_Base_DIR);
		if( ! f.isDirectory() ) {
			SE_Exception e = new SE_Exception();
			e.setMsgJ("CODE: not found");	// GUIでメッセージを変更する
			e.setMsgE("GDA directory is not found");
			throw e;
		}
	}

	/* 各種インデックスを open するメソッドはサブクラスで定義する */
	
	public void close_surface_index (){
		this.hw_idx.close();
		this.hw_idx = null;
	}
	public void close_base_index (){
		this.bs_idx.close();
		this.bs_idx = null;
	}
	public void close_suffix_array (){
		this.sa.close();
		this.sa = null;
	}
	
	/**
	 * 検索を行うメソッド
	 * @param key		キーワード
	 * @param type		キーワード種別
	 * @param disp_num	最大用例数(-1なら無制限)
	 * @param left_kwic_char_length		左文脈長(文字数)
	 * @param right_kwic_char_length	右文脈長(文字数)
	 * @return			検索結果のリスト
	 */
	public RetrievedKeyword[] search (String key, E_KeywordType type, int disp_num, int left_kwic_char_length, int right_kwic_char_length) throws SE_Exception {
		int p,l,id,prev_file_id,file_id;
		int size_of_result = 0;
		int step = 1;
		String fn,fn_path;
		RetrievedKeyword kw;
		RetrievedKeyword[] kw_list;
		
		if(this.filelist == null){
			SE_Exception e = new SE_Exception();
			e.setMsgJ("ファイル名のリストが読み込まれていません");
			e.setMsgE("File list has not been read");
			throw e;
		}
		if(type == E_KeywordType.SURFACE){
			if(this.hw_idx == null){
				SE_Exception e = new SE_Exception();
				e.setMsgJ("出現形検索用のインデックスが読み込まれていません");
				e.setMsgE("Index file of surface forms has not been read");
				throw e;
			}
		}else if(type == E_KeywordType.BASE){
			if(this.bs_idx == null){
				SE_Exception e = new SE_Exception();
				e.setMsgJ("基本形検索用のインデックスが読み込まれていません");
				e.setMsgE("Index file of base forms has not been read");
				throw e;
			}
		}else{		// if(type == E_KeywordType.FULLTEXT)
			if(this.sa == null){
				SE_Exception e = new SE_Exception();
				e.setMsgJ("全文検索用のサフィックスアレイが読み込まれていません");
				e.setMsgE("Suffix array file has not been read");
				throw e;
			}
		}

		consult_wrapper(type,key);
		hit_number = hit_number_wrapper(type);
		if(hit_number == -1) return null;
		
		if(disp_num == -1 || hit_number <= disp_num){
			size_of_result = hit_number;
			step = 1;
		}else if(hit_number > disp_num) {
			size_of_result = disp_num;
			step = hit_number / disp_num;
		}

		kw_list = create_kw_list(size_of_result);

		id = 0;
		prev_file_id = -1;
		for(int i=0 ; i < hit_number ; i++) {
			if(step > 1 && (i % step) != 0)	continue;

			file_id = fileID_wrapper(type,i);
			fn = this.filelist.filename(file_id);
			fn_path = GDA_Base_DIR + fn;
			p = position_wrapper(type,i);
			l = length_wrapper(type,i);
			try {
				if(gda == null || id == 0) {
					gda = create_GDA(fn_path);
					gda.read_all();
					gda.kwic_left_length(left_kwic_char_length);
					gda.kwic_right_length(right_kwic_char_length);
				}else if ( prev_file_id != file_id ){
					gda = create_GDA(fn_path);
					gda.read_all();
					gda.kwic_left_length(left_kwic_char_length);
					gda.kwic_right_length(right_kwic_char_length);
				}
			} catch (SE_Exception e) {
				throw e;
			}
			kw = create_kw();
			kw.set_keyword(gda.extract_key(p,l));
			//System.out.println("posit="+p+", length="+l);
			kw.set_POS(gda.pos(p,l));
			kw.set_CONJ(gda.conj(p,l));
			kw.set_BASE(gda.base(p,l));
			kw.set_YOMI(gda.yomi(p,l));
			kw.set_SENSE(gda.sense(p,l));
			kw.set_left_context(gda.generate_left_kwic(p));
			if(type == E_KeywordType.FULLTEXT){
				kw.set_right_context(gda.generate_right_kwic(p,l));
			}else{
				kw.set_right_context(gda.generate_right_kwic(p+l));
			}
			kw.set_gda_file(fn_path);
			kw.set_posit_at_gda_file(p);
			kw.set_length_in_gda_file(l);
			if(this.corpus_type == E_Corpus.MAINITI){
				kw.set_artID(fn);
			}else{
				kw.set_midasi(gda.extract_midasi());				
			}
			
			kw_list[id] = kw;
			id++;
			if(id >= size_of_result) break;
		}
		return kw_list;
	}
	
	/**
	 * ヒット件数を返すメソッド
	 */
	public int hit_number(){
		return hit_number;
	}
	/**
	 *  i番目の検索キーワードのGDAファイルにおける位置を返すメソッド
	public int position(E_KeywordType type,int id) throws SE_Exception{
		//if(id < 0 || id >= hit_number) return -1;
		return position_wrapper(type,id);
	}
	 */
	/**
	 * i番目の検索キーワードのGDAファイルにおける長さを返すメソッド
	public int length(E_KeywordType type,int id) throws SE_Exception{
		//if(id < 0 || id >= hit_number) return -1;
		return length_wrapper(type,id); 
	}
	 */

	/* KWICの幅は検索毎に引数として指定するように仕様を変更
	// 左右の幅をセットするメソッド、引数は文字数
	public void set_kwic_length (int char_length) {
		this.left_kwic_char_length = char_length;
		this.right_kwic_char_length = char_length;
	}
	// 左の幅をセットするメソッド、引数は文字数
	public void set_kwic_left_length (int char_length) {
		this.left_kwic_char_length = char_length;
	}
	// 右の幅をセットするメソッド、引数は文字数
	public void set_kwic_right_length (int char_length) {
		this.right_kwic_char_length = char_length;
	}
	*/
	/**
	 * ファイルIDを引数に取り、そのファイルのパスを返すメソッド
	 */
	public String file_path(int file_id){
		String fn = filelist.filename(file_id);
		if(fn == null) return null;
		return this.GDA_Base_DIR + "/" + fn;
	}
	
	private void consult_wrapper(E_KeywordType type,String key) throws SE_Exception{
		if(type == E_KeywordType.SURFACE){
			hw_idx.consult(key);
		}else if(type == E_KeywordType.BASE){
			bs_idx.consult(key);
		}else{	// ==E_KeywordType.FULLTEXT
			sa.consult(key);
		}
	}
	private int hit_number_wrapper(E_KeywordType type) {
		if(type == E_KeywordType.SURFACE){
			return hw_idx.hit_number();
		}else if(type == E_KeywordType.BASE){
			return bs_idx.hit_number();
		}else{	// ==E_KeywordType.FULLTEXT
			return sa.hit_number();
		}
	}
	private int fileID_wrapper(E_KeywordType type,int id) throws SE_Exception {
		if(type == E_KeywordType.SURFACE){
			return hw_idx.fileID(id);
		}else if(type == E_KeywordType.BASE){
			return bs_idx.fileID(id);
		}else{	// ==E_KeywordType.FULLTEXT
			return sa.fileID(id);
		}
	}
	private int position_wrapper(E_KeywordType type,int id) throws SE_Exception {
		if(type == E_KeywordType.SURFACE){
			return hw_idx.position(id);
		}else if(type == E_KeywordType.BASE){
			return bs_idx.position(id);
		}else{	// ==E_KeywordType.FULLTEXT
			return sa.position_at_gda_file(id);
		}
	}
	private int length_wrapper(E_KeywordType type,int id) throws SE_Exception {
		if(type == E_KeywordType.SURFACE){
			return hw_idx.length(id);
		}else if(type == E_KeywordType.BASE){
			return bs_idx.length(id);
		}else{
			return sa.key_length();
		}
	}
}