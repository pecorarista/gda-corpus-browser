package jp.or.gsk.gdacb.gui;

import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_CORPUS;
import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_OS;
import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.E_KeywordType;
import jp.or.gsk.gdacb.E_OS;
import jp.or.gsk.gdacb.gui.parts.AnimatedLabel;
import jp.or.gsk.gdacb.gui.parts.JButtonHL;
import jp.or.gsk.gdacb.gui.parts.JFileChooserEx;
import jp.or.gsk.gdacb.gui.parts.JTextField_PositiveInt;
import jp.or.gsk.gdacb.search_engine.SE_Exception;
import jp.or.gsk.gdacb.search_engine.TSV_Exporter;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.SwingWorker;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

class BatchView extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private WindowManager wm = null;
	private ViewFrame pf = null;	// 親のフレーム
	Conf_Batch_View conf_batch = null;

	private JFileChooserEx file_chooser = null; 
	private Color step_fg_color = new Color(51, 0, 153);  //  @jve:decl-index=0:
	private TSV_Exporter tsv = null;
	private String input_filename = null;
	
	private JTextField jTextField_input = null;
	private JButton jButton_input = null;
	private JTextField jTextField_output = null;
	private JButton jButton_output = null;
	private JLabel jLabel_step1 = null;
	private JLabel jLabel_step2 = null;
	private JLabel jLabel_step3 = null;
	private JLabel jLabel_step4 = null;
	private JLabel jLabel_keytype = null;
	private JLabel jLabel_coding = null;
	private JLabel jLabel_sense = null;
	private JLabel jLabel_kwic_len = null;
	private JPanel jPanel_kwic_len = null;
	private JLabel jLabel_kwic_len_left = null;
	private JTextField_PositiveInt jTextField_kwic_len_left = null;
	private JTextField_PositiveInt jTextField_kwic_len_right = null;
	private JLabel jLabel_kwic_len_right = null;
	private JCheckBox jCheckBox_sense = null;
	private JPanel jPanel_coding = null;
	private JRadioButton jRadioButton_sjis = null;
	private JRadioButton jRadioButton_euc = null;
	private JRadioButton jRadioButton_jis = null;
	private JRadioButton jRadioButton_utf8 = null;
	private JPanel jPanel_input = null;
	private JPanel jPanel_output = null;
	private JLabel jLabel_padding_c = null;
	private JButton jButton_batch = null;
	private JComboBox jComboBox_keytype = null;
	private JLabel jLabel_title = null;
	private JLabel jLabel_padding_left = null;
	private JLabel jLabel_padding_right = null;
	private JPanel jPanel_op = null;
	private JTextArea jTextArea_msg = null;
	private AnimatedLabel jLabel_anim = null;
	private JScrollPane jScrollPane_msg = null;
	private JLabel jLabel_max_num = null;
	private JTextField_PositiveInt jTextField_max_num = null;
	private JPanel jPanel_max_num = null;
	private JRadioButton jRadioButton_limit = null;
	private JRadioButton jRadioButton_unlimit = null;

	/**
	 * This is the default constructor
	 */
	public BatchView(WindowManager mgr,ViewFrame parent,Conf_Batch_View conf) {
		super();
		this.wm = mgr;
		this.pf = parent;
		this.conf_batch = conf;
		initialize();
	}
	
	// 一括検索を実行する
	private void perform_batch_search(){
		if(this.conf_batch.target_corpus.get() == E_Corpus.MAINITI){
			if(wm.se_mai == null){
				showMsg("エラー:GDAフォルダ(新聞)が正しく設定されていません (メニューバーの「Option」→「設定」で設定できます)",Color.red);
				return;
			}
		}else{
			if(wm.se_iwa == null){
				showMsg("エラー:GDAフォルダ(岩波)が正しく設定されていません (メニューバーの「Option」→「設定」で設定できます)",Color.red);
				return;
			}
		}
		input_filename = jTextField_input.getText().trim();
		String ofile = jTextField_output.getText().trim();
		if(input_filename.equals("")){
			showMsg("エラー:入力ファイルが指定されていません",Color.red);
			return;
		}
		if(ofile.equals("")){
			showMsg("エラー:出力ファイルが指定されていません",Color.red);
			return;
		}
		File out = new File(ofile);
		if(out.isDirectory()){
			showMsg("エラー:出力ファイルと同名のフォルダが存在します",Color.red);
			return;
		}else if(out.isFile()){
			int res = JOptionPane.showConfirmDialog(this,
						"出力ファイルを上書しますか?",
						"CONFIRMATION", JOptionPane.OK_CANCEL_OPTION);
			if(res != JOptionPane.OK_OPTION) return;
		}

		tsv = new TSV_Exporter(ofile,this.conf_batch.target_corpus.get());

		SwingWorker<String,String> worker = new SwingWorker<String,String>() {
			@Override
			public String doInBackground() {
				// バックグラウンドでタブ区切りテキストの出力を行う
				tsv.setPrintHeader(true);
				int output_num = getMaxOutputNum();
				if(isAddSenseFlag()) tsv.enablePrintSenseDesc(wm.iwa_dic);
				tsv.setOutputCoding(getOutputCoding());
				E_KeywordType key_type = getKeywordType();
				int left_len = getLeftKWICLength();
				int right_len = getRightKWICLength();
				try {
					if(conf_batch.target_corpus.get() == E_Corpus.MAINITI){
						if(key_type == E_KeywordType.BASE){
							wm.se_mai.open_base_index();
						}else if(key_type == E_KeywordType.SURFACE){
							wm.se_mai.open_surface_index();
						}else if(key_type == E_KeywordType.FULLTEXT){
							wm.se_mai.open_suffix_array();
						}
						tsv.batch_search(wm.se_mai, input_filename, key_type, output_num, left_len, right_len);
					}else{
						if(key_type == E_KeywordType.BASE){
							wm.se_iwa.open_base_index();
						}else if(key_type == E_KeywordType.SURFACE){
							wm.se_iwa.open_surface_index();
						}else if(key_type == E_KeywordType.FULLTEXT){
							wm.se_iwa.open_suffix_array();
						}
						tsv.batch_search(wm.se_iwa, input_filename, key_type, output_num, left_len, right_len);
					}
				} catch (SE_Exception e) {
					if(e.MsgE.equals("CODE: illegal format")) return(e.MsgJ);
					e.printStackTrace();
					// showMsg(e.MsgJ,Color.red);
					System.err.println(e.MsgE);
					return(e.MsgJ);
				}
				return "OK";
			}
			@Override
            protected void process(java.util.List<String> chunks) {
				// doInBacground() の中で public(X) が実行されたとき、
				// X を受けとって処理を行うメソッド
				// ここでは何もしない
			}
			@Override
			public void done() {
				// doInBackground() の戻値は get() で得る
				try {
					if( get().equals("OK") ){
						//Date current_date = new Date();
						//// DateFormat dfm = DateFormat.getDateInstance();
						//DateFormat dfm = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						//showMsg("一括検索が完了し、結果をファイルに出力しました\n作成日時: "+dfm.format(current_date));
						showMsg("一括検索が完了し、結果をファイルに出力しました。");
					}else{
						showMsg(get(),Color.red);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				jButton_batch.setEnabled(true);
				jLabel_anim.stopAnimation();
			}
		};
		
		showMsg("検索ならびにファイル出力中...");
		jButton_batch.setEnabled(false);
		jLabel_anim.startAnimation();
		worker.execute();
	}	
	/**
	 * 一括検索の設定を記録する
	 */
	void save_parameters(){
		conf_batch.batch_keyword_type.set(getKeywordType());
		conf_batch.batch_max_output_num.set(getMaxOutputNum());
		conf_batch.batch_left_kwic_length.set(getLeftKWICLength());
		conf_batch.batch_right_kwic_length.set(getRightKWICLength());
		conf_batch.batch_add_sense.set(isAddSenseFlag());
		conf_batch.batch_output_coding.set(getOutputCoding());
	}
	/**
	 * 対象コーパスを変更する
	 */
	void change_target_corpus(){
		if(this.conf_batch.target_corpus.get() == E_Corpus.MAINITI){
			if(wm.se_mai == null) wm.prepare_search_engine_MAI(wm.conf.gda_dir_MAI.get(), true, false);
		}else{
			if(wm.se_iwa == null) wm.prepare_search_engine_IWA(wm.conf.gda_dir_IWA.get(), true, false);
		}
	}
	/**
	 * メッセージ表示エリアのテキストを消去するメソッド
	 */
	private void removeMsg() {
		if(jTextArea_msg != null) jTextArea_msg.setText("");
	}

	/**
	 * 画面上で選択されているキーワードタイプを返すメソッド
	 */
	private E_KeywordType getKeywordType(){
		String s = (String)jComboBox_keytype.getSelectedItem();
		for(E_KeywordType kt: E_KeywordType.values()){
			if(s.equals(kt.label())) return kt;
		}
		return null;
	}
	/**
	 * 指定されたキーワードタイプを画面上に反映するメソッド
	 */
	private void setKeywordType(E_KeywordType t){
		jComboBox_keytype.setSelectedItem(t.label());
	}
	/**
	 * 最大出力件数を返すメソッド
	 * 「全て出力する」ときは -1 を返す
	 */
	private int getMaxOutputNum() {
		if(jRadioButton_unlimit.isSelected()){
			return -1;
		}else{
			int num = KWICConfTab.normalized_number_in_TF(jTextField_max_num);
			if(num <= 0){
				jTextField_max_num.setText("0");
				return -1;
			}else{
				return num;
			}
		}
	}
	/**
	 * 指定された最大出力件数を画面上に反映するメソッド
	 */
	private void setMaxOutputNum(int n){
		if(n == -1) {
			jRadioButton_unlimit.setSelected(true);
            //jTextField_max_num.setText("0");
		}else{
			jRadioButton_limit.setSelected(true);
			jTextField_max_num.setText( Integer.toString(n) );
		}
	}
	/**
	 * 左文脈の幅を返すメソッド
	 */
	private int getLeftKWICLength (){
		int num = KWICConfTab.normalized_number_in_TF(jTextField_kwic_len_left);
		return num;
	}
	/**
	 * 左文脈の幅を画面上に反映するメソッド
	 */
	private void setLeftKWICLength (int n){
		jTextField_kwic_len_left.setText( Integer.toString(n) );
	}
	/**
	 * 右文脈の幅を返すメソッド
	 */
	private int getRightKWICLength (){
		int num = KWICConfTab.normalized_number_in_TF(jTextField_kwic_len_right);
		return num;
	}
	/**
	 * 右文脈の幅を画面上に反映するメソッド
	 */
	private void setRightKWICLength (int n){
		jTextField_kwic_len_right.setText( Integer.toString(n) );
	}
	/**
	 * 「語義に語釈文を付与する」のON/OFFを返すメソッド
	 */
	private boolean isAddSenseFlag(){
		return jCheckBox_sense.isSelected();
	}
	/**
	 * 引数として与えられた「語義に語釈文を付与する」のON/OFFを画面に反映させるメソッド
	 */
	private void setAddSenseFlag(boolean b){
		jCheckBox_sense.setSelected(b);
	}
	/**
	 * 出力文字コードを返すメソッド
	 */
	private String getOutputCoding(){
		if(jRadioButton_sjis.isSelected()){
			return "Shift_JIS";
		}else if(jRadioButton_euc.isSelected()){
			return "EUC-JP";
		}else if(jRadioButton_jis.isSelected()){
			return "ISO-2022-JP";
		}else if(jRadioButton_utf8.isSelected()){
			return "UTF-8";
		}else{
			return null;
		}
	}
	/**
	 * 引数として与えられた出力文字コードを画面に反映させるメソッド
	 */
	private void setOutputCoding(String code){
		if(code.equals("Shift_JIS")){
			jRadioButton_sjis.setSelected(true);
		}else if(code.equals("EUC-JP")){
			jRadioButton_sjis.setSelected(true);
		}else if(code.equals("ISO-2022-JP")){
			jRadioButton_jis.setSelected(true);
		}else if(code.equals("UTF-8")){
			jRadioButton_utf8.setSelected(true);
		}
	}
	/**
	 * ダイアログによってテキストフィールドにファイルのパスを埋めるメソッド
	 * @param tf			テキストフィールド
	 * @param title			ダイアログのタイトル
	 * @param dialog_type	ダイアログのタイプ
	 */
	private void fill_form_by_dialog(JTextField tf,String title,int dialog_type){
		/* ToDo FileChooser temporary off */
		int res = -1;
		
		if(file_chooser == null){
			file_chooser = new JFileChooserEx();
			file_chooser.setDialogType(JFileChooser.FILES_ONLY);
			file_chooser.setOverrideFileCheckEnabled(false);
			file_chooser.setOverrideDirectoryCheckEnabled(false);
		}
		file_chooser.setDialogTitle(title);
		if(! tf.getText().trim().equals("")){
			File current_file = new File(tf.getText().trim());
			if(current_file.isFile()){
				file_chooser.setCurrentDirectory( current_file.getAbsoluteFile().getParentFile() );
			}else if(current_file.getAbsoluteFile().getParentFile().isDirectory()){
				file_chooser.setCurrentDirectory( current_file.getAbsoluteFile().getParentFile() );
			}
		}
		file_chooser.reset_selected_file();
		if(dialog_type == JFileChooser.OPEN_DIALOG){
			res = file_chooser.showOpenDialog(this);
		}else if(dialog_type == JFileChooser.SAVE_DIALOG){
			res = file_chooser.showSaveDialog(this);
		}
		if(res == JFileChooser.APPROVE_OPTION){
			tf.setText( file_chooser.getSelectedFile().getAbsolutePath() );
		}
	}
	/**
	 * メッセージ表示エリアにメッセージを表示するメソッド
	 * @param msg		表示テキスト
	 * @param fg_color	文字色
	 */
	private void showMsg(String msg,Color fg_color){
		jTextArea_msg.setText(msg);
		jTextArea_msg.setForeground(fg_color);
	}
	/**
	 * メッセージ表示エリアにメッセージを表示するメソッド
	 * @param msg		表示テキスト
	 */
	private void showMsg(String msg){
		showMsg(msg,Color.black);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gBC_L_kwic_len = new GridBagConstraints();
		gBC_L_kwic_len.gridx = 1;
		gBC_L_kwic_len.insets = new Insets(0, 10, 0, 10);
		gBC_L_kwic_len.anchor = GridBagConstraints.WEST;
		gBC_L_kwic_len.weighty = 2.0D;
		gBC_L_kwic_len.gridy = 9;
		jLabel_kwic_len = new JLabel();
		jLabel_kwic_len.setText("文脈幅");
		GridBagConstraints gBC_P_kwic_len = new GridBagConstraints();
		gBC_P_kwic_len.gridx = 2;
		gBC_P_kwic_len.fill = GridBagConstraints.NONE;
		gBC_P_kwic_len.weightx = 1.0D;
		gBC_P_kwic_len.anchor = GridBagConstraints.WEST;
		gBC_P_kwic_len.gridy = 9;
		GridBagConstraints gBC_P_max_num = new GridBagConstraints();
		gBC_P_max_num.gridx = 2;
		gBC_P_max_num.fill = GridBagConstraints.NONE;
		gBC_P_max_num.weightx = 1.0D;
		gBC_P_max_num.anchor = GridBagConstraints.WEST;
		gBC_P_max_num.gridy = 8;
		GridBagConstraints gBC_L_max_num = new GridBagConstraints();
		gBC_L_max_num.gridx = 1;
		gBC_L_max_num.anchor = GridBagConstraints.WEST;
		gBC_L_max_num.insets = new Insets(0, 10, 0, 10);
		gBC_L_max_num.gridy = 8;
		jLabel_max_num = new JLabel();
		jLabel_max_num.setText("最大出力件数");
		GridBagConstraints gBC_P_op = new GridBagConstraints();
		gBC_P_op.gridx = 1;
		gBC_P_op.gridwidth = GridBagConstraints.RELATIVE;
		gBC_P_op.fill = GridBagConstraints.BOTH;
		gBC_P_op.weighty = 2.0D;
		gBC_P_op.insets = new Insets(0, 0, 10, 0);
		gBC_P_op.gridy = 13;
		GridBagConstraints gBC_CB_keytype = new GridBagConstraints();
		gBC_CB_keytype.fill = GridBagConstraints.NONE;
		gBC_CB_keytype.gridy = 7;
		gBC_CB_keytype.weightx = 0.0D;
		gBC_CB_keytype.anchor = GridBagConstraints.WEST;
		gBC_CB_keytype.gridx = 2;
		GridBagConstraints gBC_L_padding_right = new GridBagConstraints();
		gBC_L_padding_right.gridx = 3;
		gBC_L_padding_right.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_padding_right.weightx = 1.0D;
		gBC_L_padding_right.gridy = 0;
		jLabel_padding_right = new JLabel();
		jLabel_padding_right.setText("");
		jLabel_padding_right.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_L_padding_left = new GridBagConstraints();
		gBC_L_padding_left.gridx = 0;
		gBC_L_padding_left.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_padding_left.weightx = 1.0D;
		gBC_L_padding_left.gridy = 0;
		jLabel_padding_left = new JLabel();
		jLabel_padding_left.setText("");
		jLabel_padding_left.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_L_padding_title = new GridBagConstraints();
		gBC_L_padding_title.gridx = 1;
		gBC_L_padding_title.gridwidth = GridBagConstraints.RELATIVE;
		gBC_L_padding_title.anchor = GridBagConstraints.WEST;
		gBC_L_padding_title.weighty = 6.0D;
		gBC_L_padding_title.weightx = 8.0D;
		gBC_L_padding_title.gridy = 0;
		jLabel_title = new JLabel();
		jLabel_title.setText("複数のキーワードを一括して検索し、タブ区切り形式(TSV)のファイルに出力します");
		jLabel_title.setForeground(new Color(204, 0, 51));
		GridBagConstraints gBC_L_step4 = new GridBagConstraints();
		gBC_L_step4.gridx = 1;
		gBC_L_step4.gridy = 12;
		gBC_L_step4.anchor = GridBagConstraints.WEST;
		gBC_L_step4.insets = new Insets(0, 0, 0, 0);
		gBC_L_step4.weighty = 1.0D;
		gBC_L_step4.gridwidth = GridBagConstraints.REMAINDER;
		jLabel_step4 = new JLabel();
		jLabel_step4.setText("4. 実行ボタンを押して下さい");
		jLabel_step4.setForeground(step_fg_color);
		GridBagConstraints gBC_P_output = new GridBagConstraints();
		gBC_P_output.gridx = 1;
		gBC_P_output.fill = GridBagConstraints.HORIZONTAL;
		gBC_P_output.gridwidth = 2;
		gBC_P_output.weighty = 2.0D;
		gBC_P_output.anchor = GridBagConstraints.NORTHWEST;
		gBC_P_output.gridy = 5;
		GridBagConstraints gBC_P_input = new GridBagConstraints();
		gBC_P_input.gridx = 1;
		gBC_P_input.fill = GridBagConstraints.HORIZONTAL;
		gBC_P_input.gridwidth = 2;
		gBC_P_input.weighty = 2.0D;
		gBC_P_input.anchor = GridBagConstraints.NORTHWEST;
		gBC_P_input.gridy = 3;
		GridBagConstraints gBC_P_coding = new GridBagConstraints();
		gBC_P_coding.gridx = 2;
		gBC_P_coding.fill = GridBagConstraints.NONE;
		gBC_P_coding.anchor = GridBagConstraints.WEST;
		gBC_P_coding.gridy = 11;
		GridBagConstraints gBC_CB_sense = new GridBagConstraints();
		gBC_CB_sense.gridx = 2;
		gBC_CB_sense.anchor = GridBagConstraints.WEST;
		gBC_CB_sense.fill = GridBagConstraints.NONE;
		gBC_CB_sense.weightx = 1.0D;
		gBC_CB_sense.gridy = 10;
		GridBagConstraints gBC_L_sense = new GridBagConstraints();
		gBC_L_sense.gridx = 1;
		gBC_L_sense.anchor = GridBagConstraints.WEST;
		gBC_L_sense.insets = new Insets(0, 10, 0, 10);
		gBC_L_sense.weighty = 1.0D;
		gBC_L_sense.gridy = 10;
		jLabel_sense = new JLabel();
		jLabel_sense.setText("語義");
		GridBagConstraints gBC_L_coding = new GridBagConstraints();
		gBC_L_coding.gridx = 1;
		gBC_L_coding.anchor = GridBagConstraints.WEST;
		gBC_L_coding.insets = new Insets(0, 10, 0, 10);
		gBC_L_coding.weighty = 1.0D;
		gBC_L_coding.gridy = 11;
		jLabel_coding = new JLabel();
		jLabel_coding.setText("文字コード");
		GridBagConstraints gBC_L_keyword = new GridBagConstraints();
		gBC_L_keyword.gridx = 1;
		gBC_L_keyword.anchor = GridBagConstraints.WEST;
		gBC_L_keyword.insets = new Insets(0, 10, 0, 10);
		gBC_L_keyword.weighty = 2.0D;
		gBC_L_keyword.gridy = 7;
		jLabel_keytype = new JLabel();
		jLabel_keytype.setText("キーワード種別");
		GridBagConstraints gBC_L_step3 = new GridBagConstraints();
		gBC_L_step3.gridx = 1;
		gBC_L_step3.gridy = 6;
		gBC_L_step3.anchor = GridBagConstraints.WEST;
		gBC_L_step3.insets = new Insets(00, 0, 0, 0);
		gBC_L_step3.weighty = 1.0D;
		gBC_L_step3.gridwidth = 2;
		jLabel_step3 = new JLabel();
		jLabel_step3.setText("3. 以下のオプションを設定して下さい");
		jLabel_step3.setForeground(step_fg_color);
		GridBagConstraints gBC_L_step2 = new GridBagConstraints();
		gBC_L_step2.gridx = 1;
		gBC_L_step2.gridy = 4;
		gBC_L_step2.anchor = GridBagConstraints.NORTHWEST;
		gBC_L_step2.insets = new Insets(0, 0, 0, 0);
		gBC_L_step2.weighty = 1.0D;
		gBC_L_step2.gridwidth = 2;
		jLabel_step2 = new JLabel();
		jLabel_step2.setText("2. 出力ファイルを指定して下さい");
		jLabel_step2.setForeground(step_fg_color);
		GridBagConstraints gBC_L_step1 = new GridBagConstraints();
		gBC_L_step1.gridx = 1;
		gBC_L_step1.gridy = 1;
		gBC_L_step1.anchor = GridBagConstraints.NORTHWEST;
		gBC_L_step1.insets = new Insets(0, 0, 0, 0);
		gBC_L_step1.weighty = 1.0D;
		gBC_L_step1.gridwidth = 2;
		jLabel_step1 = new JLabel();
		jLabel_step1.setText("1. 入力ファイルを指定して下さい");
		jLabel_step1.setToolTipText("入力ファイルのフォーマットについてはヘルプを御覧下さい(ダブルクリックでヘルプを表示)");
		jLabel_step1.setHorizontalTextPosition(SwingConstants.LEADING);
		jLabel_step1.setForeground(step_fg_color);
		//jLabel_step1.setIcon(new ImageIcon("icon/comment_yellow.gif"));
		URL url = this.getClass().getResource("icon/comment_yellow.gif");
		if(url != null) jLabel_step1.setIcon(new ImageIcon(url));
		jLabel_step1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				// ここでは batch_view.html#input_format のように#付きのURIを与えて、
				// 入力フォーマットの説明が書いてある場所を直接表示したい。
				// * コマンドラインでURIを指定しても、#以降は無視される。
				// * Desktopを使うと正常に表示される。
				//   ただし、Windowsでは無視される。
				//   Windows の Firefox で#つきのURIを直接与えるとちゃんと表示されるので、
				//   Java側の問題と思われる。
				
				// ここでは Windows の場合は、#以降を無視し、設定ビューで指定したブラウザで表示する。
				// それ以外は、#が有効になるように Desktop を使ってデフォルトのブラウザで表示する。
				
				if(e.getClickCount() == 2){
					File help_file = new File("manual"+File.separator+"batch_view.html");
					if(! help_file.isFile()){
						wm.show_fatal_error_message_in_dialog("ヘルブファイル "+help_file.getPath()+" が存在しません", null);
						return;
					}
					if(SYSTEM_OS == E_OS.Windows){
						try {
							wm.web_browser.show_html_file(help_file, wm.conf);
						} catch (SE_Exception e1) {
							wm.show_exception_message_in_dialog(e1,null);
						}
					}else{
						URI help_uri = null;
						try {
							help_uri = new URI("file://"+help_file.toURI().getRawPath()+"#input_format");
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
							help_uri = help_file.toURI();
						}
						try {
							boolean flag = wm.web_browser.show_URI_by_desktop_web_browser(help_uri);
							if(!flag) wm.web_browser.show_html_file(help_file,wm.conf);
						} catch (SE_Exception e1) {
							wm.show_exception_message_in_dialog(e1,null);
						}
					}
				}
			}
		});
		this.setSize(750, 500);
		this.setLayout(new GridBagLayout());
		this.add(jLabel_padding_left, gBC_L_padding_left);
		this.add(jLabel_padding_right, gBC_L_padding_right);
		this.add(jLabel_title, gBC_L_padding_title);
		this.add(jLabel_step1, gBC_L_step1);
		this.add(getJPanel_input(), gBC_P_input);
		this.add(jLabel_step2, gBC_L_step2);
		this.add(getJPanel_output(), gBC_P_output);
		this.add(jLabel_step3, gBC_L_step3);
		this.add(jLabel_keytype, gBC_L_keyword);
		this.add(getJComboBox_keytype(), gBC_CB_keytype);
		this.add(jLabel_max_num, gBC_L_max_num);
		this.add(getJPanel_max_num(), gBC_P_max_num);
		this.add(jLabel_kwic_len, gBC_L_kwic_len);
		this.add(getJPanel_kwic_len(), gBC_P_kwic_len);
		this.add(jLabel_sense, gBC_L_sense);
		this.add(getJCheckBox_sense(), gBC_CB_sense);
		this.add(jLabel_coding, gBC_L_coding);
		this.add(getJPanel_coding(), gBC_P_coding);
		this.add(jLabel_step4, gBC_L_step4);
		this.add(getJPanel_op(), gBC_P_op);
		
		ButtonGroup max_output_num_group = new ButtonGroup();
		max_output_num_group.add(jRadioButton_limit);
		max_output_num_group.add(jRadioButton_unlimit);
		ButtonGroup coding_group = new ButtonGroup();
		coding_group.add(jRadioButton_sjis);
		coding_group.add(jRadioButton_euc);
		coding_group.add(jRadioButton_jis);
		coding_group.add(jRadioButton_utf8);
		
		setKeywordType(conf_batch.batch_keyword_type.get());
		setMaxOutputNum(conf_batch.batch_max_output_num.get());
		setLeftKWICLength(conf_batch.batch_left_kwic_length.get());
		setRightKWICLength(conf_batch.batch_right_kwic_length.get());
		setAddSenseFlag(conf_batch.batch_add_sense.get());
		setOutputCoding(conf_batch.batch_output_coding.get());
		
		if(SYSTEM_CORPUS == E_Corpus.MAINITI){
			jLabel_sense.setForeground(Color.gray);
			jCheckBox_sense.setEnabled(false);
		}

		change_target_corpus();
	}

	/**
	 * This method initializes jTextField_input	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_input() {
		if (jTextField_input == null) {
			jTextField_input = new JTextField();
			jTextField_input.setPreferredSize(new Dimension(80, 28));
			jTextField_input.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusGained(java.awt.event.FocusEvent e) {
					removeMsg();
				}
			});
		}
		return jTextField_input;
	}

	/**
	 * This method initializes jButton_input	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_input() {
		if (jButton_input == null) {
			jButton_input = new JButton();
			jButton_input.setText("選択");
			jButton_input.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeMsg();
					fill_form_by_dialog(jTextField_input,
							"入力ファイルを指定して下さい",
							JFileChooser.OPEN_DIALOG);
				}
			});
		}
		return jButton_input;
	}

	/**
	 * This method initializes jTextField_output	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_output() {
		if (jTextField_output == null) {
			jTextField_output = new JTextField();
			jTextField_output.setPreferredSize(new Dimension(80, 28));
			jTextField_output.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusGained(java.awt.event.FocusEvent e) {
					removeMsg();
				}
			});
		}
		return jTextField_output;
	}

	/**
	 * This method initializes jButton_output	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_output() {
		if (jButton_output == null) {
			jButton_output = new JButton();
			jButton_output.setText("選択");
			jButton_output.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeMsg();
					fill_form_by_dialog(jTextField_output,
							"出力ファイルを指定して下さい",
							JFileChooser.SAVE_DIALOG);
				}
			});
		}
		return jButton_output;
	}

	/**
	 * This method initializes jCheckBox_sense	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox_sense() {
		if (jCheckBox_sense == null) {
			jCheckBox_sense = new JCheckBox();
			jCheckBox_sense.setText("語義IDに語釈文を付与する");
			jCheckBox_sense.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					removeMsg();
				}
			});
		}
		return jCheckBox_sense;
	}

	/**
	 * This method initializes jPanel_coding	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_coding() {
		if (jPanel_coding == null) {
			GridBagConstraints gBC_L_padding_c = new GridBagConstraints();
			gBC_L_padding_c.gridx = 4;
			gBC_L_padding_c.weightx = 1.0D;
			gBC_L_padding_c.gridy = 0;
			jLabel_padding_c = new JLabel();
			jLabel_padding_c.setText(" ");
			GridBagConstraints gBC_RB_sjis = new GridBagConstraints();
			gBC_RB_sjis.gridx = 0;
			gBC_RB_sjis.anchor = GridBagConstraints.WEST;
			gBC_RB_sjis.insets = new Insets(0, 0, 0, 10);
			gBC_RB_sjis.gridy = 0;
			GridBagConstraints gBC_RB_utf8 = new GridBagConstraints();
			gBC_RB_utf8.gridx = 3;
			gBC_RB_utf8.anchor = GridBagConstraints.WEST;
			gBC_RB_utf8.gridy = 0;
			GridBagConstraints gBC_RB_jis = new GridBagConstraints();
			gBC_RB_jis.gridx = 2;
			gBC_RB_jis.anchor = GridBagConstraints.WEST;
			gBC_RB_jis.insets = new Insets(0, 0, 0, 10);
			gBC_RB_jis.gridy = 0;
			GridBagConstraints gBC_RB_euc = new GridBagConstraints();
			gBC_RB_euc.gridx = 1;
			gBC_RB_euc.anchor = GridBagConstraints.WEST;
			gBC_RB_euc.insets = new Insets(0, 0, 0, 10);
			gBC_RB_euc.gridy = 0;
			jPanel_coding = new JPanel();
			jPanel_coding.setLayout(new GridBagLayout());
			jPanel_coding.add(getJRadioButton_sjis(), gBC_RB_sjis);
			jPanel_coding.add(getJRadioButton_euc(), gBC_RB_euc);
			jPanel_coding.add(getJRadioButton_jis(), gBC_RB_jis);
			jPanel_coding.add(getJRadioButton_utf8(), gBC_RB_utf8);
			jPanel_coding.add(jLabel_padding_c, gBC_L_padding_c);
		}
		return jPanel_coding;
	}

	/**
	 * This method initializes jRadioButton_sjis	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_sjis() {
		if (jRadioButton_sjis == null) {
			jRadioButton_sjis = new JRadioButton();
			jRadioButton_sjis.setText("Shift JIS");
			jRadioButton_sjis.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeMsg();
				}
			});
		}
		return jRadioButton_sjis;
	}

	/**
	 * This method initializes jRadioButton_euc	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_euc() {
		if (jRadioButton_euc == null) {
			jRadioButton_euc = new JRadioButton();
			jRadioButton_euc.setText("EUC");
			jRadioButton_euc.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeMsg();
				}
			});
		}
		return jRadioButton_euc;
	}

	/**
	 * This method initializes jRadioButton_jis	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_jis() {
		if (jRadioButton_jis == null) {
			jRadioButton_jis = new JRadioButton();
			jRadioButton_jis.setText("JIS");
			jRadioButton_jis.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeMsg();
				}
			});
		}
		return jRadioButton_jis;
	}

	/**
	 * This method initializes jRadioButton_utf8	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_utf8() {
		if (jRadioButton_utf8 == null) {
			jRadioButton_utf8 = new JRadioButton();
			jRadioButton_utf8.setText("UTF-8");
			jRadioButton_utf8.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeMsg();
				}
			});
		}
		return jRadioButton_utf8;
	}

	/**
	 * This method initializes jPanel_input	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_input() {
		if (jPanel_input == null) {
			GridBagConstraints gBC_B_input = new GridBagConstraints();
			gBC_B_input.gridx = 0;
			gBC_B_input.insets = new Insets(0, 5, 0, 5);
			gBC_B_input.gridy = 0;
			GridBagConstraints gBC_TF_input = new GridBagConstraints();
			gBC_TF_input.fill = GridBagConstraints.HORIZONTAL;
			gBC_TF_input.gridy = 0;
			gBC_TF_input.weightx = 1.0;
			gBC_TF_input.insets = new Insets(0, 5, 0, 5);
			gBC_TF_input.gridx = 1;
			jPanel_input = new JPanel();
			jPanel_input.setLayout(new GridBagLayout());
			jPanel_input.add(getJButton_input(), gBC_B_input);
			jPanel_input.add(getJTextField_input(), gBC_TF_input);
		}
		return jPanel_input;
	}

	/**
	 * This method initializes jPanel_output	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_output() {
		if (jPanel_output == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 9;
			GridBagConstraints gBC_B_output = new GridBagConstraints();
			gBC_B_output.gridx = 0;
			gBC_B_output.insets = new Insets(0, 5, 0, 5);
			gBC_B_output.gridy = 0;
			GridBagConstraints gBC_TF_output = new GridBagConstraints();
			gBC_TF_output.fill = GridBagConstraints.HORIZONTAL;
			gBC_TF_output.gridy = 0;
			gBC_TF_output.weightx = 1.0;
			gBC_TF_output.insets = new Insets(0, 5, 0, 5);
			gBC_TF_output.gridx = 1;
			jPanel_output = new JPanel();
			jPanel_output.setLayout(new GridBagLayout());
			jPanel_output.add(getJButton_output(), gBC_B_output);
			jPanel_output.add(getJTextField_output(), gBC_TF_output);
		}
		return jPanel_output;
	}

	/**
	 * This method initializes jButton_batch	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_batch() {
		if (jButton_batch == null) {
			jButton_batch = new JButtonHL();
			jButton_batch.setText("実行");
			jButton_batch.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeMsg();
					perform_batch_search();
				}
			});
		}
		return jButton_batch;
	}

	/**
	 * This method initializes jComboBox_keytype	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox_keytype() {
		if (jComboBox_keytype == null) {
			jComboBox_keytype = new JComboBox();
			jComboBox_keytype.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeMsg();
				}
			});
			jComboBox_keytype.addItem(E_KeywordType.FULLTEXT.label());
			jComboBox_keytype.addItem(E_KeywordType.BASE.label());
			jComboBox_keytype.addItem(E_KeywordType.SURFACE.label());
		}
		return jComboBox_keytype;
	}

	/**
	 * This method initializes jPanel_op	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_op() {
		if (jPanel_op == null) {
			GridBagConstraints gBC_SP_msg = new GridBagConstraints();
			gBC_SP_msg.fill = GridBagConstraints.BOTH;
			gBC_SP_msg.gridy = 0;
			gBC_SP_msg.weightx = 1.0;
			gBC_SP_msg.weighty = 0.0D;
			gBC_SP_msg.insets = new Insets(0, 0, 0, 5);
			gBC_SP_msg.gridx = 2;
			GridBagConstraints gBC_L_anim = new GridBagConstraints();
			gBC_L_anim.gridx = 1;
			gBC_L_anim.insets = new Insets(0, 0, 0, 10);
			gBC_L_anim.gridy = 0;
			jLabel_anim = new AnimatedLabel();
			GridBagConstraints gBC_B_batch = new GridBagConstraints();
			gBC_B_batch.anchor = GridBagConstraints.CENTER;
			gBC_B_batch.gridx = 0;
			gBC_B_batch.insets = new Insets(0, 10, 0, 10);
			gBC_B_batch.gridy = 0;
			jPanel_op = new JPanel();
			jPanel_op.setLayout(new GridBagLayout());
			jPanel_op.setPreferredSize(new Dimension(200, 46));
			jPanel_op.add(getJButton_batch(), gBC_B_batch);
			jPanel_op.add(jLabel_anim, gBC_L_anim);
			jPanel_op.add(getJScrollPane_msg(), gBC_SP_msg);
		}
		return jPanel_op;
	}

	/**
	 * This method initializes jTextArea_msg	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea_msg() {
		if (jTextArea_msg == null) {
			jTextArea_msg = new JTextArea();
			jTextArea_msg.setToolTipText("メッセージ表示エリア");
			jTextArea_msg.setEditable(false);
			jTextArea_msg.setText("");
			jTextArea_msg.setLineWrap(true);
			jTextArea_msg.setBackground(ViewFrame.bg_color_of_message_area);
		}
		return jTextArea_msg;
	}

	/**
	 * This method initializes jScrollPane_msg	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane_msg() {
		if (jScrollPane_msg == null) {
			jScrollPane_msg = new JScrollPane();
			jScrollPane_msg.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane_msg.setPreferredSize(new Dimension(50, 46));
			jScrollPane_msg.setViewportView(getJTextArea_msg());
		}
		return jScrollPane_msg;
	}

	/**
	 * This method initializes jTextField_max_num	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField_PositiveInt getJTextField_max_num() {
		if (jTextField_max_num == null) {
			jTextField_max_num = new JTextField_PositiveInt();
			jTextField_max_num.setPreferredSize(new Dimension(40, 28));
			jTextField_max_num.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextField_max_num.restore_value_if_invalid(1);
				}
			});
			jTextField_max_num.addFocusListener(new java.awt.event.FocusAdapter() {   
				public void focusGained(java.awt.event.FocusEvent e) {    
					jRadioButton_limit.setSelected(true);
				}
				public void focusLost(java.awt.event.FocusEvent e) {
					jTextField_max_num.restore_value_if_invalid(1);
				}
			});
		}
		return jTextField_max_num;
	}

	/**
	 * This method initializes jPanel_max_num	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_max_num() {
		if (jPanel_max_num == null) {
			GridBagConstraints gBC_RB_unlimit = new GridBagConstraints();
			gBC_RB_unlimit.gridx = 2;
			gBC_RB_unlimit.insets = new Insets(0, 10, 0, 0);
			gBC_RB_unlimit.gridy = 0;
			GridBagConstraints gBC_RB_limit = new GridBagConstraints();
			gBC_RB_limit.gridx = 0;
			gBC_RB_limit.gridy = 0;
			GridBagConstraints gBC_TF_max_num = new GridBagConstraints();
			gBC_TF_max_num.anchor = GridBagConstraints.WEST;
			gBC_TF_max_num.gridx = 1;
			gBC_TF_max_num.gridy = 0;
			gBC_TF_max_num.fill = GridBagConstraints.NONE;
			jPanel_max_num = new JPanel();
			jPanel_max_num.setLayout(new GridBagLayout());
			jPanel_max_num.add(getJTextField_max_num(), gBC_TF_max_num);
			jPanel_max_num.add(getJRadioButton_limit(), gBC_RB_limit);
			jPanel_max_num.add(getJRadioButton_unlimit(), gBC_RB_unlimit);
		}
		return jPanel_max_num;
	}

	/**
	 * This method initializes jRadioButton_limit	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_limit() {
		if (jRadioButton_limit == null) {
			jRadioButton_limit = new JRadioButton();
		}
		return jRadioButton_limit;
	}

	/**
	 * This method initializes jRadioButton_unlimit	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_unlimit() {
		if (jRadioButton_unlimit == null) {
			jRadioButton_unlimit = new JRadioButton();
			jRadioButton_unlimit.setText("制限なし");
		}
		return jRadioButton_unlimit;
	}
	
	/**
	 * This method initializes jPanel_kwic_len	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_kwic_len(){
		GridBagConstraints gBC_TF_kwic_right_len = new GridBagConstraints();
		gBC_TF_kwic_right_len.fill = GridBagConstraints.VERTICAL;
		gBC_TF_kwic_right_len.gridy = 0;
		gBC_TF_kwic_right_len.weightx = 1.0;
		gBC_TF_kwic_right_len.insets = new Insets(0, 10, 0, 10);
		gBC_TF_kwic_right_len.gridx = 3;
		GridBagConstraints gBC_L_kwic_len_right = new GridBagConstraints();
		gBC_L_kwic_len_right.gridx = 2;
		gBC_L_kwic_len_right.insets = new Insets(0, 20, 0, 0);
		gBC_L_kwic_len_right.gridy = 0;
		jLabel_kwic_len_right = new JLabel();
		jLabel_kwic_len_right.setText("右");
		GridBagConstraints gBC_TF_kwic_len_left = new GridBagConstraints();
		gBC_TF_kwic_len_left.fill = GridBagConstraints.VERTICAL;
		gBC_TF_kwic_len_left.gridy = 0;
		gBC_TF_kwic_len_left.weightx = 1.0;
		gBC_TF_kwic_len_left.insets = new Insets(0, 10, 0, 10);
		gBC_TF_kwic_len_left.gridx = 1;
		GridBagConstraints gBC_L_kwic_len_left = new GridBagConstraints();
		gBC_L_kwic_len_left.gridx = 0;
		gBC_L_kwic_len_left.weightx = 1.0D;
		gBC_L_kwic_len_left.gridy = 0;
		jLabel_kwic_len_left = new JLabel();
		jLabel_kwic_len_left.setText("左");
		if (jPanel_kwic_len == null) {
			jPanel_kwic_len = new JPanel();
			jPanel_kwic_len.setLayout(new GridBagLayout());
			jPanel_kwic_len.add(jLabel_kwic_len_left, gBC_L_kwic_len_left);
			jPanel_kwic_len.add(getJTextField_kwic_len_left(), gBC_TF_kwic_len_left);
			jPanel_kwic_len.add(jLabel_kwic_len_right, gBC_L_kwic_len_right);
			jPanel_kwic_len.add(getJTextField_kwic_len_right(), gBC_TF_kwic_right_len);
		}
		return jPanel_kwic_len;
	}

	/**
	 * This method initializes jTextField_kwic_len_left
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField_PositiveInt getJTextField_kwic_len_left() {
		if (jTextField_kwic_len_left == null) {
			jTextField_kwic_len_left = new JTextField_PositiveInt();
			jTextField_kwic_len_left.setPreferredSize(new Dimension(40, 28));
			jTextField_kwic_len_left.setToolTipText("左文脈の幅(文字数)を指定します");
			jTextField_kwic_len_left.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextField_kwic_len_left.restore_value_if_invalid(1);
				}
			});
			jTextField_kwic_len_left.addFocusListener(new java.awt.event.FocusAdapter() {   
				public void focusLost(java.awt.event.FocusEvent e) {
					jTextField_kwic_len_left.restore_value_if_invalid(1);
				}
			});
		}
		return jTextField_kwic_len_left;
	}

	/**
	 * This method initializes jTextField_kwic_len_left
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField_PositiveInt getJTextField_kwic_len_right() {
		if (jTextField_kwic_len_right == null) {
			jTextField_kwic_len_right = new JTextField_PositiveInt();
			jTextField_kwic_len_right.setPreferredSize(new Dimension(40, 28));
			jTextField_kwic_len_right.setToolTipText("右文脈の幅(文字数)を指定します");
			jTextField_kwic_len_right.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextField_kwic_len_right.restore_value_if_invalid(1);
				}
			});
			jTextField_kwic_len_right.addFocusListener(new java.awt.event.FocusAdapter() {   
				public void focusLost(java.awt.event.FocusEvent e) {
					jTextField_kwic_len_right.restore_value_if_invalid(1);
				}
			});

		}
		return jTextField_kwic_len_right;
	}
}
