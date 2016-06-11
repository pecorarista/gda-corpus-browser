package jp.or.gsk.gdacb.gui;

import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_CORPUS;
import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.gui.parts.JButtonHL;
import jp.or.gsk.gdacb.gui.parts.JFileChooserEx;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JButton;

class ExportTab extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private KWIC_View pv = null;	// parent KWIC View
	JFileChooserEx FileChooserTSV; 
	
	private JLabel jLabel_contents = null;
	private JRadioButton jRadioButton_tbl_asIs = null;
	private JRadioButton jRadioButton_tbl_init = null;
	private JCheckBox jCheckBox_sense = null;
	private JPanel jPanel_op = null;
	private JButton jButton_export = null;
	private JLabel jLabel_sense = null;
	private JLabel jLabel_coding = null;
	private JPanel jPanel_coding = null;
	private JRadioButton jRadioButton_sjis = null;
	private JRadioButton jRadioButton_euc = null;
	private JRadioButton jRadioButton_jis = null;
	private JRadioButton jRadioButton_utf8 = null;

	private JLabel jLabel_title = null;

	private JLabel jLabel_padding_left = null;

	private JLabel jLabel_padding_right = null;

	ExportTab(KWIC_View parent){
		super();
		this.pv = parent;
		this.FileChooserTSV = new JFileChooserEx();
		initialize();
	}
	
	// 「テーブルに表示されている通りにエクスポートする」のON/OFFを返すメソッド
	boolean isOutputTableAsIsFlag(){
		return jRadioButton_tbl_asIs.isSelected();
	}
	// 引数として与えられた「テーブルに表示されている通りにエクスポートする」のON/OFFを
	// 画面に反映させるメソッド
	private void setOutputTableAsIsFlag(boolean b){
		jRadioButton_tbl_asIs.setSelected(b);
	}
	// 「語義に語釈文を付与する」のON/OFFを返すメソッド
	boolean isAddSenseFlag(){
		return jCheckBox_sense.isSelected();
	}
	// 引数として与えられた「語義に語釈文を付与する」のON/OFFを
	// 画面に反映させるメソッド
	private void setAddSenseFlag(boolean b){
		jCheckBox_sense.setSelected(b);
	}
	// 出力文字コードを返すメソッド
	String getOutputCoding(){
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
	// 引数として与えられた出力文字コードを画面に反映させるメソッド
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
	// エクスポート条件の設定を記録する
	void save_export_options(){
		pv.conf_kwic.export_table_asis.set( isOutputTableAsIsFlag() );
		pv.conf_kwic.export_add_sense.set( isAddSenseFlag() );
		pv.conf_kwic.export_output_coding.set( getOutputCoding() );
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gBC_L_padding_right = new GridBagConstraints();
		gBC_L_padding_right.gridx = 5;
		gBC_L_padding_right.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_padding_right.weightx = 8.0D;
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
		GridBagConstraints gBC_L_title = new GridBagConstraints();
		gBC_L_title.gridx = 1;
		gBC_L_title.gridwidth = GridBagConstraints.REMAINDER;
		gBC_L_title.anchor = GridBagConstraints.WEST;
		gBC_L_title.gridy = 0;
		jLabel_title = new JLabel();
		jLabel_title.setText("表示されている例文をタブ区切り形式のファイルに出力します");
		jLabel_title.setForeground(new Color(204, 0, 51));
		GridBagConstraints gBC_P_coding = new GridBagConstraints();
		gBC_P_coding.gridx = 2;
		gBC_P_coding.anchor = GridBagConstraints.CENTER;
		gBC_P_coding.gridwidth = 2;
		gBC_P_coding.gridy = 3;
		GridBagConstraints gBC_L_coding = new GridBagConstraints();
		gBC_L_coding.gridx = 1;
		gBC_L_coding.anchor = GridBagConstraints.WEST;
		gBC_L_coding.insets = new Insets(0, 0, 0, 10);
		gBC_L_coding.gridy = 3;
		jLabel_coding = new JLabel();
		jLabel_coding.setText("文字コード");
		jLabel_coding.setToolTipText("出力ファイルの文字コードを指定します");
		GridBagConstraints gBC_L_sense = new GridBagConstraints();
		gBC_L_sense.gridx = 1;
		gBC_L_sense.anchor = GridBagConstraints.WEST;
		gBC_L_sense.insets = new Insets(0, 0, 0, 10);
		gBC_L_sense.gridy = 2;
		jLabel_sense = new JLabel();
		jLabel_sense.setText("語義");
		if(SYSTEM_CORPUS == E_Corpus.MAINITI) jLabel_sense.setEnabled(false);
		GridBagConstraints gBC_P_op = new GridBagConstraints();
		gBC_P_op.gridx = 4;
		gBC_P_op.gridy = 1;
		gBC_P_op.weightx = 2.0D;
		gBC_P_op.gridheight = GridBagConstraints.REMAINDER;
		GridBagConstraints gBC_CB_sense = new GridBagConstraints();
		gBC_CB_sense.gridx = 2;
		gBC_CB_sense.anchor = GridBagConstraints.WEST;
		gBC_CB_sense.gridwidth = 2;
		gBC_CB_sense.gridy = 2;
		GridBagConstraints gBC_RB_tbl_init = new GridBagConstraints();
		gBC_RB_tbl_init.gridx = 3;
		gBC_RB_tbl_init.anchor = GridBagConstraints.WEST;
		gBC_RB_tbl_init.insets = new Insets(0, 5, 0, 0);
		gBC_RB_tbl_init.gridy = 1;
		GridBagConstraints gBC_RB_tbl_asIs = new GridBagConstraints();
		gBC_RB_tbl_asIs.gridx = 2;
		gBC_RB_tbl_asIs.anchor = GridBagConstraints.WEST;
		gBC_RB_tbl_asIs.gridy = 1;
		GridBagConstraints gBC_L_contents = new GridBagConstraints();
		gBC_L_contents.gridx = 1;
		gBC_L_contents.anchor = GridBagConstraints.WEST;
		gBC_L_contents.insets = new Insets(0, 0, 0, 10);
		gBC_L_contents.gridy = 1;
		jLabel_contents = new JLabel();
		jLabel_contents.setText("出力オプション");
		this.setSize(500, 200);
		this.setLayout(new GridBagLayout());
		this.add(jLabel_padding_left, gBC_L_padding_left);
		this.add(jLabel_padding_right, gBC_L_padding_right);
		this.add(jLabel_title, gBC_L_title);
		this.add(jLabel_contents, gBC_L_contents);
		this.add(getJRadioButton_tbl_asIs(), gBC_RB_tbl_asIs);
		this.add(getJRadioButton_tbl_init(), gBC_RB_tbl_init);
		this.add(jLabel_sense, gBC_L_sense);
		this.add(getJCheckBox_sense(), gBC_CB_sense);
		this.add(jLabel_coding, gBC_L_coding);
		this.add(getJPanel_coding(), gBC_P_coding);
		this.add(getJPanel_op(), gBC_P_op);
		
		ButtonGroup format_group = new ButtonGroup();
		format_group.add(jRadioButton_tbl_asIs);
		format_group.add(jRadioButton_tbl_init);
		ButtonGroup output_coding_group = new ButtonGroup();
		output_coding_group.add(jRadioButton_sjis);
		output_coding_group.add(jRadioButton_euc);
		output_coding_group.add(jRadioButton_jis);
		output_coding_group.add(jRadioButton_utf8);
		
		setOutputTableAsIsFlag(pv.conf_kwic.export_table_asis.get());
		setAddSenseFlag(pv.conf_kwic.export_add_sense.get());
		setOutputCoding(pv.conf_kwic.export_output_coding.get());
	}

	/**
	 * This method initializes jRadioButton_tbl_asIs	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_tbl_asIs() {
		if (jRadioButton_tbl_asIs == null) {
			jRadioButton_tbl_asIs = new JRadioButton();
			jRadioButton_tbl_asIs.setOpaque(false);
			jRadioButton_tbl_asIs.setText("現在の表示");
			jRadioButton_tbl_asIs.setToolTipText("現在表示されている表をそのまま出力します");
		}
		return jRadioButton_tbl_asIs;
	}

	/**
	 * This method initializes jRadioButton_tbl_init	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_tbl_init() {
		if (jRadioButton_tbl_init == null) {
			jRadioButton_tbl_init = new JRadioButton();
			jRadioButton_tbl_init.setOpaque(false);
			jRadioButton_tbl_init.setText("デフォルト表示");
			jRadioButton_tbl_init.setToolTipText("列の並びはデフォルトの順序になります。ソート、フィルタも解除されます。");
		}
		return jRadioButton_tbl_init;
	}

	/**
	 * This method initializes jCheckBox_sense	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox_sense() {
		if (jCheckBox_sense == null) {
			jCheckBox_sense = new JCheckBox();
			jCheckBox_sense.setOpaque(false);
			jCheckBox_sense.setText("語義IDに語釈文を付与する");
			if(SYSTEM_CORPUS == E_Corpus.MAINITI) jCheckBox_sense.setEnabled(false);
		}
		return jCheckBox_sense;
	}

	/**
	 * This method initializes jPanel_op	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_op() {
		if (jPanel_op == null) {
			GridBagConstraints gBC_B_export = new GridBagConstraints();
			gBC_B_export.gridx = 0;
			gBC_B_export.gridy = 0;
			jPanel_op = new JPanel();
			jPanel_op.setLayout(new GridBagLayout());
			jPanel_op.add(getJButton_export(), gBC_B_export);
		}
		return jPanel_op;
	}

	/**
	 * This method initializes jButton_export	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_export() {
		if (jButton_export == null) {
			jButton_export = new JButtonHL();
			jButton_export.setText("出力");
			jButton_export.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					pv.export();
				}
			});
		}
		return jButton_export;
	}

	/**
	 * This method initializes jPanel_coding	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_coding() {
		if (jPanel_coding == null) {
			GridBagConstraints gBC_RB_utf8 = new GridBagConstraints();
			gBC_RB_utf8.gridx = 3;
			gBC_RB_utf8.anchor = GridBagConstraints.WEST;
			gBC_RB_utf8.weightx = 0.0D;
			gBC_RB_utf8.insets = new Insets(0, 5, 0, 0);
			gBC_RB_utf8.gridy = 0;
			GridBagConstraints gBC_RB_jis = new GridBagConstraints();
			gBC_RB_jis.gridx = 2;
			gBC_RB_jis.anchor = GridBagConstraints.WEST;
			gBC_RB_jis.weightx = 0.0D;
			gBC_RB_jis.insets = new Insets(0, 5, 0, 0);
			gBC_RB_jis.gridy = 0;
			GridBagConstraints gBC_RB_euc = new GridBagConstraints();
			gBC_RB_euc.gridx = 1;
			gBC_RB_euc.anchor = GridBagConstraints.WEST;
			gBC_RB_euc.weightx = 0.0D;
			gBC_RB_euc.insets = new Insets(0, 5, 0, 0);
			gBC_RB_euc.gridy = 0;
			GridBagConstraints gBC_RB_sjis = new GridBagConstraints();
			gBC_RB_sjis.gridx = 0;
			gBC_RB_sjis.anchor = GridBagConstraints.WEST;
			gBC_RB_sjis.weightx = 0.0D;
			gBC_RB_sjis.gridy = 0;
			jPanel_coding = new JPanel();
			jPanel_coding.setLayout(new GridBagLayout());
			jPanel_coding.setToolTipText("出力ファイルの文字コードを指定します");
			jPanel_coding.add(getJRadioButton_sjis(), gBC_RB_sjis);
			jPanel_coding.add(getJRadioButton_euc(), gBC_RB_euc);
			jPanel_coding.add(getJRadioButton_jis(), gBC_RB_jis);
			jPanel_coding.add(getJRadioButton_utf8(), gBC_RB_utf8);
			jPanel_coding.setOpaque(false);
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
			jRadioButton_sjis.setOpaque(false);
			jRadioButton_sjis.setText("Shift JIS");
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
			jRadioButton_euc.setOpaque(false);
			jRadioButton_euc.setText("EUC");
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
			jRadioButton_jis.setOpaque(false);
			jRadioButton_jis.setText("JIS");
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
			jRadioButton_utf8.setOpaque(false);
			jRadioButton_utf8.setText("UTF-8");
		}
		return jRadioButton_utf8;
	}
}
