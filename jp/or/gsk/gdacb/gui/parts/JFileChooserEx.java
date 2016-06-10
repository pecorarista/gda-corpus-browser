package jp.or.gsk.gdacb.gui.parts;

import java.io.File;
import java.awt.Component;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
/**
 * 以下の点を拡張したJFileChooser
 *   - 最初に開くディレクトリを覚えておく機能
 *   - showSaveDialog()実行時、既存のファイルを上書するかを確認する
 * 
 * @author kshirai
 */
public class JFileChooserEx extends JFileChooser {
	private static final long serialVersionUID = 1L;
	private File current_dir;
	private boolean check_override_file = true;
	private boolean check_override_dir = true;
	private File null_file = new File("");
	private File[] null_files = null;
//	private boolean run_open_dialog_flag = false;
	private boolean run_save_dialog_flag = false;

	public JFileChooserEx(){
		super();
		current_dir = new File(".").getAbsoluteFile().getParentFile();
	}
	
	/**
	 * 保存ダイアログを開いたとき、選択した既存ファイルを上書してもいいかと
	 * ユーザに確認するかを設定するメソッド
	 */
	public void setOverrideFileCheckEnabled (boolean b){
		this.check_override_file = b;
	}
	/**
	 * 保存ダイアログを開いたとき、選択した既存ディレクトリを上書してもいいかと
	 * ユーザに確認するかを設定するメソッド
	 */
	public void setOverrideDirectoryCheckEnabled (boolean b){
		this.check_override_file = b;
	}
	/**
	 * 前回選択したファイルをリセットするメソッド
	 */
	public void reset_selected_file() {
		if(this.getSelectedFile() != null)
			this.setSelectedFile(null_file);
	}
	/**
	 * 前回選択した複数のファイルをリセットするメソッド
	 * (isMultiSelectionEnabled() が true のときに使う)
	 */
	public void reset_selected_files() {
		if(null_files == null) {
			null_files = new File[1];
			null_files[0] = null_file;
		}
		this.setSelectedFiles(null_files);
	}
	
	// ユーザがOKボタンを押したときに実行されるメソッド
	@Override
	public void approveSelection(){
		int res;
		
		if(this.run_save_dialog_flag){
			File f = this.getSelectedFile();
			if(this.check_override_file && f.isFile()){
				res = JOptionPane.showConfirmDialog(this,
						"指定したファイルは既に存在しています。上書しますか?",
						"Confirmation",
						JOptionPane.OK_CANCEL_OPTION);
				if(res != JOptionPane.OK_OPTION) return;
			}else if(this.check_override_dir && f.isDirectory()){
				res = JOptionPane.showConfirmDialog(this,
						"指定したフォルダは既に存在しています。上書しますか?",
						"Confirmation",
						JOptionPane.OK_CANCEL_OPTION);
				if(res != JOptionPane.OK_OPTION) return;
			}
		}
		super.approveSelection();
	}
	
	@Override
	public int showOpenDialog(Component parent){
		int responce;
		this.setCurrentDirectory(this.current_dir);
//		this.run_open_dialog_flag = true;
		responce = super.showOpenDialog(parent);
//		this.run_open_dialog_flag = false;
		if(responce == JFileChooser.APPROVE_OPTION){
			this.current_dir =
				this.getSelectedFile().getAbsoluteFile().getParentFile();
		}
		return responce;
    }
	
	@Override
	public int showSaveDialog(Component parent){
		int responce;
		this.setCurrentDirectory(this.current_dir);
		this.run_save_dialog_flag = true;
		responce = super.showSaveDialog(parent);
		this.run_save_dialog_flag = false;
		if(responce == JFileChooser.APPROVE_OPTION){
			this.current_dir = 
				this.getSelectedFile().getAbsoluteFile().getParentFile();
		}
		return responce;
    }
	
	@Override
	public void setCurrentDirectory(File dir){
		super.setCurrentDirectory(dir);
		this.current_dir = dir;
	}
}
