package jp.or.gsk.gdacb;
/**
 * フィルタ条件を定義する列挙型
 * フィルタタブ内での表示順序も定義している
 * 
 * @author kshirai
 */
public enum E_FilterType {
	// 説明、インデックス
	// インデックスは設定ファイルでの値も兼ねる
	NONE("", 0),
	EQUAL("と一致", 1),
	CONTAIN("を含む", 2),
	BEGIN("で始まる", 3),
	END("で終わる", 4),
	REGEXP("正規表現", 5);
	
	private String label;
	private int index;
	E_FilterType(String s,int i){
		this.label = s;
		this.index = i;
	}

	public String label() {
		return this.label;
	}
	public int index() {
		return this.index;
	}
}
