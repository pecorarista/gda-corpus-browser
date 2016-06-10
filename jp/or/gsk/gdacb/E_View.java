package jp.or.gsk.gdacb;
/**
 * ビューの種類を定義する列挙型
 */
public enum E_View {
	// 名称、英語名称
	SEARCH("検索", "SEARCH"),
	BROWSE("ファイル閲覧", "BROWSE"),
	BATCH("一括検索", "BATCH"),
	//CONFIGURATION("設定", "CONF");
	PRESERVE("(前回と同じ)", "PRESERVE");

	private String view_name, view_name_en;

	private E_View(String t1,String t2) {
		this.view_name = t1;
		this.view_name_en = t2;
	}
	public String viewName() {
		return this.view_name;
	}
	public String viewNameEn() {
		return this.view_name_en;
	}
	@Override
	public String toString(){
		if(this == PRESERVE){
			return this.view_name;
		}else{
			return this.view_name + "ビュー";
		}
	}
}
