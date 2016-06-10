/**
 * 設定ファイルのクラス
 * メモ: 設定ファイルのフィールド名の重複をチェックするには main()メソッドを実行する
 * 設定ファイル保存の方針
 * - 各ビューで「保存」したとき、設定ファイルに保存する
 * - ある種類のビューの最後のウィンドウを閉じたとき、その設定項目を内部に保持する
 * - 最後のウィンドウを閉じたとき、設定ファイルに保存し、ツールを終了する
 * - ツールを終了したとき、各ビューの内容を設定ファイルに保存する
 *   同じ種類のビューが複数あるときは、最後に開いたビューの内容をファイルに保存する
 */
package jp.or.gsk.gdacb.gui;

import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_CORPUS;
import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_OS;
import jp.or.gsk.gdacb.*;
import jp.or.gsk.gdacb.search_engine.SE_Exception;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.table.TableColumnModel;

abstract class ConfigurationClass {
	static final int sorter_num = 3;	// ソートキーの数
	static final int filter_num = 3;	// フィルタの数
	static final String font_family_name = "ＭＳ ゴシック";

	interface CONFIGURE_ITEM {
		abstract void set_field_name(String s);
		abstract String get_field_name();
		abstract boolean parse(String s);
		abstract void write(BufferedWriter bw) throws IOException;
		
	}
	// Internal class
	// 設定項目の変数の型ごとにクラスを作成し、
	// 設定ファイルの読み込み、書き込みを実現するメソッドを用意する
	class CONF_FILE implements CONFIGURE_ITEM {
		private File gda_dir;
		private String field_name = null;

		void set(File f){  this.gda_dir = f;  }
		File get(){	 return this.gda_dir;  }
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			this.gda_dir = new File(val);
			return true;
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			out.write(gda_dir.getPath());
			out.write("\n");
		}
	}
	class CONF_BOOLEAN implements CONFIGURE_ITEM {
		private boolean bool;
		private String field_name = null;

		void set(boolean b){  this.bool = b;  }
		boolean get(){	return this.bool;  }
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			int i;
			if(val.equals("")) return false;
			try{
				i = Integer.parseInt(val);
			} catch (NumberFormatException e) {
				return false;
			}
			if(i == 1){
				this.bool = true;
				return true;
			}else if (i == 0){
				this.bool = false;
				return true;
			}else{
				return false;
			}
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			out.write( this.bool ? "1" : "0" );
			out.write("\n");
		}
	}
	class CONF_STRING implements CONFIGURE_ITEM {
		private String str;
		private String field_name = null;

		void set(String s){  this.str = s;  };
		String get(){  return this.str;  };
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			this.str = val;
			return true;
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			out.write(this.str);
			out.write("\n");
		}
	}
	class CONF_INTEGER implements CONFIGURE_ITEM {
		protected int num;
		private String field_name = null;

		void set(int i){  this.num = i;  }
		int get(){	return this.num;  }
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			if(val.equals("")) return false;
			try{
				int i = Integer.parseInt(val);
				this.num = i;
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			out.write( Integer.toString(this.num) );
			out.write("\n");
		}
	}
	class CONF_INTEGER_LIST implements CONFIGURE_ITEM {
		private int[] int_list;
		private int size;
		private String field_name = null;

		CONF_INTEGER_LIST (int size){
			this.int_list = new int[size];
			this.size = size;
		}
		void set(int idx,int i){  this.int_list[idx] = i; };
		int get(int idx){  return this.int_list[idx];  }
		int size(){  return this.size;  };
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			int i;
			String[] sa;
			if(val.equals("")) return false;
			sa = val.split(" ");
			if(sa.length != this.size) return false;
			try {
				for(i=0 ; i < this.size ; i++) set( i, Integer.parseInt(sa[i]) );
			} catch (NumberFormatException e){
				return false;
			}
			return true;
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			for(int i=0 ; i < this.size ; i++){
				if(i != 0) out.write(" ");
				out.write( Integer.toString(this.int_list[i]) );
			}
			out.write("\n");
		}
	}
	class CONF_E_ColWA implements CONFIGURE_ITEM {
		private E_ColWA col_width_adjust;
		private String field_name = null;

		void set(E_ColWA t){  this.col_width_adjust = t;  }
		E_ColWA get(){	return this.col_width_adjust;  }
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			if(val.equals("")) return false;
			int i;
			try{
				i = Integer.parseInt(val);
			} catch (NumberFormatException e) {
				return false;
			}
			for(E_ColWA cwa: E_ColWA.values()){
				if(cwa.conf_value() == i){
					this.col_width_adjust = cwa;
					return true;
				}
			}
			return false;
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			out.write( Integer.toString(this.col_width_adjust.conf_value()) );
			out.write("\n");
		}
	}
	class CONF_E_KeywordType implements CONFIGURE_ITEM {
		private E_KeywordType type;
		private String field_name = null;

		void set(E_KeywordType t){  this.type = t;  }
		E_KeywordType get(){  return this.type;  }
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			if(val.equals("")) return false;
			for(E_KeywordType kt: E_KeywordType.values()){
				if(val.equals(kt.label())){
					this.type = kt;
					return true;
				}
			}
			return false;
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			out.write(this.type.label());
			out.write("\n");
		}
	}
	class CONF_E_MD_KeywordType implements CONFIGURE_ITEM {
		private E_MD_KeywordType type;
		private String field_name = null;

		void set(E_MD_KeywordType t){  this.type = t;  }
		E_MD_KeywordType get(){  return this.type;  }
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			int i;
			if(val.equals("")) return false;
			try{
				i = Integer.parseInt(val);
			} catch (NumberFormatException e) {
				return false;
			}
			for(E_MD_KeywordType kt: E_MD_KeywordType.values()){
				if(i == kt.conf_value()){
					this.type = kt;
					return true;
				}
			}
			return false;
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			out.write( Integer.toString(this.type.conf_value()) );
			out.write("\n");
		}
	}
	class CONF_E_Corpus implements CONFIGURE_ITEM {
		private E_Corpus corpus;
		private String field_name = null;

		void set(E_Corpus c){  this.corpus = c;  }
		E_Corpus get(){  return this.corpus;  }
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			int i;
			if(val.equals("")) return false;
			try{
				i = Integer.parseInt(val);
			} catch (NumberFormatException e) {
				return false;
			}
			if(i == E_Corpus.MAINITI.id()){
				this.corpus = E_Corpus.MAINITI;
				return true;
			}else if(i == E_Corpus.IWANAMI.id()){
				this.corpus = E_Corpus.IWANAMI;
				return true;
			}else{
				return false;
			}
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			out.write( Integer.toString(this.corpus.id()) );
			out.write("\n");
		}
	}
	class CONF_E_WebBrowser implements CONFIGURE_ITEM {
		private E_WebBrowser browser;
		private String field_name = null;

		void set(E_WebBrowser wb){  this.browser = wb;  }
		E_WebBrowser get(){  return this.browser;  }
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			//this.browser = E_WebBrowser.UNDEF;
			if(val.equals("")) return false;
			for(E_WebBrowser wb: E_WebBrowser.values()){
				if(val.equals(wb.id())){
					this.browser = wb;
					return true;
				}
			}
			return false;
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			out.write(this.browser.id());
			out.write("\n");
		}
	}
	class CONF_E_View implements CONFIGURE_ITEM {
		private E_View view;
		private String field_name = null;

		void set(E_View v){  this.view = v;  }
		E_View get(){  return this.view;  }
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			if(val.equals("")) return false;
			for(E_View v: E_View.values()){
				if(val.equals(v.viewName())){
					this.view = v;
					return true;
				}
			}
			return false;
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			out.write(this.view.viewName());
			out.write("\n");
		}
	}
	class CONF_ABS_SORTER_LIST implements CONFIGURE_ITEM {
		private AbstractSorter[] as_list;
		private String field_name = null;

		CONF_ABS_SORTER_LIST () {
			this.as_list = new AbstractSorter[sorter_num];
			for(int i=0 ; i < sorter_num ; i++) this.as_list[i] = null;
		}
		void set(int idx,AbstractSorter as){  this.as_list[idx] = as; };
		void set_list(AbstractSorter[] asl) {
			if(asl == null){
				for(int i=0 ; i < sorter_num ; i++) this.as_list[i] = null;
			}else{
				this.as_list = asl;
			}
		}
		AbstractSorter get(int idx){  return this.as_list[idx];  }
		AbstractSorter[] get_list(){  return this.as_list;  }
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			int i,key,order;
			String[] sa1,sa2;
			if(val.equals("")) return false;
			sa1 = val.split(" ");
			if(sa1.length != sorter_num) return false;
			for(i=0 ; i < sa1.length ; i++){
				sa2 = sa1[i].split(",");
				if(sa2.length != 2) return false;
				try {
					key = Integer.parseInt(sa2[0]);
					order = Integer.parseInt(sa2[1]);
				} catch (NumberFormatException e) {
					return false;
				}
				if(order == 1){
					this.as_list[i] = new AbstractSorter(key,SortOrder.ASCENDING);
				}else if(order == 2){
					this.as_list[i] = new AbstractSorter(key,SortOrder.DESCENDING);
				}else{
					return false;
				}
			}
			return true;
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			for(int i=0 ; i < sorter_num ; i++){
				if(i != 0) out.write(" ");
				if(this.as_list == null || this.as_list[i] == null){
					out.write("-1,1");
				}else{
					out.write( Integer.toString(this.as_list[i].key_index) );
					out.write(",");
					out.write( (this.as_list[i].order == SortOrder.ASCENDING) ? "1" : "2" );
				}
			}
			out.write("\n");
		}
	}
	class CONF_ABS_FILTER_LIST implements CONFIGURE_ITEM {
		private AbstractFilter[] af_list;
		private String field_name = null;

		CONF_ABS_FILTER_LIST () {
			this.af_list = new AbstractFilter[filter_num];
			for(int i=0 ; i < filter_num ; i++) this.af_list[i] = null;
		}
		void set(int idx,AbstractFilter af){  this.af_list[idx] = af; };
		void set_list(AbstractFilter[] afl) {
			if(afl == null){
				for(int i=0 ; i < filter_num ; i++) this.af_list[i] = null;
			}else{
				this.af_list = afl;
			}
		}
		AbstractFilter get(int idx){  return this.af_list[idx];  };
		AbstractFilter[] get_list(){  return this.af_list;  };
		public void set_field_name(String s){ this.field_name = s; }
		public String get_field_name(){ return this.field_name; }
		public boolean parse(String val){
			int i,key_idx,type,not;
			E_FilterType filter_type;
			String[] sa1,sa2;
			if(val.equals("")) return false;
			sa1 = val.split(" ");
			if(sa1.length != filter_num) return false;
			for(i=0 ; i < sa1.length ; i++){
				sa2 = sa1[i].split(",");
				if(sa2.length != 4) return false;
				try {
					key_idx = Integer.parseInt(sa2[0]);
					type = Integer.parseInt(sa2[2]);
					not = Integer.parseInt(sa2[3]);
				} catch (NumberFormatException e) {
					return false;
				};
				if(key_idx < -1) return false;
				filter_type = null;
				for(E_FilterType ft: E_FilterType.values()){
					if(type == ft.index()){
						filter_type = ft;
						break;
					}
				}
				if(filter_type == null) return false;
				if(not == 1){
					this.af_list[i] = new AbstractFilter(key_idx,sa2[1],filter_type,true);
				}else if(not == 0){
					this.af_list[i] = new AbstractFilter(key_idx,sa2[1],filter_type,false);
				}else{
					return false;
				}
			}
			return true;
		}
		public void write(BufferedWriter out) throws IOException {
			out.write(this.field_name+"\t");
			for(int i=0 ; i < filter_num ; i++){
				if(i != 0) out.write(" ");
				if(this.af_list == null || this.af_list[i] == null){
					out.write("-1,,0,0");
				}else{
					out.write( Integer.toString(this.af_list[i].filter_key_index) );
					out.write(",");
					out.write( this.af_list[i].filter_key );
					out.write(",");
					out.write( Integer.toString(this.af_list[i].filter_type.index()) );
					out.write(",");
					out.write( this.af_list[i].not_flag ? "1" : "0" );
				}
			}
			out.write("\n");
		}
	}
	class CONF_FILTER_COMBINATION extends CONF_INTEGER implements CONFIGURE_ITEM {
		@Override
		public boolean parse(String val){
			if(val.equals("")) return false;
			try{
				int i = Integer.parseInt(val);
				if(i == 1 || i == 2){
					num = i;
					return true;
				}else{
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}
}

/**
 * ツールの全ての設定項目を保持するクラス
 */
class Configuration extends ConfigurationClass {
	File conf_file = null;
	ArrayList<CONFIGURE_ITEM> conf_item_list = null;
	
	Conf_ViewFrame conf_vf;
	Conf_KWIC_View conf_kwic;
	Conf_Browse_View conf_browse;
	Conf_Batch_View conf_batch;
	
	// 設定ビューで指定する項目
	CONF_FILE gda_dir_MAI;
	CONF_FILE gda_dir_IWA;
	CONF_BOOLEAN set_web_browser_by_list_flag;
	CONF_E_WebBrowser web_browser_by_list;
	CONF_STRING web_browser_by_input;
	CONF_BOOLEAN newline_sent_flag;
	CONF_E_View default_view;
	// その他
	CONF_STRING version;
	CONF_E_View last_view;

	// constructor
	Configuration (String conf_file_name) {
		this.conf_file = new File(conf_file_name);
		
		this.conf_vf = new Conf_ViewFrame();
		this.conf_kwic = new Conf_KWIC_View();
		this.conf_browse = new Conf_Browse_View();
		this.conf_batch = new Conf_Batch_View();

		// 各項目について以下の処理を行う
		// (1)初期値の設定、(2)フィールド名の設定、(3)conf_item_listの登録(update_conf_item_listメソッドの中で)
		this.version = new CONF_STRING();
		this.version.set(GDA_Corpus_Browser.version);
		this.version.set_field_name("version");

		this.gda_dir_MAI = new CONF_FILE();
		this.gda_dir_MAI.set(new File(""));
		this.gda_dir_MAI.set_field_name("GDA_base_dir_MAI");
		
		this.gda_dir_IWA = new CONF_FILE();
		this.gda_dir_IWA.set(new File(""));
		this.gda_dir_IWA.set_field_name("GDA_base_dir_IWA");
		
		this.set_web_browser_by_list_flag = new CONF_BOOLEAN();
		this.set_web_browser_by_list_flag.set(true);
		this.set_web_browser_by_list_flag.set_field_name("select_web_browser_by_list_flag");

		this.web_browser_by_list = new CONF_E_WebBrowser();
		this.web_browser_by_list.set(E_WebBrowser.UNDEF);
		this.web_browser_by_list.set_field_name("web_browser_list");

		this.web_browser_by_input = new CONF_STRING();
		this.web_browser_by_input.set("");
		this.web_browser_by_input.set_field_name("web_browser_file_path");
		
		this.newline_sent_flag = new CONF_BOOLEAN();
		this.newline_sent_flag.set(false);
		this.newline_sent_flag.set_field_name("file_browse_nl_sent_flag");

		this.default_view = new CONF_E_View();
		this.default_view.set(E_View.BROWSE);
		this.default_view.set_field_name("default_view");
		
		// 設定ビューで設定しない項目
		this.last_view = new CONF_E_View();
		this.last_view.set(E_View.BROWSE);
		this.last_view.set_field_name("last_view");

		update_conf_item_list();
	}
	
	private void update_conf_item_list(){
		this.conf_item_list = new ArrayList<CONFIGURE_ITEM>();

		this.conf_item_list.add(this.version);
		this.conf_item_list.add(this.gda_dir_MAI);
		this.conf_item_list.add(this.gda_dir_IWA);
		this.conf_item_list.add(this.set_web_browser_by_list_flag);
		this.conf_item_list.add(this.web_browser_by_list);
		this.conf_item_list.add(this.web_browser_by_input);
		this.conf_item_list.add(this.newline_sent_flag);
		this.conf_item_list.add(this.default_view);
		this.conf_item_list.add(this.last_view);
	}

	// targt_corpusの値に応じて、毎日もしくは岩波のGDAディレクトリを返すメソッド
	File get_gda_dir (E_Corpus corpus){
		if(corpus == E_Corpus.MAINITI){
			return this.gda_dir_MAI.get();
		}else{
			return this.gda_dir_IWA.get();
		}
	}
	
	boolean read_conf_file () throws IOException, NumberFormatException, SE_Exception {
		int line_no = 0, read_item_num = 0;
		BufferedReader in = null;
		boolean format_error_flag = false, fill_all_field_flag, hit_field_flag;
		String line;
		String[] line_array;
		ArrayList<CONFIGURE_ITEM> all_conf_item_list;

		if(! conf_file.isFile()) {
			return true;	// 設定ファイルがないときは新規に作成する仕様にする(エラーではない)
		}

		all_conf_item_list = get_all_conf_item_list();

		try {
			//in = new BufferedReader(new FileReader(conf_file));
			in = new BufferedReader( new InputStreamReader( new FileInputStream(conf_file), "UTF-8") );
			
			while( (line=in.readLine()) != null ){
				line_no++;
				if(! line.contains("\t")){
					format_error_flag = true;
					show_warn(line_no,"no separator found");
					continue;
				}
				line_array = line.split("\t",-1);
				if(line_array.length > 2){
					format_error_flag = true;
					show_warn(line_no,"two or more separators found");
					continue;
				}
				//if(line_array[1].equals("")){
				//	format_error_flag = true;
				//	show_warn(line_no,"empty field value");
				//	continue;
				//}
				hit_field_flag = false;
				for(CONFIGURE_ITEM item: all_conf_item_list){
					if(line_array[0].equals( item.get_field_name() )){
						hit_field_flag = true;
						if( item.parse(line_array[1]) ){
							break;
						}else{
							format_error_flag = true;
							show_warn(line_no,"illegal format");
							break;
						}
					}
				}
				if(! hit_field_flag){
					format_error_flag = true;
					show_warn(line_no,"unknown field `"+line_array[0]+"'");
				}else{
					read_item_num++;
				}
			}
		} catch (IOException e) {
//			e.printStackTrace();
			throw e;
		} catch (NumberFormatException e) {
//			e.printStackTrace();
			throw e;
//		} catch (SE_Exception e){
//			e.printStackTrace();
//			throw e;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {}
		}
		
		/* 以下、設定ファイル読み込み後の処理 */

		fill_all_field_flag = (read_item_num == all_conf_item_list.size()) ? true : false;
		this.conf_vf.changeFontSize(this.conf_vf.font_size.get());
		// システム設定と設定ファイルのコーパス設定に矛盾があれば修正
		/*
    	if(SYSTEM_CORPUS == E_Corpus.MAINITI && this.conf_vf.target_corpus.get() == E_Corpus.IWANAMI){ 
    		this.conf_vf.target_corpus.set(E_Corpus.MAINITI);
    	}else if(SYSTEM_CORPUS == E_Corpus.IWANAMI && this.conf_vf.target_corpus.get() == E_Corpus.MAINITI){ 
    		this.conf_vf.target_corpus.set(E_Corpus.IWANAMI);
    	}
    	*/
		if(SYSTEM_CORPUS == E_Corpus.MAINITI){
			if(this.conf_kwic.target_corpus.get() == E_Corpus.IWANAMI)
				this.conf_kwic.target_corpus.set(E_Corpus.MAINITI);
			if(this.conf_browse.target_corpus.get() == E_Corpus.IWANAMI)
				this.conf_browse.target_corpus.set(E_Corpus.MAINITI);
			if(this.conf_batch.target_corpus.get() == E_Corpus.IWANAMI)
				this.conf_batch.target_corpus.set(E_Corpus.MAINITI);
		}else if(SYSTEM_CORPUS == E_Corpus.IWANAMI){
			if(this.conf_kwic.target_corpus.get() == E_Corpus.MAINITI)
				this.conf_kwic.target_corpus.set(E_Corpus.IWANAMI);
			if(this.conf_browse.target_corpus.get() == E_Corpus.MAINITI)
				this.conf_browse.target_corpus.set(E_Corpus.IWANAMI);
			if(this.conf_batch.target_corpus.get() == E_Corpus.MAINITI)
				this.conf_batch.target_corpus.set(E_Corpus.IWANAMI);			
		}
    	// ウェブブラウザの設定とOSに矛盾があれば修正
    	E_WebBrowser wb = web_browser_by_list.get(); 
    	if(wb != E_WebBrowser.UNDEF && wb != E_WebBrowser.DEFAULT && wb.os() != SYSTEM_OS){
    		System.err.println("Warning: OS of configured Web browser is different with current system");
    		web_browser_by_list.set( E_WebBrowser.UNDEF );
    	}
    	// バージョン番号の矛盾を修正
    	if(! GDA_Corpus_Browser.version.equals( this.version.get() )){
    		this.version.set(GDA_Corpus_Browser.version);
    	}
    	
    	if(format_error_flag) return false;
    	if(fill_all_field_flag){
    		return true;
    	}else{
    		System.err.println("Warning: not all fields are defined");
    		return false;
    	}
	}
	
	void write_conf_file() throws IOException {
		BufferedWriter out = null;
		try {
			//out = new BufferedWriter( new FileWriter( conf_file ) );
			out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(conf_file), "UTF-8" ) );
			
			for(CONFIGURE_ITEM item: get_all_conf_item_list()) item.write(out);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {}
		}
	}
	private void show_warn(int line_no,String msg){
		System.err.println("ERROR in configuration file at line "+line_no+" : "+msg);
	}
	/**
	 * 全ての設定項目のリストを得る
	 */
	private ArrayList<CONFIGURE_ITEM> get_all_conf_item_list(){
		ArrayList<CONFIGURE_ITEM> l = new ArrayList<CONFIGURE_ITEM>();
		for(CONFIGURE_ITEM i: this.conf_item_list) l.add(i);
		for(CONFIGURE_ITEM i: this.conf_vf.conf_item_list) l.add(i);
		for(CONFIGURE_ITEM i: this.conf_kwic.conf_item_list) l.add(i);
		for(CONFIGURE_ITEM i: this.conf_browse.conf_item_list) l.add(i);
		for(CONFIGURE_ITEM i: this.conf_batch.conf_item_list) l.add(i);
		return l;
	}
	
	public static void main(String[] args) {
		/* フィールド名の重複をチェックする */
		Configuration conf = new Configuration("config.txt");
		HashMap<String,Integer> field_names = new HashMap<String,Integer>();
		for(CONFIGURE_ITEM cf_item: conf.get_all_conf_item_list()){
			if(field_names.containsKey(cf_item.get_field_name())){
				int i = field_names.get(cf_item.get_field_name());
				field_names.put(cf_item.get_field_name(),i+1);
			}else{
				field_names.put(cf_item.get_field_name(), 1);
			}
		}

		System.out.println("Number of items: "+conf.get_all_conf_item_list().size());
		boolean flag = true;
		for(String fn: field_names.keySet()){
			if(field_names.get(fn) > 1){
				System.out.println("duplicate field name: "+fn+","+field_names.get(fn));
				flag = false;
			}
		}
		if(flag) System.out.println("OK: no duplicate field name found");
		
		/* clone()のテスト
		Conf_Batch_View cvb = conf.conf_batch.clone();
		cvb.batch_max_output_num.set(1000);
		System.out.println(conf.conf_batch.batch_max_output_num.get());
		System.out.println(cvb.batch_max_output_num.get());
		*/
		/* 読み込みのテスト
		try {
			flag = conf.read_conf_file();
			if(flag) System.out.println("OK: success to read configuration file");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SE_Exception e) {
			e.printStackTrace();
		}
		*/
		/* 書き込みのテスト
		try {
			conf.write_conf_file();
			System.out.println("OK: success to write configuration file");
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
}

/**
 * ビュー(ウィンドウ)に関する設定項目を保持するクラス
 */
class Conf_ViewFrame extends ConfigurationClass {
	ArrayList<CONFIGURE_ITEM> conf_item_list = null;
	Font font = null;

	//CONF_E_Corpus target_corpus;	// 仕様変更: ビュー毎に保存
	CONF_INTEGER font_size;

	Conf_ViewFrame(){
		// 各項目について以下の処理を行う
		// (1)初期値の設定、(2)フィールド名の設定、(3)conf_item_listの登録(update_conf_item_listメソッドの中で)
		/*
		this.target_corpus = new CONF_E_Corpus();
		if(SYSTEM_CORPUS == E_Corpus.IWANAMI){
			this.target_corpus.set(E_Corpus.IWANAMI);
		}else{
			this.target_corpus.set(E_Corpus.MAINITI);
		}
		this.target_corpus.set_field_name("target_corpus");
		*/

		this.font_size = new CONF_INTEGER();
		this.font_size.set(14);
		this.font_size.set_field_name("font_size");
		
		setFont(this.font_size.get());
		update_conf_item_list();
	}
	private void update_conf_item_list(){
		this.conf_item_list = new ArrayList<CONFIGURE_ITEM>();

		//this.conf_item_list.add(this.target_corpus);
		this.conf_item_list.add(this.font_size);
	}
	
	@Override
	public Conf_ViewFrame clone(){
		Conf_ViewFrame cl = new Conf_ViewFrame();

		// cl.target_corpus.set( this.target_corpus.get() );
		cl.font_size.set( this.font_size.get() );
		cl.font = new Font(font_family_name, Font.PLAIN, this.font_size.get() );

		cl.update_conf_item_list();
		return cl;
	}
	/**
	 * フォントサイズを変更するメソッド
	 * @param new_size	変更後のフォントサイズ
	 */
	void changeFontSize(int new_size){
		if(new_size == font_size.get()) return;
		setFont(new_size);
		font_size.set(new_size);
	}
	/**
	 * フォントを変更するメソッド
	 * @param family	変更後のフォントファミリー
	 */
	void changeFont(String family){
		setFont(family,font_size.get());
	}
	/**
	 * フォントを設定する(保存する)メソッド
	 * @param size	フォントサイズ
	 */
	private void setFont(int size){
		//font = new Font(Font.DIALOG, Font.PLAIN, size );
		font = new Font(font_family_name, Font.PLAIN, size );	
	}
	/**
	 * フォントを設定する(保存する)メソッド
	 * @param family	フォントファミリー
	 * @param size		フォントサイズ
	 */
	private void setFont(String family,int size){
		font = new Font(family, Font.PLAIN, size);

		// 利用可能な font family のリストを得る
//		String[] ffn_list = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
//		for(String ffn: ffn_list) System.out.println(ffn);

		// 物理フォントが存在するかチェック
//		String fs = "HGS明朝E";
//		Font f = new Font( fs, Font.PLAIN, 14 );
//		if(f.getFamily().equals(fs)){
//			System.out.println("aru "+f.getFamily());
//		}else{
//			System.out.println("nai "+f.getFamily());
//		}
		// 以下は使用するフォントの候補
//		font = new Font(Font.DIALOG, Font.PLAIN, size );		
//		font = new Font(Font.MONOSPACED, Font.PLAIN, size );		
//		font = new Font("Dialog", Font.PLAIN, size );
//		font = new Font("HGS創英ﾌﾟﾚｾﾞﾝｽEB", Font.PLAIN, size );	// 等幅
//		font = new Font("HGS明朝E", Font.PLAIN, size );		// 等幅
//		font = new Font("ＭＳ ゴシック", Font.PLAIN, size );	// 等幅
//		font = new Font("ＭＳ 明朝", Font.PLAIN, size );		// 等幅
//		font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, size );	// 可変、でも割ときれい
//		font = new Font("ＭＳ Ｐ明朝", Font.PLAIN, size );		// 可変
//		font = new Font("MS PMincho", Font.PLAIN, size );
//		font = new Font("MS Mincho", Font.PLAIN, size );
	}
}

/**
 * 検索ビューの設定項目を保持するクラス
 */
class Conf_KWIC_View extends ConfigurationClass {
	ArrayList<CONFIGURE_ITEM> conf_item_list = null;

	CONF_E_Corpus target_corpus;
	CONF_INTEGER_LIST column_width_MAI;
	CONF_INTEGER_LIST column_width_IWA;
	CONF_INTEGER_LIST displayed_column_posit_MAI;
	CONF_INTEGER_LIST displayed_column_posit_IWA;
	CONF_BOOLEAN use_flexible_TA_for_cell_flag;
	CONF_BOOLEAN allow_row_selection_flag;
	// in 検索タブ
	CONF_E_KeywordType keyword_type;
	CONF_BOOLEAN always_sort_flag;
	CONF_BOOLEAN always_filter_flag;
	// in ソートタブ
	CONF_ABS_SORTER_LIST abs_sorter_list;
	// in フィルタータブ
	CONF_ABS_FILTER_LIST abs_filter_list;
	CONF_INTEGER filter_combination;	// AND = 1, OR = 2
	// in エクスポートタブ
	CONF_BOOLEAN export_table_asis;
	CONF_BOOLEAN export_add_sense;
	CONF_STRING export_output_coding;
	// in 設定タブ in KWICビュー
	CONF_INTEGER max_display_num;
	CONF_INTEGER left_kwic_length;
	CONF_INTEGER right_kwic_length;
	CONF_E_ColWA column_width_adjustment;
	
	Conf_KWIC_View(){
		int i;
		
		// 各項目について以下の処理を行う
		// (1)初期値の設定、(2)フィールド名の設定、(3)conf_item_listの登録(update_conf_item_listメソッドの中で)
		this.target_corpus = new CONF_E_Corpus();
		if(SYSTEM_CORPUS == E_Corpus.IWANAMI){
			this.target_corpus.set(E_Corpus.IWANAMI);
		}else{
			this.target_corpus.set(E_Corpus.MAINITI);
		}
		this.target_corpus.set_field_name("target_corpus_kwic");
		
		this.column_width_MAI = new CONF_INTEGER_LIST( KWIC_TableModel_MAI.columnArray.length );
		for(i=0 ; i < KWIC_TableModel_MAI.columnArray.length ; i++)
			this.column_width_MAI.set( i, KWIC_TableModel_MAI.columnArray[i].defaultColumnWidth );
		this.column_width_MAI.set_field_name("column_width_MAI");

		this.column_width_IWA = new CONF_INTEGER_LIST( KWIC_TableModel_IWA.columnArray.length );
		for(i=0 ; i < KWIC_TableModel_IWA.columnArray.length ; i++)
			column_width_IWA.set( i, KWIC_TableModel_IWA.columnArray[i].defaultColumnWidth );
		this.column_width_IWA.set_field_name("column_width_IWA");

		this.displayed_column_posit_MAI = new CONF_INTEGER_LIST( KWIC_TableModel_MAI.columnArray.length );
		for(i=0 ; i < KWIC_TableModel_MAI.columnArray.length ; i++)
			displayed_column_posit_MAI.set(i,i);
		this.displayed_column_posit_MAI.set_field_name("column_order_MAI");

		this.displayed_column_posit_IWA = new CONF_INTEGER_LIST( KWIC_TableModel_IWA.columnArray.length );
		for(i=0 ; i < KWIC_TableModel_IWA.columnArray.length ; i++)
			displayed_column_posit_IWA.set(i,i);
		this.displayed_column_posit_IWA.set_field_name("column_order_IWA");

		this.use_flexible_TA_for_cell_flag = new CONF_BOOLEAN();
		this.use_flexible_TA_for_cell_flag.set(false);
		this.use_flexible_TA_for_cell_flag.set_field_name("cell_area_flexible_size_flag");

		this.allow_row_selection_flag = new CONF_BOOLEAN();
		this.allow_row_selection_flag.set(false);
		this.allow_row_selection_flag.set_field_name("select_row_in_KWIC_table_flag");

		this.keyword_type = new CONF_E_KeywordType();
		this.keyword_type.set(E_KeywordType.FULLTEXT);
		this.keyword_type.set_field_name("keyword_type_search_tab");

		this.always_sort_flag = new CONF_BOOLEAN();
		this.always_sort_flag.set(false);
		this.always_sort_flag.set_field_name("always_sort_flag");

		this.always_filter_flag = new CONF_BOOLEAN();
		this.always_filter_flag.set(false);
		this.always_filter_flag.set_field_name("always_filter_flag");

		this.abs_sorter_list = new CONF_ABS_SORTER_LIST();
		for(i=0 ; i < sorter_num ; i++) this.abs_sorter_list.set(i,null);
		this.abs_sorter_list.set_field_name("sorter");

		this.abs_filter_list = new CONF_ABS_FILTER_LIST();
		for(i=0 ; i < filter_num ; i++) this.abs_filter_list.set(i,null);
		this.abs_filter_list.set_field_name("filter");

		this.filter_combination = new CONF_FILTER_COMBINATION();
		this.filter_combination.set(1);	// AND
		this.filter_combination.set_field_name("filter_combination");

		this.export_table_asis = new CONF_BOOLEAN();
		this.export_table_asis.set(true);
		this.export_table_asis.set_field_name("export_table_as_is_flag");

		this.export_add_sense = new CONF_BOOLEAN();
		this.export_add_sense.set(false);
		this.export_add_sense.set_field_name("add_sense_definition_flag_export_tab");

		this.export_output_coding = new CONF_STRING();
		this.export_output_coding.set("Shift_JIS");
		this.export_output_coding.set_field_name("coding_system_export_tab");

		this.max_display_num = new CONF_INTEGER();
		this.max_display_num.set(50);
		this.max_display_num.set_field_name("max_sentence_number_KWIC_table");

		this.left_kwic_length = new CONF_INTEGER();
		this.left_kwic_length.set(20);
		this.left_kwic_length.set_field_name("length_of_left_context");

		this.right_kwic_length = new CONF_INTEGER();
		this.right_kwic_length.set(20);
		this.right_kwic_length.set_field_name("length_of_right_context");

		this.column_width_adjustment = new CONF_E_ColWA();
		this.column_width_adjustment.set(E_ColWA.DEFAULT);
		this.column_width_adjustment.set_field_name("column_width_adjustment");
		
		update_conf_item_list();
	}
	private void update_conf_item_list(){
		this.conf_item_list = new ArrayList<CONFIGURE_ITEM>();

		this.conf_item_list.add(this.target_corpus);
		this.conf_item_list.add(this.column_width_MAI);
		this.conf_item_list.add(this.column_width_IWA);
		this.conf_item_list.add(this.displayed_column_posit_MAI);
		this.conf_item_list.add(this.displayed_column_posit_IWA);
		this.conf_item_list.add(this.use_flexible_TA_for_cell_flag);
		this.conf_item_list.add(this.allow_row_selection_flag);
		this.conf_item_list.add(this.keyword_type);
		this.conf_item_list.add(this.always_sort_flag);
		this.conf_item_list.add(this.always_filter_flag);
		this.conf_item_list.add(this.abs_sorter_list);
		this.conf_item_list.add(this.abs_filter_list);
		this.conf_item_list.add(this.filter_combination);
		this.conf_item_list.add(this.export_table_asis);
		this.conf_item_list.add(this.export_add_sense);
		this.conf_item_list.add(this.export_output_coding);
		this.conf_item_list.add(this.max_display_num);
		this.conf_item_list.add(this.left_kwic_length);
		this.conf_item_list.add(this.right_kwic_length);
		this.conf_item_list.add(this.column_width_adjustment);
	}
	
	void set_current_column_width (JTable table,E_Corpus corpus_type) {
		int i,j;
		TableColumnModel tcm = table.getColumnModel();
		for(i=0 ; i < tcm.getColumnCount() ; i++){
			// 表示中の列のインデックスから元のテーブルのインデックスを得る
			j = tcm.getColumn(i).getModelIndex();
			if(corpus_type == E_Corpus.MAINITI){
				column_width_MAI.set( j, tcm.getColumn(i).getWidth() );
			}else{
				column_width_IWA.set( j, tcm.getColumn(i).getWidth() );
			}
		}
	}
	void set_current_displayed_column_posit (JTable table,E_Corpus corpus_type) {
		int i,j;
		TableColumnModel tcm = table.getColumnModel();
		for(i=0 ; i < tcm.getColumnCount() ; i++){
			// 表示中の列のインデックスから元のテーブルのインデックスを得る
			j = tcm.getColumn(i).getModelIndex();
			if(corpus_type == E_Corpus.MAINITI){
				displayed_column_posit_MAI.set(j,i);
			}else{
				displayed_column_posit_IWA.set(j,i);
			}
		}
//		System.out.print("%%%");
//		for(int k=0 ; k < tcm.getColumnCount() ; k++){ System.out.print(displayed_column_posit_MAI.get(k)+","); }; System.out.print("\n");
	}

	@Override
	public Conf_KWIC_View clone(){
		int i;
		Conf_KWIC_View cl = new Conf_KWIC_View();
		
		cl.target_corpus.set( this.target_corpus.get() );
		for(i=0 ; i < KWIC_TableModel_MAI.columnArray.length ; i++)
			cl.column_width_MAI.set( i, this.column_width_MAI.get(i) );
		for(i=0 ; i < KWIC_TableModel_IWA.columnArray.length ; i++)
			cl.column_width_IWA.set( i, this.column_width_IWA.get(i) );
		for(i=0 ; i < KWIC_TableModel_MAI.columnArray.length ; i++)
			cl.displayed_column_posit_MAI.set( i, this.displayed_column_posit_MAI.get(i) );
		for(i=0 ; i < KWIC_TableModel_IWA.columnArray.length ; i++)
			cl.displayed_column_posit_IWA.set( i, this.displayed_column_posit_IWA.get(i) );
		cl.use_flexible_TA_for_cell_flag.set( this.use_flexible_TA_for_cell_flag.get() );
		cl.allow_row_selection_flag.set( this.allow_row_selection_flag.get() );
		cl.keyword_type.set( this.keyword_type.get() );
		cl.always_sort_flag.set( this.always_sort_flag.get() );
		cl.always_filter_flag.set( this.always_filter_flag.get() );
		for(i=0 ; i < sorter_num ; i++) cl.abs_sorter_list.set( i, this.abs_sorter_list.get(i) );
		for(i=0 ; i < filter_num ; i++) cl.abs_filter_list.set( i, this.abs_filter_list.get(i) );
		cl.filter_combination.set( this.filter_combination.get() );
		cl.export_table_asis.set( this.export_table_asis.get() );
		cl.export_add_sense.set( this.export_add_sense.get() );
		cl.export_output_coding.set( this.export_output_coding.get() );
		cl.max_display_num.set( this.max_display_num.get() );
		cl.left_kwic_length.set( this.left_kwic_length.get() );
		cl.right_kwic_length.set( this.right_kwic_length.get() );
		cl.column_width_adjustment.set( this.column_width_adjustment.get() );
		
		cl.update_conf_item_list();
		return cl;
	}
}

/**
 * 閲覧ビューの設定項目を保持するクラス
 */
class Conf_Browse_View extends ConfigurationClass {
	ArrayList<CONFIGURE_ITEM> conf_item_list = null;
	CONF_E_Corpus target_corpus;
	CONF_E_MD_KeywordType metadata_keyword_type_MAI;
	CONF_E_MD_KeywordType metadata_keyword_type_IWA;

	Conf_Browse_View(){
		// 各項目について以下の処理を行う
		// (1)初期値の設定、(2)フィールド名の設定、(3)conf_item_listの登録(update_conf_item_listメソッドの中で)
		this.target_corpus = new CONF_E_Corpus();
		if(SYSTEM_CORPUS == E_Corpus.IWANAMI){
			this.target_corpus.set(E_Corpus.IWANAMI);
		}else{
			this.target_corpus.set(E_Corpus.MAINITI);
		}
		this.target_corpus.set_field_name("target_corpus_browse");

		this.metadata_keyword_type_MAI = new CONF_E_MD_KeywordType();
		this.metadata_keyword_type_MAI.set(E_MD_KeywordType.UNDEF);
		this.metadata_keyword_type_MAI.set_field_name("keyword_type_browse_view_MAI");

		this.metadata_keyword_type_IWA = new CONF_E_MD_KeywordType();
		this.metadata_keyword_type_IWA.set(E_MD_KeywordType.UNDEF);
		this.metadata_keyword_type_IWA.set_field_name("keyword_type_browse_view_IWA");
		
		update_conf_item_list();
	}
	private void update_conf_item_list(){
		this.conf_item_list = new ArrayList<CONFIGURE_ITEM>();

		this.conf_item_list.add(this.target_corpus);
		this.conf_item_list.add(this.metadata_keyword_type_MAI);
		this.conf_item_list.add(this.metadata_keyword_type_IWA);
	}

	@Override
	public Conf_Browse_View clone(){
		Conf_Browse_View cl = new Conf_Browse_View();

		cl.target_corpus.set( this.target_corpus.get() );
		cl.metadata_keyword_type_MAI.set( this.metadata_keyword_type_MAI.get() );
		cl.metadata_keyword_type_IWA.set( this.metadata_keyword_type_IWA.get() );

		cl.update_conf_item_list();
		return cl;
	}
}

/**
 * 一括検索ビューの設定項目を保持するクラス
 */
class Conf_Batch_View extends ConfigurationClass {
	ArrayList<CONFIGURE_ITEM> conf_item_list = null;
	CONF_E_Corpus target_corpus;
	CONF_E_KeywordType batch_keyword_type;
	CONF_INTEGER batch_max_output_num;
	CONF_INTEGER batch_left_kwic_length;
	CONF_INTEGER batch_right_kwic_length;
	CONF_BOOLEAN batch_add_sense;
	CONF_STRING batch_output_coding;

	Conf_Batch_View(){
		// 各項目について以下の処理を行う
		// (1)初期値の設定、(2)フィールド名の設定、(3)conf_item_listの登録(update_conf_item_listメソッドの中で)
		this.target_corpus = new CONF_E_Corpus();
		if(SYSTEM_CORPUS == E_Corpus.IWANAMI){
			this.target_corpus.set(E_Corpus.IWANAMI);
		}else{
			this.target_corpus.set(E_Corpus.MAINITI);
		}
		this.target_corpus.set_field_name("target_corpus_batch");

		this.batch_keyword_type = new CONF_E_KeywordType();
		this.batch_keyword_type.set(E_KeywordType.FULLTEXT);
		this.batch_keyword_type.set_field_name("keyword_type_batch_view");

		this.batch_max_output_num = new CONF_INTEGER();
		this.batch_max_output_num.set(50);
		this.batch_max_output_num.set_field_name("max_sentence_number_batch_view");

		this.batch_left_kwic_length = new CONF_INTEGER();
		this.batch_left_kwic_length.set(20);
		this.batch_left_kwic_length.set_field_name("length_of_left_context_batch_view");
		
		this.batch_right_kwic_length = new CONF_INTEGER();
		this.batch_right_kwic_length.set(20);
		this.batch_right_kwic_length.set_field_name("length_of_right_context_batch_view");

		this.batch_add_sense = new CONF_BOOLEAN();
		this.batch_add_sense.set(false);
		this.batch_add_sense.set_field_name("add_sense_definition_flag_batch_view");

		this.batch_output_coding = new CONF_STRING();
		this.batch_output_coding.set("Shift_JIS");
		this.batch_output_coding.set_field_name("coding_system_batch_view");

		update_conf_item_list();
	}
	private void update_conf_item_list(){
		this.conf_item_list = new ArrayList<CONFIGURE_ITEM>();

		this.conf_item_list.add(this.target_corpus);
		this.conf_item_list.add(this.batch_keyword_type);
		this.conf_item_list.add(this.batch_max_output_num);
		this.conf_item_list.add(this.batch_left_kwic_length);
		this.conf_item_list.add(this.batch_right_kwic_length);
		this.conf_item_list.add(this.batch_add_sense);
		this.conf_item_list.add(this.batch_output_coding);
	}
	
	@Override
	public Conf_Batch_View clone(){
		Conf_Batch_View cl = new Conf_Batch_View();

		cl.target_corpus.set( this.target_corpus.get() );
		cl.batch_keyword_type.set( this.batch_keyword_type.get() );
		cl.batch_max_output_num.set( this.batch_max_output_num.get() );
		cl.batch_left_kwic_length.set( this.batch_left_kwic_length.get() );
		cl.batch_right_kwic_length.set( this.batch_right_kwic_length.get() );
		cl.batch_add_sense.set( this.batch_add_sense.get() );
		cl.batch_output_coding.set( this.batch_output_coding.get() );

		cl.update_conf_item_list();
		return cl;
	}
}