package jp.or.gsk.gdacb.gui.parts;

/* JTextFieldに入力された文字が数値かどうかをチェックするInputVerifier
 * 使い方は以下の通り
 *   JTextField textField1 = new JTextField("1000");
 *   textField1.setInputVerifier(new IntegerInputVerifier());
 * ただし、数値以外の文字を入力したときはundoされず、フォーカスが移らなくなる
 *
 * バグ(?) 10a のような文字列の入力を受け付けてしまう
 */

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;

class IntegerInputVerifier extends InputVerifier {
	@Override
	public boolean verify (JComponent c) {
		boolean verified = false;
	    JTextField textField = (JTextField)c;
	    try{
	    	Integer.parseInt(textField.getText());
	    	verified = true;
	    }catch(NumberFormatException e) {
	    	UIManager.getLookAndFeel().provideErrorFeedback(c);
	    	//Toolkit.getDefaultToolkit().beep();
	    }
	    return verified;
	}
}