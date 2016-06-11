package jp.or.gsk.gdacb.gui.parts;

import javax.swing.JTextField;

public class JTextField_PositiveInt extends JTextField {
	private static final long serialVersionUID = 1L;
	String current_value = null;

	public JTextField_PositiveInt () {
		super();
	}

	@Override
	public void setText(String s) {
		super.setText(s);
		this.current_value = s;
	}
	
	/**
	 * フォームに埋められている値が数値でないなら、以前の値に復元するメソッド
	 * そうでない場合は current_value の値を更新する
	 * 
	 * このクラスのオブジェクトに actionPerformed() と focusLost() の
	 * リスナーを登録し、その中でこのメソッドを取り出す
	 * 
	 * 本来はリスナーの登録もこのファイル内(クラス定義)の中で行いたいが、
	 * 設定ビューではリスナーに別のアクションも割り当てる必要があるので、見送る。
	 * 
	 * @threshold	閾値(この値以上の整数しか入力できない
	 */
	public void restore_value_if_invalid(int threshold) {
		try {
            int n = Integer.parseInt(this.getText());
            if(n < threshold){
    			java.awt.Toolkit.getDefaultToolkit().beep();// beep
            	this.setText(current_value);
                return;
            }
		} catch (NumberFormatException e) {
			java.awt.Toolkit.getDefaultToolkit().beep();	// beep
            this.setText(current_value);
            return;
		}
		current_value = this.getText();
	}
}
