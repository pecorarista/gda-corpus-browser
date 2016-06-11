package jp.or.gsk.gdacb;
/**
 * ウェブブラウザを定義する列挙型
 * @author kshirai
 *
 */
public enum E_WebBrowser {
	// ID, 名称, パス, OS
	UNDEF("UNDEF", "        ", null, null),
	DEFAULT("DEFAULT", "デフォルトブラウザ", null, null),
	IE_WIN("IE_win", "Internet Explorer", "C:\\Program Files\\Internet Explorer\\iexplore.exe", E_OS.Windows),
	FF_WIN("FireFox_win", "FireFox", "C:\\Program Files\\Mozilla Firefox\\firefox.exe", E_OS.Windows),
	//MZ_WIN("Mozilla_win", "Mozilla", "C:\\Program Files\\mozilla.org\\Mozilla\\mozilla.exe", E_OS.Windows),
	SF_MAC("Safari_mac", "Safari", "/Applications/Safari.app", E_OS.MacOSX),
	FF_MAC("FireFox_mac", "FireFox", "/Applications/Firefox.app", E_OS.MacOSX),
	//MZ_MAC("Mozilla_mac", "Mozilla", "/Applications/Mozilla.app", E_OS.MacOSX),
	FF_LIN("FireFox_lin", "FireFox", "firefox", E_OS.Linux);
	//MZ_LIN("Mozilla_lin", "Mozilla", "mozilla", E_OS.Linux),
	//OP_LIN("Opera_lin", "Opera", "opera", E_OS.Linux);

	private String id, label, path;
	private E_OS os;
	private E_WebBrowser(String i,String l,String p,E_OS o){
		this.id = i;
		this.label = l;
		this.path = p;
		this.os = o;
	}
	
	public String id(){
		return this.id;
	}
	public String label(){
		return this.label;
	}
	public String path(){
		return this.path;
	}
	public E_OS os(){
		return this.os;
	}
	@Override
	public String toString(){
		return this.label;
	}
}
