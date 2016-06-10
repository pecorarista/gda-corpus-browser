package jp.or.gsk.gdacb.search_engine;

/**
 * GDAコーパスから以下のファイルを作成する
 *   プレインテキスト (タグを除いたテキストをマージしたファイル)
 *   メタデータファイル (メタデータをマージしたファイル)
 *     メタデータ=記事のタイトル(新聞) or 辞書の見出し(岩波)
 */

import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.gui.MergeGDAFrame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class DumpGDAFile {
	private String gda_dir = null;
	private E_Corpus corpus = null;
	private File merged_text_file = null;
	private File merged_metadata_file = null;
	private ArrayList<String> file_list = null;
	private MergeGDAFrame frame = null;
	
	// constructor
	public DumpGDAFile(String gda_corpus_directory,E_Corpus target_corpus){
		this.gda_dir = gda_corpus_directory;
		this.corpus = target_corpus;
	}
	
	/**
	 * マージしたテキストファイルの出力ファイルを設定するメソッド
	 * @param s  出力ファイル名
	 */
	public void set_merged_text_file (String s){
		this.merged_text_file = new File(s);
	}
	/**
	 * マージしたテキストファイルの出力ファイルを設定するメソッド
	 * @param f  出力ファイル
	 */
	public void set_merged_text_file (File f){
		this.merged_text_file = f;
	}
	/**
	 * メタデータの出力ファイルを設定するメソッド
	 * @param s  出力ファイル名
	 */
	public void set_merged_metadata_file (String s){
		this.merged_metadata_file = new File(s);
	}
	/**
	 * メタデータの出力ファイルを設定するメソッド
	 * @param f  出力ファイル
	 */
	public void set_merged_metadata_file (File f){
		this.merged_metadata_file = f;
	}

	/**
	 * ファイル名のリストを読み込むメソッド
	 */
	public void read_filename_list(String fname) throws SE_Exception{
		String f;
		BufferedReader in = null;
		file_list = new ArrayList<String>();
		try {
			in = new BufferedReader( new InputStreamReader( new FileInputStream(fname) ) );
			while( (f = in.readLine()) != null ){
				file_list.add(f);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("ファイルリスト"+fname+"が存在しません");
			e2.setMsgE("FATAL ERROR: no such file `"+fname+"'");
			throw e2;
		} catch (IOException e) {
			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("ファイルリスト"+fname+"の読み込みに失敗しました");
			e2.setMsgE("FATAL ERROR: fail to read `"+fname+"'");
			throw e2;
		} finally {
			try {
				if(in != null) in.close();
			} catch (IOException e){}
		}
	}
	// swing worker の進行状況を表示するプログレスバーを設定するメソッド
//	public void setProgressBar(JProgressBar pb){
//		 this.worker.addPropertyChangeListener(new ProgressListener(pb));
//	}
	// swing worker の結果を返す TextArea を設定するメソッド
//	public void setTextArea(JTextArea ta){
//		this.text_area = ta;
//}
	/**
	 *  進行状況を表示するテキストエリアとプログレスバーを含む Frame を設定するメソッド
	 */
	public void setFrame(MergeGDAFrame fr){
		this.frame = fr;
	}
	/**
	 *  swing worker を使って、マージしたテキストならびにメタデータを出力するメソッド
	 */
	public void dump_by_swing_worker() {
		SwingWorker<String,String> worker = get_dump_worker();
		if(frame != null && frame.jProgressBar != null)
			worker.addPropertyChangeListener(new ProgressListener(frame.jProgressBar));
		worker.execute();
	}
	
	/**
	 * 以下のファイルを出力するメソッド
	 * (1)GDAファイルからタグを除去してマージしたテキストファイル
	 * (2)GDAファイルから抽出したメタデータのファイル
	 */
	void dump_files () throws SE_Exception{
		DataOutputStream out_text = null;
		DataOutputStream out_md = null;
		GDA_File gda;
		
		if(file_list == null){
			System.err.println("File list is not read");
			return;
		}
		if(merged_text_file == null && merged_metadata_file == null){
			System.err.println("no output file is specified");
			return;
		}

		if(merged_text_file != null){
			try {
				out_text = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(merged_text_file) ) );
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				SE_Exception e2 = new SE_Exception();
				e2.setMsgJ("インデックスファイル"+merged_text_file.getName()+"の作成に失敗しました");
				e2.setMsgE("Fail to create index file `"+merged_text_file.getName()+"'");
			}
		}
		if(merged_metadata_file != null){
			try {
				out_md = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(merged_metadata_file) ) );
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				SE_Exception e2 = new SE_Exception();
				e2.setMsgJ("インデックスファイル"+merged_metadata_file.getName()+"の作成に失敗しました");
				e2.setMsgE("Fail to create index file `"+merged_metadata_file.getName()+"'");
			}
		}
		
		try {
			for(String f: file_list){
				if(this.corpus == E_Corpus.MAINITI){
					gda = new GDA_File_MAI(this.gda_dir + File.separator + f);
				}else{
					gda = new GDA_File_IWA(this.gda_dir + File.separator + f);
				}
				gda.read_all();
				if(out_text != null) gda.output_text(out_text);
				if(out_md != null) gda.output_metadata(out_md);
			}
		} catch (SE_Exception e){
			SE_Exception e2 = new SE_Exception();
			if(e.MsgE.equals("CODE: GDA_File::output_text")){
				e2.setMsgJ("インデックスファイル"+merged_text_file.getName()+"の書き込みに失敗しました");
				e2.setMsgE("Fail to write index file `"+merged_text_file.getName()+"'");
				throw e2;
			}else if(e.MsgE.equals("CODE: GDA_File::output_metadata")){
				e2.setMsgJ("インデックスファイル"+merged_metadata_file.getName()+"の書き込みに失敗しました");
				e2.setMsgE("Fail to write index file `"+merged_metadata_file.getName()+"'");
				throw e2;
			}else{
				throw e;
			}
		} finally {
			try {
				if(out_text != null) out_text.close();
				if(out_md != null) out_md.close();
			} catch (IOException e) {}
		}
	}
	/**
	 * GDAファイルをマージする swing worker を返すメソッド
	 */
	private SwingWorker<String,String> get_dump_worker () {
		return new SwingWorker<String,String>(){
			@Override
			public String doInBackground(){
				 DataOutputStream out_text = null;
				 DataOutputStream out_md = null;
				 GDA_File gda;
				if(file_list == null)
					return "ERROR: list of files has not been read";
				if(merged_text_file == null)
					return "ERROR: output file (merged text) is not specified";
				if(merged_metadata_file == null)
					return "ERROR: output file (metadata) is not specified";
				
				try {
					out_text = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(merged_text_file) ) );
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return "インデックスファイル"+merged_text_file.getName()+"の作成に失敗しました";
				}
				try {
					out_md = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(merged_metadata_file) ) );
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return "インデックスファイル"+merged_metadata_file.getName()+"の作成に失敗しました";
				}
				publish("");
				
				int cnt = 0;
				int num_of_file = file_list.size();
				try {
					for(String f: file_list){
						if(corpus == E_Corpus.MAINITI){
							gda = new GDA_File_MAI(gda_dir + File.separator + f);
						}else{
							gda = new GDA_File_IWA(gda_dir + File.separator + f);
						}
						gda.read_all();
						gda.output_text(out_text);
						gda.output_metadata(out_md);
						cnt++;
						if(cnt % 100 == 0){
							setProgress(100*cnt/num_of_file);
						}
					}
//				} catch (IOException e) {
//					e.printStackTrace();
//					return corpus.corpus_name()+"のインデックスファイルの書き込みに失敗しました";
				} catch (SE_Exception e) {
                    if(e.MsgE.equals("CODE: GDA_File::output_text")){
                    	return "インデックスファイル"+merged_text_file.getName()+"の書き込みに失敗しました";
                    }else if(e.MsgE.equals("CODE: GDA_File::output_metadata")){
                    	return "インデックスファイル"+merged_metadata_file.getName()+"の書き込みに失敗しました";
                    }else{
                    	//return "インデックスファイルの書き込みに失敗しました";
                    	return e.MsgJ;
                    }
				} finally {
					try {
						if(out_text != null) out_text.close();
						if(out_md != null) out_md.close();
					} catch (IOException e) {}
				}
				return "OK";
			}
			@Override
			protected void process(java.util.List<String> chunks){
				;
			}
			@Override
			public void done() {
				if(frame == null) return;
				try {
					String worker_result = get();
					if(worker_result.equals("OK")){
						frame.show_message(corpus.corpus_name()+"のインデックスファイルの作成が完了しました");
					}else{
						// show error
						frame.show_message(worker_result,true);
						// ToDo 完成したファイルが存在し、それを上書しようとしたとき、
						// パーミッションなどのエラーで作成に失敗したときは、完成したファイルを削除しないようにしたい
						remove_incomplete_merged_files();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					frame.show_message(corpus.corpus_name()+"のインデックスファイルの作成が中断されました",true);
					remove_incomplete_merged_files();
				} catch (ExecutionException e) {
					e.printStackTrace();
					frame.show_message(corpus.corpus_name()+"のインデックスファイルの作成に失敗しました",true);
					remove_incomplete_merged_files();
				}
				frame.restore_button();
			}
		};
	}
	/**
	 * マージしたテキストファイル、メタデータのファイルを削除する
	 * マージに失敗したとき、不完全な出力ファイルを削除するために呼び出す
	 */
	private void remove_incomplete_merged_files (){
		//System.out.print("remove_incomplete_merged_files():");
		if(this.merged_text_file == null || this.merged_metadata_file == null) return;
		if(this.merged_text_file.isFile() &&
		   ((this.corpus == E_Corpus.MAINITI && this.merged_text_file.length() == 3080679) ||
		    (this.corpus == E_Corpus.IWANAMI && this.merged_text_file.length() == 6214908)) &&
		   this.merged_metadata_file.isFile() &&
		   ((this.corpus == E_Corpus.MAINITI && this.merged_metadata_file.length() == 198960) ||
		    (this.corpus == E_Corpus.IWANAMI && this.merged_metadata_file.length() == 1321952))){
			return;
		}
		if(this.merged_text_file.isFile()){
			this.merged_text_file.delete();
			//System.out.print(" "+this.merged_text_file.getName());
		}
		if(this.merged_metadata_file.isFile()){
			this.merged_metadata_file.delete();
			//System.out.print(" "+this.merged_metadata_file.getName());
		}
		//System.out.print("\n");
	}
	/* 処理の進行状況を観察する ProgressListener のクラス
	 * 以下は推測だが
	 *   evt が他のコンポーネントから送られてくるイベント
	 *   evt のプロパティネーム( getPropertyName() )が"process"のとき、処理の進行度を表わす
	 *   evt.getNewValue() で進行度を受けとり、
	 *   progressBar.setValue でプログレスバーに進行度を渡し、描画する   
	 */
	class ProgressListener implements PropertyChangeListener {
	    private final JProgressBar progressBar;
	    ProgressListener(JProgressBar progressBar) {
	        this.progressBar = progressBar;
	        this.progressBar.setValue(0);
	    }
	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
	        String strPropertyName = evt.getPropertyName();
	        if ("progress".equals(strPropertyName)) {
	            progressBar.setIndeterminate(false);
	            int progress = (Integer)evt.getNewValue();
	            progressBar.setValue(progress);
	        }
	    }
	}
	
	public static void main(String[] args) {
		//DumpGDAFile prog = new DumpGDAFile("../data/mai_final",E_Corpus.MAINITI);
		DumpGDAFile prog = new DumpGDAFile("../data/iwa_final",E_Corpus.IWANAMI);
		try {
			prog.set_merged_text_file("merged_text.txt");
			prog.set_merged_metadata_file("merged_metadata.txt");
			//prog.read_filename_list("index/mainiti_fn.txt");
			prog.read_filename_list("index/iwanami_fn.txt");
			prog.dump_files();
		} catch (SE_Exception e) {
			//e.printStackTrace();
			System.err.println(e.MsgE);
		}
		System.out.println("Done");
	}
}
