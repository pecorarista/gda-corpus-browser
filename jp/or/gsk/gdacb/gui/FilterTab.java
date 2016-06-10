package jp.or.gsk.gdacb.gui;

import java.util.HashSet;
import java.util.Set;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.gui.parts.JButtonHL;
import jp.or.gsk.gdacb.gui.parts.JButtonS;

import java.awt.Insets;

class FilterTab extends JPanel {
	private static final long serialVersionUID = 1L;

	int filter_combination = 1;
	
	private KWIC_View pv = null;	// parent KWIC View

	private Set<RowFilter<KWIC_TableModel,Integer>> filter_set =
		new HashSet<RowFilter<KWIC_TableModel,Integer>>(3);
	
	private FilterTab_KeyPanel jPanel_cond1 = null;
	private FilterTab_KeyPanel jPanel_cond2 = null;
	private FilterTab_KeyPanel jPanel_cond3 = null;
	private JPanel jPanel_op = null;
	private JRadioButton jRadioButton_and = null;
	private JRadioButton jRadioButton_or = null;
	private JButton jButton_filter = null;
	private JButtonS jButton_cancel = null;
	private JButtonS jButton_reset = null;
	private JPanel jPanel_and_or = null;
	private JLabel jLabel_padding_left = null;
	private JLabel jLabel_padding_right = null;

	/**
	 * Constructor
	 */
	public FilterTab(KWIC_View parent){
		super();
		this.pv = parent;
		initialize();
	}
	
	// フィルタタブで指定されているフィルタ条件をRowFilterとして返すメソッド
	RowFilter<KWIC_TableModel,Integer> getFilter () {
		RowFilter<KWIC_TableModel,Integer> filter;

		filter_set.clear();
		filter = jPanel_cond1.getFilter();
		if(filter != null) filter_set.add(filter);
		filter = jPanel_cond2.getFilter();
		if(filter != null) filter_set.add(filter);
		filter = jPanel_cond3.getFilter();
		if(filter != null) filter_set.add(filter);

		if(filter_set.size() == 0) return null;

		if( jRadioButton_and.isSelected() ) {
			return RowFilter.andFilter(filter_set);
		}else{
			return RowFilter.orFilter(filter_set);
		}
	}
	// 引数として与えられたフィルタ条件を画面に反映させるメソッド
	private void setAbstractFilterList(AbstractFilter[] af_list){
		jPanel_cond1.setAbstractFilter( af_list[0] );
		jPanel_cond2.setAbstractFilter( af_list[1] );
		jPanel_cond3.setAbstractFilter( af_list[2] );
	}
	// 複数フィルタの組み合わせ方(ANDかOR)を引数に取り、画面に反映させるメソッド
	private void set_filter_combination (int c){
		if(c == 1){
			jRadioButton_and.setSelected(true);
		}else if(c == 2){
			jRadioButton_or.setSelected(true);
		}
	}
	// フィルタキーの内容を更新するメソッド(対象コーパスを切り替えたときに使う)
	void update_filter_key_lists(E_Corpus corpus){
		jPanel_cond1.update_filter_key_list(corpus);
		jPanel_cond2.update_filter_key_list(corpus);
		jPanel_cond3.update_filter_key_list(corpus);
		reset_all();
	}
	// フィルタ条件の設定を記録するメソッド
	void save_filters() {
		pv.conf_kwic.abs_filter_list.set_list( getAbstractFilterList() );
		pv.conf_kwic.filter_combination.set( filter_combination );
	}

	// 抽象フィルターのリストを返すメソッド
	private AbstractFilter[] getAbstractFilterList() {
		AbstractFilter af1 = jPanel_cond1.getAbstractFilter();
		AbstractFilter af2 = jPanel_cond2.getAbstractFilter();
		AbstractFilter af3 = jPanel_cond3.getAbstractFilter();

		AbstractFilter[] af_list = new AbstractFilter[3];
		int i = 0;
		if(af1 != null){
			af_list[i] = af1;
			i++;
		}
		if(af2 != null){
			af_list[i] = af2;
			i++;
		}
		if(af3 != null){
			af_list[i] = af3;
			i++;
		}
		for( ; i < af_list.length ; i++){
			af_list[i] = null;
		}

		return af_list;
	}
	// フィルタリング条件をリセットするメソッド
	private void reset_all () {
		jPanel_cond1.reset();
		jPanel_cond2.reset();
		jPanel_cond3.reset();
		if(pv.initialized_flag) pv.cancel_filter();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gBC_L_padding_left = new GridBagConstraints();
		gBC_L_padding_left.gridx = 0;
		gBC_L_padding_left.weightx = 1.0D;
		gBC_L_padding_left.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_padding_left.gridy = 0;
		GridBagConstraints gBC_L_padding_right = new GridBagConstraints();
		gBC_L_padding_right.gridx = 4;
		gBC_L_padding_right.weightx = 6.0D;
		gBC_L_padding_right.gridy = 0;
		jLabel_padding_right = new JLabel();
		jLabel_padding_right.setText("");
		jLabel_padding_right.setPreferredSize(new Dimension(10, 10));
		jLabel_padding_left = new JLabel();
		jLabel_padding_left.setText("");
		jLabel_padding_left.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_P_and_or = new GridBagConstraints();
		gBC_P_and_or.gridx = 2;
		gBC_P_and_or.gridheight = GridBagConstraints.REMAINDER;
		gBC_P_and_or.weightx = 2.0D;
		gBC_P_and_or.fill = GridBagConstraints.BOTH;
		gBC_P_and_or.gridy = 0;
		GridBagConstraints gBC_P_op = new GridBagConstraints();
		gBC_P_op.gridx = 3;
		gBC_P_op.gridy = 0;
		gBC_P_op.weightx = 2.0D;
		gBC_P_op.fill = GridBagConstraints.VERTICAL;
		gBC_P_op.gridheight = GridBagConstraints.REMAINDER;
		GridBagConstraints gBC_P_cond3 = new GridBagConstraints();
		gBC_P_cond3.gridx = 1;
		gBC_P_cond3.gridwidth = 1;
		gBC_P_cond3.anchor = GridBagConstraints.WEST;
		gBC_P_cond3.weighty = 1.0D;
		gBC_P_cond3.weightx = 2.0D;
		gBC_P_cond3.fill = GridBagConstraints.HORIZONTAL;
		gBC_P_cond3.gridy = 2;
		GridBagConstraints gBC_P_cond2 = new GridBagConstraints();
		gBC_P_cond2.gridx = 1;
		gBC_P_cond2.gridwidth = 1;
		gBC_P_cond2.anchor = GridBagConstraints.WEST;
		gBC_P_cond2.weighty = 1.0D;
		gBC_P_cond2.weightx = 2.0D;
		gBC_P_cond2.fill = GridBagConstraints.HORIZONTAL;
		gBC_P_cond2.gridy = 1;
		GridBagConstraints gBC_P_cond1 = new GridBagConstraints();
		gBC_P_cond1.gridx = 1;
		gBC_P_cond1.gridwidth = 1;
		gBC_P_cond1.anchor = GridBagConstraints.WEST;
		gBC_P_cond1.weighty = 1.0D;
		gBC_P_cond1.weightx = 2.0D;
		gBC_P_cond1.fill = GridBagConstraints.HORIZONTAL;
		gBC_P_cond1.gridy = 0;
		this.setSize(600, 200);
		this.setLayout(new GridBagLayout());
		this.add(jLabel_padding_left, gBC_L_padding_left);
		this.add(jLabel_padding_right, gBC_L_padding_right);
		this.add(getJPanel_cond1(), gBC_P_cond1);
		this.add(getJPanel_cond2(), gBC_P_cond2);
		this.add(getJPanel_cond3(), gBC_P_cond3);
		this.add(getJPanel_and_or(), gBC_P_and_or);
		this.add(getJPanel_op(), gBC_P_op);
		
		ButtonGroup filter_and_or_group = new ButtonGroup();
		filter_and_or_group.add(jRadioButton_and);
		filter_and_or_group.add(jRadioButton_or);
		// jRadioButton_and.setSelected(true);
		
		update_filter_key_lists(pv.conf_kwic.target_corpus.get());
		setAbstractFilterList(pv.conf_kwic.abs_filter_list.get_list());
		set_filter_combination(pv.conf_kwic.filter_combination.get());
	}

	/**
	 * This method initializes jPanel_cond1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_cond1() {
		if (jPanel_cond1 == null) {
			jPanel_cond1 = new FilterTab_KeyPanel("条件1");
			jPanel_cond1.setOpaque(false);
			jPanel_cond1.setLayout(new GridBagLayout());
		}
		return jPanel_cond1;
	}

	/**
	 * This method initializes jPanel_cond2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_cond2() {
		if (jPanel_cond2 == null) {
			jPanel_cond2 = new FilterTab_KeyPanel("条件2");
			jPanel_cond2.setOpaque(false);
			jPanel_cond2.setLayout(new GridBagLayout());
		}
		return jPanel_cond2;
	}

	/**
	 * This method initializes jPanel_cond3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_cond3() {
		if (jPanel_cond3 == null) {
			jPanel_cond3 = new FilterTab_KeyPanel("条件3");
			jPanel_cond3.setOpaque(false);
			jPanel_cond3.setLayout(new GridBagLayout());
		}
		return jPanel_cond3;
	}

	/**
	 * This method initializes jPanel_op	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_op() {
		if (jPanel_op == null) {
			GridBagConstraints gBC_B_reset = new GridBagConstraints();
			gBC_B_reset.gridx = 1;
			gBC_B_reset.fill = GridBagConstraints.NONE;
			gBC_B_reset.weighty = 1.0D;
			gBC_B_reset.insets = new Insets(0, 5, 0, 5);
			gBC_B_reset.gridy = 1;
			GridBagConstraints gBC_B_cancel = new GridBagConstraints();
			gBC_B_cancel.gridx = 0;
			gBC_B_cancel.fill = GridBagConstraints.NONE;
			gBC_B_cancel.weighty = 1.0D;
			gBC_B_cancel.insets = new Insets(0, 5, 0, 5);
			gBC_B_cancel.gridy = 1;
			GridBagConstraints gBC_B_filter = new GridBagConstraints();
			gBC_B_filter.gridx = 0;
			gBC_B_filter.fill = GridBagConstraints.NONE;
			gBC_B_filter.gridwidth = 2;
			gBC_B_filter.weighty = 1.0D;
			gBC_B_filter.gridy = 0;
			jPanel_op = new JPanel();
			jPanel_op.setLayout(new GridBagLayout());
//			jPanel_op.setPreferredSize(new Dimension(75, 100));
			jPanel_op.setOpaque(false);
			jPanel_op.add(getJButton_filter(), gBC_B_filter);
			jPanel_op.add(getJButton_cancel(), gBC_B_cancel);
			jPanel_op.add(getJButton_reset(), gBC_B_reset);
		}
		return jPanel_op;
	}

	/**
	 * This method initializes jRadioButton_and	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_and() {
		if (jRadioButton_and == null) {
			jRadioButton_and = new JRadioButton();
			jRadioButton_and.setText("AND");
			jRadioButton_and.setVerticalTextPosition(SwingConstants.BOTTOM);
			jRadioButton_and.setToolTipText("複数のフィルタ条件を同時に満たす文だけを表示します");
			jRadioButton_and.setOpaque(false);
			jRadioButton_and.setHorizontalTextPosition(SwingConstants.CENTER);
			jRadioButton_and.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if(jRadioButton_and.isSelected()){
						filter_combination = 1;
					}else{
						filter_combination = 2;
					}
				}
			});
		}
		return jRadioButton_and;
	}

	/**
	 * This method initializes jRadioButton_or	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton_or() {
		if (jRadioButton_or == null) {
			jRadioButton_or = new JRadioButton();
			jRadioButton_or.setText("OR");
			jRadioButton_or.setHorizontalTextPosition(SwingConstants.CENTER);
			jRadioButton_or.setToolTipText("複数のフィルタ条件のいずれかを満たす文だけを表示します");
			jRadioButton_or.setOpaque(false);
			jRadioButton_or.setVerticalTextPosition(SwingConstants.BOTTOM);
		}
		return jRadioButton_or;
	}

	/**
	 * This method initializes jButton_filter	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_filter() {
		if (jButton_filter == null) {
			jButton_filter = new JButtonHL();
			jButton_filter.setText("フィルタ");
			jButton_filter.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					pv.filter(getFilter());
				}
			});
		}
		return jButton_filter;
	}

	/**
	 * This method initializes jButton_cancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButtonS getJButton_cancel() {
		if (jButton_cancel == null) {
			jButton_cancel = new JButtonS();
			jButton_cancel.setText(" 解除 ");
			jButton_cancel.setToolTipText("フィルタの適用を解除します");
			jButton_cancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					pv.cancel_filter();
				}
			});
		}
		return jButton_cancel;
	}

	/**
	 * This method initializes jButton_reset	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButtonS getJButton_reset() {
		if (jButton_reset == null) {
			jButton_reset = new JButtonS();
			jButton_reset.setText("クリア");
			jButton_reset.setToolTipText("このタブで設定したフィルタ条件をリセットします");
			jButton_reset.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					reset_all();
				}
			});
		}
		return jButton_reset;
	}

	/**
	 * This method initializes jPanel_and_or	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_and_or() {
		if (jPanel_and_or == null) {
			GridBagConstraints gBC_RB_or = new GridBagConstraints();
			gBC_RB_or.anchor = GridBagConstraints.CENTER;
			gBC_RB_or.gridy = 1;
			gBC_RB_or.gridx = 0;
			GridBagConstraints gBC_RB_and = new GridBagConstraints();
			gBC_RB_and.anchor = GridBagConstraints.CENTER;
			gBC_RB_and.gridy = 0;
			gBC_RB_and.weighty = 0.2D;
			gBC_RB_and.gridx = 0;
			jPanel_and_or = new JPanel();
			jPanel_and_or.setLayout(new GridBagLayout());
			jPanel_and_or.setBackground(new Color(227, 227, 252));
			jPanel_and_or.add(getJRadioButton_and(), gBC_RB_and);
			jPanel_and_or.add(getJRadioButton_or(), gBC_RB_or);
		}
		return jPanel_and_or;
	}
}
