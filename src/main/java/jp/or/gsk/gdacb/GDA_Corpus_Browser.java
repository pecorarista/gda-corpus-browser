package jp.or.gsk.gdacb;

import jp.or.gsk.gdacb.gui.*;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * GDAコーパスの検索を行うツール
 */
public class GDA_Corpus_Browser {
	public static final String version = "1.00";
	public static final String release_date = "2012/4/10";	// ToDo
	public static final String about_me_msg = gen_copyright_msg();
    /**
	 * システムが取り扱うコーパスの種類を定義する
	 * 毎日のみ、岩波のみ、両方、のいずれか
	 */
	public static E_Corpus SYSTEM_CORPUS = E_Corpus.BOTH;
	//public static E_Corpus SYSTEM_CORPUS = E_Corpus.MAINITI;
	//public static E_Corpus SYSTEM_CORPUS = E_Corpus.IWANAMI;
    /**
     * OSの種別
     */
    public static E_OS SYSTEM_OS = null;
    /**
     * デフォルトブラウザで URL をオープンするコマンド
     */
    public static String default_web_browser_open_url_command = null;
    public static String tmp_web_browser_command = null;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
    	if(args.length >= 1){
    		if(args[0].equals("news")){
    			SYSTEM_CORPUS = E_Corpus.MAINITI;
    		}else if(args[0].equals("iwa")){
    			SYSTEM_CORPUS = E_Corpus.IWANAMI;
    		}
    	}
    	/*
    	System.setProperty("file.encoding","UTF-8");
    	System.out.println(System.getProperty("file.encoding"));
    	*/ 
    	
    	// OS の判定
//    	System.out.println("OS: "+System.getProperty("os.name"));
    	String os_property = System.getProperty("os.name");
    	if(os_property.startsWith("Windows")){
    		SYSTEM_OS = E_OS.Windows;
    	}else if(os_property.startsWith("Mac")){
    		SYSTEM_OS = E_OS.MacOSX;
    	}else if(os_property.startsWith("Linux")){
    		SYSTEM_OS = E_OS.Linux;
    	}else{
    		SYSTEM_OS = E_OS.Undef;
    	}
    	run_GUI();
    }
    
    static void run_GUI () {
         SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			   	// Look & Feel を決める
		    	// 使用可能なものは以下の通り
				// "javax.swing.plaf.metal.MetalLookAndFeel"
		    	// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" (Windowsのみ)
		    	// "com.sun.java.swing.plaf.motif.MotifLookAndFeel"
		    	// "com.apple.laf.AquaLookAndFeel" (Macのみ)
		    	String look_and_feel;
		    	if(SYSTEM_OS == E_OS.MacOSX){
		    		// 最終リリースではMacではこれを使いたい ToDo
		    		//look_and_feel = "com.apple.laf.AquaLookAndFeel";
		    		look_and_feel = "javax.swing.plaf.metal.MetalLookAndFeel";
		    		UIManager.put("swing.boldMetal", Boolean.FALSE);
		    	}else{
		    		look_and_feel = "javax.swing.plaf.metal.MetalLookAndFeel";
					// MetalLookAndFeel のデフォルトフォントでbold体を使わないようにする
		    		UIManager.put("swing.boldMetal", Boolean.FALSE);
		    	}

		    	// look and feel の変更
				try {
					UIManager.setLookAndFeel(look_and_feel);
					// ツール起動後に変更する場合は以下のメソッドを実行する
					// SwingUtilities.updateComponentTreeUI(this);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
//				System.out.println(UIManager.getLookAndFeel());
//				System.out.println(UIManager.getSystemLookAndFeelClassName());

				/* デフォルトのフォントを変更する(以下はLabelのフォントを変更する場合)
				 * UIManager.put("Label.font",new Font("HGS創英角ｺﾞｼｯｸUB",Font.PLAIN,14)); 
				 * 全ての指定可能なキー(`*.font')を調べる方法は以下の通り
				   FontUIResource fontUIResource = new FontUIResource(font);
				   for(Object o: UIManager.getLookAndFeelDefaults().keySet()) {
						if(o.toString().toLowerCase().endsWith("font")) {
							System.out.println(o.toString());
						}
					}
				*/

				new WindowManager();
			}
		});    	
    }
    private static String gen_copyright_msg() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("GDAコーパスブラウザ\n\n");
    	sb.append("Ver. "+version+" (released "+release_date+")\n\n");
    	sb.append("Copyright (c) 2010 言語資源協会\n\n");
    	sb.append("Licensed under the Apache License, Version 2.0.\n");
    	sb.append("(http://www.apache.org/licenses/LICENSE-2.0)");
    	/*
    	sb.append("you may not use this file except in compliance with the License.\n");
    	sb.append("You may obtain a copy of the License at\n\n"); 
    	sb.append("  http://www.apache.org/licenses/LICENSE-2.0\n\n");
    	sb.append("Unless required by applicable law or agreed to in writing, software\n");
    	sb.append("distributed under the License is distributed on an \"AS IS\" BASIS,\n");
    	sb.append("WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
    	sb.append("See the License for the specific language governing permissions and\n");
    	sb.append("limitations under the License.");
    	*/
    	
    	return sb.toString();
    }
    
}
