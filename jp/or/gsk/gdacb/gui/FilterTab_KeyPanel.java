package jp.or.gsk.gdacb.gui;

import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.E_FilterType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.RowFilter;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

/**
 *  フィルタの1つの条件を入力するパネル
 * @author kshirai
 *
 */
class FilterTab_KeyPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private String key_label = null;
	private int filter_key_index = -1;
	private String filter_cond_str = null;
	private JComboBox jComboBox_key = null;
	private JTextField jTextField_filter = null;
	private JCheckBox jCheckBox_not = null;
	private JComboBox jComboBox_cond = null;
	private JLabel jLabel_padding1 = null;
	private JLabel jLabel_padding2 = null;
	private JLabel jLabel_padding3 = null;

	/**
	 * This is the default constructor
	 */
	FilterTab_KeyPanel(String klabel){
		super();
		this.key_label = klabel;
		initialize();
	}
	
	// 指定されているフィルタ条件をRowFilterとして返すメソッド
	RowFilter<KWIC_TableModel,Integer> getFilter () {
		RowFilter<KWIC_TableModel,Integer> filter = null; 

		filter_key_index = jComboBox_key.getSelectedIndex();
		if(filter_key_index == 0) {
			return null;
		}else{
			filter_key_index--;
		}

		filter_cond_str = jTextField_filter.getText().trim();

		boolean not_flag = jCheckBox_not.isSelected();
		
		E_FilterType filter_type = getFilterType();
		if(filter_type == E_FilterType.NONE) {
			return null;
		}else if(filter_type == E_FilterType.CONTAIN) {
			if( filter_cond_str.equals("") ) return null;
			filter = new RowFilter<KWIC_TableModel,Integer>() {
				@Override
				public boolean include(Entry<? extends KWIC_TableModel, ? extends Integer> entry) {
					String s = entry.getStringValue(filter_key_index);
					return s.contains(filter_cond_str);
				}
			};
			return not_flag ? RowFilter.notFilter(filter) : filter;
		}else if(filter_type == E_FilterType.BEGIN){
			if( filter_cond_str.equals("") ) return null;
			filter = new RowFilter<KWIC_TableModel,Integer>() {
				@Override
				public boolean include(Entry<? extends KWIC_TableModel, ? extends Integer> entry) {
					String s = entry.getStringValue(filter_key_index);
					return s.startsWith(filter_cond_str);
				}
			};
			return not_flag ? RowFilter.notFilter(filter) : filter;
		}else if(filter_type == E_FilterType.END) {
			if( filter_cond_str.equals("") ) return null;
			filter = new RowFilter<KWIC_TableModel,Integer>() {
				@Override
				public boolean include(Entry<? extends KWIC_TableModel, ? extends Integer> entry) {
					String s = entry.getStringValue(filter_key_index);
					return s.endsWith(filter_cond_str);
				}
			};
			return not_flag ? RowFilter.notFilter(filter) : filter;
		}else if(filter_type == E_FilterType.EQUAL) {
			filter = new RowFilter<KWIC_TableModel,Integer>() {
				@Override
				public boolean include(Entry<? extends KWIC_TableModel, ? extends Integer> entry) {
					String s = entry.getStringValue(filter_key_index);
					return s.equals(filter_cond_str);
				}
			};
			return not_flag ? RowFilter.notFilter(filter) : filter;
		}else if(filter_type == E_FilterType.REGEXP) {
			if( filter_cond_str.equals("") ) return null;
			filter = new RowFilter<KWIC_TableModel,Integer>() {
				@Override
				public boolean include(Entry<? extends KWIC_TableModel, ? extends Integer> entry) {
					String s = entry.getStringValue(filter_key_index);
					Pattern p = Pattern.compile(filter_cond_str);
					Matcher m = p.matcher(s);
					return m.find();
				}
			};
			return not_flag ? RowFilter.notFilter(filter) : filter;
		}else{
			System.err.println("Out of Index in FILTER CONDITIONS");
			return null;
		}

		/* フィルタメモ
		RowFilter<KwicTableModel,Integer> filter1 = new RowFilter<KwicTableModel,Integer>() {
			@Override
			public boolean include(Entry<? extends KwicTableModel, ? extends Integer> entry) {
				// entry はテーブルの1行を表す
				// entry.getModel()			元のTableModeを得る
				// entry.getValue(i)		i番目(インデックスがi)の列のオブジェクトを返す
				// entry.getStringValue(i)	i番目(インデックスがi)の列の文字列を返す
				// entry.getIdentifier()	行番号(行のインデックス)を返す

				return true;	// フィルタを通すときはtrue、それ以外はfalseを返す
			};
		};
		*/
	}
	// 画面に設定されているソートの条件を返すメソッド
	AbstractFilter getAbstractFilter () {
		int key_idx = jComboBox_key.getSelectedIndex();
		String key = jTextField_filter.getText().trim();
		E_FilterType type = getFilterType();

		if(key_idx == 0) return null;
		key_idx--;	// 最初の項目は""なので、1を引く
		
		if(type == E_FilterType.NONE) return null;

		if(key.equals("") && type != E_FilterType.EQUAL)
			return null;
		
		return new AbstractFilter( key_idx, key, type, jCheckBox_not.isSelected() );
	}
	// 引数として与えられたフィルタ条件を画面に反映させるメソッド
	void setAbstractFilter(AbstractFilter af){
		if(af == null) return;
		jComboBox_key.setSelectedIndex(af.filter_key_index + 1);
		jTextField_filter.setText( af.filter_key );
		jComboBox_cond.setSelectedIndex( af.filter_type.index() );
		jCheckBox_not.setSelected( af.not_flag );
	}
	// フィルタキーの内容を更新するメソッド(対象コーパスを切り替えたときに使う)
	void update_filter_key_list(E_Corpus corpus){
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
	// フィルタ条件をリセットするメソッド
	void reset () {
		jComboBox_key.setSelectedIndex(0);
		jTextField_filter.setText("");
		jComboBox_cond.setSelectedIndex(0);
		jCheckBox_not.setSelected(false);
	}

	// フィルタタイプを返すメソッド
	private E_FilterType getFilterType () {
		int idx = jComboBox_cond.getSelectedIndex();
		for(E_FilterType fs: E_FilterType.values()){
			if(idx == fs.index()){
				return fs;
			}
		}
		return E_FilterType.NONE;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gBC_L_padding3 = new GridBagConstraints();
		gBC_L_padding3.gridx = 6;
		gBC_L_padding3.gridy = 0;
		jLabel_padding3 = new JLabel();
		jLabel_padding3.setText("");
		jLabel_padding3.setPreferredSize(new Dimension(5, 10));
		GridBagConstraints gBC_L_padding2 = new GridBagConstraints();
		gBC_L_padding2.gridx = 4;
		gBC_L_padding2.gridy = 0;
		jLabel_padding2 = new JLabel();
		jLabel_padding2.setText("");
		jLabel_padding2.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_L_padding1 = new GridBagConstraints();
		gBC_L_padding1.gridx = 2;
		gBC_L_padding1.gridy = 0;
		jLabel_padding1 = new JLabel();
		jLabel_padding1.setText("");
		jLabel_padding1.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_CB_cond = new GridBagConstraints();
		gBC_CB_cond.fill = GridBagConstraints.NONE;
		gBC_CB_cond.gridy = 0;
		gBC_CB_cond.gridx = 5;
		GridBagConstraints gBC_CB_not = new GridBagConstraints();
		gBC_CB_not.gridx = 7;
		gBC_CB_not.anchor = GridBagConstraints.WEST;
		gBC_CB_not.gridy = 0;
		GridBagConstraints gBC_TF_filter = new GridBagConstraints();
		gBC_TF_filter.fill = GridBagConstraints.HORIZONTAL;
		gBC_TF_filter.gridy = 0;
		gBC_TF_filter.weightx = 0.0D;
		gBC_TF_filter.anchor = GridBagConstraints.WEST;
		gBC_TF_filter.gridx = 3;
		GridBagConstraints gBC_L_name = new GridBagConstraints();
		gBC_L_name.gridx = 0;
		gBC_L_name.anchor = GridBagConstraints.WEST;
		gBC_L_name.weightx = 0.0D;
		gBC_L_name.gridy = 0;
//		jLabel_name = new JLabel();
//		jLabel_name.setText(key_label);
//		jLabel_name.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_CB_key = new GridBagConstraints();
		gBC_CB_key.fill = GridBagConstraints.NONE;
		gBC_CB_key.gridy = 0;
		gBC_CB_key.anchor = GridBagConstraints.WEST;
		gBC_CB_key.gridx = 1;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		// キーの名称(第1キーなど)、とりあえず保留
		this.add(getJComboBox_key(), gBC_CB_key);
		this.add(jLabel_padding1, gBC_L_padding1);
		this.add(getJTextField_filter(), gBC_TF_filter);
		this.add(jLabel_padding2, gBC_L_padding2);
		this.add(getJComboBox_cond(), gBC_CB_cond);
		this.add(jLabel_padding3, gBC_L_padding3);
		this.add(getJCheckBox_not(), gBC_CB_not);
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

	/**
	 * This method initializes jTextField_filter	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_filter() {
		if (jTextField_filter == null) {
			jTextField_filter = new JTextField();
			// フォントサイズを変えたとき、PreferredSize と MinimumSize の両方を設定しておかないと
			// フォームの幅がつぶれてしまう
			jTextField_filter.setPreferredSize(new Dimension(150, 28));
			jTextField_filter.setMinimumSize(new Dimension(150, 28));
		}
		return jTextField_filter;
	}

	/**
	 * This method initializes jCheckBox_not	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox_not() {
		if (jCheckBox_not == null) {
			jCheckBox_not = new JCheckBox();
			jCheckBox_not.setText("NOT");
			jCheckBox_not.setOpaque(false);
		}
		return jCheckBox_not;
	}

	/**
	 * This method initializes jComboBox_cond	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox_cond() {
		if (jComboBox_cond == null) {
			jComboBox_cond = new JComboBox();
			jComboBox_cond.setBackground(ViewFrame.bg_color_of_tab_area);
			for(E_FilterType ft: E_FilterType.values()) jComboBox_cond.addItem(ft.label());
			jComboBox_cond.setSelectedIndex(0);
		}
		return jComboBox_cond;
	}
}

class AbstractFilter {
	int filter_key_index;
	String filter_key;
	E_FilterType filter_type;
	boolean not_flag;
	
	AbstractFilter (int idx,String key,E_FilterType ft,boolean n) {
		this.filter_key_index = idx;
		this.filter_key = key;
		this.filter_type = ft;
		this.not_flag = n;
	}
}
