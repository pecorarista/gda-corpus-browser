package jp.or.gsk.gdacb.gui;

import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.E_MD_KeywordType;
import jp.or.gsk.gdacb.gui.parts.*;
import jp.or.gsk.gdacb.search_engine.*;

import java.util.Iterator;
import java.util.TreeSet;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

class BrowseView extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final String[] column_metadata_MAI = { "ファイルID", "記事タイトル" }; 
	private static final String[] column_metadata_IWA = { "ファイルID", "見出し" };

	private WindowManager wm = null;
	private ViewFrame pf = null;	// 親のフレーム
	Conf_Browse_View conf_browse = null;
	
	private TreeSet<Integer> hit_fileID_set = null;
	
	private JLabel jLabel_key = null;
	private JTextField jTextField_key = null;
	private JButton jButton_search = null;
	private JScrollPane jScrollPane_metadata = null;
	private JTable jTable_metadata = null;
	private JComboBox jComboBox_key_type = null;
	private JButtonHL jButton_browse = null;
	private JPanel jPanel_table = null;
	private JLabel jLabel_hit_num_msg = null;
	private JLabel jLabel_hit_num = null;
	private JLabel jLabel_padding_hn = null;
	private JPanel jPanel_search = null;
	private JLabel jLabel_padding_left = null;
	private JLabel jLabel_padding_right = null;
	private JLabel jLabel_padding = null;
	private JCheckBox jCheckBox_show_all = null;

	/**
	 * This is the default constructor
	 */
	public BrowseView(WindowManager mgr,ViewFrame parent,Conf_Browse_View conf){
		super();
		this.wm = mgr;
		this.pf = parent;
		this.conf_browse = conf;
		initialize();
	}
	/**
	 * 設定を保存するメソッド
	 */
	void save_parameters() {
		String key_str = (String)jComboBox_key_type.getSelectedItem();
		for(E_MD_KeywordType kt: E_MD_KeywordType.values()){
			if(this.conf_browse.target_corpus.get() == kt.corpus() && key_str.equals(kt.label())){
				if(this.conf_browse.target_corpus.get() == E_Corpus.MAINITI){
					conf_browse.metadata_keyword_type_MAI.set(kt);
				}else{
					conf_browse.metadata_keyword_type_IWA.set(kt);
				}
			}
		}
	}
	/**
	 * 検索対象となるコーパスを変更したときに以下を実行するメソッド
	 *   (変更前のコーパスに関する)設定項目を保存
	 *   キーワードタイプを選択するComboBoxの更新
	 *   メタデータ検索用のSuffix Arrayのセットアップ
	 *   テーブルの初期化 
	 */
	void change_target_corpus (){
		hit_fileID_set = null;
		// キーワードタイプを選択するComboBoxのセットアップ
		setup_jcombobox_key_type();
		// キーワード入力フィールドのリセット
		jTextField_key.setText("");
		// 全ファイルを表示するチェックボックスのリセット
		jCheckBox_show_all.setSelected(false);

		if(this.conf_browse.target_corpus.get() == E_Corpus.MAINITI){
			if(wm.se_mai == null) wm.prepare_search_engine_MAI(wm.conf.gda_dir_MAI.get(), true, false);
			wm.prepare_metadata_suffix_array_MAI(false);
		}else{
			if(wm.se_iwa == null) wm.prepare_search_engine_IWA(wm.conf.gda_dir_IWA.get(), true, false);
			wm.prepare_metadata_suffix_array_IWA(false);
		}
		update_metadata_table();
	}
	/**
	 * キーワードタイプを選択するComboBoxの項目を対象コーパスに合わせて更新するメソッド
	 */
	private void setup_jcombobox_key_type() {
		if(jComboBox_key_type.getItemCount() >= 1){
			save_parameters();
			jComboBox_key_type.removeAllItems();
		}
		for(E_MD_KeywordType kt: E_MD_KeywordType.values()){
			if(this.conf_browse.target_corpus.get() == kt.corpus()) jComboBox_key_type.addItem(kt.label());
		}
		if(this.conf_browse.target_corpus.get() == E_Corpus.MAINITI){
			if(conf_browse.metadata_keyword_type_MAI.get() != E_MD_KeywordType.UNDEF)
				jComboBox_key_type.setSelectedItem(conf_browse.metadata_keyword_type_MAI.get().label());
		}else{
			if(conf_browse.metadata_keyword_type_IWA.get() != E_MD_KeywordType.UNDEF)
				jComboBox_key_type.setSelectedItem(conf_browse.metadata_keyword_type_IWA.get().label());
		}
	}
	
	/**
	 * メタデータを検索するメソッド
	 * 結果を this.hit_fileID_set に格納する
	 */
	private void search_metadata() {
		jCheckBox_show_all.setSelected(false);
		String key = jTextField_key.getText().trim();
		if(key.equals("")){
			hit_fileID_set = null;
			update_metadata_table();
			return;
		}
		// ↓マージしたテキストファイル(*.mer)が存在しないときは作成を促すメッセージを表示する
		if(this.conf_browse.target_corpus.get() == E_Corpus.MAINITI){
			wm.prepare_metadata_suffix_array_MAI(true);
		}else{
			wm.prepare_metadata_suffix_array_IWA(true);
		}
		if(wm.sa_md == null){
			hit_fileID_set = null;
		}else{
			try {
				if(this.conf_browse.target_corpus.get() == E_Corpus.MAINITI){
					if(jComboBox_key_type.getSelectedItem().equals( E_MD_KeywordType.ART_TITLE.label() )){
						wm.sa_md_mai.consult(key);
						hit_fileID_set = wm.sa_md_mai.fileID_set();
					}else if(jComboBox_key_type.getSelectedItem().equals( E_MD_KeywordType.ART_ID.label() )){
						wm.sa_md_mai.consult(key);
						hit_fileID_set = wm.sa_md_mai.fileID_set_entryID_exact_match();
					}else{
						hit_fileID_set = null;
					}
				}else{
					if(jComboBox_key_type.getSelectedItem().equals( E_MD_KeywordType.HEADWORD.label() )){
						wm.sa_md_iwa.consult(key);
						hit_fileID_set = wm.sa_md_iwa.fileID_set();
					}else if(jComboBox_key_type.getSelectedItem().equals( E_MD_KeywordType.HEADWORD_EXACT.label() )){
						wm.sa_md_iwa.consult(key);
						hit_fileID_set = wm.sa_md_iwa.fileID_set_midasi_exact_match();
					}else if(jComboBox_key_type.getSelectedItem().equals( E_MD_KeywordType.SENSE_ID.label() )){
						hit_fileID_set = get_fileID_set_from_sem(key);
					}else{
						hit_fileID_set = null;
					}
				}
				update_metadata_table();
			} catch (SE_Exception e) {
				System.err.println(e.MsgE);
				hit_fileID_set = null;
			}
		}
	}
	/**
	 * this.hit_fileID_set の内容をテーブルに表示するメソッド
	 */
	private void update_metadata_table() {
		String line = null;
		DefaultTableModel tm = new DefaultTableModel((this.conf_browse.target_corpus.get() == E_Corpus.MAINITI) ? column_metadata_MAI : column_metadata_IWA, 0);
		if(hit_fileID_set != null){
			for(Integer id: hit_fileID_set){
				try {
					line = wm.sa_md.extract_metadata(id);
				} catch (SE_Exception e) {
					//e.printStackTrace();
					System.err.println("ERROR: fail to extract metadata for file ID "+id);
				}
				tm.addRow( line.split("\t") );
			}
		}
		jTable_metadata.setModel(tm);
		jTable_metadata.changeSelection(0, 0, false, false);
		// 第1カラム(ファイルID)幅を決める
		// フォントサイズの変更に対応するため、ヘッダの表示幅+20に設定する
		TableColumnModel tcm = jTable_metadata.getColumnModel();
		// ヘッダの幅を求める
		TableCellRenderer ren = tcm.getColumn(0).getHeaderRenderer();
        if(ren == null) ren = jTable_metadata.getTableHeader().getDefaultRenderer();
        Component com = ren.getTableCellRendererComponent(jTable_metadata, tcm.getColumn(0).getHeaderValue(), false, false, -1, 0);
        int w = com.getPreferredSize().width + 20;
        tcm.getColumn(0).setPreferredWidth(w);
        tcm.getColumn(0).setMaxWidth(w);
        
        show_hit_number();
    }
	/**
	 * ウェブブラウザでGDAファイルを表示するメソッド
	 */
	private void show_gda_file (){
		String file_path;

		if(jTable_metadata.getRowCount() <= 0) return;
		int row = jTable_metadata.getSelectedRow();
		if(row == -1) return;
		
		int file_id = get_file_ID(row);
		if(this.conf_browse.target_corpus.get() == E_Corpus.MAINITI){
			file_path = (wm.se_mai == null) ? null : wm.se_mai.file_path(file_id);
		}else{
			file_path = (wm.se_iwa == null) ? null : wm.se_iwa.file_path(file_id);
		}
		if(file_path == null){
			wm.show_error_message_in_dialog("該当のGDAファイルが見つかりません",pf);
			System.err.println("ERROR: fail to obtain path to GDA file");
		}else{
			wm.open_GDA_file_by_web_browser(this.conf_browse.target_corpus.get(),file_path,-1,-1,pf);
		}
	}
	/**
	 * 表内で選択されている行に該当するファイルのIDを返すメソッド
	 * @param row	表で選択されている行
	 * @return		ファイルID
	 */
	private int get_file_ID(int row){
		if(hit_fileID_set == null) return -1;
		int count = 0;
		int id;
		for(Iterator<Integer> it = hit_fileID_set.iterator() ; it.hasNext() ; ){
			id = it.next();
			if(row == count) return id;
			count++;
		}
		return -1;
	}
	/**
	 * ヒット件数を画面に表示するメソッド
	 */
	private void show_hit_number() {
		String num;
		if(hit_fileID_set == null){
			num = "0";
		}else{
			num = Integer.toString(hit_fileID_set.size());
		}
		jLabel_hit_num.setText(num);
	}
	/**
	 * SEM属性の値から、岩波の語義に該当するファイルIDの集合を返すメソッド
	 * @param sem	SEM属性の値(iwa:で始まる語義IDやjpn:などを半角空白で区切った文字列)
	 * @return		ファイルIDのTreeSet
	 */
	private TreeSet<Integer> get_fileID_set_from_sem(String sem){
		String[] sem_list;
		String entry_id_str;
		TreeSet<Integer> entry_id = null;
		TreeSet<Integer> fileID_set = new TreeSet<Integer>(); 

		if(sem.contains(" ")){
			sem_list = sem.split(" ");
		}else{
			sem_list = new String[1];
			sem_list[0] = sem;
		}
		for(String s: sem_list){
			entry_id_str = convSEMELMtoENTRYID(s);
			if(entry_id_str == null) continue;
			try {
				wm.sa_md_iwa.consult(entry_id_str);
				entry_id = wm.sa_md_iwa.fileID_set_entryID_exact_match();
			} catch (SE_Exception e) {
				e.printStackTrace();
			}
			if(entry_id == null || entry_id.size() == 0) continue;
			fileID_set.add( entry_id.first() );
		}
		return fileID_set;
	}
	/**
	 * 語義ID(iwa:A.B.C)から見出しID(A)に相当する部分を抽出するメソッド
	 * @param sem_elm	語義ID
	 * @return			見出しID
	 */
	private String convSEMELMtoENTRYID(String sem_elm) {
		int s;
		if(IwanamiDic.isIwanamiSenseID(sem_elm)){
			s = 4;
		}else{
			s = 0;
		}
		int	p = sem_elm.indexOf(".");
		if(p == -1) p = sem_elm.length();
		return sem_elm.substring(s,p);
	}	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.weighty = 1.0D;
		gridBagConstraints1.gridy = 1;
		GridBagConstraints gBC_L_padding_right = new GridBagConstraints();
		gBC_L_padding_right.gridx = 2;
		gBC_L_padding_right.weightx = 1.0D;
		gBC_L_padding_right.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_padding_right.gridy = 0;
		jLabel_padding_right = new JLabel();
		jLabel_padding_right.setText("");
		jLabel_padding_right.setPreferredSize(new Dimension(1, 16));
		GridBagConstraints gGC_L_padding_left = new GridBagConstraints();
		gGC_L_padding_left.gridx = 0;
		gGC_L_padding_left.weightx = 1.0D;
		gGC_L_padding_left.gridheight = GridBagConstraints.REMAINDER;
		gGC_L_padding_left.gridy = 0;
		jLabel_padding_left = new JLabel();
		jLabel_padding_left.setText("");
		jLabel_padding_left.setPreferredSize(new Dimension(1, 16));
		GridBagConstraints gBC_P_search = new GridBagConstraints();
		gBC_P_search.gridx = 1;
		gBC_P_search.weightx = 10.0D;
		gBC_P_search.fill = GridBagConstraints.BOTH;
		gBC_P_search.weighty = 4.0D;
		gBC_P_search.gridy = 0;
		GridBagConstraints gBC_P_table = new GridBagConstraints();
		gBC_P_table.gridx = 1;
		gBC_P_table.fill = GridBagConstraints.BOTH;
		gBC_P_table.anchor = GridBagConstraints.WEST;
		gBC_P_table.weighty = 20.0D;
		gBC_P_table.gridy = 2;
		GridBagConstraints gBC_B_browse = new GridBagConstraints();
		gBC_B_browse.gridx = 1;
		gBC_B_browse.insets = new Insets(10, 0, 10, 0);
		gBC_B_browse.gridy = 3;
		jLabel_key = new JLabel();
		jLabel_key.setText("検索キー");
		this.setLayout(new GridBagLayout());
		this.add(jLabel_padding_left, gGC_L_padding_left);
		this.add(jLabel_padding_right, gBC_L_padding_right);
		this.add(getJPanel_search(), gBC_P_search);
		this.add(jLabel_padding, gridBagConstraints1);
		this.add(getJPanel_table(), gBC_P_table);
		this.add(getJButton_browse(), gBC_B_browse);

		hit_fileID_set = null;
		change_target_corpus();
		//update_metadata_table(); // change_target_corpus()の中で実行
	}

	/**
	 * This method initializes jTextField_key	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_key() {
		if (jTextField_key == null) {
			jTextField_key = new JTextField();
			jTextField_key.setPreferredSize(new Dimension(150, 28));
			jTextField_key.setMinimumSize(new Dimension(150, 28));
			jTextField_key.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					search_metadata();
				}
			});
		}
		return jTextField_key;
	}

	/**
	 * This method initializes jButton_search	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_search() {
		if (jButton_search == null) {
			jButton_search = new JButton();
			jButton_search.setText("検索");
			jButton_search.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					search_metadata();
				}
			});
		}
		return jButton_search;
	}

	/**
	 * This method initializes jScrollPane_metadata	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane_metadata() {
		if (jScrollPane_metadata == null) {
			jScrollPane_metadata = new JScrollPane();
			jScrollPane_metadata.setViewportView(getJTable_metadata());
		}
		return jScrollPane_metadata;
	}

	/**
	 * This method initializes jTable_metadata	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable_metadata() {
		if (jTable_metadata == null) {
			jTable_metadata = new JTable();
			jTable_metadata.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			jTable_metadata.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if(e.getClickCount() == 2) show_gda_file();
				}
			});
			jTable_metadata.setDefaultEditor(Object.class, null);
			//jTable_metadata.setCellSelectionEnabled(false);
			jTable_metadata.getTableHeader().setReorderingAllowed(false);	// 列の入れ換えを禁止
		}
		return jTable_metadata;
	}

	/**
	 * This method initializes jComboBox_key_type	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox_key_type() {
		if (jComboBox_key_type == null) {
			jComboBox_key_type = new JComboBox();
		}
		return jComboBox_key_type;
	}

	/**
	 * This method initializes jButton_browse	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButtonHL getJButton_browse() {
		if (jButton_browse == null) {
			jButton_browse = new JButtonHL();
			jButton_browse.setText("表示");
			jButton_browse.setToolTipText("選択されたファイルをウェブブラウザで表示");
			jButton_browse.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					show_gda_file();
				}
			});
		}
		return jButton_browse;
	}

	/**
	 * This method initializes jPanel_table	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_table() {
		if (jPanel_table == null) {
			GridBagConstraints gBC_CB_show_all = new GridBagConstraints();
			gBC_CB_show_all.gridx = 3;
			gBC_CB_show_all.gridy = 0;
			GridBagConstraints gBC_SP_metadata = new GridBagConstraints();
			gBC_SP_metadata.fill = GridBagConstraints.BOTH;
			gBC_SP_metadata.gridy = 1;
			gBC_SP_metadata.weightx = 1.0;
			gBC_SP_metadata.weighty = 1.0;
			gBC_SP_metadata.gridwidth = GridBagConstraints.REMAINDER;
			gBC_SP_metadata.gridx = 0;
			GridBagConstraints gBC_L_padding_hn = new GridBagConstraints();
			gBC_L_padding_hn.gridx = 2;
			gBC_L_padding_hn.weightx = 1.0D;
			gBC_L_padding_hn.fill = GridBagConstraints.HORIZONTAL;
			gBC_L_padding_hn.gridy = 0;
			jLabel_padding_hn = new JLabel();
			jLabel_padding_hn.setText(" ");
			jLabel_padding_hn.setPreferredSize(new Dimension(50, 16));
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			GridBagConstraints gBC_L_hit_num = new GridBagConstraints();
			gBC_L_hit_num.gridx = 1;
			gBC_L_hit_num.gridy = 0;
			jLabel_hit_num = new JLabel();
			jLabel_hit_num.setText("0");
			jLabel_hit_num.setForeground(Color.blue);
			jLabel_hit_num_msg = new JLabel();
			jLabel_hit_num_msg.setText("表示ファイル数: ");
			jPanel_table = new JPanel();
			jPanel_table.setLayout(new GridBagLayout());
			//jPanel_table.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			//jPanel_table.setBackground(table_bg_color);
			jPanel_table.add(jLabel_hit_num_msg, gridBagConstraints);
			jPanel_table.add(jLabel_hit_num, gBC_L_hit_num);
			jPanel_table.add(jLabel_padding_hn, gBC_L_padding_hn);
			jPanel_table.add(getJCheckBox_show_all(), gBC_CB_show_all);
			jPanel_table.add(getJScrollPane_metadata(), gBC_SP_metadata);
		}
		return jPanel_table;
	}

	/**
	 * This method initializes jPanel_search	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_search() {
		if (jPanel_search == null) {
			jLabel_padding = new JLabel();
			jLabel_padding.setText("");
			jLabel_padding.setPreferredSize(new Dimension(1, 16));
			GridBagConstraints gBC_B_search = new GridBagConstraints();
			gBC_B_search.gridx = 3;
			gBC_B_search.weightx = 2.0D;
			gBC_B_search.gridy = 0;
			GridBagConstraints gBC_TF_key = new GridBagConstraints();
			gBC_TF_key.fill = GridBagConstraints.HORIZONTAL;
			gBC_TF_key.gridy = 0;
			gBC_TF_key.weightx = 2.0D;
			gBC_TF_key.gridx = 2;
			GridBagConstraints gBC_CB_key_type = new GridBagConstraints();
			gBC_CB_key_type.fill = GridBagConstraints.VERTICAL;
			gBC_CB_key_type.gridy = 0;
			gBC_CB_key_type.weightx = 1.0;
			gBC_CB_key_type.gridx = 1;
			GridBagConstraints gBC_L_key = new GridBagConstraints();
			gBC_L_key.gridx = 0;
			gBC_L_key.weightx = 1.0D;
			gBC_L_key.anchor = GridBagConstraints.CENTER;
			gBC_L_key.gridy = 0;
			jPanel_search = new JPanel();
			jPanel_search.setLayout(new GridBagLayout());
			jPanel_search.setBackground(ViewFrame.bg_color_of_tab_area);
			jPanel_search.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			jPanel_search.add(jLabel_key, gBC_L_key);
			jPanel_search.add(getJComboBox_key_type(), gBC_CB_key_type);
			jPanel_search.add(getJTextField_key(), gBC_TF_key);
			jPanel_search.add(getJButton_search(), gBC_B_search);
		}
		return jPanel_search;
	}

	/**
	 * This method initializes jCheckBox_show_all	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox_show_all() {
		if (jCheckBox_show_all == null) {
			jCheckBox_show_all = new JCheckBox();
			jCheckBox_show_all.setText("全ファイルの一覧を表示");
			jCheckBox_show_all.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					// ↓マージしたテキストファイル(*.mer)が存在しないときは作成を促すメッセージを表示する
					if(conf_browse.target_corpus.get() == E_Corpus.MAINITI){
						wm.prepare_metadata_suffix_array_MAI(true);
					}else{
						wm.prepare_metadata_suffix_array_IWA(true);
					}
					if(wm.sa_md == null){
						java.awt.Toolkit.getDefaultToolkit().beep();
						hit_fileID_set = null;
						return;
					}
					if(jCheckBox_show_all.isSelected()){
						try {
							hit_fileID_set = wm.sa_md.all_fileID();
							update_metadata_table();
						} catch (SE_Exception e1) {
							e1.printStackTrace();
							hit_fileID_set = null;
						}
					}else{
						search_metadata();
						hit_fileID_set = null;
					}
				}
			});
		}
		return jCheckBox_show_all;
	}
}
