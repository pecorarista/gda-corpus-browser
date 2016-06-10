/**
 * ファイルメタデータの検索キーワードの種類を定義する列挙型
 */
package jp.or.gsk.gdacb;

public enum E_MD_KeywordType {
	UNDEF(         "未定義",			E_Corpus.BOTH,   0),
	ART_TITLE(     "記事タイトル",		E_Corpus.MAINITI,1),
	ART_ID(        "記事ID",			E_Corpus.MAINITI,2),
	HEADWORD(      "見出し",			E_Corpus.IWANAMI,3),
	HEADWORD_EXACT("見出し(完全一致)",	E_Corpus.IWANAMI,4),
	SENSE_ID(      "語義ID",			E_Corpus.IWANAMI,5);
	
	private String label;
	private E_Corpus corpus;
	private int conf_value;
	
	E_MD_KeywordType(String s,E_Corpus c,int v){
		this.label = s;
		this.corpus = c;
		this.conf_value = v;
	}
	
	public String label() {
		return this.label;
	}
	public E_Corpus corpus() {
		return this.corpus;
	}
	public int conf_value() {
		return this.conf_value;
	}
}
