package jp.or.gsk.gdacb.gui;

import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_CORPUS;
import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.gui.parts.JButtonHL;
import jp.or.gsk.gdacb.search_engine.DumpGDAFile;
import jp.or.gsk.gdacb.search_engine.SE_Exception;
import java.io.File;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.border.EtchedBorder;

import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MergeGDAFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private WindowManager wm = null; 
	
	public JProgressBar jProgressBar = null;

	private JPanel jContentPane = null;
	private JTextArea jTextArea_expl = null;
	private JLabel jLabel_title = null;
	private JPanel jPanel_op = null;
	private JTextArea jTextArea_msg = null;
	private JLabel jLabel_padding_left = null;
	private JLabel jLabel_padding_right = null;
	private JButton jButton_close = null;
	private JLabel jLabel_corpus_mai = null;
	private JLabel jLabel_corpus_iwa = null;
	private JButtonHL jButton_exec_mai = null;
	private JButtonHL jButton_exec_iwa = null;

	/**
	 * This is the default constructor
	 */
	public MergeGDAFrame(WindowManager mgr) {
		super();
		this.wm = mgr;
		initialize();
	}
	
	public void show_message (String msg,boolean error_flag){
		if(error_flag){
			jTextArea_msg.setForeground(Color.red);
		}else{
			jTextArea_msg.setForeground(Color.black);
		}
		jTextArea_msg.setText(msg);
	}
	public void show_message (String msg){
		show_message(msg,false);
	}
	public void restore_button (){
		jProgressBar.setIndeterminate(false);
		jProgressBar.setValue(0);
		jButton_exec_mai.setEnabled(true);
		jButton_exec_iwa.setEnabled(true);
		jButton_close.setEnabled(true);
	}

	private void merge_GDA_files (E_Corpus corpus){
		File output_text_file = new File(corpus.indexBaseFilename()+"_sa.mer");
		File output_metadata_file = new File(corpus.indexBaseFilename()+"_md.mer");
		if(output_text_file.exists() || output_metadata_file.exists()){
			int res = JOptionPane.showConfirmDialog(this,
						"インデックスは既に存在します。ファイルを上書しますか?",
						"CONFIRMATION", JOptionPane.YES_NO_OPTION);
			if(res == JOptionPane.NO_OPTION) return;
		}
		File gda_dir;
		if(corpus == E_Corpus.MAINITI){
			gda_dir = wm.conf.gda_dir_MAI.get(); 
		}else{
			gda_dir = wm.conf.gda_dir_IWA.get(); 
		}
		if(gda_dir == null || gda_dir.getPath().equals("")){
			show_message("GDAフォルダが設定されていません。メニューバーの「Option」→「設定」で設定して下さい。",true);
			return;
		}else if(! gda_dir.isDirectory()){
			show_message("指定されたGDAフォルダは存在しません。メニューバーの「Option」→「設定」で再設定して下さい。",true);
			return;
		}

		DumpGDAFile gda_dumper = new DumpGDAFile(gda_dir.getPath(),corpus);
		gda_dumper.set_merged_text_file(output_text_file);
		gda_dumper.set_merged_metadata_file(output_metadata_file);
		gda_dumper.setFrame(this);

		// プログレスバーの状態を「準備中」にする
		jProgressBar.setIndeterminate(true);
		jButton_exec_mai.setEnabled(false);
		jButton_exec_iwa.setEnabled(false);
		jButton_close.setEnabled(false);
		try {
			gda_dumper.read_filename_list(corpus.indexBaseFilename()+"_fn.txt");
		} catch (SE_Exception e) {
			// e.printStackTrace();
			show_message(e.MsgJ,true);
			System.err.println(e.MsgE);
			restore_button();
			return;
		}
		show_message("インデックス作成中...");
		gda_dumper.dump_by_swing_worker();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(450, 300);
		this.setContentPane(getJContentPane());
		this.setLocationRelativeTo(null);
		this.setTitle("Indexing");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				wm.close_merge_gda_frame();
			}
		});

		if(SYSTEM_CORPUS == E_Corpus.MAINITI){
			jLabel_corpus_iwa.setEnabled(false);
			jButton_exec_iwa.setEnabled(false);
		}else if(SYSTEM_CORPUS == E_Corpus.IWANAMI){
			jLabel_corpus_mai.setEnabled(false);
			jButton_exec_mai.setEnabled(false);
		}
		this.setVisible(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gBC_B_close = new GridBagConstraints();
			gBC_B_close.gridx = 1;
			gBC_B_close.weighty = 1.0D;
			gBC_B_close.gridy = 6;
			GridBagConstraints gBC_L_padding_right = new GridBagConstraints();
			gBC_L_padding_right.gridx = 2;
			gBC_L_padding_right.weightx = 1.0D;
			gBC_L_padding_right.gridy = 0;
			jLabel_padding_right = new JLabel();
			jLabel_padding_right.setText("");
			jLabel_padding_right.setPreferredSize(new Dimension(10, 10));
			GridBagConstraints gBC_L_padding_left = new GridBagConstraints();
			gBC_L_padding_left.gridx = 0;
			gBC_L_padding_left.weightx = 1.0D;
			gBC_L_padding_left.gridy = 0;
			jLabel_padding_left = new JLabel();
			jLabel_padding_left.setText("");
			jLabel_padding_left.setPreferredSize(new Dimension(10, 10));
			GridBagConstraints gBC_ProgBar = new GridBagConstraints();
			gBC_ProgBar.gridx = 1;
			gBC_ProgBar.fill = GridBagConstraints.HORIZONTAL;
			gBC_ProgBar.anchor = GridBagConstraints.NORTH;
			gBC_ProgBar.weighty = 0.5D;
			gBC_ProgBar.gridy = 5;
			GridBagConstraints gBC_TA_msg = new GridBagConstraints();
			gBC_TA_msg.fill = GridBagConstraints.HORIZONTAL;
			gBC_TA_msg.gridy = 4;
			gBC_TA_msg.weighty = 0.5D;
			gBC_TA_msg.anchor = GridBagConstraints.SOUTH;
			gBC_TA_msg.gridx = 1;
			GridBagConstraints gBC_P_op = new GridBagConstraints();
			gBC_P_op.gridx = 1;
			gBC_P_op.anchor = GridBagConstraints.NORTH;
			gBC_P_op.weighty = 1.0D;
			gBC_P_op.fill = GridBagConstraints.BOTH;
			gBC_P_op.gridy = 2;
			GridBagConstraints gBC_L_title = new GridBagConstraints();
			gBC_L_title.gridx = 1;
			gBC_L_title.weighty = 1.0D;
			gBC_L_title.weightx = 8.0D;
			gBC_L_title.gridy = 0;
			jLabel_title = new JLabel();
			jLabel_title.setText("検索インデックスの作成");
			jLabel_title.setForeground(new Color(204, 0, 51));
			GridBagConstraints gBC_TA_expl = new GridBagConstraints();
			gBC_TA_expl.fill = GridBagConstraints.HORIZONTAL;
			gBC_TA_expl.gridy = 1;
			gBC_TA_expl.gridx = 1;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(jLabel_padding_left, gBC_L_padding_left);
			jContentPane.add(jLabel_padding_right, gBC_L_padding_right);
			jContentPane.add(jLabel_title, gBC_L_title);
			jContentPane.add(getJTextArea_expl(), gBC_TA_expl);
			jContentPane.add(getJPanel_op(), gBC_P_op);
			jContentPane.add(getJTextArea_msg(), gBC_TA_msg);
			jContentPane.add(getJProgressBar(), gBC_ProgBar);
			jContentPane.add(getJButton_close(), gBC_B_close);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextArea_expl	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea_expl() {
		if (jTextArea_expl == null) {
			jTextArea_expl = new JTextArea();
			jTextArea_expl.setText("GDAコーパスの検索インデックスを作成して、ファイルに保存します。");
			jTextArea_expl.setPreferredSize(new Dimension(100, 60));
			jTextArea_expl.setOpaque(false);
			jTextArea_expl.setEditable(false);
			jTextArea_expl.setForeground(Color.darkGray);
			jTextArea_expl.setLineWrap(true);
		}
		return jTextArea_expl;
	}

	/**
	 * This method initializes jPanel_op	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_op() {
		if (jPanel_op == null) {
			GridBagConstraints gBC_B_exec_iwa = new GridBagConstraints();
			gBC_B_exec_iwa.gridx = 1;
			gBC_B_exec_iwa.insets = new Insets(5, 5, 5, 5);
			gBC_B_exec_iwa.gridy = 1;
			GridBagConstraints gBC_B_exec_mai = new GridBagConstraints();
			gBC_B_exec_mai.gridx = 0;
			gBC_B_exec_mai.insets = new Insets(5, 5, 5, 5);
			gBC_B_exec_mai.gridy = 1;
			GridBagConstraints gBC_L_corpus_iwa = new GridBagConstraints();
			gBC_L_corpus_iwa.gridx = 1;
			gBC_L_corpus_iwa.weightx = 1.0D;
			gBC_L_corpus_iwa.gridy = 0;
			jLabel_corpus_iwa = new JLabel();
			jLabel_corpus_iwa.setText(E_Corpus.IWANAMI.corpus_name());
			jLabel_corpus_iwa.setToolTipText(E_Corpus.IWANAMI.corpus_name()+"のインデックスを作成します");
			GridBagConstraints gBC_L_corpus_mai = new GridBagConstraints();
			gBC_L_corpus_mai.gridx = 0;
			gBC_L_corpus_mai.weightx = 1.0D;
			gBC_L_corpus_mai.gridy = 0;
			jLabel_corpus_mai = new JLabel();
			jLabel_corpus_mai.setText(E_Corpus.MAINITI.corpus_name());			
			jLabel_corpus_mai.setToolTipText(E_Corpus.MAINITI.corpus_name()+"のインデックスを作成します");
			jPanel_op = new JPanel();
			jPanel_op.setLayout(new GridBagLayout());
			jPanel_op.add(jLabel_corpus_mai, gBC_L_corpus_mai);
			jPanel_op.add(jLabel_corpus_iwa, gBC_L_corpus_iwa);
			jPanel_op.add(getJButton_exec_mai(), gBC_B_exec_mai);
			jPanel_op.add(getJButton_exec_iwa(), gBC_B_exec_iwa);
		}
		return jPanel_op;
	}

	/**
	 * This method initializes jTextArea_msg	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea_msg() {
		if (jTextArea_msg == null) {
			jTextArea_msg = new JTextArea();
			jTextArea_msg.setBackground(ViewFrame.bg_color_of_message_area);
			jTextArea_msg.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			jTextArea_msg.setPreferredSize(new Dimension(100, 60));
			jTextArea_msg.setEditable(false);
			jTextArea_msg.setLineWrap(true);
		}
		return jTextArea_msg;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jButton_close	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_close() {
		if (jButton_close == null) {
			jButton_close = new JButton();
			jButton_close.setText("閉じる");
			jButton_close.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					wm.close_merge_gda_frame();
				}
			});
		}
		return jButton_close;
	}

	/**
	 * This method initializes jButton_exec_mai	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButtonHL getJButton_exec_mai() {
		if (jButton_exec_mai == null) {
			jButton_exec_mai = new JButtonHL();
			jButton_exec_mai.setText("作成");
			jButton_exec_mai.setToolTipText(E_Corpus.MAINITI.corpus_name()+"のインデックスを作成します");
			jButton_exec_mai.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					show_message("");
					merge_GDA_files(E_Corpus.MAINITI);
				}
			});
		}
		return jButton_exec_mai;
	}

	/**
	 * This method initializes jButton_exec_iwa	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButtonHL getJButton_exec_iwa() {
		if (jButton_exec_iwa == null) {
			jButton_exec_iwa = new JButtonHL();
			jButton_exec_iwa.setText("作成");
			jButton_exec_iwa.setToolTipText(E_Corpus.IWANAMI.corpus_name()+"のインデックスを作成します");
			jButton_exec_iwa.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					show_message("");
					merge_GDA_files(E_Corpus.IWANAMI);
				}
			});
		}
		return jButton_exec_iwa;
	}
}
