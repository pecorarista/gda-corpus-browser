package jp.or.gsk.gdacb.gui;

import jp.or.gsk.gdacb.E_ColWA;
import jp.or.gsk.gdacb.gui.parts.JTextField_PositiveInt;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

class KWICConfTab extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private KWIC_View pv = null;	// parent KWIC View

	private JLabel jLabel_padding_left = null;
	private JLabel jLabel_padding_right = null;
	private JLabel jLabel_disp_num = null;
	private JLabel jLabel_kwic_len = null;
	private JLabel jLabel_cw = null;
	private JPanel jPanel_disp_num = null;
	private JTextField_PositiveInt jTextField_disp_num = null;
	private JPanel jPanel_kwic_len = null;
	private JLabel jLabel_kwiclen_left = null;
	private JLabel jLabel_kwiclen_right = null;
	private JTextField_PositiveInt jTextField_kwiclen_left = null;
	private JTextField_PositiveInt jTextField_kwiclen_right = null;
	private JPanel jPanel_cw = null;
	private JRadioButton jRadioButton_cw_default = null;
	private JRadioButton jRadioButton_cw_auto = null;
	private JRadioButton jRadioButton_cw_custom = null;
	private JRadioButton jRadioButton_max = null;
	private JRadioButton jRadioButton_unlimit = null;


	/**
	 * This is the default constructor
	 */
	public KWICConfTab(KWIC_View parent) {
		super();
		this.pv = parent;
		initialize();
	}
	/**
	 * 画面の設定内容を保存するメソッド
	 */
	void save_parameters(){
		save_max_display_num();
		save_kwic_left_len();
		save_kwic_right_len();
		save_column_width_adjustment();
	}
	private void save_max_display_num (){
		if(jRadioButton_unlimit.isSelected()){
			pv.conf_kwic.max_display_num.set(-1);
		}else{
			int num = normalized_number_in_TF(jTextField_disp_num);
			if(num <= 0){
				jTextField_disp_num.setText("");
				pv.conf_kwic.max_display_num.set(-1);
			}else{
				pv.conf_kwic.max_display_num.set(num);
			}
		}
	}
	private void save_kwic_left_len(){
		pv.conf_kwic.left_kwic_length.set( normalized_number_in_TF(jTextField_kwiclen_left) );
		/*
		if(mf.conf.target_corpus.get() == E_Corpus.MAINITI && mf.se_mai != null){
			mf.se_mai.set_kwic_left_length(pv.conf_kwic.left_kwic_length.get());
		}else if(mf.se_iwa != null){
			mf.se_iwa.set_kwic_left_length(pv.conf_kwic.left_kwic_length.get());
		}
		*/
	}
	private void save_kwic_right_len(){
		pv.conf_kwic.right_kwic_length.set( normalized_number_in_TF(jTextField_kwiclen_right) );
		/*
		if(mf.conf.target_corpus.get() == E_Corpus.MAINITI && mf.se_mai != null){
			mf.se_mai.set_kwic_right_length(pv.conf_kwic.right_kwic_length.get());
		}else if(mf.se_iwa != null){
			mf.se_iwa.set_kwic_right_length(pv.conf_kwic.right_kwic_length.get());
		}
		*/
	}
	private void save_column_width_adjustment(){
		pv.conf_kwic.column_width_adjustment.set( status_RB_column_width() );
	}
	/**
	 * 設定ファイルの内容を画面に反映させるメソッド
	 */
	private void fill_forms(){
		if(pv.conf_kwic.max_display_num.get() == -1){
			jRadioButton_unlimit.setSelected(true);
		}else{
			jRadioButton_max.setSelected(true);
			jTextField_disp_num.setText( Integer.toString(pv.conf_kwic.max_display_num.get()) );
		}
		jTextField_kwiclen_left.setText( Integer.toString( pv.conf_kwic.left_kwic_length.get() ));
		jTextField_kwiclen_right.setText( Integer.toString( pv.conf_kwic.right_kwic_length.get() ));
		if(pv.conf_kwic.column_width_adjustment.get() == E_ColWA.DEFAULT)
			jRadioButton_cw_default.setSelected(true);
		if(pv.conf_kwic.column_width_adjustment.get() == E_ColWA.AUTO)
			jRadioButton_cw_auto.setSelected(true);
		if(pv.conf_kwic.column_width_adjustment.get() == E_ColWA.CUSTOM)
			jRadioButton_cw_custom.setSelected(true);
	}
	/**
     * テキストフィールドに入力された整数を正規化して返すメソッド
     * @param tf	テキストフィールド
     * @return		正規化された整数
     */
	static int normalized_number_in_TF (JTextField_PositiveInt tf) {
		String s = tf.getText();
		if(s==null || s.equals("")) return -1;
		return( Integer.parseInt(s) );
	}
	/**
     * 列幅調整のラジオボタンの選択状況を返すメソッド
     */
	private E_ColWA status_RB_column_width () {
		if(jRadioButton_cw_default.isSelected()) return E_ColWA.DEFAULT;
		if(jRadioButton_cw_auto.isSelected()) return E_ColWA.AUTO;
		if(jRadioButton_cw_custom.isSelected()) return E_ColWA.CUSTOM;
		return null;
    }

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gBC_P_cw = new GridBagConstraints();
		gBC_P_cw.anchor = GridBagConstraints.WEST;
		gBC_P_cw.gridx = 2;
		gBC_P_cw.gridy = 2;
		gBC_P_cw.fill = GridBagConstraints.NONE;
		GridBagConstraints gBC_P_kwic_len = new GridBagConstraints();
		gBC_P_kwic_len.gridx = 2;
		gBC_P_kwic_len.anchor = GridBagConstraints.WEST;
		gBC_P_kwic_len.gridy = 1;
		GridBagConstraints gBC_P_disp_num = new GridBagConstraints();
		gBC_P_disp_num.gridx = 2;
		gBC_P_disp_num.anchor = GridBagConstraints.WEST;
		gBC_P_disp_num.gridy = 0;
		GridBagConstraints gBC_L_cw = new GridBagConstraints();
		gBC_L_cw.gridx = 1;
		gBC_L_cw.anchor = GridBagConstraints.WEST;
		gBC_L_cw.weighty = 1.0D;
		gBC_L_cw.gridy = 2;
		jLabel_cw = new JLabel();
		jLabel_cw.setText("列幅の調整");
		GridBagConstraints gBC_L_kwic_len = new GridBagConstraints();
		gBC_L_kwic_len.gridx = 1;
		gBC_L_kwic_len.anchor = GridBagConstraints.WEST;
		gBC_L_kwic_len.weighty = 1.0D;
		gBC_L_kwic_len.gridy = 1;
		jLabel_kwic_len = new JLabel();
		jLabel_kwic_len.setText("文脈幅");
		GridBagConstraints gBC_L_disp_num = new GridBagConstraints();
		gBC_L_disp_num.gridx = 1;
		gBC_L_disp_num.anchor = GridBagConstraints.WEST;
		gBC_L_disp_num.weighty = 1.0D;
		gBC_L_disp_num.insets = new Insets(0, 0, 0, 10);
		gBC_L_disp_num.gridy = 0;
		jLabel_disp_num = new JLabel();
		jLabel_disp_num.setText("最大表示件数");
		GridBagConstraints gBC_L_pad_right = new GridBagConstraints();
		gBC_L_pad_right.gridx = 3;
		gBC_L_pad_right.weightx = 8.0D;
		gBC_L_pad_right.gridy = 0;
		jLabel_padding_right = new JLabel();
		jLabel_padding_right.setText("");
		jLabel_padding_right.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_L_pad_left = new GridBagConstraints();
		gBC_L_pad_left.gridx = 0;
		gBC_L_pad_left.weightx = 1.0D;
		gBC_L_pad_left.gridy = 0;
		jLabel_padding_left = new JLabel();
		jLabel_padding_left.setText("");
		jLabel_padding_left.setPreferredSize(new Dimension(10, 10));
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(jLabel_padding_left, gBC_L_pad_left);
		this.add(jLabel_padding_right, gBC_L_pad_right);
		this.add(jLabel_disp_num, gBC_L_disp_num);
		this.add(getJPanel_disp_num(), gBC_P_disp_num);
		this.add(jLabel_kwic_len, gBC_L_kwic_len);
		this.add(getJPanel_kwic_len(), gBC_P_kwic_len);
		this.add(jLabel_cw, gBC_L_cw);
		this.add(getJPanel_cw(), gBC_P_cw);
		
		ButtonGroup display_number_group = new ButtonGroup();
		display_number_group.add(jRadioButton_max);
		display_number_group.add(jRadioButton_unlimit);
		ButtonGroup column_width_group = new ButtonGroup();
		column_width_group.add(jRadioButton_cw_default);
		column_width_group.add(jRadioButton_cw_auto);
		column_width_group.add(jRadioButton_cw_custom);
		
		fill_forms();
	}
	
	private JPanel getJPanel_disp_num() {
		if (jPanel_disp_num == null) {
			GridBagConstraints gBC_RB_max = new GridBagConstraints();
			gBC_RB_max.gridx = 0;
			gBC_RB_max.gridy = 0;
			GridBagConstraints gBC_RB_unlimit = new GridBagConstraints();
			gBC_RB_unlimit.gridx = 2;
			gBC_RB_unlimit.insets = new Insets(0, 20, 0, 0);
			gBC_RB_unlimit.gridy = 0;
			GridBagConstraints gBC_TF_disp_num = new GridBagConstraints();
			gBC_TF_disp_num.anchor = GridBagConstraints.WEST;
			gBC_TF_disp_num.gridx = 1;
			gBC_TF_disp_num.gridy = 0;
			gBC_TF_disp_num.fill = GridBagConstraints.NONE;
			jPanel_disp_num = new JPanel();
			jPanel_disp_num.setLayout(new GridBagLayout());
			jPanel_disp_num.setOpaque(false);
			jPanel_disp_num.add(getJRadioButton_max(), gBC_RB_max);
			jPanel_disp_num.add(getJTextField_disp_num(), gBC_TF_disp_num);
			jPanel_disp_num.add(getJRadioButton_unlimit(), gBC_RB_unlimit);
		}
		return jPanel_disp_num;
	}

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
					jTextField_disp_num.restore_value_if_invalid(1);
					save_max_display_num();
				}
			});
			jTextField_disp_num.addFocusListener(new java.awt.event.FocusAdapter() {   
				public void focusGained(java.awt.event.FocusEvent e) {    
					jRadioButton_max.setSelected(true);
				}
				public void focusLost(java.awt.event.FocusEvent e) {
					jTextField_disp_num.restore_value_if_invalid(1);
					save_max_display_num();
				}
			});
		}
		return jTextField_disp_num;
	}

	/**
	 * This method initializes jPanel_kwic_len	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_kwic_len() {
		if (jPanel_kwic_len == null) {
			GridBagConstraints gBC_TF_kwic_right = new GridBagConstraints();
			gBC_TF_kwic_right.fill = GridBagConstraints.NONE;
			gBC_TF_kwic_right.gridy = 0;
			gBC_TF_kwic_right.anchor = GridBagConstraints.WEST;
			gBC_TF_kwic_right.insets = new Insets(0, 10, 0, 10);
			gBC_TF_kwic_right.gridx = 3;
			GridBagConstraints gBC_TF_kwic_left = new GridBagConstraints();
			gBC_TF_kwic_left.fill = GridBagConstraints.HORIZONTAL;
			gBC_TF_kwic_left.gridy = 0;
			gBC_TF_kwic_left.anchor = GridBagConstraints.WEST;
			gBC_TF_kwic_left.insets = new Insets(0, 10, 0, 10);
			gBC_TF_kwic_left.gridx = 1;
			GridBagConstraints gBC_L_kwic_right = new GridBagConstraints();
			gBC_L_kwic_right.gridx = 2;
			gBC_L_kwic_right.anchor = GridBagConstraints.WEST;
			gBC_L_kwic_right.insets = new Insets(0, 20, 0, 0);
			gBC_L_kwic_right.gridy = 0;
			jLabel_kwiclen_right = new JLabel();
			jLabel_kwiclen_right.setText("右");
			GridBagConstraints gBC_L_kwic_left = new GridBagConstraints();
			gBC_L_kwic_left.gridx = 0;
			gBC_L_kwic_left.anchor = GridBagConstraints.WEST;
			gBC_L_kwic_left.insets = new Insets(0, 0, 0, 0);
			gBC_L_kwic_left.gridy = 0;
			jLabel_kwiclen_left = new JLabel();
			jLabel_kwiclen_left.setText("左");

			jPanel_kwic_len = new JPanel();
			jPanel_kwic_len.setLayout(new GridBagLayout());
			jPanel_kwic_len.setOpaque(false);
			jPanel_kwic_len.add(jLabel_kwiclen_left, gBC_L_kwic_left);
			jPanel_kwic_len.add(getJTextField_kwiclen_left(), gBC_TF_kwic_left);
			jPanel_kwic_len.add(jLabel_kwiclen_right, gBC_L_kwic_right);
			jPanel_kwic_len.add(getJTextField_kwiclen_right(), gBC_TF_kwic_right);

		}
		return jPanel_kwic_len;
	}
	/**
	 * This method initializes jTextField_kwiclen_left	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_kwiclen_left() {
		if (jTextField_kwiclen_left == null) {
			jTextField_kwiclen_left = new JTextField_PositiveInt();
			jTextField_kwiclen_left.setPreferredSize(new Dimension(40, 28));
			jTextField_kwiclen_left.setToolTipText("左文脈の幅(文字数)を指定します");
			jTextField_kwiclen_left.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextField_kwiclen_left.restore_value_if_invalid(0);
					save_kwic_left_len();
				}
			});
			jTextField_kwiclen_left.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					jTextField_kwiclen_left.restore_value_if_invalid(0);
					save_kwic_left_len();
				}
			});
		}
		return jTextField_kwiclen_left;
	}

	/**
	 * This method initializes jTextField_kwiclen_right	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_kwiclen_right() {
		if (jTextField_kwiclen_right == null) {
			jTextField_kwiclen_right = new JTextField_PositiveInt();
			jTextField_kwiclen_right.setPreferredSize(new Dimension(40, 28));
			jTextField_kwiclen_right.setToolTipText("右文脈の幅(文字数)を指定します");
			jTextField_kwiclen_right.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextField_kwiclen_right.restore_value_if_invalid(0);
					save_kwic_right_len();
				}
			});
			jTextField_kwiclen_right.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					jTextField_kwiclen_right.restore_value_if_invalid(0);
					save_kwic_right_len();
				}
			});
		}
		return jTextField_kwiclen_right;
	}
	/**
	 * This method initializes jPanel_cw	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_cw() {
		if (jPanel_cw == null) {
			GridBagConstraints gBC_RB_cw_custom = new GridBagConstraints();
			gBC_RB_cw_custom.gridx = 2;
			gBC_RB_cw_custom.anchor = GridBagConstraints.WEST;
			gBC_RB_cw_custom.gridy = 0;
			GridBagConstraints gBC_RB_cw_auto = new GridBagConstraints();
			gBC_RB_cw_auto.gridx = 1;
			gBC_RB_cw_auto.anchor = GridBagConstraints.WEST;
			gBC_RB_cw_auto.gridy = 0;
			GridBagConstraints gBC_RB_cw_default = new GridBagConstraints();
			gBC_RB_cw_default.gridx = 0;
			gBC_RB_cw_default.anchor = GridBagConstraints.WEST;
			gBC_RB_cw_default.gridy = 0;
			jPanel_cw = new JPanel();
			jPanel_cw.setLayout(new GridBagLayout());
			jPanel_cw.setOpaque(false);
			jPanel_cw.add(getJRadioButton_cw_default(), gBC_RB_cw_default);
			jPanel_cw.add(getJRadioButton_cw_auto(), gBC_RB_cw_auto);
			jPanel_cw.add(getJRadioButton_cw_custom(), gBC_RB_cw_custom);
		}
		return jPanel_cw;
	}
	/**
	 * This method initializes jRadioButton_cw_default	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_cw_default() {
		if (jRadioButton_cw_default == null) {
			jRadioButton_cw_default = new JRadioButton();
			jRadioButton_cw_default.setText("デフォルト");
			jRadioButton_cw_default.setOpaque(false);
			jRadioButton_cw_default.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					save_column_width_adjustment();
				}
			});
		}
		return jRadioButton_cw_default;
	}
	/**
	 * This method initializes jRadioButton_cw_auto	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_cw_auto() {
		if (jRadioButton_cw_auto == null) {
			jRadioButton_cw_auto = new JRadioButton();
			jRadioButton_cw_auto.setText("自動");
			jRadioButton_cw_auto.setOpaque(false);
			jRadioButton_cw_auto.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					save_column_width_adjustment();
				}
			});
		}
		return jRadioButton_cw_auto;
	}
	/**
	 * This method initializes jRadioButton_cw_custom	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_cw_custom() {
		if (jRadioButton_cw_custom == null) {
			jRadioButton_cw_custom = new JRadioButton();
			jRadioButton_cw_custom.setText("カスタム");
			jRadioButton_cw_custom.setOpaque(false);
			jRadioButton_cw_custom.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					save_column_width_adjustment();
				}
			});
		}
		return jRadioButton_cw_custom;
	}

	/**
	 * This method initializes jRadioButton_max	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_max() {
		if (jRadioButton_max == null) {
			jRadioButton_max = new JRadioButton();
			jRadioButton_max.setOpaque(false);
			jRadioButton_max.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					save_max_display_num();
				}
			});
		}
		return jRadioButton_max;
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
			jRadioButton_unlimit.setOpaque(false);
			jRadioButton_unlimit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					save_max_display_num();
				}
			});
		}
		return jRadioButton_unlimit;
	}
}
