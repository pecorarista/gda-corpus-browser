package jp.or.gsk.gdacb.gui;

import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_CORPUS;
import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_OS;
import jp.or.gsk.gdacb.*;
import jp.or.gsk.gdacb.gui.parts.*;

import java.io.File;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;


class ConfView extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color color_view_background = new Color(238, 242, 235);
	private static final Color color_warning_background = new Color(252, 233, 242);

	private WindowManager wm = null;

	/** 設定が保存されたかを記録するフィールド
	 *   テキストフィールドについては、文字が入力されるたびに変更をチェックしては効率が悪い
	 *   フォーカスが離れたときとリターンキーを押したときのみに変更をチェックする
	 */
	private boolean changed_flag;

	// 設定ビュー内の全てのオブジェクトが生成されたか?
	private boolean initialized_flag = false;
	private JFileChooserEx file_chooser_for_gda_dir = null;
	private JFileChooserEx file_chooser_for_web_browser = null;

	private JLabel jLabel_gdadir_mai = null;
	private JTextField jTextField_gdadir_mai = null;
	private JPanel jPanel_save = null;
	private JButton jButton_save = null;
	private JButton jButton_cancel = null;
	//private JTextArea jTextArea_update_msg = null;
	//private JLabel jLabel_corpus = null;
	//private JRadioButton jRadioButton_corpus_mai = null;
	//private JRadioButton jRadioButton_corpus_iwa = null;
	private JLabel jLabel_gdadir_iwa = null;
	private JButton jButton_gdadir_mai = null;
	private JTextField jTextField_gdadir_iwa = null;
	private JButton jButton_gdadir_iwa = null;
	private JLabel jLabel_padding_right = null;
	private JLabel jLabel_padding_left = null;

	private JPanel jPanel_general = null;
	private JLabel jLabel_web_browser = null;
	private JPanel jPanel_web_browser = null;
	private JRadioButton jRadioButton_wb_list = null;
	private JRadioButton jRadioButton_wb_input = null;
	private JComboBoxWithDisabledItems jComboBox_wb = null;
	private JButton jButton_wb_input = null;
	private JTextField jTextField_wb_input = null;
	private JLabel jLabel_file_browse = null;
	private JCheckBox jCheckBox_sent_newline = null;
	private JLabel jLabel_warn = null;
	private JButton jButton_close = null;
	private JLabel jLabel_default_view = null;
	private JComboBox jComboBox_default_view = null;
	private JLabel jLabel_padding_save1 = null;
	private JLabel jLabel_padding_save2 = null;

	/**
	 * This is the default constructor
	 */
	public ConfView(WindowManager mgr) {
		super();
		this.wm = mgr;
		this.changed_flag = false;
		initialize();
	}
	/**
	 * 設定を保存する
	 */
	private void save_parameters() {
		wm.conf.gda_dir_MAI.set( new File(jTextField_gdadir_mai.getText().trim()) );
		wm.conf.gda_dir_IWA.set( new File(jTextField_gdadir_iwa.getText().trim()) );
		
		wm.conf.set_web_browser_by_list_flag.set( jRadioButton_wb_list.isSelected() );
		wm.conf.web_browser_by_list.set( (E_WebBrowser)jComboBox_wb.getSelectedItem() );
		wm.conf.web_browser_by_input.set( jTextField_wb_input.getText().trim() );
		wm.conf.newline_sent_flag.set( jCheckBox_sent_newline.isSelected() );
		wm.conf.default_view.set( (E_View)jComboBox_default_view.getSelectedItem() );
	}
	/**
	 * ツールの情報を内部の Configure インスタンスに保存するメソッド
	 * 更に設定ファイルに保存する
	 *   GDAディレクトリについては、このビューのテキストフィールドの値と
	 *   Configureに保存されている値を比較し、変更されているかを検出するので、
	 *   このメソッドはWindowMangerではなくこのクラスで定義する
	 *
	 * @param update_se_flag	検索対象コーパスやGDAディレクトリに変更があったとき、検索エンジンを作り直すか否か
	 * @param warn_flag			検索エンジンの作成に失敗したとき警告を表示するか否か
	 * @return					設定ファイルの書き込みに成功したか否かを返す
	 */
	private boolean save (boolean update_se_flag,boolean warn_flag) {
		File gda_dir_mai, gda_dir_iwa;
		
		if(SYSTEM_CORPUS != E_Corpus.IWANAMI){
			gda_dir_mai = new File(jTextField_gdadir_mai.getText().trim());			
			if(update_se_flag){
				wm.prepare_search_engine_MAI(gda_dir_mai,false,false);
			}
		}
		if(SYSTEM_CORPUS != E_Corpus.MAINITI){
			gda_dir_iwa = new File(jTextField_gdadir_iwa.getText().trim());
			if(update_se_flag){
				wm.prepare_search_engine_IWA(gda_dir_iwa,false,false);
				wm.prepare_IwanamiDic(gda_dir_iwa,false);
			}
		}
		check_search_engine_status(warn_flag);
		save_parameters();
		return wm.save_conf_file();
	}
	/**
	 * 各アイテムの変更時に呼び出され、設定が変更されたかをチェックするアクション
	 */
	private void check_item_changed_action(){
		// 初期段階ではチェックしない
		if(!initialized_flag) return;

		show_warning_when_undefined_item_found();
		changed_flag = isChanged();
		update_save_panel();
	}
	/**
	 * Configure インスタンスが内部的に保持している設定をフォームに反映させるメソッド
	 */
	private void fill_forms () {
		/*
		if(p_mf.conf.target_corpus.get() == E_Corpus.MAINITI){
			jRadioButton_corpus_mai.setSelected(true);
		}else{
			jRadioButton_corpus_iwa.setSelected(true);
		}
		*/
		if(wm.conf.gda_dir_MAI.get() != null) {
			jTextField_gdadir_mai.setText(wm.conf.gda_dir_MAI.get().getPath());
			jTextField_gdadir_mai.setCaretPosition(jTextField_gdadir_mai.getText().length());
		}
		if(wm.conf.gda_dir_IWA.get() != null) {
			jTextField_gdadir_iwa.setText(wm.conf.gda_dir_IWA.get().getPath());
			jTextField_gdadir_iwa.setCaretPosition(jTextField_gdadir_iwa.getText().length());
		}
		if(wm.conf.set_web_browser_by_list_flag.get()){
			jRadioButton_wb_list.setSelected(true);
		}else{
			jRadioButton_wb_input.setSelected(true);
		}
		jComboBox_wb.setSelectedItem( wm.conf.web_browser_by_list.get() );
		jTextField_wb_input.setText( wm.conf.web_browser_by_input.get() );
		jCheckBox_sent_newline.setSelected( wm.conf.newline_sent_flag.get() );
		jComboBox_default_view.setSelectedItem( wm.conf.default_view.get() );
	}
	/**
	 * 設定が変更されているかを判定するメソッド
	 */
	private boolean isChanged () {
		File f;
		String s;
		/*
		if(SYSTEM_CORPUS == E_Corpus.BOTH){
			if(p_mf.conf.target_corpus.get() == E_Corpus.MAINITI &&
			   jRadioButton_corpus_iwa.isSelected()){
//				System.err.println("target corpus is changed from Mainiti to Iwanami");
				return true;
			}
			if(p_mf.conf.target_corpus.get() == E_Corpus.IWANAMI &&
			   jRadioButton_corpus_mai.isSelected()){
//				System.err.println("target corpus is changed from Iwanami to Mainiti");
				return true;
			}
		}
		*/
		f = new File( jTextField_gdadir_mai.getText().trim() );
		if( ! f.equals( wm.conf.gda_dir_MAI.get() ) ){
//			System.err.println("GDA directory of Mainiti is changed: "+f+"<->"+p_mf.conf.gda_dir_MAI.get());
			return true;
		}
		f = new File( jTextField_gdadir_iwa.getText().trim() );
		if( ! f.equals( wm.conf.gda_dir_IWA.get() ) ){
//			System.err.println("GDA directory of Iwanami is changed: "+f+"<->"+p_mf.conf.gda_dir_IWA.get());
			return true;
		}
		if( wm.conf.set_web_browser_by_list_flag.get() != jRadioButton_wb_list.isSelected()){
//			System.err.println("Way to specify Web browser is changed: "+jRadioButton_wb_list.isSelected()+"<->"+wm.conf.set_web_browser_by_list_flag.get());
			return true;
		}
		if( wm.conf.web_browser_by_list.get() != jComboBox_wb.getSelectedItem()){
//			System.err.println("Web browser(in ComboBox) is changed: "+jComboBox_wb.getSelectedItem()+"<->"+p_mf.conf.web_browser_by_list.get().id());
			return true;
		}
		s = jTextField_wb_input.getText().trim();
		if( ! s.equals( wm.conf.web_browser_by_input.get() ) ){
//			System.err.println("Web browser(in TextField) is changed: "+s+"<->"+p_mf.conf.web_browser_by_input.get());
			return true;
		}
		if( wm.conf.newline_sent_flag.get() != jCheckBox_sent_newline.isSelected() ){
//			System.err.println("Newline sentence flag is changed: "+jCheckBox_sent_newline.isSelected()+"<->"+wm.conf.newline_sent_flag.get());
			return true;
		}
		if( wm.conf.default_view.get() != jComboBox_default_view.getSelectedItem()){
//			System.err.println("Default view is changed: "+jComboBox_default_view.getSelectedItem()+"<->"+wm.conf.default_view.get());
			return true;
		}
		return false;
	}
	/**
	 * 未設定の項目があるとき、警告メッセージを表示する
	 */
	private void show_warning_when_undefined_item_found (){
		if(check_validity_all()){
			//jLabel_warn.setVisible(false);
			jLabel_warn.setForeground(color_warning_background);
		}else{
			//jLabel_warn.setVisible(true);
			jLabel_warn.setForeground(Color.RED);			
		}
	}
	/**
	 * 未設定の項目がないかをチェックする
	 */
	private boolean check_validity_all(){
		boolean valid_flag = true;
		if(! check_validity_textfield_gdadir_MAI()) valid_flag = false;
		if(! check_validity_textfield_gdadir_IWA()) valid_flag = false;
		if(! check_validity_combobox_web_browser()) valid_flag = false;
		if(! check_validity_textfield_web_browser()) valid_flag = false;
		return valid_flag;
	}
	private boolean check_validity_textfield_gdadir_MAI(){
		if(SYSTEM_CORPUS == E_Corpus.IWANAMI) return true;
		if(jTextField_gdadir_mai.getText().trim().equals("")){
			jLabel_gdadir_mai.setBackground(color_warning_background);
			jLabel_gdadir_mai.setOpaque(true);
			return false;
		}else{
			jLabel_gdadir_mai.setBackground(null);
			jLabel_gdadir_mai.setOpaque(false);
			return true;
		}
	}
	private boolean check_validity_textfield_gdadir_IWA(){
		if(SYSTEM_CORPUS == E_Corpus.MAINITI) return true;
		if(jTextField_gdadir_iwa.getText().trim().equals("")){
			jLabel_gdadir_iwa.setBackground(color_warning_background);
			jLabel_gdadir_iwa.setOpaque(true);
			return false;
		}else{
			jLabel_gdadir_iwa.setBackground(null);
			jLabel_gdadir_iwa.setOpaque(false);
			return true;
		}
	}
	private boolean check_validity_combobox_web_browser(){
		if(jRadioButton_wb_list.isSelected() &&
		   jComboBox_wb.getSelectedIndex() == 0){
			jRadioButton_wb_list.setOpaque(true);
			jRadioButton_wb_list.repaint();
			return false;
		}else{
			jRadioButton_wb_list.setOpaque(false);
			jRadioButton_wb_list.repaint();
			return true;
		}
	}
	private boolean check_validity_textfield_web_browser(){
		// 以下の(A)の行は`if(jRadioButton_wb_input.isSelected() &&'では正常に動作しない
		//   この関数は jRadioButton_wb_list の状態が変わったときに呼び出されるが、
		//   その瞬間は jRadioButton_wb_input の状態は変化しないため
		if(! jRadioButton_wb_list.isSelected() &&	// (A)
		   jTextField_wb_input.getText().trim().equals("")){
			jRadioButton_wb_input.setOpaque(true);
			jRadioButton_wb_input.repaint();
			return false;
		}else{
			jRadioButton_wb_input.setOpaque(false);
			jRadioButton_wb_input.repaint();
			return true;
		}
	}
	/**
	 * サーチエンジンが正常に準備できているかを調べ、
	 * 準備できていないときはテキストフィールドの背景を赤くして警告する
	 * @param warn_flag	正常に準備できていないとき、警告を表示するか否か
	 */
	private void check_search_engine_status (boolean warn_flag){
		StringBuilder msg = new StringBuilder();
		if(SYSTEM_CORPUS != E_Corpus.IWANAMI){
			if(wm.se_mai_status == 2){
				if(jTextField_gdadir_mai.getText().trim().equals("")){
					jTextField_gdadir_mai.setBackground(Color.WHITE);
					jTextField_gdadir_mai.setToolTipText("");
					msg.append("GDAフォルダ(新聞)が設定されていません\n");
				}else{
					jTextField_gdadir_mai.setBackground(color_warning_background);
					jTextField_gdadir_mai.setToolTipText("新聞GDAコーパスのフォルダが正しく設定されていません");
					msg.append("GDAフォルダ(新聞)が正しく設定されていません\n");
				}
				msg.append("( このままでは新聞GDAコーパスの閲覧・検索はできません )\n");
			}else{
				jTextField_gdadir_mai.setBackground(Color.WHITE);
				jTextField_gdadir_mai.setToolTipText("");
			}
		}
		if(SYSTEM_CORPUS != E_Corpus.MAINITI){
			if(wm.se_iwa_status == 2){
				if(jTextField_gdadir_iwa.getText().trim().equals("")){
					jTextField_gdadir_iwa.setBackground(Color.WHITE);
					jTextField_gdadir_iwa.setToolTipText("");
					msg.append("GDAフォルダ(岩波)が設定されていません\n");
				}else{
					jTextField_gdadir_iwa.setBackground(color_warning_background);
					jTextField_gdadir_iwa.setToolTipText("岩波GDAコーパスのフォルダが正しく設定されていません");
					msg.append("GDAフォルダ(岩波)が正しく設定されていません\n");
				}
				msg.append("( このままでは岩波GDAコーパスの閲覧・検索はできません )\n");
			}else{
				jTextField_gdadir_iwa.setBackground(Color.WHITE);
				jTextField_gdadir_iwa.setToolTipText("");
			}
		}
		if(warn_flag && msg.length() > 0)
			wm.show_warning_message_in_dialog(msg.toString(),this);
	}
	/**
	 * テキストフィールドからフォーカスが離れたとき、
	 * フォーカスの移り先が「保存」ボタンかをチェックするメソッド
	 */
	private boolean is_move_to_save_button (java.awt.event.FocusEvent e) {
		// getOppositeComponent はフォーカスが移った先のコンポーネントを返す
		Component c = e.getOppositeComponent();
		if(c != null) {
			String s = c.getName();
			if(s != null){
				// あらかじめ jButton_save.setName("save_button") として、
				// ボタンに "save_button" というラベルを与えておく 
				if( s.equals("save_button") ) return true;
			}
		}
		return false;
	}
	/**
	 * ファイルダイアログを使ってフォームを埋めるメソッド
	 */
	private void fill_form_by_file_chooser(JTextField tf,JFileChooserEx fc,String title){
		/* ToDo FileChooser temporary off */
		fc.setDialogTitle(title);
		File f = new File( tf.getText() );
		if(f.exists())
			fc.setCurrentDirectory(f.getAbsoluteFile().getParentFile());
		int res = fc.showOpenDialog(this);
		if(res == JFileChooser.APPROVE_OPTION){
			tf.setText( fc.getSelectedFile().getAbsolutePath() );
			check_item_changed_action();
		}
	}
	/**
	 * 設定変更の有無に応じて保存パネルの表示を切り換えるメソッド
	 */
	private void update_save_panel () {
		if(changed_flag) {
			jButton_cancel.setEnabled(true);
		}else{
			jButton_cancel.setEnabled(false);
		}
	}
	/*
	 * 設定変更の有無に応じて保存パネルの表示を切り換えるメソッド
	 * (ウィンドウ下部に警告メッセージを表示していたときの古い版)
	 * 
	void update_save_panel () {
		StringBuilder undef_msg = null;
		String se_msg = null;
		if(changed_flag) {
			jButton_cancel.setEnabled(true);
			jTextArea_update_msg.setText(save_message);
		}else{
			jButton_cancel.setEnabled(false);
			// 警告メッセージの表示
			if(SYSTEM_CORPUS != E_Corpus.IWANAMI){
				if(jTextField_gdadir_mai.getText().trim().equals("")){
					if(undef_msg == null){
						undef_msg = new StringBuilder();
					}else{
						undef_msg.append(", ");
					}
					undef_msg.append("GDAフォルダ(新聞)");
				}else if(wm.se_mai == null){
					se_msg = "警告: 設定されている「GDAフォルダ(新聞)」は不正なフォルダです";
				}
			}
			if(SYSTEM_CORPUS != E_Corpus.MAINITI){
				if(jTextField_gdadir_iwa.getText().trim().equals("")){
					if(undef_msg == null){
						undef_msg = new StringBuilder();
					}else{
						undef_msg.append(", ");
					}
					undef_msg.append("GDAフォルダ(岩波)");
				}else if(wm.se_iwa == null){
					se_msg = "警告: 設定されている「GDAフォルダ(岩波)」は不正なフォルダです";
				}
			}
			if(jRadioButton_wb_list.isSelected()){
				if(jComboBox_wb.getSelectedItem() == E_WebBrowser.UNDEF){
					if(undef_msg == null){
						undef_msg = new StringBuilder();
					}else{
						undef_msg.append(", ");
					}
					undef_msg.append("ウェブブラウザ(リスト)");
				}
			}else{
				if(jTextField_wb_input.getText().trim().equals("")){
					if(undef_msg == null){
						undef_msg = new StringBuilder();
					}else{
						undef_msg.append(", ");
					}
					undef_msg.append("ウェブブラウザ(ダイアログ)");
				}
			}
			String msg;
			if(se_msg == null){
				if(undef_msg == null){
					msg = "";
				}else{
					msg = "警告: "+undef_msg.toString()+" が未設定です";
				}
			}else{
				if(undef_msg == null){
					msg = se_msg;
				}else{
					msg = se_msg+"\n警告: "+undef_msg.toString()+" が未設定です";
				}
			}
			jTextArea_update_msg.setText(msg);
		}
	}
	 */
	/* (obsolete)
	 * テキストフィールドに入力された整数を正規化して返すメソッド
	 * @param tf	テキストフィールド
	 * @return		正規化された整数
	static int normalized_number_in_TF (JTextField_PositiveInt tf) {
		String s = tf.getText();
		if(s==null || s.equals("")) return -1;
		return( Integer.parseInt(s) );
	}
	 */
	/* 最大表示件数のテキストフィールドをintに変換するメソッド
	 * ただし、0以下の場合は-1を返すようにする
	private int normalize_display_num_in_TF (JTextField tf) {
		String s = tf.getText();
		if(s == null || s.equals("")) return -1;
		int n = Integer.parseInt(s);
		if(n <= 0) {
			return -1;
		}else{
			return n;
		}
	}
	*/
	/* (obsolete)
	 * ファイル名を入力するテキストフィールドのカーソルの位置を文字列の末尾に移動するメソッド
	 * このメソッドは ConfView に切り換えるときに実行する
	 * テキストフィールドより長い文字列が入力されているとき、文字列の末尾を表示させるため
	private void move_caret_of_text_field_to_end () {
		jTextField_gdadir_mai.setCaretPosition(jTextField_gdadir_mai.getText().length());
		jTextField_gdadir_iwa.setCaretPosition(jTextField_gdadir_iwa.getText().length());
	}
	 */

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gBC_L_padding_right = new GridBagConstraints();
		gBC_L_padding_right.gridx = 2;
		gBC_L_padding_right.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_padding_right.weightx = 1.0D;
		gBC_L_padding_right.gridy = 0;
		GridBagConstraints gBC_P_save = new GridBagConstraints();
		gBC_P_save.gridx = 1;
		gBC_P_save.gridy = 2;
		gBC_P_save.fill = GridBagConstraints.BOTH;
		gBC_P_save.weighty = 1.0D;
		gBC_P_save.weightx = 8.0D;
		GridBagConstraints gBC_P_kwicview = new GridBagConstraints();
		gBC_P_kwicview.gridx = 1;
		gBC_P_kwicview.fill = GridBagConstraints.BOTH;
		gBC_P_kwicview.weightx = 8.0D;
		gBC_P_kwicview.insets = new Insets(5, 5, 5, 5);
		gBC_P_kwicview.weighty = 4.0D;
		gBC_P_kwicview.gridy = 1;
		GridBagConstraints gBC_P_general = new GridBagConstraints();
		gBC_P_general.gridx = 1;
		gBC_P_general.weightx = 8.0D;
		gBC_P_general.fill = GridBagConstraints.BOTH;
		gBC_P_general.insets = new Insets(5, 5, 5, 5);
		gBC_P_general.weighty = 4.0D;
		gBC_P_general.gridy = 0;
		GridBagConstraints gBC_L_padding_left = new GridBagConstraints();
		gBC_L_padding_left.gridx = 0;
		gBC_L_padding_left.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_padding_left.weightx = 1.0D;
		gBC_L_padding_left.gridy = 0;
		jLabel_padding_left = new JLabel();
		jLabel_padding_left.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_L_right_blank = new GridBagConstraints();
		gBC_L_right_blank.gridx = 2;
		gBC_L_right_blank.weightx = 1.0D;
		gBC_L_right_blank.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_right_blank.gridy = 0;
		jLabel_padding_right = new JLabel();
		jLabel_padding_right.setPreferredSize(new Dimension(10, 10));
		
		this.setLayout(new GridBagLayout());
		this.add(jLabel_padding_left, gBC_L_padding_left);
		this.add(jLabel_padding_right, gBC_L_padding_right);
		this.add(getJPanel_general(), gBC_P_general);
		//this.add(getJPanel_kwicview(), gBC_P_kwicview);
		this.add(getJPanel_save(), gBC_P_save);
		
		/*
	    ButtonGroup target_corpus_group = new ButtonGroup();
		target_corpus_group.add(jRadioButton_corpus_mai);
		target_corpus_group.add(jRadioButton_corpus_iwa);
		*/
		ButtonGroup web_browser_group = new ButtonGroup();
		web_browser_group.add(jRadioButton_wb_list);
		web_browser_group.add(jRadioButton_wb_input);

		fill_forms();
//		current_value_of_TF_disp_num = jTextField_disp_num.getText();		
		if(SYSTEM_CORPUS == E_Corpus.IWANAMI){
			jLabel_gdadir_mai.setForeground(Color.gray);
			jTextField_gdadir_mai.setEnabled(false);
			jButton_gdadir_mai.setEnabled(false);
		}else if(SYSTEM_CORPUS == E_Corpus.MAINITI){
			jLabel_gdadir_iwa.setForeground(Color.gray);
			jTextField_gdadir_iwa.setEnabled(false);
			jButton_gdadir_iwa.setEnabled(false);
		}
		initialized_flag = true;
		show_warning_when_undefined_item_found();
		check_search_engine_status(false);
		update_save_panel();
	}
	
	/**
	 * This method initializes jTextField_gdadir_mai	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_gdadir_mai() {
		if (jTextField_gdadir_mai == null) {
			jTextField_gdadir_mai = new JTextField();
			jTextField_gdadir_mai.setPreferredSize(new Dimension(80, 28));
			jTextField_gdadir_mai.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					check_item_changed_action();
				}
			});
			jTextField_gdadir_mai.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					// フォーカスの移動先が「保存ボタン」なら、保存パネルの表示を変化させない
					if( is_move_to_save_button(e) ) return;
					check_item_changed_action();
				}
			});
		}
		return jTextField_gdadir_mai;
	}

	/**
	 * This method initializes jTextField_disp_num	
	 * 	
	 * @return javax.swing.JTextField	
	private JTextField getJTextField_disp_num() {
		if (jTextField_disp_num == null) {
			jTextField_disp_num = new JTextField();
			jTextField_disp_num.setInputVerifier(new IntegerInputVerifier());
			jTextField_disp_num.setPreferredSize(new Dimension(40, 28));
		}
		return jTextField_disp_num;
	}
	 */
	/*
//	private JFormattedTextField getJTextField_disp_num() {
	private JTextField_PositiveInt getJTextField_disp_num() {		
		if (jTextField_disp_num == null) {
//			jTextField_disp_num = new JFormattedTextField();
//			jTextField_disp_num.setFormatterFactory(new NumberFormatterFactory());
			jTextField_disp_num = new JTextField_PositiveInt();
//			jTextField_disp_num.setInputVerifier(new IntegerInputVerifier());
			jTextField_disp_num.setPreferredSize(new Dimension(40, 28));
			jTextField_disp_num.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextField_disp_num.restore_value_if_invalid(0);
					check_item_changed_action();
				}
			});
			jTextField_disp_num.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					jTextField_disp_num.restore_value_if_invalid(0);
					// フォーカスの移動先が「保存ボタン」なら、保存パネルの表示を変化させない
					if( is_move_to_save_button(e) ) return;
					check_item_changed_action();
				}
			});
		}
		return jTextField_disp_num;
	}
	*/
	
	/**
	 * This method initializes jPanel_save	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_save() {
		if (jPanel_save == null) {
			//GridBagConstraints gBC_TA_update_msg = new GridBagConstraints();
			//gBC_TA_update_msg.fill = GridBagConstraints.HORIZONTAL;
			//gBC_TA_update_msg.weighty = 1.0;
			//gBC_TA_update_msg.gridx = 2;
			//gBC_TA_update_msg.gridy = 0;
			//gBC_TA_update_msg.weightx = 8.0D;
			GridBagConstraints gBC_L_padding_save2 = new GridBagConstraints();
			gBC_L_padding_save2.gridx = 3;
			gBC_L_padding_save2.weightx = 1.0D;
			gBC_L_padding_save2.gridy = 0;
			jLabel_padding_save2 = new JLabel();
			jLabel_padding_save2.setText("");
			jLabel_padding_save2.setPreferredSize(new Dimension(10, 10));
			GridBagConstraints gBC_L_padding_save1 = new GridBagConstraints();
			gBC_L_padding_save1.gridx = 0;
			gBC_L_padding_save1.weightx = 1.0D;
			gBC_L_padding_save1.gridy = 0;
			jLabel_padding_save1 = new JLabel();
			jLabel_padding_save1.setText("");
			jLabel_padding_save1.setPreferredSize(new Dimension(10, 10));
			GridBagConstraints gBC_B_close = new GridBagConstraints();
			gBC_B_close.gridx = 4;
			gBC_B_close.anchor = GridBagConstraints.CENTER;
			gBC_B_close.insets = new Insets(0, 0, 0, 5);
			gBC_B_close.gridy = 0;
			GridBagConstraints gBC_B_cancel = new GridBagConstraints();
			gBC_B_cancel.gridx = 2;
			gBC_B_cancel.insets = new Insets(0, 10, 0, 10);
			gBC_B_cancel.gridy = 0;
			GridBagConstraints gBC_B_save = new GridBagConstraints();
			gBC_B_save.gridx = 1;
			gBC_B_save.anchor = GridBagConstraints.CENTER;
			gBC_B_save.fill = GridBagConstraints.NONE;
			gBC_B_save.gridy = 0;
			jPanel_save = new JPanel();
			jPanel_save.setLayout(new GridBagLayout());
			jPanel_save.setVisible(true);
			jPanel_save.setPreferredSize(new Dimension(600, 50));
			jPanel_save.add(getJButton_save(), gBC_B_save);
			jPanel_save.add(getJButton_cancel(), gBC_B_cancel);
			jPanel_save.add(getJButton_close(), gBC_B_close);
			jPanel_save.add(jLabel_padding_save1, gBC_L_padding_save1);
			jPanel_save.add(jLabel_padding_save2, gBC_L_padding_save2);
		}
		return jPanel_save;
	}

	/**
	 * This method initializes jButton_save	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_save() {
		if (jButton_save == null) {
			//jButton_save = new JButtonHL();
			jButton_save = new JButton();
			jButton_save.setText("保存");
			jButton_save.setName("save_button");
			jButton_save.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					save(true,true);
					show_warning_when_undefined_item_found();
					changed_flag = false;
					update_save_panel();
				}
			});
		}
		return jButton_save;
	}

	/**
	 * This method initializes jButton_cancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_cancel() {
		if (jButton_cancel == null) {
			jButton_cancel = new JButton();
			jButton_cancel.setText("キャンセル");
			jButton_cancel.setToolTipText("変更前の設定に戻します");
			jButton_cancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fill_forms();
					changed_flag = false;
					update_save_panel();
				}
			});
		}
		return jButton_cancel;
	}

	/*  警告メッセージをウィンドウの下に表示してするためのテキストエリア
	 *   仕様変更のため削除
	 * This method initializes jTextArea_update_msg	
	 * 	
	 * @return javax.swing.JTextArea
	 * 
	private JTextArea getJTextArea_update_msg() {
		if (jTextArea_update_msg == null) {
			jTextArea_update_msg = new JTextArea();
			jTextArea_update_msg.setForeground(Color.red);
			//jTextArea_update_msg.setBackground(MainFrame.bg_color_of_message_area);
			jTextArea_update_msg.setOpaque(false);
			jTextArea_update_msg.setEditable(false);
			jTextArea_update_msg.setWrapStyleWord(false);
			jTextArea_update_msg.setLineWrap(true);
			//jTextArea_update_msg.setToolTipText("メッセージ表示エリア");
			//jTextArea_update_msg.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		}
		return jTextArea_update_msg;
	}
	 */

	/*
	 * This method initializes jRadioButton_corpus_mai	
	 * 	
	 * @return javax.swing.JRadioButton	
	private JRadioButton getJRadioButton_corpus_mai() {
		if (jRadioButton_corpus_mai == null) {
			jRadioButton_corpus_mai = new JRadioButton();
			jRadioButton_corpus_mai.setText(E_Corpus.MAINITI.corpus_name());
			jRadioButton_corpus_mai.setOpaque(false);
			jRadioButton_corpus_mai.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					check_item_changed_action();
				}
			});
			if(SYSTEM_CORPUS == E_Corpus.IWANAMI)
				jRadioButton_corpus_mai.setEnabled(false);
		}
		return jRadioButton_corpus_mai;
	}
	 */
	/*
	 * This method initializes jRadioButton_corpus_iwa	
	 * 	
	 * @return javax.swing.JRadioButton	
	private JRadioButton getJRadioButton_corpus_iwa() {
		if (jRadioButton_corpus_iwa == null) {
			jRadioButton_corpus_iwa = new JRadioButton();
			jRadioButton_corpus_iwa.setText(E_Corpus.IWANAMI.corpus_name());
			jRadioButton_corpus_iwa.setOpaque(false);
			jRadioButton_corpus_iwa.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					check_item_changed_action();
				}
			});
			if(SYSTEM_CORPUS == E_Corpus.MAINITI)
				jRadioButton_corpus_iwa.setEnabled(false);
		}
		return jRadioButton_corpus_iwa;
	}
	 */
	/**
	 * This method initializes jButton_gdadir_mai	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_gdadir_mai() {
		if (jButton_gdadir_mai == null) {
			jButton_gdadir_mai = new JButton();
			jButton_gdadir_mai.setText("選択");
			jButton_gdadir_mai.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fill_form_by_file_chooser(jTextField_gdadir_mai,getFileChooserGDA(),
					    "新聞GDAフォルダを選択して下さい");
				}
			});
		}
		return jButton_gdadir_mai;
	}
	/**
	 * This method initializes jTextField_gdadir_iwa	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_gdadir_iwa() {
		if (jTextField_gdadir_iwa == null) {
			jTextField_gdadir_iwa = new JTextField();
			jTextField_gdadir_iwa.setPreferredSize(new Dimension(80, 28));
			jTextField_gdadir_iwa.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					check_item_changed_action();
				}
			});
			jTextField_gdadir_iwa.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					// フォーカスの移動先が「保存ボタン」なら、保存パネルの表示を変化させない
					if( is_move_to_save_button(e) ) return;
					check_item_changed_action();
				}
			});
		}
		return jTextField_gdadir_iwa;
	}
	/**
	 * This method initializes jButton_gdadir_iwa	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_gdadir_iwa() {
		if (jButton_gdadir_iwa == null) {
			jButton_gdadir_iwa = new JButton();
			jButton_gdadir_iwa.setText("選択");
			jButton_gdadir_iwa.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fill_form_by_file_chooser(jTextField_gdadir_iwa, getFileChooserGDA(),
					    "岩波GDAフォルダを指定して下さい");
				}
			});
		}
		return jButton_gdadir_iwa;
	}
	/**
	 * This method initializes jPanel_general	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_general() {
		if (jPanel_general == null) {
 			GridBagConstraints gBC_CB_default_view = new GridBagConstraints();
 			gBC_CB_default_view.fill = GridBagConstraints.NONE;
 			gBC_CB_default_view.gridy = 4;
 			gBC_CB_default_view.weightx = 1.0;
 			gBC_CB_default_view.anchor = GridBagConstraints.WEST;
 			gBC_CB_default_view.gridwidth = GridBagConstraints.REMAINDER;
 			gBC_CB_default_view.gridx = 1;
			GridBagConstraints gBC_L_default_view = new GridBagConstraints();
			gBC_L_default_view.gridx = 0;
			gBC_L_default_view.insets = new Insets(0, 10, 0, 10);
			gBC_L_default_view.anchor = GridBagConstraints.WEST;
			gBC_L_default_view.gridy = 4;
			jLabel_default_view = new JLabel();
			jLabel_default_view.setText("初期のビュー");
			jLabel_default_view.setToolTipText("ツール起動時に最初に表示するビューを設定します");
			GridBagConstraints gBC_L_warn = new GridBagConstraints();
			gBC_L_warn.gridx = 0;
			gBC_L_warn.insets = new Insets(0, 0, 0, 10);
			gBC_L_warn.gridwidth = GridBagConstraints.REMAINDER;
			gBC_L_warn.anchor = GridBagConstraints.EAST;
			gBC_L_warn.gridy = 5;
			jLabel_warn = new JLabel();
			jLabel_warn.setText("! 未設定の項目があります");
			jLabel_warn.setForeground(color_warning_background);
			GridBagConstraints gBC_CB_sent_newline = new GridBagConstraints();
			gBC_CB_sent_newline.gridx = 1;
			gBC_CB_sent_newline.gridwidth = GridBagConstraints.REMAINDER;
			gBC_CB_sent_newline.anchor = GridBagConstraints.WEST;
			gBC_CB_sent_newline.gridy = 3;
			GridBagConstraints gBC_L_file_browse = new GridBagConstraints();
			gBC_L_file_browse.gridx = 0;
			gBC_L_file_browse.anchor = GridBagConstraints.WEST;
			gBC_L_file_browse.insets = new Insets(0, 10, 0, 10);
			gBC_L_file_browse.weighty = 2.0D;
			gBC_L_file_browse.gridy = 3;
			jLabel_file_browse = new JLabel();
			jLabel_file_browse.setText("ファイル表示");
			GridBagConstraints gBC_P_web_browser = new GridBagConstraints();
			gBC_P_web_browser.gridx = 1;
			gBC_P_web_browser.gridy = 2;
			gBC_P_web_browser.fill = GridBagConstraints.BOTH;
			gBC_P_web_browser.insets = new Insets(0, 0, 5, 0);
			gBC_P_web_browser.gridwidth = GridBagConstraints.REMAINDER;
			GridBagConstraints gBC_L_web_browser = new GridBagConstraints();
			gBC_L_web_browser.gridx = 0;
			gBC_L_web_browser.anchor = GridBagConstraints.WEST;
			gBC_L_web_browser.insets = new Insets(0, 10, 0, 10);
			gBC_L_web_browser.weighty = 2.0D;
			gBC_L_web_browser.gridy = 2;
			jLabel_web_browser = new JLabel();
			jLabel_web_browser.setText("ウェブブラウザ");
			GridBagConstraints gBC_P_corpus = new GridBagConstraints();
			gBC_P_corpus.gridx = 1;
			gBC_P_corpus.gridwidth = GridBagConstraints.REMAINDER;
			gBC_P_corpus.anchor = GridBagConstraints.WEST;
			gBC_P_corpus.gridy = 1;
			GridBagConstraints gBC_B_gdadir_iwa = new GridBagConstraints();
			gBC_B_gdadir_iwa.gridx = 1;
			gBC_B_gdadir_iwa.anchor = GridBagConstraints.CENTER;
			gBC_B_gdadir_iwa.gridy = 1;
			GridBagConstraints gBC_TF_gdadir_iwa = new GridBagConstraints();
			gBC_TF_gdadir_iwa.anchor = GridBagConstraints.WEST;
			gBC_TF_gdadir_iwa.gridx = 2;
			gBC_TF_gdadir_iwa.gridy = 1;
			gBC_TF_gdadir_iwa.weightx = 1.0D;
			gBC_TF_gdadir_iwa.gridwidth = 2;
			gBC_TF_gdadir_iwa.insets = new Insets(0, 10, 0, 10);
			gBC_TF_gdadir_iwa.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gBC_B_gdadir_mai = new GridBagConstraints();
			gBC_B_gdadir_mai.gridx = 1;
			gBC_B_gdadir_mai.anchor = GridBagConstraints.CENTER;
			gBC_B_gdadir_mai.gridy = 0;
			GridBagConstraints gBC_TF_gdadir_mai = new GridBagConstraints();
			gBC_TF_gdadir_mai.anchor = GridBagConstraints.WEST;
			gBC_TF_gdadir_mai.gridwidth = 2;
			gBC_TF_gdadir_mai.gridx = 2;
			gBC_TF_gdadir_mai.gridy = 0;
			gBC_TF_gdadir_mai.weightx = 1.0D;
			gBC_TF_gdadir_mai.insets = new Insets(0, 10, 0, 10);
			gBC_TF_gdadir_mai.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gBC_L_gen_title = new GridBagConstraints();
			gBC_L_gen_title.gridx = 0;
			gBC_L_gen_title.anchor = GridBagConstraints.NORTHWEST;
			gBC_L_gen_title.insets = new Insets(5, 5, 0, 0);
			gBC_L_gen_title.gridy = 0;
			GridBagConstraints gBC_L_gdadir_iwa = new GridBagConstraints();
			gBC_L_gdadir_iwa.gridx = 0;
			gBC_L_gdadir_iwa.anchor = GridBagConstraints.WEST;
			gBC_L_gdadir_iwa.weighty = 2.0D;
			gBC_L_gdadir_iwa.insets = new Insets(0, 10, 0, 10);
			gBC_L_gdadir_iwa.gridy = 1;
			GridBagConstraints gBC_L_gdadir_mai = new GridBagConstraints();
			gBC_L_gdadir_mai.gridx = 0;
			gBC_L_gdadir_mai.anchor = GridBagConstraints.WEST;
			gBC_L_gdadir_mai.weighty = 2.0D;
			gBC_L_gdadir_mai.insets = new Insets(0, 10, 0, 10);
			gBC_L_gdadir_mai.gridy = 0;
			//GridBagConstraints gBC_L_corpus = new GridBagConstraints();
			//gBC_L_corpus.anchor = GridBagConstraints.WEST;
			//gBC_L_corpus.gridy = 1;
			//gBC_L_corpus.weighty = 2.0D;
			//gBC_L_corpus.insets = new Insets(0, 10, 0, 10);
			//gBC_L_corpus.gridx = 0;
			jLabel_gdadir_iwa = new JLabel();
			jLabel_gdadir_iwa.setText("GDAフォルダ(岩波)");
			jLabel_gdadir_mai = new JLabel();
			jLabel_gdadir_mai.setText("GDAフォルダ(新聞)");
			//jLabel_corpus = new JLabel();
			//jLabel_corpus.setText("対象コーパス");
			//jLabel_gen_title = new JLabel();
			//jLabel_gen_title.setText("[全般]");
			//jLabel_gen_title.setForeground(Color.red);
			jPanel_general = new JPanel();
			jPanel_general.setLayout(new GridBagLayout());
			jPanel_general.setBackground(color_view_background);
			jPanel_general.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			//jPanel_general.add(jLabel_gen_title, gBC_L_gen_title);
			jPanel_general.add(jLabel_gdadir_mai, gBC_L_gdadir_mai);
			jPanel_general.add(getJButton_gdadir_mai(), gBC_B_gdadir_mai);
			jPanel_general.add(getJTextField_gdadir_mai(), gBC_TF_gdadir_mai);
			jPanel_general.add(jLabel_gdadir_iwa, gBC_L_gdadir_iwa);
			jPanel_general.add(getJTextField_gdadir_iwa(), gBC_TF_gdadir_iwa);
			jPanel_general.add(getJButton_gdadir_iwa(), gBC_B_gdadir_iwa);
			jPanel_general.add(jLabel_web_browser, gBC_L_web_browser);
			jPanel_general.add(getJPanel_web_browser(), gBC_P_web_browser);
			jPanel_general.add(jLabel_file_browse, gBC_L_file_browse);
			jPanel_general.add(getJCheckBox_sent_newline(), gBC_CB_sent_newline);
			jPanel_general.add(jLabel_warn, gBC_L_warn);
			jPanel_general.add(jLabel_default_view, gBC_L_default_view);
			jPanel_general.add(getJComboBox_default_view(), gBC_CB_default_view);
		}
		return jPanel_general;
	}

	/**
	 * This method initializes jPanel_web_browser	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_web_browser() {
		if (jPanel_web_browser == null) {
			GridBagConstraints gBC_TF_wb_input = new GridBagConstraints();
			gBC_TF_wb_input.fill = GridBagConstraints.HORIZONTAL;
			gBC_TF_wb_input.gridy = 1;
			gBC_TF_wb_input.weightx = 1.0;
			gBC_TF_wb_input.insets = new Insets(0, 10, 0, 10);
			gBC_TF_wb_input.gridx = 2;
			GridBagConstraints gBC_B_wb_input = new GridBagConstraints();
			gBC_B_wb_input.gridx = 1;
			gBC_B_wb_input.gridy = 1;
			GridBagConstraints gGC_CB_wb = new GridBagConstraints();
			gGC_CB_wb.fill = GridBagConstraints.NONE;
			gGC_CB_wb.gridy = 0;
			gGC_CB_wb.weightx = 1.0;
			gGC_CB_wb.gridwidth = 2;
			gGC_CB_wb.anchor = GridBagConstraints.WEST;
			gGC_CB_wb.insets = new Insets(0, 0, 1, 0);
			gGC_CB_wb.gridx = 1;
			GridBagConstraints gBC_RB_wb_input = new GridBagConstraints();
			gBC_RB_wb_input.gridx = 0;
			gBC_RB_wb_input.anchor = GridBagConstraints.WEST;
			gBC_RB_wb_input.insets = new Insets(0, 0, 0, 10);
			gBC_RB_wb_input.weighty = 1.0D;
			gBC_RB_wb_input.gridy = 1;
			GridBagConstraints gBC_RB_wb_list = new GridBagConstraints();
			gBC_RB_wb_list.gridx = 0;
			gBC_RB_wb_list.anchor = GridBagConstraints.WEST;
			gBC_RB_wb_list.weighty = 1.0D;
			gBC_RB_wb_list.gridy = 0;
			jPanel_web_browser = new JPanel();
			jPanel_web_browser.setLayout(new GridBagLayout());
			jPanel_web_browser.setOpaque(false);
			jPanel_web_browser.add(getJRadioButton_wb_list(), gBC_RB_wb_list);
			jPanel_web_browser.add(getJRadioButton_wb_input(), gBC_RB_wb_input);
			jPanel_web_browser.add(getJComboBox_wb(), gGC_CB_wb);
			jPanel_web_browser.add(getJButton_wb_input(), gBC_B_wb_input);
			jPanel_web_browser.add(getJTextField_wb_input(), gBC_TF_wb_input);
		}
		return jPanel_web_browser;
	}

	/**
	 * This method initializes jRadioButton_wb_list	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_wb_list() {
		if (jRadioButton_wb_list == null) {
			jRadioButton_wb_list = new JRadioButton();
			jRadioButton_wb_list.setText("リストから選択");
			jRadioButton_wb_list.setBackground(color_warning_background);
			jRadioButton_wb_list.setOpaque(false);
			jRadioButton_wb_list.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					check_item_changed_action();
				}
			});
		}
		return jRadioButton_wb_list;
	}

	/**
	 * This method initializes jRadioButton_wb_input	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_wb_input() {
		if (jRadioButton_wb_input == null) {
			jRadioButton_wb_input = new JRadioButton();
			jRadioButton_wb_input.setBackground(color_warning_background);
			jRadioButton_wb_input.setText("ダイアログで選択");
			jRadioButton_wb_input.setOpaque(false);
		}
		return jRadioButton_wb_input;
	}

	/**
	 * This method initializes jComboBox_wb	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBoxWithDisabledItems getJComboBox_wb() {
		if (jComboBox_wb == null) {
			jComboBox_wb = new JComboBoxWithDisabledItems();
			jComboBox_wb.setBackground(Color.white);
			jComboBox_wb.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					check_item_changed_action();
				}
			});
			wm.web_browser.setup_combobox_of_select_web_browser(jComboBox_wb);
		}
		return jComboBox_wb;
	}

	/**
	 * This method initializes jButton_wb_input	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_wb_input() {
		if (jButton_wb_input == null) {
			jButton_wb_input = new JButton();
			jButton_wb_input.setText("選択");
			jButton_wb_input.addActionListener(new java.awt.event.ActionListener() {   
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					fill_form_by_file_chooser(jTextField_wb_input, getFileChooserWB(),
				    	"ウェブブラウザを指定して下さい");
				}
			
			});
		}
		return jButton_wb_input;
	}

	/**
	 * This method initializes jTextField_wb_input	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_wb_input() {
		if (jTextField_wb_input == null) {
			jTextField_wb_input = new JTextField();
			jTextField_wb_input.setPreferredSize(new Dimension(100, 28));
			jTextField_wb_input.setMinimumSize(new Dimension(100, 28));
			jTextField_wb_input.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					check_item_changed_action();
				}
			});
			jTextField_wb_input.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					// フォーカスの移動先が「保存ボタン」なら、保存パネルの表示を変化させない
					if( is_move_to_save_button(e) ) return;
					check_item_changed_action();
				}
			});
		}
		return jTextField_wb_input;
	}

	/**
	 * This method initializes jComboBox_default_view	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox_default_view() {
		if (jComboBox_default_view == null) {
			jComboBox_default_view = new JComboBox();
			jComboBox_default_view.addItem(E_View.PRESERVE);
			jComboBox_default_view.addItem(E_View.BROWSE);
			jComboBox_default_view.addItem(E_View.SEARCH);
			jComboBox_default_view.addItem(E_View.BATCH);
			jComboBox_default_view.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					check_item_changed_action();
				}
			});
		}
		return jComboBox_default_view;
	}

	/**
	 * This method initializes file_chooser_for_gda_dir	
	 */
	private JFileChooserEx getFileChooserGDA (){
		//return null;
		/* ToDo FileChooser temporary off */ 
		if(file_chooser_for_gda_dir == null){
			file_chooser_for_gda_dir = new JFileChooserEx();
			file_chooser_for_gda_dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		return file_chooser_for_gda_dir;
	}

	/**
	 * This method initializes file_chooser_for_web_browser
	 */
	private JFileChooserEx getFileChooserWB (){
		//return null;
		/* ToDo FileChooser temporary off */ 
		if(file_chooser_for_web_browser == null){
			file_chooser_for_web_browser = new JFileChooserEx();
			file_chooser_for_web_browser.setCurrentDirectory(new File("/"));
			if(SYSTEM_OS == E_OS.MacOSX){
				file_chooser_for_web_browser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			}else{
				file_chooser_for_web_browser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
		}
		return file_chooser_for_web_browser;
	}

	/**
	 * This method initializes jCheckBox_sent_newline	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox_sent_newline() {
		if (jCheckBox_sent_newline == null) {
			jCheckBox_sent_newline = new JCheckBox();
			jCheckBox_sent_newline.setOpaque(false);
			jCheckBox_sent_newline.setText("文ごとに改行する");
			jCheckBox_sent_newline.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					check_item_changed_action();
				}
			});
		}
		return jCheckBox_sent_newline;
	}

	/**
	 * This method initializes jButton_close	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_close() {
		if (jButton_close == null) {
			jButton_close = new JButton();
			jButton_close.setText("閉じる");
			jButton_close.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(changed_flag){
						int ans = JOptionPane.showConfirmDialog(null,"変更された設定を保存しますか",
									"Warning",JOptionPane.YES_NO_CANCEL_OPTION);
						if(ans == JOptionPane.CANCEL_OPTION) return;
						if(ans == JOptionPane.YES_OPTION){
							save(true,false);
						}
					}
					wm.close_conf_view();
				}
			});
		}
		return jButton_close;
	}
}
