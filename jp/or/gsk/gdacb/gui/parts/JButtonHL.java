package jp.or.gsk.gdacb.gui.parts;

//目立つようにハイライトしたボタン

import java.awt.Color;
import java.awt.Insets;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;


public class JButtonHL extends JButton {
	private static final long serialVersionUID = 1L;
	
//	private ImageIcon icon = new ImageIcon("icon/nomal21.gif");
	private Color fg_color = new Color(255, 241, 0);
	//private Color fg_color = new Color(234, 85, 4);
	//private Color bg_color = new Color(96, 25, 134);

	public JButtonHL(){
		super();
		initialize();
	}
	public JButtonHL(Action a){
		super(a);
		initialize();
	}
	public JButtonHL(Icon icon){
		super(icon);
		initialize();
	}
	public JButtonHL(String text){
		super(text);
		initialize();
	}
	public JButtonHL(String text,Icon icon){
		super(text,icon);
		initialize();
	}

	private void initialize(){
//		this.setIcon(icon);
//		this.setHorizontalTextPosition(SwingConstants.CENTER);
//		Dimension dim = new Dimension(icon.getIconWidth(),icon.getIconHeight());
//		this.setPreferredSize(dim);

		this.setUI(new RoundedCornerButtonUI());
		this.setBorderPainted(false);
		
		this.setMargin(new Insets(5,15,5,15));

		this.setForeground(fg_color);
		// this.setBackground(bg_color);
	}
}
