package jp.or.gsk.gdacb.gui;

import jp.or.gsk.gdacb.search_engine.RetrievedKeyword_MAI;
import javax.swing.table.DefaultTableCellRenderer;


public class KWIC_TableModel_MAI extends KWIC_TableModel {
	private static final long serialVersionUID = 1L;

	// 項目名、値のクラス、編集可能か?、表示幅
	public static final ColumnContext[] columnArray = {
		new ColumnContext("ID", Integer.class, false, 30),
		new ColumnContext("左文脈", String.class, false, 290),
		new ColumnContext("キーワード", String.class, false, 80),
		new ColumnContext("右文脈", String.class, false, 290),
		new ColumnContext("品詞", String.class, false, 50),
		new ColumnContext("活用形", String.class, false, 130),
		new ColumnContext("読み", String.class, false, 80),
		new ColumnContext("基本形", String.class, false, 80),
		new ColumnContext("語義", String.class, false, 130),
		new ColumnContext("記事ID", String.class, false, 80)
		};
	// 「左文脈」に該当する列のインデックス
	public static int idx_of_left_context = 1;	
	// 「語義」に該当する列のインデックス 
	public static int idx_of_sense = 8;
	
	KWIC_TableModel_MAI () {
		super();
		this.set_columnArray(columnArray);
		this.idx_of_left_context_abst = idx_of_left_context;
		this.idx_of_sense_abst = idx_of_sense;
		set_cell_renderer_list();
	}
	KWIC_TableModel_MAI (RetrievedKeyword_MAI[] rkl) {
		super();
		this.set_columnArray(columnArray);
		this.idx_of_left_context_abst = idx_of_left_context;
		this.idx_of_sense_abst = idx_of_sense;
		set_cell_renderer_list();
		if(rkl != null){
			for(int i=0 ; i < rkl.length ; i++){
				this.addRow( get_row_object_list( rkl[i], i+1 ) );
			}
		}
	}

	// テーブルの1行に該当するオブジェクトのリストを返すメソッド
	// 表示の順序はこのメソッドで定義する
	public static Object[] get_row_object_list(RetrievedKeyword_MAI rk,int id){
		Object[] obj = { id,
				rk.left_context, rk.keyword_in_text, rk.right_context,
				rk.POS, rk.conj, rk.yomi, rk.base, rk.sense,
				rk.artID };
		return obj;
	}
		
	// cell_renderer_list を設定する
	private void set_cell_renderer_list () {
		// 毎日、岩波共通の列の設定
		set_cell_renderer_list_common();
		
		// 記事ID: 背景色を変更
		DefaultTableCellRenderer tcr_misc = new DefaultTableCellRenderer();
		tcr_misc.setBackground(color_misc_cell_bg);
		this.cell_renderer_list[9] = tcr_misc;
	}
}
