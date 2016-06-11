package jp.or.gsk.gdacb.gui.parts;

// テキストとボタンの間のマージンを(わずかに)縮めたボタン

import java.awt.Insets;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class JButtonS extends JButton {
	private static final long serialVersionUID = 1L;

	// 設定するマージン、[参考] JButtonのデフォルトは(0,2,0,2)
	private Insets margin = new Insets(0,0,0,0);
	
	public JButtonS(){
		super();
		this.setMargin(margin);
	}
	public JButtonS(Action a){
		super(a);
		this.setMargin(margin);
	}
	public JButtonS(Icon icon){
		super(icon);
		this.setMargin(margin);
	}
	public JButtonS(String text){
		super(text);
		this.setMargin(margin);
	}
	public JButtonS(String text,Icon icon){
		super(text,icon);
		this.setMargin(margin);
	}
}
