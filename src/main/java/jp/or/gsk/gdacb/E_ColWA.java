package jp.or.gsk.gdacb;
/**
 * 列幅の調整方法を定義する列挙型
 * ColWA = Column Width Adjustment 
 * @author kshirai
 */
public enum E_ColWA {
	DEFAULT("デフォルト", 1),
	AUTO("自動調整", 2),
	CUSTOM("カスタム", 3);

	private String label;
	private int conf_value;		// 設定ファイルの中での値
	private E_ColWA(String s,int i){
		this.label = s;
		this.conf_value = i;
	}
	public String label() {
		return this.label;
	}
	public int conf_value() {
		return this.conf_value;
	}
}
