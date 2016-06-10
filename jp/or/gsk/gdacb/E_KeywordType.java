/**
 * キーワードの種類を定義する列挙型
 */
package jp.or.gsk.gdacb;

public enum E_KeywordType {
	UNDEF("未定義"),
	SURFACE("出現形"),
	BASE("基本形"),
	FULLTEXT("文字列");
	
	private String label;
	
	E_KeywordType(String s){
		this.label = s;
	}
	
	public String label() {
		return this.label;
	}
}
