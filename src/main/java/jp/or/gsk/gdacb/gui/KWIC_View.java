package jp.or.gsk.gdacb.gui;

import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_CORPUS;
import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.E_KeywordType;
import jp.or.gsk.gdacb.gui.parts.JFileChooserEx;
import jp.or.gsk.gdacb.gui.parts.JTextArea_FlexibleSize;
import jp.or.gsk.gdacb.gui.parts.RowHeaderTable;
import jp.or.gsk.gdacb.search_engine.RetrievedKeyword;
import jp.or.gsk.gdacb.search_engine.RetrievedKeyword_IWA;
import jp.or.gsk.gdacb.search_engine.RetrievedKeyword_MAI;
import jp.or.gsk.gdacb.search_engine.SE_Exception;
import jp.or.gsk.gdacb.search_engine.TSV_Exporter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;


class KWIC_View extends JPanel {
	private static final long serialVersionUID = 1L;
	
	static final Color color_table_header_background = new Color(246,245,236);
	private static final Color color_keyword = KWIC_TableModel.color_keyword;
	private static final Color color_checkbox_on = Color.RED;
	private static final Color color_checkbox_off = Color.GRAY;
	
	private WindowManager wm = null;
	ViewFrame pf = null;
	Conf_KWIC_View conf_kwic = null;
	boolean initialized_flag = false;
	private String sense_id_in_selected_cell = null;

	private JPopupMenu jPopupMenu_func = null;
	private JPopupMenu jPopupMenu_on_table = null;
	private SearchTab search_tab = null;
	private SortTab sort_tab = null;
	private FilterTab filter_tab = null;
	private ExportTab export_tab = null;
	private KWICConfTab conf_tab = null;
	private String search_keyword = null;
	private int hit_number = 0;
	private KWIC_JTable jTable_kwic = null;
	private KWIC_TableModel kwic_table_model;
	private TableRowSorter<KWIC_TableModel> kwic_table_sorter = null;
	private JTable jRowHeaderTable_kwic = null;
	private RetrievedKeyword[] retrieved_keywords = null;
	private int selected_row_in_kwic_table_model = -1;
	private JTabbedPane jTabbedPane_mode = null;
	private JLabel jLabel_blank = null;
	private JLabel jLabel_result_key = null;
	private JLabel jLabel_result_hitnum = null;
	private JScrollPane jScrollPane_kwic = null;
	private JPanel jPanel_status = null;
	private JCheckBox jCheckBox_sort_on = null;
	private JCheckBox jCheckBox_filter_on = null;
	private JTextArea_FlexibleSize jTextAreaFL_cell = null;
	//private JPopupMenu jPopupMenu_reset_col_order = null;
	//private JMenuItem jMenuItem_reset_col_order2 = null;
	private JCheckBoxMenuItem jCheckBoxMenuItem_switch_flex_size = null;
	private JCheckBoxMenuItem jCheckBoxMenuItem_switch_sel_mode = null;
	private JMenuItem jMenuItem_reset_col_order1 = null;
	private JMenuItem jMenuItem_tsv = null;
	private JButton jButton_func = null;
	private JMenuItem jMenuItem_copy_cb = null;
	private JMenuItem jMenuItem_sel_row = null;
	private JMenuItem jMenuItem_show_gda_file = null;
	private JMenuItem jMenuItem_show_tree = null;
	private JMenuItem jMenuItem_show_sense_all = null;

	public KWIC_View(WindowManager mgr,ViewFrame frame,Conf_KWIC_View conf) {
		super();
		this.wm = mgr;
		this.pf = frame;
		this.conf_kwic = conf;
		initialize();
		this.initialized_flag = true;
	}

	/**
	 * キーワード検索をして、結果をテーブルに表示するメソッド
	 */
	void search () {
		// 検索する
		search_keyword = search_tab.getSearchKeyword();
		if(search_keyword.equals("")) return;
		try {
			if (this.conf_kwic.target_corpus.get() == E_Corpus.MAINITI) {
				if(wm.se_mai == null) {
					wm.show_error_message_in_dialog("検索できません。\nメニューバーの「Option」→「設定」を選択し、\n設定画面でGDAフォルダを設定して下さい。",pf);
					return;
				}
				open_index(search_tab.search_type);
				retrieved_keywords = wm.se_mai.search( search_keyword, search_tab.search_type, conf_kwic.max_display_num.get(), conf_kwic.left_kwic_length.get(), conf_kwic.right_kwic_length.get() );
				hit_number = wm.se_mai.hit_number();
				if(wm.iwa_dic != null) wm.iwa_dic.clear_cache();
			}else{
				if(wm.se_iwa == null) {
					wm.show_error_message_in_dialog("検索できません。\nメニューバーの「Option」→「設定」を選択し、\n設定画面でGDAフォルダを設定して下さい。",pf);
					return;
				}
				open_index(search_tab.search_type);
				retrieved_keywords = wm.se_iwa.search( search_keyword, search_tab.search_type, conf_kwic.max_display_num.get(), conf_kwic.left_kwic_length.get(), conf_kwic.right_kwic_length.get() );
				hit_number = wm.se_iwa.hit_number();
				if(wm.iwa_dic != null) wm.iwa_dic.clear_cache();
			}
		} catch (SE_Exception e) {
			wm.show_exception_message_in_dialog(e,pf);
			return;
		}
		
		// 前回表示したテーブルの列の幅を記録する
		save_current_column_width();
		//  前回表示したテーブルの列の位置を記録する
		save_current_displayed_column_posit();
		
		jTable_kwic.setVisible(false);

		update_JTable_kwic(retrieved_keywords);
		jScrollPane_kwic.setViewportView(jTable_kwic);
		
		// 検索結果(ヒット件数など)を表示
		show_search_result();

		jTextAreaFL_cell.setText(""); 
		set_sort_check_button_on(false);
		set_filter_check_button_on(false);
    	
    	// 検索と同時にソートする/フィルタリングするという設定のとき、ソートやフィルタリングを実行
    	if(search_tab.isSortAlways()) sort();
    	if(search_tab.isFilterAlways()) filter(filter_tab.getFilter());
    	
		jTable_kwic.setVisible(true);
	}
	
	// 検索結果をソートするメソッド
	// 戻値は実際にフィルタをかけたか否か
	boolean sort () {
		ArrayList<RowSorter.SortKey> sort_key_list = sort_tab.getSortKeyList();
		if(kwic_table_sorter == null) return false;
		if(sort_key_list == null) return false;
		if(sort_key_list.size() == 0) return false;
		kwic_table_sorter.setSortKeys(sort_key_list);
		set_sort_check_button_on(true);
		return true;
	}
	// ソートを解除するメソッド
	void cancel_sort () {
		if(kwic_table_sorter != null) kwic_table_sorter.setSortKeys(null);
		set_sort_check_button_on(false);
	}
	// 検索結果にフィルタをかけるメソッド
	// 戻値は実際にフィルタをかけたか否か
	boolean filter (RowFilter<KWIC_TableModel,Integer> rowfilter) {
		if(kwic_table_sorter == null) return false;
		if(rowfilter == null) return false;
		kwic_table_sorter.setRowFilter(rowfilter);
		set_filter_check_button_on(true);
		jTextAreaFL_cell.setText("");
		show_search_result();
		return true;
	}
	// 検索結果のフィルタを解除するメソッド
	void cancel_filter () {
		if(kwic_table_sorter != null) kwic_table_sorter.setRowFilter(null);
		set_filter_check_button_on(false);
		jTextAreaFL_cell.setText("");
		show_search_result();
		jTable_kwic.clear_easy_filter_popup_menu();
	}
	// TSVファイルにエクスポートするメソッド
	void export(){
		if(kwic_table_model == null || kwic_table_model.getRowCount() == 0){
			wm.show_notice_message_in_dialog("例文が表示されていません",pf);
			return;
		}

		JFileChooserEx FC_TSV = export_tab.FileChooserTSV;
		int res = FC_TSV.showSaveDialog(this);
		if(res == JFileChooser.CANCEL_OPTION) return;
		
		TSV_Exporter tsv = new TSV_Exporter(FC_TSV.getSelectedFile(),this.conf_kwic.target_corpus.get());
		tsv.setPrintHeader(true);
		tsv.setPrintSearchKey(false);
		if(export_tab.isAddSenseFlag())
			tsv.enablePrintSenseDesc(wm.iwa_dic);
		tsv.setOutputCoding(export_tab.getOutputCoding());

		try {
			if(export_tab.isOutputTableAsIsFlag()){
				tsv.export(jTable_kwic);
			}else{
				tsv.export(retrieved_keywords);
			}
		} catch (SE_Exception e){
			wm.show_exception_message_in_dialog(e,pf);
		}
		wm.show_notice_message_in_dialog("タブ区切りテキストファイルを出力しました", pf);
	}

	/**
	 * 「検索ビュー」内で設定ファイルに保存するべき全ての項目を記録する
	 */
	void save_parameters(){
		search_tab.save_search_keyword_type();
		search_tab.save_always_sort_filter_flag();
		sort_tab.save_sorters();
		filter_tab.save_filters();
		export_tab.save_export_options();
		conf_tab.save_parameters();
		save_current_column_width();
		save_current_displayed_column_posit();
		conf_kwic.use_flexible_TA_for_cell_flag.set(jCheckBoxMenuItem_switch_flex_size.isSelected());
		conf_kwic.allow_row_selection_flag.set(jCheckBoxMenuItem_switch_sel_mode.isSelected());
	}
	/**
	 * 検索対象コーパスを変更したときに必要な処理を行う
	 */
	void change_target_corpus(){
		cancel_sort();
		cancel_filter();
		sort_tab.update_sorter_key_lists(this.conf_kwic.target_corpus.get());
		filter_tab.update_filter_key_lists(this.conf_kwic.target_corpus.get());
		search_tab.setSortAlways(false);
		search_tab.setFilterAlways(false);

		search_tab.setSearchKeyword("");
		search_keyword = "";
		hit_number = 0;
		retrieved_keywords = null;
		update_JTable_kwic(null);
		jScrollPane_kwic.setViewportView(jTable_kwic);
		show_search_result();
		jTextAreaFL_cell.setText("");
		
		if(this.conf_kwic.target_corpus.get() == E_Corpus.MAINITI){
			if(wm.se_mai == null) wm.prepare_search_engine_MAI(wm.conf.gda_dir_MAI.get(), true, false);
		}else{
			if(wm.se_iwa == null) wm.prepare_search_engine_IWA(wm.conf.gda_dir_IWA.get(), true, false);
		}
	}
	/**
	 * フォントを変更したときに呼び出すメソッド
	 * フォントの変更は ViewFrame#update_font() で実行する
	 * ここでは以下の処理のみ行う
	 * ・テーブルの初期化
	 * ・ポップアップメニューのフォント更新
	 */
	void update_font(){
        // KWICビュー内の表の消去
    	clear_kwic_table();
    	// KWICビュー内のポップアップメニューの更新
    	ViewFrame.recursiveUpdateUI(jPopupMenu_func);
    	ViewFrame.recursiveUpdateUI(jPopupMenu_on_table);
    	ViewFrame.recursiveUpdateUI(jTable_kwic.easy_filter_popup);
    	//CommonFrame.recursiveUpdateUI(jPopupMenu_reset_col_order);
	}

	/*
	private void resize_cell_area(boolean expand) {
		Dimension dim_sp = jScrollPane_cell.getSize();
		Dimension dim_ta = jTextArea_cell.getSize();
		if(expand){
			if(dim_sp.height < dim_ta.height){
//				jScrollPane_cell.setPreferredSize(new Dimension(dim_sp.width,dim_ta.height));
				jScrollPane_cell.setSize(dim_sp.width,dim_ta.height);
				System.out.println("ScrollPane(resized): "+jScrollPane_cell.getSize());
			}
		}else{
			Dimension dim_sp_min = jScrollPane_cell.getMinimumSize();
			System.out.println(dim_sp+" "+dim_sp_min);
			if(dim_sp.height != dim_sp_min.height){
//				jScrollPane_cell.setPreferredSize(new Dimension(dim_sp.width,dim_sp_min.height));
				jScrollPane_cell.setSize(dim_sp.width,dim_sp_min.height);
				System.out.println("ScrollPane(resized): "+jScrollPane_cell.getSize());
			}
		}
	}
	*/
	// 列の幅を記録する
	private void save_current_column_width () {
		if(jTable_kwic != null)
			conf_kwic.set_current_column_width(jTable_kwic,this.conf_kwic.target_corpus.get());
	}
	// 列の入れ換えを記録する
	private void save_current_displayed_column_posit () {
		if(jTable_kwic != null)
			conf_kwic.set_current_displayed_column_posit(jTable_kwic,this.conf_kwic.target_corpus.get());
	}
	/**
	 * インデックスをオープンする
	 */
	private void open_index(E_KeywordType type) throws SE_Exception {
		try{
			if(type == E_KeywordType.SURFACE){
				if(this.conf_kwic.target_corpus.get() == E_Corpus.MAINITI){
					wm.se_mai.open_surface_index();
				}else{
					wm.se_iwa.open_surface_index();
				}
			}else if(type == E_KeywordType.BASE){
				if(this.conf_kwic.target_corpus.get() == E_Corpus.MAINITI){
					wm.se_mai.open_base_index();
				}else{
					wm.se_iwa.open_base_index();
				}
			}else if(type == E_KeywordType.FULLTEXT){
				if(this.conf_kwic.target_corpus.get() == E_Corpus.MAINITI){
					wm.se_mai.open_suffix_array();
				}else{
					wm.se_iwa.open_suffix_array();
				}
			}
		} catch (SE_Exception e){
			//System.err.println(e.MsgE);
			//JOptionPane.showMessageDialog(null,e.MsgJ, "FATAL ERROR", JOptionPane.ERROR_MESSAGE, wm.getFatalErrorIcon());
			throw e;
		}
	}
	/**
	 * 検索結果(キー、表示件数、ヒット件数)を表示させる
	 */
	private void show_search_result (){
		String msg;
		if(search_keyword == null || search_keyword.equals("")){
			// 不可視状態にするとレイアウトが崩れる
			// jLabel_result_key.setVisible(false);
			// jLabel_result_hitnum.setVisible(false);
			jLabel_result_key.setText(" ");
			jLabel_result_hitnum.setText(" ");
			return;
		}

		jLabel_result_key.setText(search_keyword);
		if(hit_number == -1) { 
			msg = " の検索結果: 0件表示";
		}else{
			int disp_num = jTable_kwic.getRowCount();
//			if(hit_number > disp_num) {
//				msg = " の検索結果: "+Integer.toString(disp_num)+"件表示(ヒット件数="+Integer.toString(hit_number)+")";
//			}else{
//				msg = " の検索結果: "+Integer.toString(disp_num)+"件表示";
//			}
			msg = " の検索結果: "+Integer.toString(disp_num)+"件表示(ヒット件数="+Integer.toString(hit_number)+")";
		}
		jLabel_result_hitnum.setText(msg);
	}
	/**
	 * 「ソートON」のチェックボタンの状態を切り返るメソッド
	 */
	void set_sort_check_button_on (boolean toggle) {
		if(toggle){
			jCheckBox_sort_on.setSelected(true);
			jCheckBox_sort_on.setText("ソートON");
			jCheckBox_sort_on.setForeground(color_checkbox_on);
		}else{
			jCheckBox_sort_on.setSelected(false);
			jCheckBox_sort_on.setText("ソートOFF");
			jCheckBox_sort_on.setForeground(color_checkbox_off);
		}
	}
	/**
	 * 「フィルタON」のチェックボタンの状態を切り返るメソッド
	 */
	private void set_filter_check_button_on (boolean toggle) {
		if(toggle){
			jCheckBox_filter_on.setSelected(true);
			jCheckBox_filter_on.setText("フィルタON");
			jCheckBox_filter_on.setForeground(color_checkbox_on);
		}else{
			jCheckBox_filter_on.setSelected(false);
			jCheckBox_filter_on.setText("フィルタOFF");
			jCheckBox_filter_on.setForeground(color_checkbox_off);
		}
	}
	/**
	 * 列の入れ換えを元に戻すメソッド
	 */
	private void reset_column_ordering(){
		jTable_kwic.setVisible(false);
		save_current_displayed_column_posit();
		jTable_kwic.set_column_posit_when_reset(conf_kwic,this.conf_kwic.target_corpus.get());
		jScrollPane_kwic.setViewportView(jTable_kwic);
		jTable_kwic.setVisible(true);
	}
	/**
	 * 検索結果のKWICを表示するテーブルを更新するメソッド
	 */
	private void update_JTable_kwic(RetrievedKeyword[] rkl) {
		if(rkl == null || rkl.length == 0) {
			// kwic_table_model = null;
			kwic_table_model =
				(this.conf_kwic.target_corpus.get() == E_Corpus.MAINITI) ?
				new KWIC_TableModel_MAI() :
				new KWIC_TableModel_IWA();
		}else{
			kwic_table_model =
				(this.conf_kwic.target_corpus.get() == E_Corpus.MAINITI) ?
				new KWIC_TableModel_MAI((RetrievedKeyword_MAI[])rkl) :
				new	KWIC_TableModel_IWA((RetrievedKeyword_IWA[])rkl);
		}

//		jTable_kwic = new JTable(kwic_table_model);
		jTable_kwic.setModel(kwic_table_model);
		
//		System.out.println("here1: row count="+jTable_kwic.getRowCount());

		if(kwic_table_model != null){
//			System.out.println("here2: row count="+jTable_kwic.getRowCount());
			//jTable_kwic.set_row_sorter();
			kwic_table_sorter = kwic_table_model.get_row_sorter();
			jTable_kwic.setRowSorter(kwic_table_sorter);
			// jTableの表の中身はこのタイミングで更新されるらしい
//			System.out.println("here3: row count="+jTable_kwic.getRowCount());

			// 列の幅の調整
			jTable_kwic.set_column_width(conf_kwic,this.conf_kwic.target_corpus.get());
			// 列のアピアランスの設定
			jTable_kwic.set_column_appearance();
			// 列の入れ換えを復元
			jTable_kwic.set_column_posit_when_search(conf_kwic,this.conf_kwic.target_corpus.get());
			// 行の高さを調整
			if(rkl != null && rkl.length > 0)
				jTable_kwic.setRowHeight(pf.conf_vf.font_size.get()+2);

			// 簡易フィルタのポップアップメニューを初期化
			jTable_kwic.clear_easy_filter_popup_menu();
			
			// 行ヘッダの更新
			jRowHeaderTable_kwic = new RowHeaderTable(jTable_kwic, "", 20);
			jScrollPane_kwic.setRowHeaderView(jRowHeaderTable_kwic);
	        //行ヘッダの列見出し(表の左上)にタイトルを表示する
	        jScrollPane_kwic.setCorner(JScrollPane.UPPER_LEFT_CORNER, jRowHeaderTable_kwic.getTableHeader());
	        //Dimension sz = new Dimension(jRowHeaderTable_kwic.getPreferredSize().width, jTable_kwic.getPreferredSize().height);
	        Dimension sz = new Dimension(20, jTable_kwic.getPreferredSize().height);
	        jScrollPane_kwic.getRowHeader().setPreferredSize(sz); // 行ヘッダーのサイズを設定
	        jRowHeaderTable_kwic.setRowHeight(jTable_kwic.getRowHeight());	// 行ヘッダーのセルの高さをテーブル本体と合わせる
	        // 背景色の変更
	        DefaultTableCellRenderer tcr = (DefaultTableCellRenderer) jRowHeaderTable_kwic.getColumnModel().getColumn(0).getCellRenderer();
	        tcr.setBackground(color_table_header_background);
	        jRowHeaderTable_kwic.getTableHeader().setBackground(color_table_header_background);
			// 行ヘッダをクリックしたとき、ポップアップメニューを表示する
	        jRowHeaderTable_kwic.addMouseListener(new java.awt.event.MouseAdapter() {   
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3){
						// 左クリック or 右クリック
						show_popupmenu_on_table(e);
						/*
						Point pt = e.getPoint();
						RowHeaderTable tbl = (RowHeaderTable) e.getSource();
						int row_idx_in_view = tbl.rowAtPoint(pt);
						int row_idx_in_model = jTable_kwic.convertRowIndexToModel(row_idx_in_view);
						System.out.println("header clicked: "+row_idx_in_view+","+row_idx_in_model);
						*/
					}
				}
			});
		}
	}
	/**
	 * 検索結果のKWICを表示するテーブルを初期化するメソッド
	 */
	private void clear_kwic_table(){
		search_keyword = "";
		hit_number = 0;
		retrieved_keywords = null;
		update_JTable_kwic(null);
		show_search_result();
		jTextAreaFL_cell.setText("");
	}
	/**
	 * 選択されたテーブルのセルの内容をテキストフィールドに入れるメソッド
	 */
	void fill_Text_of_Table_Cell(String cell_str,boolean sense_flag) {
		String def;
		if(sense_flag && SYSTEM_CORPUS != E_Corpus.MAINITI){ 
//			try {
				if(wm.iwa_dic == null){
					def = null;
				}else{
					def = wm.iwa_dic.addSenseDescToSEM(cell_str);
				}
				if(def != null){
					jTextAreaFL_cell.setText(def);
				}else{
					jTextAreaFL_cell.setText(cell_str);
				}
//			} catch (SE_Exception e2) {
//				e2.printStackTrace();
//				System.err.println(e2.MsgE);
//				jTextAreaFL_cell.setText(cell_str);
//			}
		}else{
			jTextAreaFL_cell.setText(cell_str.trim());
		}
	}
	/** ???
	 * セル表示エリアに表示されているテキストから語義IDを抽出するメソッド
	 * 語義IDが複数あるときは、カーソルのある行の語義IDを取得する
	private String retrieve_senseID_from_cell_textarea(){
		int len = 0;
		Matcher m;

		String msg = jTextAreaFL_cell.getText();
		int cursor_posit = jTextAreaFL_cell.getCaretPosition();
		Pattern p = Pattern.compile("^(iwa:[^=]+)=");
		for(String s: msg.split("\n")){
			if(len <= cursor_posit && cursor_posit <= len + s.length()){
				m = p.matcher(s);
				if(m.find()){
					return m.group(1);
				}else{
					break;
				}
			}
		}
		m = p.matcher(msg);
		return m.find() ? m.group(1) : null;
	}
	 */
	/**
	 * テーブルのセルまたは行ヘッダをクリックしたときにポップアップメニューを表示する
	 */
	void show_popupmenu_on_table (java.awt.event.MouseEvent e){
		int row_idx_in_view, col_idx_in_model;

		Point pt = e.getPoint();
		Object srcobj = e.getSource();
		if(srcobj == jTable_kwic){
			row_idx_in_view = jTable_kwic.rowAtPoint(pt);
			col_idx_in_model = jTable_kwic.getColumnModel().getColumn( jTable_kwic.columnAtPoint(pt) ).getModelIndex();
			selected_row_in_kwic_table_model = jTable_kwic.convertRowIndexToModel(row_idx_in_view);

			if(SYSTEM_CORPUS != E_Corpus.MAINITI &&
			   col_idx_in_model == kwic_table_model.idx_of_sense_abst){
				String s = (String)kwic_table_model.getValueAt(selected_row_in_kwic_table_model, kwic_table_model.idx_of_sense_abst);
				Pattern p = Pattern.compile("(iwa:[^ ]+)");
				Matcher m = p.matcher(s);
				if(m.find()){
					// 現在のセルにある語義IDを保存する。語義IDが複数あるときは先頭の語義IDを保存する
					this.sense_id_in_selected_cell = m.group(1);
					jMenuItem_show_sense_all.setVisible(true);
				}else{
					this.sense_id_in_selected_cell = null;
					jMenuItem_show_sense_all.setVisible(false);
				}
			}else{
				this.sense_id_in_selected_cell = null;
				jMenuItem_show_sense_all.setVisible(false);
			}
			jMenuItem_copy_cb.setVisible(true);
			jMenuItem_sel_row.setVisible( ! jCheckBoxMenuItem_switch_sel_mode.isSelected() );
			jPopupMenu_on_table.show(jTable_kwic,e.getPoint().x, e.getPoint().y);
		}else if(srcobj == jRowHeaderTable_kwic){
			row_idx_in_view = jRowHeaderTable_kwic.rowAtPoint(pt);
			selected_row_in_kwic_table_model = jTable_kwic.convertRowIndexToModel(row_idx_in_view);

			jMenuItem_show_sense_all.setVisible(false);
			jMenuItem_copy_cb.setVisible(false);
			jMenuItem_sel_row.setVisible( ! jCheckBoxMenuItem_switch_sel_mode.isSelected() );
			jPopupMenu_on_table.show(jRowHeaderTable_kwic,e.getPoint().x, e.getPoint().y);
		}
	}
	/**
	 * 選択したセルの内容をクリップボードにコピーするメソッド
	 */
	private void copy_cell_to_clipboard (java.awt.event.ActionEvent e) {
		String s;
		int row = jTable_kwic.getSelectedRow();
		int col = jTable_kwic.getSelectedColumn();
		int col_in_model = jTable_kwic.getColumnModel().getColumn(col).getModelIndex();
		Class<?> cl = jTable_kwic.getColumnClass(col_in_model);
		if(cl == String.class){
			s = (String) jTable_kwic.getValueAt(row,col);
		}else if(cl == Integer.class){
			s = Integer.toString( (Integer)jTable_kwic.getValueAt(row,col) );
		}else{
			return;
		}
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new StringSelection(s),null);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gBC_B_func = new GridBagConstraints();
		gBC_B_func.gridx = 1;
		gBC_B_func.insets = new Insets(0, 0, 0, 10);
		gBC_B_func.gridy = 4;
		GridBagConstraints gBC_TAFL_cell = new GridBagConstraints();
		gBC_TAFL_cell.fill = GridBagConstraints.HORIZONTAL;
		gBC_TAFL_cell.gridy = 4;
		gBC_TAFL_cell.weightx = 1.0;
		gBC_TAFL_cell.weighty = 0.0D;
		gBC_TAFL_cell.gridx = 0;
		gBC_TAFL_cell.insets = new Insets(0, 0, 0, 10);
		GridBagConstraints gBC_P_status = new GridBagConstraints();
		gBC_P_status.gridx = 0;
		gBC_P_status.fill = GridBagConstraints.HORIZONTAL;
		gBC_P_status.gridy = 2;
		gBC_P_status.gridwidth = GridBagConstraints.HORIZONTAL;
		GridBagConstraints gBC_SP_kwic = new GridBagConstraints();
		gBC_SP_kwic.fill = GridBagConstraints.BOTH;
		gBC_SP_kwic.gridy = 3;
		gBC_SP_kwic.weightx = 1.0;
		gBC_SP_kwic.weighty = 1.0D;
		gBC_SP_kwic.gridx = 0;
		gBC_SP_kwic.gridwidth = GridBagConstraints.REMAINDER;
		jLabel_result_hitnum = new JLabel();
		jLabel_result_hitnum.setText(" ");
		jLabel_result_key = new JLabel();
		jLabel_result_key.setText(" ");
		jLabel_result_key.setForeground(color_keyword);
		GridBagConstraints gBC_L_blank = new GridBagConstraints();
		gBC_L_blank.gridx = 0;
		gBC_L_blank.gridy = 1;
		gBC_L_blank.gridwidth = GridBagConstraints.REMAINDER;
		jLabel_blank = new JLabel();
		jLabel_blank.setText(" ");
		jLabel_blank.setPreferredSize(new Dimension(10, 16));
		GridBagConstraints gBC_TP_mode = new GridBagConstraints();
		gBC_TP_mode.fill = GridBagConstraints.BOTH;
		gBC_TP_mode.gridy = 0;
		gBC_TP_mode.weightx = 1.0;
		gBC_TP_mode.weighty = 0.0D;
		gBC_TP_mode.gridx = 0;
		gBC_TP_mode.gridwidth = GridBagConstraints.REMAINDER;
		this.setSize(400, 200);
		this.setLayout(new GridBagLayout());
		this.add(getJTabbedPane_mode(), gBC_TP_mode);
		this.add(jLabel_blank, gBC_L_blank);
		this.add(getJPanel_status(), gBC_P_status);
		this.add(getJScrollPane_kwic(), gBC_SP_kwic);
		this.add(getTextAreaFL_cell(), gBC_TAFL_cell);
		this.add(getJButton_func(), gBC_B_func);
		
		getJPopupMenu_on_table();
		
		/* それぞれのタブのinitialize()で実行
		search_tab.set_keyword_type(p_mf.conf.keyword_type.get());
		search_tab.setSortAlways(p_mf.conf.always_sort_flag.get());
		search_tab.setFilterAlways( p_mf.conf.always_filter_flag.get() );

        sort_tab.update_sorter_key_lists(p_mf.conf);
        sort_tab.setAbstractSorterList(p_mf.conf.abs_sorter_list.get_list());

        filter_tab.update_filter_key_lists(p_mf.conf);
		filter_tab.setAbstractFilterList(p_mf.conf.abs_filter_list.get_list());
		filter_tab.set_filter_combination(p_mf.conf.filter_combination.get());
		
		export_tab.setOutputTableAsIsFlag(p_mf.conf.export_table_asis.get());
		export_tab.setAddSenseFlag(p_mf.conf.export_add_sense.get());
		export_tab.setOutputCoding(p_mf.conf.export_output_coding.get());
		*/
		jCheckBoxMenuItem_switch_flex_size.setSelected( conf_kwic.use_flexible_TA_for_cell_flag.get() );
		jCheckBoxMenuItem_switch_sel_mode.setSelected( conf_kwic.allow_row_selection_flag.get() );
		
		if(this.conf_kwic.target_corpus.get() == E_Corpus.MAINITI){
			if(wm.se_mai == null) wm.prepare_search_engine_MAI(wm.conf.gda_dir_MAI.get(), true, false);
		}else{
			if(wm.se_iwa == null) wm.prepare_search_engine_IWA(wm.conf.gda_dir_IWA.get(), true, false);
		}
	}

	/**
	 * This method initializes jTabbedPane_mode	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane_mode() {
		if (jTabbedPane_mode == null) {
			jTabbedPane_mode = new JTabbedPane();
			
			search_tab = new SearchTab(this);
			sort_tab = new SortTab(this);
			filter_tab = new FilterTab(this);
			export_tab = new ExportTab(this);
			conf_tab = new KWICConfTab(this);
			
			search_tab.setBackground(ViewFrame.bg_color_of_tab_area);
			sort_tab.setBackground(ViewFrame.bg_color_of_tab_area);
			filter_tab.setBackground(ViewFrame.bg_color_of_tab_area);
			export_tab.setBackground(ViewFrame.bg_color_of_tab_area);
			conf_tab.setBackground(ViewFrame.bg_color_of_tab_area);
			
            jTabbedPane_mode.addTab("検索", null, search_tab, null);
            jTabbedPane_mode.addTab("ソート", null, sort_tab, null);
            jTabbedPane_mode.addTab("フィルタ", null, filter_tab, null);
            jTabbedPane_mode.addTab("ファイル出力", null, export_tab, null);
            jTabbedPane_mode.addTab("設定", null, conf_tab, null);

            // Alt+数字 でタブを切り換える, Macでは機能しない
            jTabbedPane_mode.setMnemonicAt(0, KeyEvent.VK_1);
            jTabbedPane_mode.setMnemonicAt(1, KeyEvent.VK_2);
            jTabbedPane_mode.setMnemonicAt(2, KeyEvent.VK_3);
            jTabbedPane_mode.setMnemonicAt(3, KeyEvent.VK_4);
            jTabbedPane_mode.setMnemonicAt(4, KeyEvent.VK_5);
		}
		return jTabbedPane_mode;
	}

	/**
	 * This method initializes jScrollPane_kwic	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane_kwic() {
		if (jScrollPane_kwic == null) {
			Dimension dim = new Dimension(100,150);
			jScrollPane_kwic = new JScrollPane();
			jScrollPane_kwic.setPreferredSize(dim);
			jScrollPane_kwic.setMinimumSize(dim);
			jScrollPane_kwic.setViewportView(getJTable_kwic());
			
			/*
			RowHeaderList rowHeader = new RowHeaderList(new DefaultListModel(), jTable_kwic);
	        rowHeader.setFixedCellWidth(20);
	        jScrollPane_kwic.setRowHeaderView(rowHeader);
	        jScrollPane_kwic.getRowHeader().addChangeListener(new ChangeListener() {
	        	@Override
				public void stateChanged(ChangeEvent e) {
	        		JViewport viewport = (JViewport) e.getSource();
	        		jScrollPane_kwic.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
	        	}
	        });
	        */
			/*
			JTable jRowHeaderTable_kwic = new RowHeaderTable(jTable_kwic, "HEAD", 48);
	        jScrollPane_kwic.setRowHeaderView(jRowHeaderTable_kwic);
	        jScrollPane_kwic.setCorner(JScrollPane.UPPER_LEFT_CORNER, jRowHeaderTable_kwic.getTableHeader());
	        Dimension sz = new Dimension(jRowHeaderTable_kwic.getPreferredSize().width, jTable_kwic.getPreferredSize().height);
	        jScrollPane_kwic.getRowHeader().setPreferredSize(sz); // 行ヘッダーのサイズ
	        */
		}
		return jScrollPane_kwic;
	}

	/**
	 * This method initializes jTable_kwic	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private KWIC_JTable getJTable_kwic() {
		if (jTable_kwic == null) {
			jTable_kwic = new KWIC_JTable();
			/* KWIC_JTable#initialize() の中に移動
			 * 
			jTable_kwic.setDefaultEditor(Object.class, null);
			jTable_kwic.setCellSelectionEnabled(true);
			//jTable_kwic.setRowSelectionAllowed(false);
			//jTable_kwic.setColumnSelectionAllowed(false);
			jTable_kwic.setSelectionBackground(new Color(204, 204, 204));
			jTable_kwic.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//			jTable_kwic.setAutoCreateRowSorter(true);
            */
 			
			jTable_kwic.set_parent_KWIC_View(this); 
			// ヘッダ上で右クリックしたとき、
			// 「列順序のリセット」メニューを含むポップアップメニューを表示する
			//jTable_kwic.getTableHeader().setComponentPopupMenu(getJPopupMenu_reset_col_order());	
		}
		return jTable_kwic;
	}

	/**
	 * This method initializes jPanel_status	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_status() {
		if (jPanel_status == null) {
			GridBagConstraints gBC_CB_filter_on = new GridBagConstraints();
			gBC_CB_filter_on.gridx = 3;
			gBC_CB_filter_on.anchor = GridBagConstraints.WEST;
			gBC_CB_filter_on.gridy = 0;
			GridBagConstraints gBC_CB_sort_on = new GridBagConstraints();
			gBC_CB_sort_on.gridx = 2;
			gBC_CB_sort_on.anchor = GridBagConstraints.WEST;
			gBC_CB_sort_on.gridy = 0;
			GridBagConstraints gBC_L_result_hitnum = new GridBagConstraints();
			gBC_L_result_hitnum.anchor = GridBagConstraints.WEST;
			gBC_L_result_hitnum.gridwidth = 1;
			gBC_L_result_hitnum.gridx = 1;
			gBC_L_result_hitnum.gridy = 0;
			gBC_L_result_hitnum.weightx = 1.0D;
			gBC_L_result_hitnum.insets = new Insets(0, 0, 5, 0);
			GridBagConstraints gBC_L_result_key = new GridBagConstraints();
			gBC_L_result_key.anchor = GridBagConstraints.WEST;
			gBC_L_result_key.gridx = 0;
			gBC_L_result_key.gridy = 0;
			gBC_L_result_key.insets = new Insets(0, 10, 5, 0);
			jPanel_status = new JPanel();
			jPanel_status.setLayout(new GridBagLayout());
			jPanel_status.add(jLabel_result_key, gBC_L_result_key);
			jPanel_status.add(jLabel_result_hitnum, gBC_L_result_hitnum);
			jPanel_status.add(getJCheckBox_sort_on(), gBC_CB_sort_on);
			jPanel_status.add(getJCheckBox_filter_on(), gBC_CB_filter_on);
		}
		return jPanel_status;
	}

	/**
	 * This method initializes jCheckBox_sort_on	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox_sort_on() {
		if (jCheckBox_sort_on == null) {
			jCheckBox_sort_on = new JCheckBox();
			jCheckBox_sort_on.setText("ソートOFF");
			jCheckBox_sort_on.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// テーブルのヘッダをクリックしてソートしたときも「ソート」チェックボタンの状態を変更したいので、
					// itemStateChanged ではなく actionPerformed イベントを使う
					// itemStateChanged の場合、テーブルヘッダをクリックしたときも実行され、
					// ソートタブに条件を入力していない状態のときは強制的に「ソート」チェックボタンをOFFにしてしまう
					if(jCheckBox_sort_on.isSelected()){
						boolean done_flag = sort();
						if(! done_flag){
							java.awt.Toolkit.getDefaultToolkit().beep();	// beep
							set_sort_check_button_on(false);
						}
					}else{
						cancel_sort();
					}
				}
			});
			set_sort_check_button_on(false);
		}
		return jCheckBox_sort_on;
	}

	/**
	 * This method initializes jCheckBox_filter_on	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox_filter_on() {
		if (jCheckBox_filter_on == null) {
			jCheckBox_filter_on = new JCheckBox();
			jCheckBox_filter_on.setText("フィルタOFF");
			jCheckBox_filter_on.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// 簡易フィルタ(テーブルのヘッダをクリックしてフィルタリング)を使ったときも「フィルタ」チェックボタンの状態を変更したいので、
					// itemStateChanged ではなく actionPerformed イベントを使う
					// itemStateChanged の場合、簡易フィルタでフィルタリングをしたときも実行され、
					// フィルタタブに条件を入力していない状態のときは強制的に「フィルタ」チェックボタンをOFFにしてしまう
					if(jCheckBox_filter_on.isSelected()){
						boolean done_flag = filter(filter_tab.getFilter());
						if(! done_flag){
							java.awt.Toolkit.getDefaultToolkit().beep();	// beep
							set_filter_check_button_on(false);
						}
					}else{
						cancel_filter();
					}
				}
			});
			set_filter_check_button_on(false);
		}
		return jCheckBox_filter_on;
	}

	/**
	 * This method initializes TextArea_FlexibleSize	
	 */
	private JTextArea_FlexibleSize getTextAreaFL_cell() {
		if (jTextAreaFL_cell == null) {
			// サイズ固定のエリアを表示するときの高さは、
			// フォントサイズが14のときの2行分の長さを指定
			// 本当はフォントサイズによって変更できるとよいのだが
			jTextAreaFL_cell = new JTextArea_FlexibleSize(100,36,false);
		}
		return jTextAreaFL_cell;
	}
	
	/**
	 * This method initializes jPopupMenu_reset_col_order	
	 * 	
	 * @return javax.swing.JPopupMenu
	 */
	/* 列順序をデフォルトに戻すためのポップアップメニュー
	private JPopupMenu getJPopupMenu_reset_col_order() {
		if (jPopupMenu_reset_col_order == null) {
			jPopupMenu_reset_col_order = new JPopupMenu();
			jPopupMenu_reset_col_order.add(getJMenuItem_reset_col_order2());
		}
		return jPopupMenu_reset_col_order;
	}
	 */
	/**
	 * This method initializes jMenuItem_reset_col_order2	
	 * 	
	 * @return javax.swing.JMenuItem
	 */
	/* 列順序をデフォルトに戻すためのポップアップメニュー
	private JMenuItem getJMenuItem_reset_col_order2() {
		if (jMenuItem_reset_col_order2 == null) {
			jMenuItem_reset_col_order2 = new JMenuItem();
			jMenuItem_reset_col_order2.setText("列順序のリセット");
			jMenuItem_reset_col_order2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					reset_column_ordering();
				}
			});
		}
		return jMenuItem_reset_col_order2;
	}
	 */
	/**
	 * This method initializes jPopupMenu_copy_cb
	 * 	
	 * @return javax.swing.JPopupMenu
	 */
	/* クリップボードへのコピーを行うポップアップメニュー
	private JPopupMenu getJPopupMenu_copy_cb() {
		if (jPopupMenu_copy_cb == null) {
			jPopupMenu_copy_cb = new JPopupMenu();
			jPopupMenu_copy_cb.add(getJMenuItem_copy_cb());
		}
		return jPopupMenu_copy_cb;
	}
	*/
	/**
	 * This method initializes jMenuItem_copy_cb
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	/* クリップボードへのコピーを行うメニューアイテム
	private JMenuItem getJMenuItem_copy_cb() {
		if (jMenuItem_copy_cb == null) {
			jMenuItem_copy_cb = new JMenuItem();
			jMenuItem_copy_cb.setText("コピー");
			jMenuItem_copy_cb.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// 選択したセルの内容をクリップボードにコピー
					String s;
					int row = jTable_kwic.getSelectedRow();
					int col = jTable_kwic.getSelectedColumn();
					int col_in_model = jTable_kwic.getColumnModel().getColumn(col).getModelIndex();
					Class<?> cl = jTable_kwic.getColumnClass(col_in_model);
					if(cl == String.class){
						s = (String) jTable_kwic.getValueAt(row,col);
						p_mf.set_clipboard(s);
					}else if(cl == Integer.class){
						s = Integer.toString( (Integer)jTable_kwic.getValueAt(row,col) );
						p_mf.set_clipboard(s);
					}
				}
			});
		}
		return jMenuItem_copy_cb;
	}
	*/
	/**
	 * This method initializes jPopupMenu_reset_col_order	
	 * 	
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu_func() {
		if (jPopupMenu_func == null) {
			jPopupMenu_func = new JPopupMenu();
			jPopupMenu_func.add(getJCheckBoxMenuItem_switch_flex_size());
			jPopupMenu_func.add(getJCheckBoxMenuItem_switch_sel_mode());
			jPopupMenu_func.add(getJMenuItem_reset_col_order1());
			jPopupMenu_func.add(getJMenuItem_tsv());
		}
		return jPopupMenu_func;
	}
	/**
	 * This method initializes jCheckBoxMenuItem_switch_flex_size	
	 * 	
	 * @return javax.swing.JCheckBoxMenuItem	
	 */
	private JCheckBoxMenuItem getJCheckBoxMenuItem_switch_flex_size() {
		if (jCheckBoxMenuItem_switch_flex_size == null) {
			jCheckBoxMenuItem_switch_flex_size = new JCheckBoxMenuItem();
			jCheckBoxMenuItem_switch_flex_size.setText("セル表示エリアの高さを自動調整");
			jCheckBoxMenuItem_switch_flex_size
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							if(jCheckBoxMenuItem_switch_flex_size.isSelected()){
								jTextAreaFL_cell.setShowFlexibleTextArea(true);
							}else{
								jTextAreaFL_cell.setShowFlexibleTextArea(false);
							}
						}
					});
		}
		return jCheckBoxMenuItem_switch_flex_size;
	}
	/**
	 * This method initializes jCheckBoxMenuItem_switch_flex_size	
	 * 	
	 * @return javax.swing.JCheckBoxMenuItem	
	 */
	private JCheckBoxMenuItem getJCheckBoxMenuItem_switch_sel_mode() {
		if (jCheckBoxMenuItem_switch_sel_mode == null) {
			jCheckBoxMenuItem_switch_sel_mode = new JCheckBoxMenuItem();
			jCheckBoxMenuItem_switch_sel_mode.setText("クリックで行を選択");
			jCheckBoxMenuItem_switch_sel_mode
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							// 以下の3つのメソッドは以下の順で実行する。順番を変えると正常に動作しない
							if(jCheckBoxMenuItem_switch_sel_mode.isSelected()){
								jTable_kwic.setCellSelectionEnabled(false);
								jTable_kwic.setColumnSelectionAllowed(false);
								jTable_kwic.setRowSelectionAllowed(true);
							}else{
								jTable_kwic.setRowSelectionAllowed(false);
								jTable_kwic.setColumnSelectionAllowed(false);
								jTable_kwic.setCellSelectionEnabled(true);
							}
						}
					});
		}
		return jCheckBoxMenuItem_switch_sel_mode;
	}
	/**
	 * This method initializes jMenuItem_reset_col_order1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_reset_col_order1() {
		if (jMenuItem_reset_col_order1 == null) {
			jMenuItem_reset_col_order1 = new JMenuItem();
			jMenuItem_reset_col_order1.setText("列順序のリセット");
			jMenuItem_reset_col_order1
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							reset_column_ordering();
						}
					});
		}
		return jMenuItem_reset_col_order1;
	}
	/**
	 * This method initializes jMenuItem_tsv	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_tsv() {
		if (jMenuItem_tsv == null) {
			jMenuItem_tsv = new JMenuItem();
			jMenuItem_tsv.setText("ファイル出力");
			jMenuItem_tsv.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					export();
				}
			});
		}
		return jMenuItem_tsv;
	}
	/**
	 * This method initializes jButton_func	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_func() {
		if (jButton_func == null) {
			jButton_func = new JButton();
			jButton_func.setText("FUNC");
			jButton_func.setComponentPopupMenu(getJPopupMenu_func());
			jButton_func.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1){
						jPopupMenu_func.show( (JButton)e.getSource(), e.getPoint().x, e.getPoint().y );
					}
				}
			});
		}
		return jButton_func;
	}
	private JPopupMenu getJPopupMenu_on_table() {
		if (jPopupMenu_on_table == null) {
			jPopupMenu_on_table = new JPopupMenu();
			jPopupMenu_on_table.add(getJMenuItem_show_gda_file());
			jPopupMenu_on_table.add(getJMenuItem_show_tree());
			jPopupMenu_on_table.add(getJMenuItem_show_sense_all());
			jPopupMenu_on_table.add(getJMenuItem_copy_cb());
			jPopupMenu_on_table.add(getJMenuItem_sel_row());
		}
		return jPopupMenu_on_table;
	}
	private JMenuItem getJMenuItem_copy_cb() {
		if (jMenuItem_copy_cb == null) {
			jMenuItem_copy_cb = new JMenuItem();
			jMenuItem_copy_cb.setText("セルをコピー");
			jMenuItem_copy_cb.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					copy_cell_to_clipboard(e);
				}
			});
		}
		return jMenuItem_copy_cb;
	}
	private JMenuItem getJMenuItem_sel_row() {
		if (jMenuItem_sel_row == null) {
			jMenuItem_sel_row = new JMenuItem();
			jMenuItem_sel_row.setText("行を選択");
			jMenuItem_sel_row.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//select_row();
					jTable_kwic.addColumnSelectionInterval(0, jTable_kwic.getColumnCount()-1);
				}
			});
		}
		return jMenuItem_sel_row;
	}
	private JMenuItem getJMenuItem_show_gda_file() {
		if (jMenuItem_show_gda_file == null) {
			jMenuItem_show_gda_file = new JMenuItem();
			jMenuItem_show_gda_file.setText("文書を表示");
			jMenuItem_show_gda_file.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					/*
					int p = 0, l = 0;
					try {
						if(p_mf.conf.target_corpus.get() == E_Corpus.MAINITI){
							p = p_mf.se_mai.position(p_mf.conf.keyword_type.get(), selected_row_in_kwic_table);
							l = p_mf.se_mai.length(p_mf.conf.keyword_type.get(), selected_row_in_kwic_table);
						}else{
							p = p_mf.se_iwa.position(p_mf.conf.keyword_type.get(), selected_row_in_kwic_table);
							l = p_mf.se_iwa.length(p_mf.conf.keyword_type.get(), selected_row_in_kwic_table);
						}
					} catch (SE_Exception e1) {
						JOptionPane.showMessageDialog(p_mf,
								e1.MsgJ, "ERROR", JOptionPane.ERROR_MESSAGE);
					}
					p_mf.open_GDA_file_by_web_browser(rk.gda_file, p, l);
					*/
					RetrievedKeyword rk = retrieved_keywords[selected_row_in_kwic_table_model];
					wm.open_GDA_file_by_web_browser(conf_kwic.target_corpus.get(),rk.gda_file, rk.posit_at_gda_file, rk.length_in_gda_file, pf);
				}
			});
		}
		return jMenuItem_show_gda_file;
	}
	private JMenuItem getJMenuItem_show_tree() {
		if (jMenuItem_show_tree == null) {
			jMenuItem_show_tree = new JMenuItem();
			jMenuItem_show_tree.setText("構文木を表示");
			jMenuItem_show_tree.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					RetrievedKeyword rk = retrieved_keywords[selected_row_in_kwic_table_model];
					wm.show_syn_tree_by_web_browser(conf_kwic.target_corpus.get(), rk.gda_file, rk.posit_at_gda_file, rk.length_in_gda_file, pf);
				}
			});
		}
		return jMenuItem_show_tree;
	}
	private JMenuItem getJMenuItem_show_sense_all() {
		if (jMenuItem_show_sense_all == null) {
			jMenuItem_show_sense_all = new JMenuItem();
			jMenuItem_show_sense_all.setText("この語義を含む辞書見出しを表示");
			jMenuItem_show_sense_all.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					wm.show_iwanami_entry_by_web_browser(sense_id_in_selected_cell,pf);
				}
			});
		}
		return jMenuItem_show_sense_all;
	}
}