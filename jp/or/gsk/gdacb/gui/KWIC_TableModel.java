package jp.or.gsk.gdacb.gui;

import jp.or.gsk.gdacb.gui.parts.LeftDotRenderer;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.util.Comparator;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public abstract class KWIC_TableModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	static final Color color_keyword = new Color(128,0,0);	// MacOSXのトウガラシ
	private static final Color color_sent_cell_bg = new Color(232,245,251);		// 薄い青
	private static final Color color_mor_cell_bg = new Color(254,246,249);		// 薄い赤
	static final Color color_misc_cell_bg = new Color(230,243,236);	// 薄い緑

	// 列の概観を決める DefaultTableCellRenderer のリスト
	DefaultTableCellRenderer[] cell_renderer_list = null;
	// 以下は下位クラスで値が設定される 
	ColumnContext[] columnArray;	// 列の定義
	int idx_of_left_context_abst;	// 「左文脈」の列のインデックス
	int idx_of_sense_abst;			// 「語義」の列のインデックス


	@Override
	public boolean isCellEditable(int row, int col) {
        return columnArray[col].isEditable;
    }
	@Override
    public Class<?> getColumnClass(int modelIndex) {
        return columnArray[modelIndex].columnClass;
    }
	@Override
    public int getColumnCount() {
        return columnArray.length;
    }
	@Override
    public String getColumnName(int modelIndex) {
        return columnArray[modelIndex].columnName;
    }

	void set_columnArray(ColumnContext[] ca) {
		this.columnArray = ca;
	}
	
	// TableRowSorter を新規に作成し、返すメソッド
	TableRowSorter<KWIC_TableModel> get_row_sorter (){
		TableRowSorter<KWIC_TableModel> row_sorter = new TableRowSorter<KWIC_TableModel>(this);
		// 3つのキーでソートが可能。最後にクリックした列が primary key になる
		row_sorter.setMaxSortKeys(3);
		// 左文脈については文字列を末尾から先頭に並べ換えたときの順序でソートする
		row_sorter.setComparator(idx_of_left_context_abst, new Comparator<String>() {
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
		});
		return row_sorter;
	}
	
	// 各列のアピアランスを定義する TableCellRederer を用意するメソッド
	void set_cell_renderer_list_common () {
		this.cell_renderer_list = new DefaultTableCellRenderer[columnArray.length];
		
		// ID: 中央に表示
		DefaultTableCellRenderer tcr_ID = new DefaultTableCellRenderer();
		tcr_ID.setHorizontalAlignment(SwingConstants.CENTER);
		this.cell_renderer_list[0] = tcr_ID;

		// 左文脈: 右寄せ、背景色を変更
		//DefaultTableCellRenderer tcr_kwic_l = new DefaultTableCellRenderer();
		DefaultTableCellRenderer tcr_kwic_l = new LeftDotRenderer();
		tcr_kwic_l.setHorizontalAlignment(SwingConstants.LEADING);
		tcr_kwic_l.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		// System.out.println(tcr_kwic_l.getComponentOrientation().isLeftToRight());
		// tcr_kwic_l.setHorizontalTextPosition(SwingConstants.LEFT);
		tcr_kwic_l.setBackground(color_sent_cell_bg);
		this.cell_renderer_list[1] = tcr_kwic_l;

		// キーワード: 文字色、背景色を変更
		DefaultTableCellRenderer tcr_kw = new DefaultTableCellRenderer();
		tcr_kw.setForeground(color_keyword);
		tcr_kw.setBackground(color_sent_cell_bg);
		this.cell_renderer_list[2] = tcr_kw;

		// 右文脈: 背景色を変更
		DefaultTableCellRenderer tcr_kwic_r = new DefaultTableCellRenderer();
		tcr_kwic_r.setBackground(color_sent_cell_bg);
		this.cell_renderer_list[3] = tcr_kwic_r;

		// 形態素情報(品詞,活用形,読み,基本形,語義): 背景色を変更
		DefaultTableCellRenderer tcr_mor = new DefaultTableCellRenderer();
		tcr_mor.setBackground(color_mor_cell_bg);
		this.cell_renderer_list[4] = tcr_mor;
		this.cell_renderer_list[5] = tcr_mor;
		this.cell_renderer_list[6] = tcr_mor;
		this.cell_renderer_list[7] = tcr_mor;
		this.cell_renderer_list[8] = tcr_mor;
	}
}
