package jp.or.gsk.gdacb;
/**
 * コーパスの種類を定義する列挙型
 * @author kshirai
 */
public enum E_Corpus {
	// ID、コーパス名、ファイル数、インデックスの基本名、設定ファイル名
	MAINITI(1, "新聞GDAコーパス",  3000, "index/mainiti", "config.txt"),
	IWANAMI(2, "岩波GDAコーパス", 60321, "index/iwanami", "config.txt"),
	BOTH(   3, "",                  0, "",              "config.txt");
	
	private int id;
	private int number_of_file;
	private String name, index_base_filename, conf_filename;
	private E_Corpus(int i,String s1,int n,String s2, String s3) {
		this.id = i;
		this.name = s1;
		this.number_of_file = n;
		this.index_base_filename = s2;
		this.conf_filename = s3;
	}
	public int id() {
		return this.id;
	}
	public String corpus_name() {
		return this.name;
	}
	public int numberOfFile() {
		return this.number_of_file;
	}
	public String indexBaseFilename() {
		return this.index_base_filename;
	}
	public String confFilename() {
		return this.conf_filename;
	}
}
