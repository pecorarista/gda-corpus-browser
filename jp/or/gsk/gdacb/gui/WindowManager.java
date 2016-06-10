/**
 * GUI全体を制御するクラス
 */
package jp.or.gsk.gdacb.gui;

import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_CORPUS;
import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_OS;
import jp.or.gsk.gdacb.*;
import jp.or.gsk.gdacb.search_engine.*;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class WindowManager {
	private static final String fail_read_conf_msg = "設定ファイルの読み込みに失敗しました";
	private static final File help_file = new File("manual"+File.separator+"00index.html");
	private static final String common_window_title = "GDA Corpus Browser - ";

	Configuration conf = null;
    ViewList view_list = null;
    // 開いている設定ビュー(設定ビューを開いていないときはnull)
    private ConfViewFrame conf_frame = null;
    // インデックス作成画面(開いていないときはnull)
    private MergeGDAFrame merge_gda_frame = null;

    SearchEngine_MAI se_mai = null;
	SearchEngine_IWA se_iwa = null;
	IwanamiDic iwa_dic = null;
	SA_Metadata_MAI sa_md_mai = null;
	SA_Metadata_IWA sa_md_iwa = null;
	SA_Metadata sa_md = null;
	// 初期のビュー
	E_View init_view = null;
    // サーチエンジンの状態  0:不明(初期状態)、1:OK、2:エラー
    int se_mai_status = 0;
    int se_iwa_status = 0;
	WebBrowserHandler web_browser = null;
    //JMenu jMenu_window = null;

    private boolean show_conf_view_at_launch_flag = false;
    private int id_of_gda_tmpfile = 0;	// GDAファイルの一時ファイルにつける識別番号 
    private int id_of_tree_tmpfile = 0;	// 構文木の一時ファイルにつける識別番号
    private ImageIcon fatal_error_icon = null;

	public WindowManager(){
		this.conf = new Configuration( SYSTEM_CORPUS.confFilename() );
		this.view_list = new ViewList();
		initial_setup_of_system();
		
		if(this.init_view == E_View.BATCH){
			create_new_batch_view(null);
		}else if(this.init_view == E_View.SEARCH){
			create_new_kwic_view(null);
		}else{
			create_new_browse_view(null);
		}

		if(this.show_conf_view_at_launch_flag) open_conf_view();
	}
	
	private void initial_setup_of_system() {
		// 設定ファイルの読み込み
		try {
	    	boolean flag = conf.read_conf_file();
	    	if(!flag){
	    		show_error_message_in_dialog(fail_read_conf_msg,null);
	    	}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Fail to read configuration file");
    		show_error_message_in_dialog(fail_read_conf_msg,null);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.err.println("Fail to read configuration file");
    		show_error_message_in_dialog(fail_read_conf_msg,null);
		} catch (SE_Exception e) {
			e.printStackTrace();
			System.err.println(e.MsgE);
    		show_error_message_in_dialog(fail_read_conf_msg,null);
		}

		// 初期のビューの決定
		if(this.conf.default_view.get() == E_View.PRESERVE){
			this.init_view = this.conf.last_view.get();
		}else{
			this.init_view = this.conf.default_view.get();
		}
    	
    	/* サーチエンジンの作成 */
		E_Corpus corpus = null;
		if(this.init_view == E_View.BROWSE){
			corpus = this.conf.conf_browse.target_corpus.get();
		}else if(this.init_view == E_View.SEARCH){
			corpus = this.conf.conf_kwic.target_corpus.get();
		}else if(this.init_view == E_View.BATCH){
			corpus = this.conf.conf_batch.target_corpus.get();
		}
    	if(corpus == E_Corpus.MAINITI){
    		prepare_search_engine_MAI(conf.gda_dir_MAI.get(),true,true);
    		this.se_iwa = null;
    		if(this.se_mai == null) this.show_conf_view_at_launch_flag = true;
    	}else if(corpus == E_Corpus.IWANAMI){
    		prepare_search_engine_IWA(conf.gda_dir_IWA.get(),true,true);
    		this.se_mai = null;
    		if(this.se_iwa == null) this.show_conf_view_at_launch_flag = true;
    	}
    	if(SYSTEM_CORPUS != E_Corpus.MAINITI){
    		prepare_IwanamiDic(conf.gda_dir_IWA.get(),true);
    	}else{
    		this.iwa_dic = null;
    	}
    	
    	// ウェブブラウザ
    	web_browser = new WebBrowserHandler(SYSTEM_OS);
	}
	/**
	 * 設定ファイルに保存する
	 */
	boolean save_conf_file() {
		try {
			this.conf.write_conf_file();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			show_fatal_error_message_in_dialog("設定ファイルの書き込みに失敗しました",null);
			return false;
		}
	}
	/**
	 * 閲覧ビューのウィンドウを新しく作成する
	 */
	void create_new_browse_view(ViewFrame source){
		if(source != null) source.save_parameter_global();
		ViewFrame fr = new ViewFrame(this,E_View.BROWSE,source);
		// change_target_corpus() は検索エンジンの準備の処理を含む
		// 検索エンジン作成時のエラーのダイアログは、ビューを表示した後に表示するため、ここで実行する
		// → 仕様変更:
		//   ビュー作成時には検索エンジン作成時のエラーをダイアログで表示しないようにしたため、
		//   BrowseView#initialize() の中に実行するようにした 
		// fr.browse_view.change_target_corpus();

		int id = view_list.get_max_id(E_View.BROWSE);
		id++;
		this.view_list.add(fr, id);
	}
	/**
	 * 検索ビューのウィンドウを新しく作成する
	 */
	void create_new_kwic_view(ViewFrame source){
		if(source != null) source.save_parameter_global();
		ViewFrame fr = new ViewFrame(this,E_View.SEARCH,source);

		int id = view_list.get_max_id(E_View.SEARCH);
		id++;
		this.view_list.add(fr, id);
	}
	/**
	 * 一括検索ビューのウィンドウを新しく作成する
	 */
	void create_new_batch_view(ViewFrame source){
		if(source != null) source.save_parameter_global();
		ViewFrame fr = new ViewFrame(this,E_View.BATCH,source);
		// change_target_corpus() は検索エンジンの準備の処理を含む
		// 検索エンジン作成時のエラーのダイアログは、ビューを表示した後に表示するため、ここで実行する
		// → 仕様変更:
		//   ビュー作成時には検索エンジン作成時のエラーをダイアログで表示しないようにしたため、
		//   BrowseView#initialize() の中に実行するようにした 
		//fr.batch_view.change_target_corpus();

		int id = view_list.get_max_id(E_View.BATCH);
		id++;
		this.view_list.add(fr, id);
	}
	/**
	 * ビューのウィンドウを閉じる
	 */
	void close_view(ViewFrame frame){
		// 閉じようとするビューと同じ種類のビューが他に存在しないとき、そのビューの設定項目を保存する
		int view_count = view_list.count_view(frame.view_type);
		if(view_count == 1) frame.save_parameter_global();
		
		if(view_list.size() == 1){
			// 最後のビューのとき、確認ダイアログを表示してから、ツールを終了する
			quit_with_confirmation(frame);
		}else{
			this.view_list.remove(frame);
			frame.dispose();
		}
	}
	/**
	 * 設定ビューのウィンドウを新しく作成する
	 */
	void open_conf_view(){
		if(this.conf_frame == null){
			this.conf_frame = new ConfViewFrame(this);
		}else{
			this.conf_frame.toFront();
		}
	}
	/**
	 * 設定ビューのウィンドウを閉じる(設定の保存はしない)
	 */
	void close_conf_view(){
		this.conf_frame.dispose();
		this.conf_frame = null;
	}
	/**
	 * インデックス作成のウィンドウを新しく作成する
	 */
	void open_merge_gda_frame(){
		if(this.merge_gda_frame == null){
			this.merge_gda_frame = new MergeGDAFrame(this);
		}else{
			this.merge_gda_frame.toFront();
		}
	}
	/**
	 * インデックス作成のウィンドウを閉じる
	 */
	void close_merge_gda_frame(){
		this.merge_gda_frame.dispose();
		this.merge_gda_frame = null;
	}
	/**
	 * 確認ダイアログを表示してからツールを終了する(メニューバーの「終了」を選択したとき)
	 */
	void quit_with_confirmation(ViewFrame frame){
		int responce = JOptionPane.showConfirmDialog(frame,
				"ツールを終了しますか?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
		if(responce != JOptionPane.OK_OPTION) return;
		quit(frame);
	}
	/**
	 * ツールを終了する(メニューバーの「直ちに終了」を選択したとき)
	 */
	void quit(ViewFrame frame){
		ViewFrame fr;

		// 「終了」を実行したビューと異なる種類のビューの設定を保存
		for(E_View v: E_View.values()){
			if(v == frame.view_type) continue;
			fr = view_list.latest_view_frame(v);
			if(fr != null) fr.save_parameter_global();
		}
		// 「終了」を実行したビューの設定を保存
		frame.save_parameter_global();
		// last_view(「終了」を実行したビューの種類)を保存
		this.conf.last_view.set(frame.view_type);
		// 設定ファイルの保存
		save_conf_file();
		// 一時ファイルの削除
		remove_temporary_files();

		System.exit(0);
	}

	/**
	 * 検索エンジンの初期セットアップや更新を行うメソッド(毎日)
	 * @new_gda_dir			新しく指定されたGDAコーパスのディレクトリ
	 * @force_create_flag	常に検索エンジンを作り直すか否か
	 * @warn_flag			作成に失敗したときに警告を表示するか否か
	 */
	void prepare_search_engine_MAI(File new_gda_dir,boolean force_create_flag,boolean warn_flag){
		File prev_gda_dir = this.conf.gda_dir_MAI.get();
		if(force_create_flag ||
		   prev_gda_dir == null || prev_gda_dir.getPath().equals("") ||
		   (! prev_gda_dir.equals(new_gda_dir))){
			try {
				this.se_mai = new SearchEngine_MAI(new_gda_dir.getPath());
				this.se_mai_status = 1;
			} catch (SE_Exception e) {
	    		System.err.println(e.MsgE);
	    		if(warn_flag){
	    			String msg = e.MsgJ;
	    			if(e.MsgJ.equals("CODE: not found")){
	    				msg = "指定されたGDAフォルダ(新聞)は存在しません";
	    			}else if(e.MsgJ.equals("CODE: not specified")){
	    				msg = "GDAフォルダ(新聞)が指定されていません";
	    			}else if(e.MsgJ.equals("CODE: illegal directory")){
	    				msg = "GDAフォルダ(新聞)が正しく指定されていません";
	    			}
	    			show_error_message_in_dialog(msg,null);
	    		}
	    		this.se_mai = null;
	    		this.se_mai_status = 2;
	    		return;
			}
			try {
				this.se_mai.read_file_list();
			} catch (SE_Exception e) {
	    		System.err.println(e.MsgE);
	    		if(warn_flag) show_exception_message_in_dialog(e,null);
			}
		}
	}
	/**
	 * 検索エンジンの初期セットアップや更新を行うメソッド(岩波)
	 * @new_gda_dir			新しく指定されたGDAコーパスのディレクトリ
	 * @force_create_flag	常に検索エンジンを作り直すか否か
	 * @warn_flag			作成に失敗したときに警告を表示するか否か
	 */
	void prepare_search_engine_IWA(File new_gda_dir,boolean force_create_flag,boolean warn_flag){
		File prev_gda_dir = this.conf.gda_dir_IWA.get();
		if(force_create_flag ||
		   prev_gda_dir == null || prev_gda_dir.getPath().equals("") ||
		   (! prev_gda_dir.equals(new_gda_dir))){
			try {
				this.se_iwa = new SearchEngine_IWA(new_gda_dir.getPath());
				this.se_iwa_status = 1;
			} catch (SE_Exception e) {
	    		System.err.println(e.MsgE);
	    		if(warn_flag){
	    			String msg = e.MsgJ;
	    			if(e.MsgJ.equals("CODE: not found")){
	    				msg = "指定されたGDAフォルダ(岩波)は存在しません";
	    			}else if(e.MsgJ.equals("CODE: not specified")){
	    				msg = "GDAフォルダ(岩波)が指定されていません";
	    			}else if(e.MsgJ.equals("CODE: illegal directory")){
	    				msg = "GDAフォルダ(岩波)が正しく指定されていません";
	    			}
	    			show_error_message_in_dialog(msg,null);
	    		}
	    		this.se_iwa = null;
	    		this.se_iwa_status = 2;
	    		return;
			}
			try {
				this.se_iwa.read_file_list();
			} catch (SE_Exception e) {
	    		System.err.println(e.MsgE);
	    		if(warn_flag) show_exception_message_in_dialog(e,null);
			}
		}
	}
	/**
	 * 岩波の語釈文を表示するためのクラスをセットアップするメソッド 
	 * @new_gda_dir			新しく指定されたGDAコーパスのディレクトリ
	 * @force_create_flag	常に検索エンジンを作り直すか否か
	 */
	void prepare_IwanamiDic(File new_gda_dir,boolean force_create_flag){
		File prev_gda_dir = this.conf.gda_dir_IWA.get();
		if(force_create_flag ||
		   prev_gda_dir == null || prev_gda_dir.getPath().equals("") ||
		   (! prev_gda_dir.equals(new_gda_dir))){
			this.iwa_dic = new IwanamiDic(new_gda_dir.getPath());
		}
	}
	/**
	 * メタデータ(毎日の記事見出し)の検索エンジンのセットアップを行うメソッド 
	 * @param warn_flag	警告を表示するか
	 */
	void prepare_metadata_suffix_array_MAI(Boolean warn_flag){
		if(sa_md_mai == null){
			sa_md_mai = new SA_Metadata_MAI(E_Corpus.MAINITI.indexBaseFilename()+"_md.mer",
											E_Corpus.MAINITI.indexBaseFilename()+"_md.idx");
			try {
				sa_md_mai.open();
				sa_md_mai.create_line_index();
			} catch (SE_Exception e) {
				System.err.println(e.MsgE);
	    		if(warn_flag) show_exception_message_in_dialog(e,null);
				sa_md_mai = null;
			}
		}
		sa_md = sa_md_mai;
	}
	/**
	 * メタデータ(岩波の辞書見出し)の検索エンジンのセットアップを行うメソッド 
	 * @param warn_flag	警告を表示するか
	 */
	void prepare_metadata_suffix_array_IWA(Boolean warn_flag){
		if(sa_md_iwa == null){
			sa_md_iwa = new SA_Metadata_IWA(E_Corpus.IWANAMI.indexBaseFilename()+"_md.mer",
											E_Corpus.IWANAMI.indexBaseFilename()+"_md.idx");
			try {
				sa_md_iwa.open();
				sa_md_iwa.create_line_index();
			} catch (SE_Exception e) {
				System.err.println(e.MsgE);
	    		if(warn_flag) show_exception_message_in_dialog(e,null);
				sa_md_iwa = null;
			}
		}
		sa_md = sa_md_iwa;
	}

	/**
	 * GDAファイルをブラウズする
	 *   @param corpus_type	コーパスの種別(毎日or岩波)
	 *   @param gda_file	表示するGDAファイル名(フルパス)
	 *   @param posit, len	強調表示する単語の位置、長さ (強調する単語がないときはともに-1)
	 *   @param frame		構文木を表示しようとするウィンドウ
	 */
	void open_GDA_file_by_web_browser (E_Corpus corpus_type,String gda_file,int posit,int len,ViewFrame parent){
		GDA_File gda;
		if(corpus_type == E_Corpus.MAINITI){
			gda = new GDA_File_MAI(gda_file);
		}else{
			gda = new GDA_File_IWA(gda_file);
		}

		File tmpfile = new File( get_tmp_file_name_gda() );

		Boolean safari_flag = false;
		if(conf.set_web_browser_by_list_flag.get()){
			if(conf.web_browser_by_list.get() == E_WebBrowser.SF_MAC)
				safari_flag = true;
			if(conf.web_browser_by_list.get() == E_WebBrowser.DEFAULT && web_browser.default_browser_safari_flag)
				safari_flag = true;
		}else{
			if(conf.web_browser_by_input.get().endsWith("Safari.app"))
				safari_flag = true;
		}

		try {
			gda.output_xml_all(tmpfile,posit,len,conf.newline_sent_flag.get(),safari_flag);
		} catch (SE_Exception e) {
			show_exception_message_in_dialog(e,parent);
			return;
		} catch (IOException e) {
			show_fatal_error_message_in_dialog("一時ファイルの作成に失敗しました",parent);
			return;
		}
		
		try {
			web_browser.show_xml_file(tmpfile,conf);
		} catch (SE_Exception e) {
			show_exception_message_in_dialog(e,parent);
		}
	}
	/**
	 * 構文木をブラウズする
	 *   @param corpus_type	コーパスの種別(毎日or岩波)
	 *   @param gda_file	表示するGDAファイル名(フルパス)
	 *   @param posit, len	強調表示する単語の位置、長さ (強調する単語がないときはともに-1)
	 *   @param frame		構文木を表示しようとするウィンドウ
	 */
	void show_syn_tree_by_web_browser(E_Corpus corpus_type,String gda_file,int posit,int len,ViewFrame parent){
		GDA_File gda;
		boolean flag;

		if(corpus_type == E_Corpus.MAINITI){
			gda = new GDA_File_MAI(gda_file);
		}else{
			gda = new GDA_File_IWA(gda_file);
		}

		File tmpfile = new File( get_tmp_file_name_tree() );

		try {
			flag = gda.output_xml_tree(tmpfile,posit,len);
		} catch (SE_Exception e) {
			show_exception_message_in_dialog(e,parent);
			return;
		} catch (IOException e) {
			show_fatal_error_message_in_dialog("一時ファイルの作成に失敗しました",parent);
			return;
		}
		
		if(! flag){
			System.out.println("cannot find <su> tag including the keyword");
			show_notice_message_in_dialog("構文木が見つかりませんでした",parent);
			// 一時ファイルは出力されていないので、IDをひとつ戻す
			id_of_tree_tmpfile--;
		}
		
		try {
			web_browser.show_xml_file(tmpfile,conf);
		} catch (SE_Exception e) {
			show_exception_message_in_dialog(e,parent);
		}
	}
	/**
	 * 指定された語義ID(iwa:...)を含む辞書の見出しを出力するメソッド
	 * 語義IDから見出しのIDを抽出し、それをキーとして検索し、該当のGDAファイルを得る
	 */
	void show_iwanami_entry_by_web_browser(String sense_id,ViewFrame parent){
		TreeSet<Integer> entry_id = null;
		
		if(sense_id == null) return;

		// サーチエンジンの準備
		if(se_iwa == null) prepare_search_engine_IWA(this.conf.gda_dir_IWA.get(), true, true);
		if(se_iwa == null) return;
		
		// ↓マージしたテキストファイル(*.mer)が存在しないときは作成を促すメッセージを表示する
		prepare_metadata_suffix_array_IWA(true);
		if(sa_md_iwa == null) return;

		int	p = sense_id.indexOf(".");
		if(p == -1) p = sense_id.length();
		// String entry_id = sense_id.substring(4,p);	見出しIDの抽出
        try {
        	sa_md_iwa.consult( sense_id.substring(4,p) );
        	entry_id = sa_md_iwa.fileID_set_entryID_exact_match();
        } catch (SE_Exception e) {
        	show_exception_message_in_dialog(e,parent);
			e.printStackTrace();
        }
        if(entry_id == null || entry_id.size() == 0){
			show_error_message_in_dialog("該当する辞書見出しが見つかりません",parent);
			System.err.println("fail to get entry ID");
			return;
        }
        // int id = entry_id.first();	該当する見出しのGDAファイルのID
        
        String file_path = se_iwa.file_path(entry_id.first());
		if(file_path == null){
			show_error_message_in_dialog("該当するGDAファイルが見つかりません",parent);
			System.err.println("ERROR: fail to obtain path to GDA file");
			return;
		}

		open_GDA_file_by_web_browser(E_Corpus.IWANAMI,file_path,-1,-1,parent);
	}
	/**
	 * ウェブブラウザでヘルブを表示するメソッド
	 */
	void show_help_by_web_browser(){
		//show_URI_by_desktop_web_browser(help_file.toURI());
		if(! help_file.isFile()){
			this.show_fatal_error_message_in_dialog("ヘルブファイル "+help_file.getPath()+" が存在しません",null);
			return;
		}
		try {
			web_browser.show_html_file(help_file,conf);
		} catch (SE_Exception e) {
			this.show_exception_message_in_dialog(e,null);
		}
	}
	/**
	 * ダイアログでエラーメッセージを表示する
	 */
	void show_exception_message_in_dialog(SE_Exception e,Component parent){
		if(e.MsgE.subSequence(0, 12).equals("FATAL ERROR:")){
			show_fatal_error_message_in_dialog(e.MsgJ,parent);
		}else{
			show_error_message_in_dialog(e.MsgJ,parent);
		}
	}
	void show_error_message_in_dialog(String msg,Component parent){
		JOptionPane.showMessageDialog(parent,msg,"ERROR",JOptionPane.ERROR_MESSAGE);
	}
	void show_fatal_error_message_in_dialog(String msg,Component parent){
		JOptionPane.showMessageDialog(parent,msg,"FATAL ERROR",JOptionPane.ERROR_MESSAGE,getFatalErrorIcon());
	}
	void show_notice_message_in_dialog(String msg,Component parent){
		JOptionPane.showMessageDialog(parent,msg,"NOTICE",JOptionPane.INFORMATION_MESSAGE);
	}
	void show_warning_message_in_dialog(String msg,Component parent){
		JOptionPane.showMessageDialog(parent,msg,"WARNING",JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * GDAファイルの一時ファイル名を得る
	 */
	private String get_tmp_file_name_gda (){
		id_of_gda_tmpfile++;
		return "xslt"+File.separator+"tmp_gda"+id_of_gda_tmpfile+".xml";
	}
	/**
	 * 構文木の一時ファイル名を得る
	 */
	private String get_tmp_file_name_tree (){
		id_of_tree_tmpfile++;
		return "xslt"+File.separator+"tmp_tree"+id_of_tree_tmpfile+".xml";
	}
	/**
	 * 一時ファイルを削除する
	 */
	private void remove_temporary_files (){
		Pattern tmpfile_pattern = Pattern.compile("^tmp_(gda|tree)\\d*\\.xml$");
		File tmp_dir = new File("xslt");
		if(! tmp_dir.isDirectory()) return;
		for(File f: tmp_dir.listFiles()){
			if(f.isFile() && tmpfile_pattern.matcher(f.getName()).find()){
				f.delete();
			}
		}
	}
    ImageIcon getFatalErrorIcon (){
    	if(fatal_error_icon == null){
    		URL url = this.getClass().getResource("icon/icon_skull_red_32.png");
    		fatal_error_icon = new ImageIcon(url);
    	}
    	return fatal_error_icon;
    }
	/*
	 * ウィンドウのリストを選択するためのメニュー
	 * メニューバー上で表示するが、全てのウィンドウで共有するため、このクラスの中で作成する
	private JMenu getJMenu_window() {
		if (jMenu_window == null) {
			jMenu_window = new JMenu();
			jMenu_window.setText("Window");
		}
		return jMenu_window;
	}
	 */	

	/**
	 * ウィンドウのリストを管理するための内部クラス
	 */
	class ViewList {
	    private ArrayList<AbstractViewFrame> current_view_list;
	    
	    ViewList(){
	    	this.current_view_list = new ArrayList<AbstractViewFrame>();
	    }

	    private void add(ViewFrame fr,int id){
	    	AbstractViewFrame avf = new AbstractViewFrame(fr,id);
	    	this.current_view_list.add(avf);
			fr.setTitle(common_window_title+avf.menu_title);
			// ViewFrame作成の後処理。メニューバーの「Window」の作成も含むので、このタイミングで実行する。
			fr.post_processing();
			update_menu_bar_of_all_frames(false);
	    }
	    private void remove(ViewFrame fr){
	    	for(AbstractViewFrame vw: current_view_list){
	    		if(vw.frame == fr){
	    			current_view_list.remove(vw);
	    			break;
	    		}
	    	}
			update_menu_bar_of_all_frames(true);
	    }
	    int size(){
	    	return current_view_list.size();
	    }
	    /*
	    private AbstractViewFrame get(int id){
	    	return current_view_list.get(id);
	    }
	    */
	    /*
	    private AbstractViewFrame last_item(){
	    	return current_view_list.get( current_view_list.size()-1 );
	    }
	    */
	    ViewFrame get_view_frame (int id){
	    	return current_view_list.get(id).frame;
	    }
	    String get_menu_title (int id){
	    	return current_view_list.get(id).menu_title;
	    }
	    private int count_view (E_View view_type){
	    	int c = 0;
	    	for(AbstractViewFrame vw: current_view_list)
	    		if(vw.frame.view_type == view_type) c++;
	    	return c;
	    }
	    private int get_max_id (E_View view_type){
	    	int max_id = 0;
	    	for(AbstractViewFrame vw: current_view_list){
	    		if(vw.frame.view_type != view_type) continue;
	    		if(max_id < vw.id) max_id = vw.id;
	    	}
	    	return max_id;
	    }
	    private ViewFrame latest_view_frame(E_View view_type){
	    	int i;
	    	for(i = current_view_list.size()-1 ; i >= 0 ; i--){
	    		if(current_view_list.get(i).frame.view_type == view_type){
	    			return current_view_list.get(i).frame;
	    		}
	    	}
	    	return null;
	    }
		// 全てのウィンドウのメニューバーを更新する
		private void update_menu_bar_of_all_frames(boolean update_last_item_flag){
			int i,last_id;
			last_id = this.current_view_list.size() - 1;
			if(! update_last_item_flag) last_id--; // 最後に追加したウィンドウは更新しない
			
			//for(i=0 ; i < view_list.size() ; i++)
			//	this.current_view_list.get(i).frame.setVisible(false);
			for(i=0 ; i <= last_id ; i++)
				this.current_view_list.get(i).frame.reconstruct_menu_bar();
			//for(i=0 ; i < view_list.size() ; i++)
			//	this.current_view_list.get(i).frame.setVisible(true);
		}

	    private class AbstractViewFrame {
	    	ViewFrame frame;
	    	int id;
	    	String menu_title;
	    	AbstractViewFrame(ViewFrame fr,int i) {
	    		this.frame = fr;
	    		this.id = i;
	    		if(i > 1){
	    			this.menu_title = fr.view_type.viewNameEn() + " View(" + id + ")";
	    		}else{
	    			this.menu_title = fr.view_type.viewNameEn() + " View";
	    		}
	    	}
	    }
	}
}
