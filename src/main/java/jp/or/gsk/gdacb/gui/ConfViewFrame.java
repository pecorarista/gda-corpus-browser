package jp.or.gsk.gdacb.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

class ConfViewFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private WindowManager wm = null;
	private ConfView conf_view = null;

	ConfViewFrame(WindowManager mgr){
		super();
		this.wm = mgr;
		this.conf_view = new ConfView(mgr);
		this.setSize(600, 400);
		this.setContentPane(conf_view);
		this.setTitle("GDA Corpus Browser - Configuration");
		this.setLocationRelativeTo(null);
		//this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				wm.close_conf_view();
			}
		});
		this.setVisible(true);
	}
}
