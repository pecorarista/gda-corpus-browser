package jp.or.gsk.gdacb.gui;

import jp.or.gsk.gdacb.E_ColWA;
import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.E_FilterType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


class KWIC_JTable extends JTable {
	private static final long serialVersionUID = 1L;
	private static final Color color_cell_selected_background = new Color(204, 204, 204);

	private KWIC_View p_kv = null;	// parent KWIC_View
	EasyFilterPopupMenu easy_filter_popup = null;
	//private JPopupMenu jPopupMenu_on_cell;
	//private JMenuItem jMenuItem_copy_cb;
	//private JMenuItem jMenuItem_sel_row;
	
	KWIC_JTable () {
		super();
		initialize();
	}
	KWIC_JTable(int numRows, int numColumns){
		super(numRows,numColumns);
		initialize();
	}
	KWIC_JTable(Object[][] rowData, Object[] columnNames){
		super(rowData,columnNames);
		initialize();
	}
	KWIC_JTable(TableModel dm){
		super(dm);
		initialize();
	}
	KWIC_JTable(TableModel dm, TableColumnModel cm){
		super(dm,cm);
		initialize();
	}
	KWIC_JTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm){
		super(dm,cm,sm);
		initialize();
	}
	KWIC_JTable(Vector rowData, Vector columnNames){
		super(rowData,columnNames);
		initialize();
	}
	
	// このテーブルを含む KWIC_View クラスを記憶するメソッド
	void set_parent_KWIC_View (KWIC_View parent) {
		this.p_kv = parent;
	}
		
	// 列幅を設定するメソッド
	// 引数 conf = 設定オブジェクト
	void set_column_width (Conf_KWIC_View conf,E_Corpus corpus) {
		int row,col,w,max_width;
		TableCellRenderer ren = null;
		Component com = null;
		E_ColWA policy = null;
		
		TableColumnModel tcm = this.getColumnModel();
		KWIC_TableModel model = (KWIC_TableModel) this.getModel();

		policy = conf.column_width_adjustment.get();
//		System.out.println("policy: "+policy);
//		for(int i=0 ; i < tcm.getColumnCount() ; i++){ System.out.print(i+"->"+conf.displayed_column_posit_MAI.get(i)+" "); }; System.out.print("\n");
//		for(int i=0 ; i < tcm.getColumnCount() ; i++){ System.out.print(i+"->"+conf.column_width_MAI.get(i)+" "); }; System.out.print("\n");
			
		if(policy == E_ColWA.DEFAULT ||
		   (policy == E_ColWA.AUTO && this.getRowCount() == 0)){
			// デフォルトの幅を設定
			// 「自動調整」でかつテーブルが空のとき、列の幅はデフォルトにする
			for(col=0 ; col < tcm.getColumnCount() ; col++){
				tcm.getColumn(col).setPreferredWidth( model.columnArray[col].defaultColumnWidth );
			}
		}else if(policy == E_ColWA.AUTO){
			// 自動調整
			int row_num = this.getRowCount();
			for(col=0 ; col < tcm.getColumnCount() ; col++){
				max_width = 0;
				for(row=0 ; row < row_num ; row++){
					ren = this.getCellRenderer(row,col);
					com = ren.getTableCellRendererComponent(
							this, this.getValueAt(row,col), false, false, row, col);
					w = com.getPreferredSize().width;
					if(max_width < w) max_width = w;
				}
				// テーブルのヘッダーの最適な幅を求める
				ren = tcm.getColumn(col).getHeaderRenderer();
				if(ren == null) ren = this.getTableHeader().getDefaultRenderer();
				com = ren.getTableCellRendererComponent(
						this, tcm.getColumn(col).getHeaderValue(), false, false, -1, col);
				w = com.getPreferredSize().width;
				if(max_width < w) max_width = w;
				
				max_width += this.getIntercellSpacing().width;
				
				tcm.getColumn(col).setPreferredWidth(max_width);
			}
		}else if(policy == E_ColWA.CUSTOM){
			// ユーザが指定した幅を設定
			if(corpus == E_Corpus.MAINITI) {
				for(col=0 ; col < tcm.getColumnCount() ; col++){
					tcm.getColumn(col).setPreferredWidth( conf.column_width_MAI.get(col) );
				}
			}else{
				for(col=0 ; col < tcm.getColumnCount() ; col++){
					tcm.getColumn(col).setPreferredWidth( conf.column_width_IWA.get(col) );
				}
			}
		}
	}
	
	// テーブルの各列のアピアランス(TableCellRenderer)を設定するメソッド
	void set_column_appearance () {
		TableColumnModel tcm = this.getColumnModel();
		KWIC_TableModel model = (KWIC_TableModel) this.getModel();
		for(int i=0 ; i < tcm.getColumnCount() ; i++){
			tcm.getColumn(i).setCellRenderer( model.cell_renderer_list[i] );
		}
	}
	
	/* 列の表示位置を決めるアルゴリズム (ユーザのマウス操作による列の入れ換えに対応)
	 * 
	 *   現在の列の並びをS、目標とする列の並びをGとする
	 *       [0] [1] [2] [3] [4] [5]
	 *   S =  0   1   2   3   4   5
	 *   G =  5   2   0   3   1   4
	 *   
	 *   (1)GとSが一致しない箇所のうち、最もインデックスが小さいものを求める
	 *      その位置に正しい要素が入るように列の移動をする
	 *   (2)列の移動を実現するコマンドは JTable#moveColumn(i,j) で、
	 *      iは移動する要素のSにおけるインデックス、jはGにおけるインデックスである
	 *   (3) S を更新する。
	 *   これを最後まで繰り返す
	 *   
	 *   先の例では、
	 *   (1) 一致していないのは[0]の位置、したがって、5を[0]の位置に移動する
	 *   (2) iはSにおける5の位置[5], jはGにおける5の位置[0]になる。
	 *       したがって、JTable#moveColumn(5,0)を実行する
	 *   (3) S を更新する。更新後は以下のようになる
	 *       [0] [1] [2] [3] [4] [5]
	 *   S =  5   0   1   2   3   4
	 *   G =  5   2   0   3   1   4
		
	*/
	/**
	 * 検索を実行したときに、列の並びを復元するメソッド
	 */
	void set_column_posit_when_search(Conf_KWIC_View conf,E_Corpus corpus){
		int len = (corpus == E_Corpus.MAINITI) ? conf.displayed_column_posit_MAI.size() : conf.displayed_column_posit_IWA.size();
		// 現状の列の並び(入れ換えをするのでArrayListを使う)
		ArrayList<Integer> state = new ArrayList<Integer>(len);
		// ゴールの列の並び(入れ換えないので配列を使う)
		int[] goal = new int[len];
		
		// 現在状態S、ゴールGを作成する
		// S = デフォルトの列の並び
		// G = 現状の列の並び
		for(int p=0 ; p < len ; p++){
			state.add(p);
			int i = (corpus == E_Corpus.MAINITI) ? conf.displayed_column_posit_MAI.get(p) : conf.displayed_column_posit_IWA.get(p);
			goal[i] = p; 
		}
		set_column_posit_body(state,goal);
	}
	/**
	 * 列のリセットボタンを実行したときに、列の並びを復元するメソッド
	 */
	void set_column_posit_when_reset(Conf_KWIC_View conf,E_Corpus corpus){
		int len = (corpus == E_Corpus.MAINITI) ? conf.displayed_column_posit_MAI.size() : conf.displayed_column_posit_IWA.size();
		// 現状の列の並び(入れ換えをするのでArrayListを使う)
		ArrayList<Integer> state = new ArrayList<Integer>(len);
		// ゴールの列の並び(入れ換えないので配列を使う)
		int[] goal = new int[len];
		
		// 現在状態S、ゴールGを作成する
		// S = 現状の列の並び
		// G = デフォルトの列の並び
		for(int p=0 ; p < len ; p++) state.add(0);	// ArrayListの初期化
		for(int p=0 ; p < len ; p++){
			int i = (corpus == E_Corpus.MAINITI) ? conf.displayed_column_posit_MAI.get(p) : conf.displayed_column_posit_IWA.get(p);
			state.set(i,p);
			goal[p] = p; 
		}
		set_column_posit_body(state,goal);
	}
	/**
	 * 現状の並び、目標の並びを引数に取り、実際に列の並びかえをするメソッド
	 */
	private void set_column_posit_body(ArrayList<Integer> state,int[] goal){
		int len,s_idx,g_idx,col;
		len = goal.length;
//		System.out.println(">> initial");
//		for(int i=0 ; i < len ; i++){ System.out.print(state.get(i)+" "); }; System.out.print("\n");
//		for(int i=0 ; i < len ; i++){ System.out.print(goal[i]+" "); }; System.out.print("\n");
		// 反復
		for(g_idx=0 ; g_idx < len ; g_idx++){
			if(state.get(g_idx) == goal[g_idx]) continue;
//			System.out.println(">> "+g_idx);
//			for(int i=0 ; i < len ; i++){ System.out.print(state.get(i)+" "); }; System.out.print("\n");
//			for(int i=0 ; i < len ; i++){ System.out.print(goal[i]+" "); }; System.out.print("\n");
			// 位置 g_idx に正しい列を移動する
			s_idx = state.indexOf( goal[g_idx] );
			this.moveColumn(s_idx,g_idx);
//			System.out.println("move "+s_idx+" to "+g_idx);
			// 現在の列の並びを更新する
			col = state.get(s_idx);
			state.remove(s_idx);
			state.add(g_idx,col);
		}
	}
	void clear_easy_filter_popup_menu(){
		this.easy_filter_popup.clear_items();
	}
	/**
	 * 選択したセルの内容をクリップボードにコピーするメソッド
	 * → KWIC_View の中へ移動
	private void copy_clipboard (java.awt.event.ActionEvent e) {
		String s;
		int row = this.getSelectedRow();
		int col = this.getSelectedColumn();
		int col_in_model = this.getColumnModel().getColumn(col).getModelIndex();
		Class<?> cl = this.getColumnClass(col_in_model);
		if(cl == String.class){
			s = (String) this.getValueAt(row,col);
		}else if(cl == Integer.class){
			s = Integer.toString( (Integer)this.getValueAt(row,col) );
		}else{
			return;
		}
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new StringSelection(s),null);
	}
	 */
	/**
	 * 現在のセルを含む行を選択するメソッド
	 * → KWIC_View の中へ移動
	private void select_row () {
		this.addColumnSelectionInterval(0, this.getColumnCount()-1);
	}
	 */

	private void initialize () {
		this.setDefaultEditor(Object.class, null);
		this.setCellSelectionEnabled(true);
		this.setSelectionForeground(Color.black);
		this.setSelectionBackground(color_cell_selected_background);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		this.setAutoCreateRowSorter(true);
		
		// テーブルのセルをクリックしたとき、セルの内容をテキストフィールドに表示するイベントリスナーを設定
		this.addMouseListener(new java.awt.event.MouseAdapter() {   
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3){
					if(p_kv == null) return;
					// 左クリック or 右クリック → 表示エリアにセルの内容を表示
					Point pt = e.getPoint();
					KWIC_JTable tbl = (KWIC_JTable) e.getSource();
					TableColumnModel tcm = tbl.getColumnModel();
					KWIC_TableModel model = (KWIC_TableModel) tbl.getModel();
					int column_idx_in_view = tbl.columnAtPoint(pt);
					
					String header = (String) tcm.getColumn( column_idx_in_view ).getHeaderValue();
					if(header.equals("ID")) return;	// IDは整数なので表示しない

					String cell = (String) tbl.getValueAt(tbl.rowAtPoint(pt), tbl.columnAtPoint(pt));

					boolean sense_flag = false;
					if(tcm.getColumn(column_idx_in_view).getModelIndex() == model.idx_of_sense_abst){
						sense_flag = true;
					}
					p_kv.fill_Text_of_Table_Cell(cell,sense_flag);
				}
				if(e.getButton() == MouseEvent.BUTTON3){
					// 右クリック → クリックしたセルを選択する
					Point pt = e.getPoint();
					KWIC_JTable tbl = (KWIC_JTable) e.getSource();
					tbl.changeSelection(tbl.rowAtPoint(pt), tbl.columnAtPoint(pt), false, false);
					// ポップアップメニューを表示
					p_kv.show_popupmenu_on_table(e);
					//jPopupMenu_on_cell.show( (KWIC_JTable)e.getSource(), e.getPoint().x, e.getPoint().y );
				}
			}
		});
		// ヘッダ上でマウスがクリックされたとき、「ソート」のチェックボックスをONにするイベントリスナーを設定
		this.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				// 左クリックのみ実行
				if(e.getButton() == MouseEvent.BUTTON1){
					if(p_kv != null) p_kv.set_sort_check_button_on(true);						
				}
			}
		});
		// テーブル上で右クリックしたとき、「コピー」のポップアップメニューを表示
		//   setComponentPopupMenuを設定するとマウスイベントのリスナーの動作を上書してしまう
		//   よって、ポップアップメニューの表示はリスナーの中で行うようにする
		//   → ポップアップメニューは行ヘッダと共有するように変更した。
		//     そのため、ポップアップメニューは KWIC_View の中で用意する。
		//this.setComponentPopupMenu(getJPopupMenu_on_cell());
		//jPopupMenu_on_cell = getJPopupMenu_on_cell();
		
		// ヘッダーのアピアランスの設定
		DefaultTableCellRenderer tcr_header = (DefaultTableCellRenderer) this.getTableHeader().getDefaultRenderer();
		this.getTableHeader().setDefaultRenderer(new KWIC_Table_HeaderRenderer(tcr_header));
		this.getTableHeader().setBackground(KWIC_View.color_table_header_background);
		
		this.easy_filter_popup = new EasyFilterPopupMenu();
		this.getTableHeader().setComponentPopupMenu(this.easy_filter_popup);
	}
	
	/* → KWIC_View の中へ移動
	private JPopupMenu getJPopupMenu_on_cell() {
		if (jPopupMenu_on_cell == null) {
			jPopupMenu_on_cell = new JPopupMenu();
			jPopupMenu_on_cell.add(getJMenuItem_copy_cb());
			jPopupMenu_on_cell.add(getJMenuItem_sel_row());
		}
		return jPopupMenu_on_cell;
	}
	private JMenuItem getJMenuItem_copy_cb() {
		if (jMenuItem_copy_cb == null) {
			jMenuItem_copy_cb = new JMenuItem();
			jMenuItem_copy_cb.setText("セルをコピー");
			jMenuItem_copy_cb.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					copy_clipboard(e);
				}
			});
		}
		return jMenuItem_copy_cb;
	}
	private JMenuItem getJMenuItem_sel_row() {
		if (jMenuItem_sel_row == null) {
			jMenuItem_sel_row = new JMenuItem();
			jMenuItem_sel_row.setText("行を選択");
			jMenuItem_sel_row.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					select_row();
				}
			});
		}
		return jMenuItem_sel_row;
	}
	*/

	/** ヘッダーのアピアランスを設定するための内部クラス
	  *   左寄せ
	  *   ラベルの左端とセルの境界に隙間をあける
	  */
	private class KWIC_Table_HeaderRenderer implements TableCellRenderer {
		private final TableCellRenderer tcr;
		public KWIC_Table_HeaderRenderer(TableCellRenderer tcr) {
			this.tcr  = tcr;
		}
		@Override
		public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isS, boolean hasF, int row, int col) {
			JLabel l = (JLabel)tcr.getTableCellRendererComponent(tbl, val, isS, hasF, row, col);
			l.setHorizontalAlignment(SwingConstants.LEFT);
			
			// デフォルトのセルのボーダーの内側に、
			// 左だけ1ピクセルの空間をあける empty border を組み合わせた
			// CompoundBorder を作成し、セットする。
			Border border = UIManager.getBorder( "TableHeader.cellBorder" ); 
			Border setBorder = BorderFactory.createCompoundBorder( border, new EmptyBorder( 0, 1, 0, 0 ) );
			l.setBorder(setBorder);
			
			return l;
		}
	}
	/**
	 * 簡易フィルタリングのためのポップアップメニューの内部クラス
	 */
	private class EasyFilterPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		// ポップアップメニューに表示する項目の最大値
		private static final int max_item_num = 20;
		// 作成されたメニューのカラムを保持する
		private int current_table_model_column = -1;

		public EasyFilterPopupMenu(){
			super();
			this.add(new AbstractAction("(フィルタ解除)"){
				private static final long serialVersionUID = 1L;
				// フィルタリングを解除するアクション
				@Override
				public void actionPerformed(ActionEvent e){
					p_kv.cancel_filter();
				}
			});
			this.addSeparator();
		}
		@Override
		public void show(Component c, int x, int y) {
		    JTableHeader table_header = (JTableHeader)c;
		    int column = table_header.columnAtPoint(new Point(x, y));
		    boolean flag = update_menu_items(table_header,column);
		    if(flag) super.show(c, x, y);
		}
		/**
		 *  (最初の「(フィルタ解除)」「セパレータ」を除いて)ポップアップメニューを消去する
		 */
		private void clear_items(){
			for(int i=this.getComponentCount()-1 ; i >= 2 ; i--) this.remove(i);
			this.current_table_model_column = -1;
		}
		/**
		 * ポップアップメニューの作成/更新を行う
		 * @param th	テーブルヘッダー
		 * @param col	フィルタ条件を設定する表の列
		 * @return		ポップアップメニューを表示するか否か
		 */
		private boolean update_menu_items(JTableHeader th,int col){
			int row;
			String s;
			TreeSet<String> element_set;
			E_FilterType filter_type;

			KWIC_JTable table = (KWIC_JTable)th.getTable();
			int col_in_model = table.getColumnModel().getColumn(col).getModelIndex();
			KWIC_TableModel tm = (KWIC_TableModel)table.getModel();
			
			// ID の列はポップアップメニューを表示しない
			if(tm.getColumnName(col_in_model).equals("ID")) return false;
			// 前回表示した列と同じなら、メニューは更新しない
			if(this.current_table_model_column == col_in_model) return true;
			
			// 古いメニューアイテムを消去する
			clear_items();
			
			if(tm.idx_of_left_context_abst == col_in_model){
				// 「左文脈」のとき、文字列を右方向に見た順でメニューアイテムをソート
				element_set = new TreeSet<String>(new ReverseStringComparator());
				filter_type = E_FilterType.EQUAL;
			}else if(tm.idx_of_sense_abst == col_in_model){
				// 「語義」のとき、キーを含むという条件でフィルタリング
				element_set = new TreeSet<String>();
				filter_type = E_FilterType.CONTAIN;
			}else{
				element_set = new TreeSet<String>();
				filter_type = E_FilterType.EQUAL;
			}
			// 簡易フィルタの対象となる列のクラスは全て文字列と仮定する
			//Class<?> cl = table.getColumnClass(col_in_model);
			//if(cl == String.class){
				if(tm.idx_of_sense_abst == col_in_model){
					for(row=0 ; row < tm.getRowCount(); row++){
						s = (String) tm.getValueAt(row,col_in_model);
						for(String s2: s.split(" ")) element_set.add(s2);
					}
				}else{
					for(row=0 ; row < tm.getRowCount(); row++){
						s = (String) tm.getValueAt(row,col_in_model);
						element_set.add(s);
					}
				}
			//}else if(cl == Integer.class){
			//	for(row=0 ; row < tm.getRowCount(); row++){
			//		s = Integer.toString( (Integer)tm.getValueAt(row,col_in_model) );
			//		element_set.add(s);
			//	}
			//};

			int item_count = 0; 
			for(String elm: element_set){
				if(max_item_num <= item_count) break;
				if(elm.equals("")){
					this.add(new FilterAction(col_in_model,filter_type));
				}else{
					this.add(new FilterAction(elm,col_in_model,filter_type));
				}
				item_count++;
			}
			if(item_count < element_set.size()){
				int rest = element_set.size() - item_count;
				this.add("(... "+rest+" items more)");
			}
			this.current_table_model_column = col_in_model;
			return true;
		}
	}
	/**
	 * 簡易フィルタを実行するアクションの内部クラス
	 */
	private class FilterAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private final String filter_key;
		private final int target_column;
		private final E_FilterType filter_type;
		public FilterAction(String menu_title,int col,E_FilterType ft){
			super(menu_title);
			this.filter_key = menu_title;
			this.target_column = col;
			this.filter_type = ft;
		}
		public FilterAction(int col,E_FilterType ft){	// フィルターキーが空列のとき
			super("[空列]");
			this.filter_key = "";
			this.target_column = col;
			this.filter_type = ft;
		}
		@Override
		public void actionPerformed(ActionEvent e){
			RowFilter<KWIC_TableModel,Integer> fil;
			if(this.filter_type == E_FilterType.EQUAL){
				fil = new RowFilter<KWIC_TableModel,Integer>(){
					@Override
					public boolean include(Entry<? extends KWIC_TableModel, ? extends Integer> entry) {
						String s = entry.getStringValue(target_column);
                        return s.equals(filter_key);
					}
				};
			}else if(this.filter_type == E_FilterType.CONTAIN){
				fil = new RowFilter<KWIC_TableModel,Integer>(){
					@Override
					public boolean include(Entry<? extends KWIC_TableModel, ? extends Integer> entry) {
						String s = entry.getStringValue(target_column);
						if(filter_key.equals("")){
							return s.equals("") ? true : false;
						}else{
							return s.contains(filter_key);
						}
					}
				};
			}else{
				fil = null;
			}
			p_kv.filter(fil);
		}
	}
	/**
	 * 文字列を右から左に見たときの順にソートするためのComparator (内部クラス)
	 */
	private class ReverseStringComparator implements Comparator<String> {
		@Override
		public int compare(String a, String b) {
			int a_len = a.length();
			int b_len = b.length();
			int min_len = (a_len < b_len) ? a_len : b_len; 
			for(int i=1 ; i <= min_len ; i++) {
				if(a.charAt(a_len-i) == b.charAt(b_len-i)) continue;
				return a.charAt(a_len-i) - b.charAt(b_len-i);
			}
			return a_len - b_len;
		}
	}
}
