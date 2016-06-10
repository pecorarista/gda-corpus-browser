/**
 * 仮想ウェブブラウザのクラス
 */

package jp.or.gsk.gdacb.gui;

import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_OS;
import jp.or.gsk.gdacb.E_OS;
import jp.or.gsk.gdacb.E_WebBrowser;
import jp.or.gsk.gdacb.gui.parts.JComboBoxWithDisabledItems;
import jp.or.gsk.gdacb.search_engine.SE_Exception;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class WebBrowserHandler {
	boolean default_browser_safari_flag = false;	// デフォルトブラウザが Safari か否か
	private E_OS os = null;
	private String default_web_browser_command_windows = null;
	private String default_web_browser_command_macos = null;
		
	WebBrowserHandler(E_OS os){
		this.os = os;
		if(os == E_OS.Windows){
			this.default_web_browser_command_windows = get_default_web_browser_windows();
			this.default_web_browser_command_macos = null;
		}else if(os == E_OS.MacOSX){
			this.default_web_browser_command_windows = null;
			this.default_web_browser_command_macos = get_default_web_browser_macos();
		}else{
			this.default_web_browser_command_windows = null;
			this.default_web_browser_command_macos = null;
		}
	}

	/**
	 * ウェブブラウザでHTMLファイルを表示するメソッド
	 * @param f		表示するファイル
	 * @param conf
	 */
	void show_html_file(File f,Configuration conf) throws SE_Exception {
		show_file(f,conf,true);
	}
	/**
	 * ウェブブラウザでXMLファイルを表示するメソッド
	 * @param f		表示するファイル
	 * @param conf
	 */
	void show_xml_file(File f,Configuration conf) throws SE_Exception {
		// XMLの場合、Desktop を使うと以下のような不具合が生じる
		// Windows: 常にIEが選択される
		// MacOSX: Xcode.appが選択される
		// Linux(gnome): Operaや他のコマンドが選択される
		show_file(f,conf,false);
	}
	/**
	 * ウェブブラウザでローカルファイルを表示するメソッド
	 * @param f					表示するファイル
	 * @param conf
	 * @param use_desktop_flag	デフォルトブラウザを使うとき、java.awt.Desktop を使うか?
	 */
	private void show_file(File f,Configuration conf,boolean use_desktop_flag) throws SE_Exception {
		boolean flag = false;
		String na_msg = "使用するウェブブラウザが未指定です。\nメニューバーの「Option」→「設定」から「ウェブブラウザ」の項目を設定するか、\n"+f.getPath()+"をウェブブラウザで開いて下さい。";
		if(conf.set_web_browser_by_list_flag.get()){
			E_WebBrowser wb = conf.web_browser_by_list.get();
			if(wb == null || wb == E_WebBrowser.UNDEF){
				SE_Exception e = new SE_Exception();
				e.setMsgJ(na_msg);
				e.setMsgE("Web Browser is not specifed");
				throw e;
			}else if(wb.os() != os && wb != E_WebBrowser.DEFAULT){
				SE_Exception e = new SE_Exception();
				e.setMsgJ("設定ビューで選択されたウェブブラウザが不正です(OSの不一致)");
				e.setMsgE("Web Browser of different OS is chosen");
				throw e;
			}else if(wb == E_WebBrowser.DEFAULT){
				if(use_desktop_flag) flag = show_URI_by_desktop_web_browser(f.toURI());
				//if(flag) System.err.println("Success to show file by Desktop Browser");
				if(!flag){
					flag = show_file_by_default_web_browser(f);
					//if(flag) System.err.println("Success to show file by Default Browser");
				}
				if(!flag){
					SE_Exception e = new SE_Exception();
					e.setMsgJ("デフォルトブラウザでの "+f.getPath()+" の表示に失敗しました");
					e.setMsgE("Fail to show "+f.getPath()+" by default web browser");
					throw e;
				}
			}else{
				exec_command_to_show_file_by_web_browser(f,wb.path());
			}
		}else{
			String path = conf.web_browser_by_input.get();
			if(path.equals("")){
				SE_Exception e = new SE_Exception();
				e.setMsgJ(na_msg);
				e.setMsgE("Web Browser is not specifed");
				throw e;
			}else{
				exec_command_to_show_file_by_web_browser(f,conf.web_browser_by_input.get());
			}
		}
	}
	/**
	 * 指定されたウェブブラウザでファイルを表示するメソッド
	 * @param f			表示するファイル
	 * @param wb_path	ウェブブラウザのパス
	 */
	private void exec_command_to_show_file_by_web_browser(File f,String wb_path) throws SE_Exception {
		Runtime r;
		try {
			r = Runtime.getRuntime();
			if(os == E_OS.Windows){
				r.exec(wb_path+" "+f.toURI());
			}else if(os == E_OS.MacOSX){
				r.exec("open -a "+wb_path+" "+f.getPath());	
			}else if(os == E_OS.Linux){
				r.exec(wb_path+" "+f.toURI());
			}else{
				System.err.println("ERROR: unknown OS");
			}
		} catch (IOException e) {
			SE_Exception e1 = new SE_Exception();
			e1.setMsgJ("ウェブブラウザによる "+f.getPath()+" の表示に失敗しました");
			e1.setMsgE("Fail to open "+f.getPath()+" by web browser");
			throw e1;
		}
	}
	/**
	 * デフォルトのウェブブラウザでファイルを表示するメソッド
	 * (java.awt.Desktop を使う場合)
	 * @param f		表示するファイル
	 * @return		表示に成功したか否か
	private boolean show_file_by_desktop_web_browser(File f){
		return show_URI_by_desktop_web_browser(f.toURI());
	}
	 */
	/**
	 * デフォルトのウェブブラウザでURIを表示するメソッド
	 * (java.awt.Desktop を使う場合)
	 * @param uri	URI
	 * @return		表示に成功したか否か
	 */
	boolean show_URI_by_desktop_web_browser(URI uri){
		if(Desktop.isDesktopSupported()){
			try{
				//Desktop.getDesktop().browse(new URI("http://..."));
				Desktop.getDesktop().browse(uri);
				return true;
			}catch(IOException e) {
				e.printStackTrace();
				return false;
//			}catch(URISyntaxException e) {
//				e.printStackTrace();
			}
		}
		return false;
		// ヘルプの表示に失敗したとき、ダイアログを表示する
		/*
		String msg = null;
		if(help_file.toURI().equals(uri)){
			msg = "本ツールの使用方法については "+help_file.getPath()+" を御覧下さい";
		}else{
			msg = "ブラウザで "+uri.getPath()+" を開いて下さい";
		}
		JOptionPane.showMessageDialog(this,msg,"HELP", JOptionPane.INFORMATION_MESSAGE);
		*/
	}
	/**
	 * デフォルトのウェブブラウザでファイルを表示するメソッド
	 * (自分でウェブブラウザを選んで呼び出す場合)
	 * @param f		表示するファイル
	 * @return		表示に成功したか否か 
	 */
	private boolean show_file_by_default_web_browser(File f){
		String show_command = null;
		if(os == E_OS.Windows){
			if(default_web_browser_command_windows != null){
				String regex_path = Pattern.compile("\\\\").matcher(f.getAbsolutePath()).replaceAll("\\\\\\\\");
				Pattern p = Pattern.compile("%1");
				Matcher m = p.matcher(default_web_browser_command_windows);
				if(m.find()){
					show_command = m.replaceFirst("file://"+regex_path);
				}else{
					show_command = default_web_browser_command_windows + " file://"+f.getAbsolutePath();
				}
			}
		}else if(os == E_OS.MacOSX){
			if(default_web_browser_command_macos != null){
				show_command = default_web_browser_command_macos + " " + f.getPath();
			}else if(f.getPath().endsWith(".html")){
				show_command = "open " + f.getPath();
			}
		}
		
		if(show_command == null) return false;
		try {
			Runtime.getRuntime().exec(show_command);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	void setup_combobox_of_select_web_browser(JComboBoxWithDisabledItems cb){
		int i = 0;
		cb.addItem(E_WebBrowser.UNDEF);

		if(os != E_OS.Linux){
			cb.addItem(E_WebBrowser.DEFAULT);
			i++;
			if(! is_available_default_browser()) cb.setDisableIndex(i);
		}
		
		for(E_WebBrowser wb: E_WebBrowser.values()){
			if(SYSTEM_OS == wb.os()){
				cb.addItem(wb);
				i++;
				if(! is_available_browser(wb)) cb.setDisableIndex(i);
			}
		}
	}
	/**
     * ウェブブラウザの有無をチェックするメソッド
     */
    private boolean is_available_browser(E_WebBrowser wb){
    	if(SYSTEM_OS != wb.os()) return false;
        if(wb.os() == E_OS.Windows){
        	File fp = new File(wb.path());
        	if(fp.exists())	return true;
        }else if(wb.os() == E_OS.MacOSX){
        	File fp = new File(wb.path());
        	if(fp.exists()) return true;
        }else if(wb.os() == E_OS.Linux){
        	Runtime r = Runtime.getRuntime();
        	try {
        		Process pr = r.exec("which "+wb.path());
        		if(pr.waitFor() == 0) return true;
        	} catch (IOException e) {
        		e.printStackTrace();
        	} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        return false;
    }
    /**
     * デフォルトウェブブラウザの有無をチェックするメソッド
     */
    private boolean is_available_default_browser(){
    	if(os == E_OS.Windows){
    		if(Desktop.isDesktopSupported() || default_web_browser_command_windows != null){
    			return true;
    		}else{
    			return false;
    		}
		}else if(os == E_OS.MacOSX){
			if(default_web_browser_command_macos != null){
				return true;
			}else{
				return false;
			}
		}else if(os == E_OS.Linux){
			return false;
		}else{
			if(Desktop.isDesktopSupported()){
				return true;
			}else{
				return false;
			}
		}
	}
    /**
     * Windows上のデフォルトのウェブブラウザでURLを表示するコマンドを得るメソッド
     * @return URL表示コマンド(Windowsの場合、%1がURLを表わす。)
     *         見つからないときは null を返す
     */
    private String get_default_web_browser_windows() {
    	String command = null;
    	InputStream is = null;
    	BufferedReader br = null;
    	
		try {
			Process process = Runtime.getRuntime().exec("reg query HKEY_CLASSES_ROOT\\http\\shell\\open\\command");
	    	is = process.getInputStream();
	    	br = new BufferedReader(new InputStreamReader(is));
	    	String line;
    	    Pattern p1 = Pattern.compile("REG_SZ\\s*(\\S.+)$");
    	    //Pattern p2 = Pattern.compile("%1");
	    	while ((line = br.readLine()) != null) {
	    	    Matcher m1 = p1.matcher(line);
	    	    if(m1.find()){
	    	    	command = m1.group(1);
	    	    	//if(p2.matcher(command).find()){	// コマンドに %1 が含まれているかを確認する → %1 は必ずしも存在しない(ex.IE)
	    	    		break;
    		    	//}else{
    		    	//	command = null;
    		    	//}
    		    }
    		}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) br.close();
				if(is != null) is.close();
			} catch (IOException e) { };
		}
		return(command);
    }
    /**
     * MacOS上のデフォルトのウェブブラウザでURLを表示するコマンドを得るメソッド
     * @return URL表示コマンド("open -b ...")
     */
    private String get_default_web_browser_macos() {
    	String command = null;
    	InputStream is = null;
    	BufferedReader br = null;
    	
    	try {
			String[] c = {"/bin/sh", "-c", "defaults read com.apple.LaunchServices | grep -A5 -w public.html | grep LSHandlerRoleAll"};
			Process process = Runtime.getRuntime().exec(c);
	    	is = process.getInputStream();
	    	br = new BufferedReader(new InputStreamReader(is));
	    	String line;
    	    Pattern p = Pattern.compile("\"([^\"]+)\"");
	    	while ((line = br.readLine()) != null) {
	    	    Matcher m = p.matcher(line);
	    	    if(m.find()){
	    	    	command = "open -b " + m.group(1);
	    	    	break;
    		    }
    		}
			// Mac OS 上で一度もデフォルトのウェブブラウザを設定したことのないユーザは、
			// com.apple.LaunchServices の中に public.html というエントリが存在しない。
			// よって、取得に失敗したときは Safari をデフォルトブラウザとする。
	    	if(command == null) command = "open -b com.apple.safari";
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) br.close();
				if(is != null) is.close();
			} catch (IOException e) { };
		}
		if(command != null && command.endsWith("com.apple.safari"))
			default_browser_safari_flag = true;
		return(command);
    }
}
