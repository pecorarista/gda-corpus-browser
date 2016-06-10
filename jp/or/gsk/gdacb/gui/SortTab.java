package jp.or.gsk.gdacb.gui;

import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.gui.parts.JButtonHL;
import jp.or.gsk.gdacb.gui.parts.JButtonS;
import java.util.ArrayList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RowSorter;
import javax.swing.JButton;
import javax.swing.JLabel;

class SortTab extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private KWIC_View pv = null;	// parent KWIC view
	
	private SortTab_KeyPanel jPanel_key1 = null;
	private SortTab_KeyPanel jPanel_key2 = null;
	private SortTab_KeyPanel jPanel_key3 = null;
	private JPanel jPanel_op = null;
	private JButton jButton_sort = null;
	private JButton jButton_cancel = null;
	private JButton jButton_reset = null;
	private JLabel jLabel_padding_left = null;
	private JLabel jLabel_padding_right = null;

	SortTab(KWIC_View parent){
		super();
		this.pv = parent;
		initialize();
	}
	
	// ソートタブで指定されているソート条件を
	// RowSorterのSortKeyとして返すメソッド
	ArrayList<RowSorter.SortKey> getSortKeyList () {
		int res;
		AbstractSorter a_sorter;

		ArrayList<RowSorter.SortKey> skl = new ArrayList<RowSorter.SortKey>();
		a_sorter = jPanel_key1.getAbstractSorter();
		if(a_sorter != null){
			skl.add( new RowSorter.SortKey(a_sorter.key_index,a_sorter.order) );
		}
		a_sorter = jPanel_key2.getAbstractSorter();
		if(a_sorter != null){
			res = sort_key_duplication_check(skl,2,a_sorter);
			if(res == -1) return null;
			if(res == 0)
				skl.add( new RowSorter.SortKey(a_sorter.key_index,a_sorter.order) );
		}
		a_sorter = jPanel_key3.getAbstractSorter();
		if(a_sorter != null){
			res = sort_key_duplication_check(skl,3,a_sorter);
			if(res == -1) return null;
			if(res == 0)
				skl.add( new RowSorter.SortKey(a_sorter.key_index,a_sorter.order) );
		}
		// debug 
//		System.out.print("sort key =");
//		for(RowSorter.SortKey sk: skl) System.out.print(" "+sk.getColumn()+","+sk.getSortOrder());
//		System.out.print("\n");
		return skl;
	}
	// ソートキーの重複をチェック
	private int sort_key_duplication_check (ArrayList<RowSorter.SortKey> skl,int key_num,AbstractSorter a_sorter){
		for(int i=0 ; i < skl.size() ; i++){
			if(skl.get(i).getColumn() == a_sorter.key_index){
				if(skl.get(i).getSortOrder() != a_sorter.order) {
					JOptionPane.showMessageDialog(null,"第"+key_num+"キーが矛盾しています","ERROR",JOptionPane.ERROR_MESSAGE);
					return -1;
				}else{
					return 1;
				}
			}
		}
		return 0;
	}
	// 引数として与えられたソート条件を画面に反映させるメソッド
	private void setAbstractSorterList(AbstractSorter[] as_list) {
		jPanel_key1.setAbstractSorter(as_list[0]);
		jPanel_key2.setAbstractSorter(as_list[1]);
		jPanel_key3.setAbstractSorter(as_list[2]);
	}
	// ソートキーの内容を更新するメソッド(対象コーパスを切り替えたときに使う)
	void update_sorter_key_lists(E_Corpus corpus){
		jPanel_key1.update_sorter_key_list(corpus);
		jPanel_key2.update_sorter_key_list(corpus);
		jPanel_key3.update_sorter_key_list(corpus);
		reset_all();
	}
	// ソート条件の設定を記録するメソッド
	void save_sorters() {
		pv.conf_kwic.abs_sorter_list.set_list( getAbstractSorterList() );
	}
	
	// ソート条件をリセットするメソッド
	private void reset_all () {
		jPanel_key1.reset();
		jPanel_key2.reset();
		jPanel_key3.reset();
		if(pv.initialized_flag) pv.cancel_sort();
	}
	// 抽象ソーターのリストを返すメソッド
	private AbstractSorter[] getAbstractSorterList() {
		AbstractSorter as1 = jPanel_key1.getAbstractSorter();
		AbstractSorter as2 = jPanel_key2.getAbstractSorter();
		AbstractSorter as3 = jPanel_key3.getAbstractSorter();
		AbstractSorter[] as_list = new AbstractSorter[3];
		int i = 0;
		if(as1 != null){
			as_list[i] = as1;
			i++;
		}
		if(as2 != null){
			as_list[i] = as2;
			i++;
		}
		if(as3 != null){
			as_list[i] = as3;
			i++;
		}
		for( ; i < as_list.length ; i++){
			as_list[i] = null;
		}

		return as_list;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gBC_L_padding_right = new GridBagConstraints();
		gBC_L_padding_right.gridx = 3;
		gBC_L_padding_right.weightx = 6.0D;
		gBC_L_padding_right.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_padding_right.gridy = 0;
		jLabel_padding_right = new JLabel();
		jLabel_padding_right.setText("");
		jLabel_padding_right.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_L_padding_left = new GridBagConstraints();
		gBC_L_padding_left.gridx = 0;
		gBC_L_padding_left.weightx = 1.0D;
		gBC_L_padding_left.gridheight = GridBagConstraints.REMAINDER;
		gBC_L_padding_left.gridy = 0;
		jLabel_padding_left = new JLabel();
		jLabel_padding_left.setText("");
		jLabel_padding_left.setPreferredSize(new Dimension(10, 10));
		GridBagConstraints gBC_P_op = new GridBagConstraints();
		gBC_P_op.gridx = 2;
		gBC_P_op.gridy = 0;
		gBC_P_op.weightx = 2.0D;
		gBC_P_op.weighty = 0.0D;
		gBC_P_op.fill = GridBagConstraints.VERTICAL;
		gBC_P_op.insets = new Insets(0, 20, 0, 20);
		gBC_P_op.gridheight = GridBagConstraints.REMAINDER;
		GridBagConstraints gBC_STKP_key3 = new GridBagConstraints();
		gBC_STKP_key3.gridx = 1;
		gBC_STKP_key3.fill = GridBagConstraints.NONE;
		gBC_STKP_key3.weightx = 2.0D;
		gBC_STKP_key3.weighty = 1.0D;
		gBC_STKP_key3.anchor = GridBagConstraints.WEST;
		gBC_STKP_key3.gridy = 2;
		GridBagConstraints gBC_STKP_key2 = new GridBagConstraints();
		gBC_STKP_key2.gridx = 1;
		gBC_STKP_key2.fill = GridBagConstraints.NONE;
		gBC_STKP_key2.weightx = 2.0D;
		gBC_STKP_key2.weighty = 1.0D;
		gBC_STKP_key2.anchor = GridBagConstraints.WEST;
		gBC_STKP_key2.gridy = 1;
		GridBagConstraints gBC_STKP_key1 = new GridBagConstraints();
		gBC_STKP_key1.gridx = 1;
		gBC_STKP_key1.fill = GridBagConstraints.NONE;
		gBC_STKP_key1.weightx = 2.0D;
		gBC_STKP_key1.weighty = 1.0D;
		gBC_STKP_key1.anchor = GridBagConstraints.WEST;
		gBC_STKP_key1.gridy = 0;
		this.setSize(400, 200);
		this.setLayout(new GridBagLayout());
		this.add(jLabel_padding_left, gBC_L_padding_left);
		this.add(jLabel_padding_right, gBC_L_padding_right);
		this.add(getJPanel_key1(), gBC_STKP_key1);
		this.add(getJPanel_key2(), gBC_STKP_key2);
		this.add(getJPanel_key3(), gBC_STKP_key3);
		this.add(getJPanel_op(), gBC_P_op);
		
		update_sorter_key_lists(pv.conf_kwic.target_corpus.get());
        setAbstractSorterList(pv.conf_kwic.abs_sorter_list.get_list());
	}

	/**
	 * This method initializes jPanel_key1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_key1() {
		if (jPanel_key1 == null) {
			jPanel_key1 = new SortTab_KeyPanel("第1キー");
			jPanel_key1.setLayout(new GridBagLayout());
			jPanel_key1.setOpaque(false);
		}
		return jPanel_key1;
	}

	/**
	 * This method initializes jPanel_key2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_key2() {
		if (jPanel_key2 == null) {
			jPanel_key2 = new SortTab_KeyPanel("第2キー");
			jPanel_key2.setLayout(new GridBagLayout());
			jPanel_key2.setOpaque(false);
		}
		return jPanel_key2;
	}

	/**
	 * This method initializes jPanel_key3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_key3() {
		if (jPanel_key3 == null) {
			jPanel_key3 = new SortTab_KeyPanel("第3キー");
			jPanel_key3.setLayout(new GridBagLayout());
			jPanel_key3.setOpaque(false);
		}
		return jPanel_key3;
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
			GridBagConstraints gBC_B_sort = new GridBagConstraints();
			gBC_B_sort.gridx = 0;
			gBC_B_sort.fill = GridBagConstraints.NONE;
			gBC_B_sort.insets = new Insets(0, 0, 0, 0);
			gBC_B_sort.weighty = 1.0D;
			gBC_B_sort.gridwidth = 2;
			gBC_B_sort.gridy = 0;
			jPanel_op = new JPanel();
			jPanel_op.setLayout(new GridBagLayout());
			jPanel_op.add(getJButton_sort(), gBC_B_sort);
			jPanel_op.add(getJButton_cancel(), gBC_B_cancel);
			jPanel_op.add(getJButton_reset(), gBC_B_reset);
			jPanel_op.setOpaque(false);
		}
		return jPanel_op;
	}

	/**
	 * This method initializes jButton_sort	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_sort() {
		if (jButton_sort == null) {
			jButton_sort = new JButtonHL();
			jButton_sort.setText("ソート");
			jButton_sort.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					pv.sort();
				}
			});
		}
		return jButton_sort;
	}

	/**
	 * This method initializes jButton_cancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_cancel() {
		if (jButton_cancel == null) {
			jButton_cancel = new JButtonS();
			jButton_cancel.setText(" 解除 ");
			jButton_cancel.setToolTipText("例文の順序を元に戻します");
			jButton_cancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					pv.cancel_sort();
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
	private JButton getJButton_reset() {
		if (jButton_reset == null) {
			jButton_reset = new JButtonS();
			jButton_reset.setText("クリア");
			jButton_reset.setToolTipText("このタブで入力したソートキーをリセットします");
			jButton_reset.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					reset_all();
				}
			});
		}
		return jButton_reset;
	}
}
