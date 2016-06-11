package jp.or.gsk.gdacb.gui.parts;

// テーブルに行ヘッダを追加する
// 情報源: http://www.ne.jp/asahi/hishidama/home/tech/java/swing/JTable.html#h_JTableHeader

import java.awt.Color;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


// 行ヘッダー用のJTable

public class RowHeaderTable extends JTable {
	private static final long serialVersionUID = 1L;

	private JTable data_table = null;
	private static final Color row_header_fg_color = new Color(0,255,0);

	/**
	 * 行ヘッダーテーブル コンストラクター
	 *
	 * @param dataTable	データ用JTable
	 * @param name		行ヘッダーの列見出し
	 * @param width		行ヘッダーの幅
	 */
	public RowHeaderTable(JTable dataTable, String name, int width) {
		super(new RowHeaderDataModel(dataTable), null, new RowHeaderSelectionModel(dataTable));
		this.data_table = dataTable;

		// 唯一の列
		{
			TableColumn tc = new TableColumn(0, width);

			// 中央揃え・背景灰色のレンダラーを登録する
			DefaultTableCellRenderer r = new DefaultTableCellRenderer();
			r.setHorizontalAlignment(SwingConstants.CENTER);
			r.setForeground(row_header_fg_color);
			r.setBackground(super.getTableHeader().getBackground());
			tc.setCellRenderer(r);

			tc.setHeaderValue(name);	// 列見出し(項目名)
			tc.setResizable(false);		// サイズ変更禁止

			super.addColumn(tc);
		}

		// 列ヘッダー(列見出し)
		{
			JTableHeader h = super.getTableHeader();
			h.setReorderingAllowed(false); // 列の入れ替え(ドラッグ)を禁止
			//h.addMouseListener(new RowHeaderCHMouseListener(dataTable)); // ソート解除処理を登録
		}

		// データ用テーブルにソーターがある場合、リスナーを登録する
		RowSorter<? extends TableModel> sort = dataTable.getRowSorter();
		if (sort != null) {
			sort.addRowSorterListener(new RowHeaderSortListener(this));
		}
		//super.setEnabled(false);
		
		// 行ヘッダーを選択したときの色を指定
		this.setSelectionForeground(row_header_fg_color);
		this.setSelectionBackground(this.data_table.getSelectionBackground());
	}
}

// 行ヘッダーのデータモデル（TableModel）
class RowHeaderDataModel extends DefaultTableModel implements TableModelListener {
	private static final long serialVersionUID = 1L;

	protected JTable dataTable;

	public RowHeaderDataModel(JTable dataTable) {
		this.dataTable = dataTable; // データ用テーブル

		TableModel dataModel = dataTable.getModel();
		dataModel.removeTableModelListener(this);
		dataModel.addTableModelListener(this);
	}

	@Override
	public Object getValueAt(int row, int column) {
		// データ用テーブルの行番号を返す
		// RowHeaderTable自身はソートやフィルタリングは行われない為、引数rowは表示用行番号と一致している
		//return dataTable.convertRowIndexToModel(row) + 1;
		return "■";
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Integer.class;
	}
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// 値の登録は行わない
	}
	@Override
	public boolean isCellEditable(int row, int column) {
		return false; // 編集不可
	}
	@Override
	public int getRowCount() {
		if (dataTable != null) {
			return dataTable.getRowCount(); // データテーブルの表示行数を返す
		}
		return 0;
	}
	/*
	 * TableModelListenerのメソッドであり、データ用TableModelに変更があったときに呼ばれる。
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		switch (e.getType()) {
		case TableModelEvent.INSERT: // 行追加
		case TableModelEvent.DELETE: // 行削除
			super.fireTableChanged(e);
			break;
		default:
			//System.out.println("tableChanged:" + e.getType());
			break;
		}
	}
}

/* 行ヘッダーの列見出し(=表の左上)クリック時のリスナー
class RowHeaderCHMouseListener extends MouseAdapter {
	protected JTable dataTable;

	public RowHeaderCHMouseListener(JTable dataTable) {
		this.dataTable = dataTable;
	}

	// 行ヘッダーの列見出しがクリックされた時に呼ばれる
	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			int cc = e.getClickCount();
			if (cc == 1) {
				RowSorter<? extends TableModel> sort = dataTable.getRowSorter();
				if (sort != null) {
					sort.setSortKeys(null); // ソートを解除する
					e.consume();
				}
			}
		}
	}
}
*/

// データテーブルでソートが行われたときに呼ばれるリスナー
class RowHeaderSortListener implements RowSorterListener {
	private JTable table;

	public RowHeaderSortListener(RowHeaderTable table) {
		this.table = table;
	}

	// データ用テーブルでソートが行われた時に呼ばれる
	// 行ヘッダーの再描画を行う
	@Override
	public void sorterChanged(RowSorterEvent e) {
		table.revalidate();
		table.repaint();
	}
}

//行ヘッダーで選択したときにデータテーブルに委譲するセレクションモデル
//および、データテーブルで選択したときに呼ばれるリスナー
class RowHeaderSelectionModel extends DefaultListSelectionModel implements ListSelectionListener {
	private static final long serialVersionUID = 1L;

	protected ListSelectionModel selectModel;

	public RowHeaderSelectionModel(JTable dataTable) {
		this.selectModel = dataTable.getSelectionModel();

		selectModel.removeListSelectionListener(this);
		selectModel.addListSelectionListener(this);
	}
	//ListSelectionModelインターフェースで宣言されている全メソッドに対して、selectModelのメソッドを呼ぶようにする（委譲する）
	@Override
	public void addListSelectionListener(ListSelectionListener x) {
		selectModel.addListSelectionListener(x);
	}
	@Override
	public void addSelectionInterval(int index0, int index1) {
		selectModel.addSelectionInterval(index0, index1);
	}
	@Override
	public void clearSelection() {
		selectModel.clearSelection();
	}
	@Override
	public int getAnchorSelectionIndex() {
		return selectModel.getAnchorSelectionIndex();
	}
	@Override
	public int getLeadSelectionIndex() {
		return selectModel.getLeadSelectionIndex(); 
	}
	@Override
	public int getMaxSelectionIndex() {
		return selectModel.getMaxSelectionIndex();
	}
	@Override
	public int getMinSelectionIndex() {
		return selectModel.getMinSelectionIndex();
	}
	@Override
	public int getSelectionMode() {
		return selectModel.getSelectionMode();
	}
	@Override
	public boolean getValueIsAdjusting() {
		return selectModel.getValueIsAdjusting();
	}
	@Override
	public void insertIndexInterval(int index, int length, boolean before) {
		selectModel.insertIndexInterval(index, length, before);
	}
	@Override
	public boolean isSelectedIndex(int index) {
		return selectModel.isSelectedIndex(index);
	}
	@Override
	public boolean isSelectionEmpty() {
		return selectModel.isSelectionEmpty();
	}
	@Override
	public void removeIndexInterval(int index0, int index1) {
		selectModel.removeIndexInterval(index0, index1);
	}
	@Override
	public void removeListSelectionListener(ListSelectionListener x) {
		selectModel.removeListSelectionListener(x);
	}
	@Override
	public void removeSelectionInterval(int index0, int index1) {
		selectModel.removeSelectionInterval(index0, index1);
	}
	@Override
	public void setAnchorSelectionIndex(int index) {
		selectModel.setAnchorSelectionIndex(index);
	}
	@Override
	public void setLeadSelectionIndex(int index) {
		selectModel.setLeadSelectionIndex(index);
	}
	@Override
	public void setSelectionInterval(int index0, int index1) {
		selectModel.setSelectionInterval(index0, index1);
	}
	@Override
	public void setSelectionMode(int selectionMode) {
		selectModel.setSelectionMode(selectionMode);
	}
	@Override
	public void setValueIsAdjusting(boolean valueIsAdjusting) {
		selectModel.setValueIsAdjusting(valueIsAdjusting);
	}
	/*
	 * ListSelectionListenerのメソッドであり、データ用テーブルで行選択が変更されたときに呼ばれる
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		int fi = e.getFirstIndex(), li = e.getLastIndex();
		super.removeSelectionInterval(fi, li);
		for (int i = fi; i <= li; i++) {
			if (selectModel.isSelectedIndex(i)) {
				super.addSelectionInterval(i, i);
			}
		}
	}
}