package jp.or.gsk.gdacb.gui;

import jp.or.gsk.gdacb.E_KeywordType;
import jp.or.gsk.gdacb.gui.parts.AnimatedLabel;
import jp.or.gsk.gdacb.gui.parts.JButtonHL;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.SwingWorker;

class SearchTab extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private KWIC_View pv = null;	// Parent KWIC View

	E_KeywordType search_type = E_KeywordType.BASE;

	private JLabel jLabel_keyword = null;
	private JTextField jTextField_keyword = null;
	private JPanel jPanel_op = null;
	private JButton jButton_search = null;
	private JCheckBox jCheckBox_sort_always = null;
	private JCheckBox jCheckBox_filter_always = null;
	private JComboBox jComboBox_keytype = null;
	private JLabel jLabel_padding_right = null;
	private JLabel jLabel_padding_left = null;
	private AnimatedLabel jLabel_anim = null;

	public SearchTab(KWIC_View parent) {
		super();
		this.pv = parent;
		initialize();
	}

	// フォームに入力されている検索キーワードを返すメソッド
	String getSearchKeyword () {
		return jTextField_keyword.getText().trim();
	}
	// 検索キーワードのフォームを埋めるメソッド
	void setSearchKeyword (String s) {
		jTextField_keyword.setText(s);
	}
	// キーワードのタイプを設定するメソッド
	private void set_keyword_type (E_KeywordType type){
		jComboBox_keytype.setSelectedItem(type.label());
	}
	// 「検索と同時にソートする」のON/OFF状態を返すメソッド
	boolean isSortAlways () {
		return jCheckBox_sort_always.isSelected();
	}
	// 「検索と同時にソートする」のON/OFFを設定するメソッド
	void setSortAlways (boolean b){
		jCheckBox_sort_always.setSelected(b);
	}
	// 「検索と同時にフィルタリングする」のON/OFF状態を返すメソッド
	boolean isFilterAlways () {
		return jCheckBox_filter_always.isSelected();
	}
	// 「検索と同時にフィルタリングする」のON/OFFを設定するメソッド
	void setFilterAlways (boolean b){
		jCheckBox_filter_always.setSelected(b);
	}
	
	// キーワードタイプの設定を記録するメソッド
	void save_search_keyword_type() {
		pv.conf_kwic.keyword_type.set( search_type );
	}
	// 検索と同時にソートするか、フィルタリングするかの設定を記録するメソッド
	void save_always_sort_filter_flag() {
		pv.conf_kwic.always_sort_flag.set( isSortAlways() );
		pv.conf_kwic.always_filter_flag.set( isFilterAlways() );
	}
	// SwingWorker を使って検索を行う
	private void search_by_swing_worker () {
		jButton_search.setEnabled(false);
		jTextField_keyword.setEnabled(false);
		jLabel_anim.setEnabled(true);
		jLabel_anim.startAnimation();
		SwingWorker<String,String> worker = new SwingWorker<String,String>(){
			@Override
			public String doInBackground() {
				pv.search();
				return "OK";
			}
			@Override
			protected void process(java.util.List<String> chunks){
				;
			}
			@Override
			public void done() {
				jLabel_anim.stopAnimation();
				jButton_search.setEnabled(true);
				jTextField_keyword.setEnabled(true);
				//jLabel_anim.setEnabled(false);
			}
		};
		worker.execute();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.gridy = 0;
		jLabel_padding_left = new JLabel();
		jLabel_padding_left.setText("");
		jLabel_padding_left.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_L_padding_right = new GridBagConstraints();
		gBC_L_padding_right.gridx = 5;
		gBC_L_padding_right.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_padding_right.weightx = 10.0D;
		gBC_L_padding_right.gridy = 0;
		jLabel_padding_right = new JLabel();
		jLabel_padding_right.setText("");
		jLabel_padding_right.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_CB_keytype = new GridBagConstraints();
		gBC_CB_keytype.fill = GridBagConstraints.NONE;
		gBC_CB_keytype.gridy = 0;
		gBC_CB_keytype.weightx = 0.0D;
		gBC_CB_keytype.anchor = GridBagConstraints.WEST;
		gBC_CB_keytype.insets = new Insets(0, 5, 0, 5);
		gBC_CB_keytype.gridx = 2;
		GridBagConstraints gBC_CB_filter_always = new GridBagConstraints();
		gBC_CB_filter_always.gridx = 2;
		gBC_CB_filter_always.anchor = GridBagConstraints.WEST;
		gBC_CB_filter_always.gridwidth = 2;
		gBC_CB_filter_always.gridy = 2;
		GridBagConstraints gBC_CB_sort_always = new GridBagConstraints();
		gBC_CB_sort_always.gridx = 2;
		gBC_CB_sort_always.gridwidth = 2;
		gBC_CB_sort_always.anchor = GridBagConstraints.WEST;
		gBC_CB_sort_always.gridy = 1;
		GridBagConstraints gBC_P_op = new GridBagConstraints();
		gBC_P_op.gridx = 4;
		gBC_P_op.gridy = 0;
		gBC_P_op.weightx = 0.0D;
		gBC_P_op.insets = new Insets(0, 20, 0, 20);
		gBC_P_op.fill = GridBagConstraints.BOTH;
		gBC_P_op.gridheight = GridBagConstraints.REMAINDER;
		GridBagConstraints gBC_TF_keyword = new GridBagConstraints();
		gBC_TF_keyword.fill = GridBagConstraints.HORIZONTAL;
		gBC_TF_keyword.gridy = 0;
		gBC_TF_keyword.weightx = 0.0D;
		gBC_TF_keyword.anchor = GridBagConstraints.WEST;
		gBC_TF_keyword.insets = new Insets(0, 5, 0, 5);
		gBC_TF_keyword.gridx = 3;
		GridBagConstraints gBC_L_keyword = new GridBagConstraints();
		gBC_L_keyword.gridx = 1;
		gBC_L_keyword.anchor = GridBagConstraints.WEST;
		gBC_L_keyword.weightx = 0.0D;
		gBC_L_keyword.insets = new Insets(0, 5, 0, 5);
		gBC_L_keyword.gridy = 0;
		jLabel_keyword = new JLabel();
		jLabel_keyword.setText("キーワード");
		this.setSize(400, 200);
		this.setLayout(new GridBagLayout());
		this.add(jLabel_padding_left, gridBagConstraints);
		this.add(jLabel_padding_right, gBC_L_padding_right);
		this.add(jLabel_keyword, gBC_L_keyword);
		this.add(getJComboBox_keytype(), gBC_CB_keytype);
		this.add(getJTextField_keyword(), gBC_TF_keyword);
		this.add(getJCheckBox_sort_always(), gBC_CB_sort_always);
		this.add(getJCheckBox_filter_always(), gBC_CB_filter_always);
		this.add(getJPanel_op(), gBC_P_op);
		
		set_keyword_type(pv.conf_kwic.keyword_type.get());
		setSortAlways(pv.conf_kwic.always_sort_flag.get());
		setFilterAlways(pv.conf_kwic.always_filter_flag.get());
	}

	/**
	 * This method initializes jTextField_keyword	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_keyword() {
		if (jTextField_keyword == null) {
			jTextField_keyword = new JTextField();
			jTextField_keyword.setMinimumSize(new Dimension(150, 28));
			jTextField_keyword.setPreferredSize(new Dimension(150, 28));
			jTextField_keyword.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//mf.kwic_view.search();
					search_by_swing_worker();
				}
			});
		}
		return jTextField_keyword;
	}

	/**
	 * This method initializes jPanel_op	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_op() {
		if (jPanel_op == null) {
			GridBagConstraints gBC_L_anim = new GridBagConstraints();
			gBC_L_anim.gridx = 1;
			gBC_L_anim.insets = new Insets(5, 5, 5, 5);
			gBC_L_anim.gridy = 0;
			jLabel_anim = new AnimatedLabel();
			jLabel_anim.setBackground(ViewFrame.bg_color_of_tab_area);
			//jLabel_anim.setEnabled(false);
			GridBagConstraints gBC_B_search = new GridBagConstraints();
			gBC_B_search.gridx = 0;
			gBC_B_search.fill = GridBagConstraints.NONE;
			gBC_B_search.insets = new Insets(5, 5, 5, 5);
			gBC_B_search.gridy = 0;
			jPanel_op = new JPanel();
			jPanel_op.setLayout(new GridBagLayout());
			jPanel_op.setOpaque(false);
			jPanel_op.add(getJButton_search(), gBC_B_search);
			jPanel_op.add(jLabel_anim, gBC_L_anim);
		}
		return jPanel_op;
	}

	/**
	 * This method initializes jButton_search	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_search() {
		if (jButton_search == null) {
			jButton_search = new JButtonHL();
			jButton_search.setText("検索");
			jButton_search.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//mf.kwic_view.search();
					search_by_swing_worker();
				}
			});
		}
		return jButton_search;
	}

	/**
	 * This method initializes jCheckBox_sort_always	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox_sort_always() {
		if (jCheckBox_sort_always == null) {
			jCheckBox_sort_always = new JCheckBox();
			jCheckBox_sort_always.setOpaque(false);
			jCheckBox_sort_always.setText("検索後すぐにソートする");
		}
		return jCheckBox_sort_always;
	}

	/**
	 * This method initializes jCheckBox_filter_always	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox_filter_always() {
		if (jCheckBox_filter_always == null) {
			jCheckBox_filter_always = new JCheckBox();
			jCheckBox_filter_always.setOpaque(false);
			jCheckBox_filter_always.setText("検索後すぐにフィルタリングする");
		}
		return jCheckBox_filter_always;
	}

	/**
	 * This method initializes jComboBox_keytype	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox_keytype() {
		if (jComboBox_keytype == null) {
			jComboBox_keytype = new JComboBox();
			jComboBox_keytype.addItem(E_KeywordType.FULLTEXT.label());
			jComboBox_keytype.addItem(E_KeywordType.BASE.label());
			jComboBox_keytype.addItem(E_KeywordType.SURFACE.label());
			//jComboBox_keytype.setOpaque(false);
			jComboBox_keytype.setBackground(ViewFrame.bg_color_of_tab_area);
			jComboBox_keytype.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String item = (String)jComboBox_keytype.getSelectedItem();
					for(E_KeywordType kt: E_KeywordType.values()){
						if( item.equals(kt.label()) ){
							search_type = kt;
							return;
						}
					}
					search_type = E_KeywordType.UNDEF;
				}
			});
		}
		return jComboBox_keytype;
	}
}
