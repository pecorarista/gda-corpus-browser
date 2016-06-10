package jp.or.gsk.gdacb.gui;

// KWICを表示する列の属性を定義するクラス

public class ColumnContext {
	public final String  columnName;		// 列のラベル
    public final Class<?>   columnClass;	// 値のクラス
    public final boolean isEditable;		// 編集可能か?
    public final int defaultColumnWidth;	// 列の幅のデフォルト
    public ColumnContext(String n, Class<?> c, boolean e, int w) {
        this.columnName = n;
        this.columnClass = c;
        this.isEditable = e;
        this.defaultColumnWidth = w;
    }
}
