package jp.or.gsk.gdacb.gui;

import jp.or.gsk.gdacb.E_Corpus;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SortOrder;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import java.awt.Dimension;

class SortTab_KeyPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private String key_label = null;

	public SortOrder sort_order = SortOrder.ASCENDING;
	
	private JLabel jLabel_name = null;
	private JRadioButton jRadioButton_ascending = null;
	private JRadioButton jRadioButton_descending = null;
	private JLabel jLabel_padding_right = null;
	private JComboBox jComboBox_key = null;

	private JLabel jLabel_padding1 = null;

	private JLabel jLabel_padding2 = null;

	SortTab_KeyPanel(String klabel){
		super();
		this.key_label = klabel;
		initialize();
	}
	
	// 画面に設定されているソートの条件を返すメソッド
	AbstractSorter getAbstractSorter () {
		// ComboBoxの最初の要素は""なので、1を引く
		int idx = jComboBox_key.getSelectedIndex() - 1;
		if(idx < 0){
			return null;
		}else{
			return new AbstractSorter(idx, sort_order);
		}
	}
	// 引数として与えられたソート条件を画面に反映させるメソッド
	void setAbstractSorter (AbstractSorter as) {
		if(as == null) return;
		jComboBox_key.setSelectedIndex( as.key_index + 1 );
		if(as.order == SortOrder.ASCENDING){
			jRadioButton_ascending.setSelected(true);
		}else{
			jRadioButton_descending.setSelected(true);
		}
	}
	// ソートキーの内容を更新するメソッド(対象コーパスを切り替えたときに使う)
	void update_sorter_key_list(E_Corpus corpus){
		jComboBox_key.removeAllItems();
		jComboBox_key.addItem("");
		if(corpus == E_Corpus.MAINITI){
			for(int i=0 ; i < KWIC_TableModel_MAI.columnArray.length ; i++)
				jComboBox_key.addItem( KWIC_TableModel_MAI.columnArray[i].columnName );
		}else{
			for(int i=0 ; i < KWIC_TableModel_IWA.columnArray.length ; i++)
				jComboBox_key.addItem( KWIC_TableModel_IWA.columnArray[i].columnName );
		}
	}
	// ソート条件をリセットするメソッド
	void reset () {
		jComboBox_key.setSelectedIndex(0);
		jRadioButton_ascending.setSelected(true);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gBC_L_padding2 = new GridBagConstraints();
		gBC_L_padding2.gridx = 3;
		gBC_L_padding2.gridy = 0;
		jLabel_padding2 = new JLabel();
		jLabel_padding2.setText("");
		jLabel_padding2.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_L_padding1 = new GridBagConstraints();
		gBC_L_padding1.gridx = 1;
		gBC_L_padding1.gridy = 0;
		jLabel_padding1 = new JLabel();
		jLabel_padding1.setText("");
		jLabel_padding1.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gGC_CB_key = new GridBagConstraints();
		gGC_CB_key.fill = GridBagConstraints.VERTICAL;
		gGC_CB_key.gridy = 0;
		gGC_CB_key.gridx = 2;
		GridBagConstraints gBC_L_padding_right = new GridBagConstraints();
		gBC_L_padding_right.gridx = 6;
		gBC_L_padding_right.fill = GridBagConstraints.HORIZONTAL;
		gBC_L_padding_right.weightx = 1.0D;
		gBC_L_padding_right.gridy = 0;
		jLabel_padding_right = new JLabel();
		jLabel_padding_right.setText("");
		jLabel_padding_right.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_RB_descending = new GridBagConstraints();
		gBC_RB_descending.gridx = 5;
		gBC_RB_descending.gridy = 0;
		GridBagConstraints gBC_RB_ascending = new GridBagConstraints();
		gBC_RB_ascending.gridx = 4;
		gBC_RB_ascending.gridy = 0;
		GridBagConstraints gBC_L_name = new GridBagConstraints();
		gBC_L_name.gridx = 0;
		gBC_L_name.anchor = GridBagConstraints.WEST;
		gBC_L_name.gridy = 0;
		jLabel_name = new JLabel();
		jLabel_name.setText(key_label);
		this.setSize(350, 200);
		this.setLayout(new GridBagLayout());
		this.add(jLabel_name, gBC_L_name);
		this.add(jLabel_padding1, gBC_L_padding1);
		this.add(getJComboBox_key(), gGC_CB_key);
		this.add(jLabel_padding2, gBC_L_padding2);
		this.add(getJRadioButton_ascending(), gBC_RB_ascending);
		this.add(getJRadioButton_descending(), gBC_RB_descending);
		this.add(jLabel_padding_right, gBC_L_padding_right);
		
		ButtonGroup sort_manner = new ButtonGroup();
		sort_manner.add(jRadioButton_ascending);
		sort_manner.add(jRadioButton_descending);
		jRadioButton_ascending.setSelected(true);
	}

	/**
	 * This method initializes jRadioButton_ascending	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_ascending() {
		if (jRadioButton_ascending == null) {
			jRadioButton_ascending = new JRadioButton();
			jRadioButton_ascending.setText("昇順");
			jRadioButton_ascending.setOpaque(false);
			jRadioButton_ascending
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							if( jRadioButton_ascending.isSelected() ){
								sort_order = SortOrder.ASCENDING;
							}else{
								sort_order = SortOrder.DESCENDING;
							}
						}
					});
		}
		return jRadioButton_ascending;
	}

	/**
	 * This method initializes jRadioButton_descending	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_descending() {
		if (jRadioButton_descending == null) {
			jRadioButton_descending = new JRadioButton();
			jRadioButton_descending.setText("降順");
			jRadioButton_descending.setOpaque(false);
		}
		return jRadioButton_descending;
	}

	/**
	 * This method initializes jComboBox_key	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox_key() {
		if (jComboBox_key == null) {
			jComboBox_key = new JComboBox();
			jComboBox_key.setBackground(ViewFrame.bg_color_of_tab_area);
		}
		return jComboBox_key;
	}
}

class AbstractSorter {
	int key_index;
	SortOrder order;
	
	AbstractSorter(int idx,SortOrder o){
		this.key_index = idx;
		this.order = o;
	}
}
