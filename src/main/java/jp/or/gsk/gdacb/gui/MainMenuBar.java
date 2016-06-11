/**
 * メニューバーのクラス 
 */
package jp.or.gsk.gdacb.gui;

import static jp.or.gsk.gdacb.GDA_Corpus_Browser.SYSTEM_CORPUS;
import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.GDA_Corpus_Browser;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

class MainMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	/**
	 * WindowManager
	 */
	private WindowManager wm = null;
	/**
	 * 親のフレーム
	 */
	private ViewFrame pf = null;
	/**
	 * このメニューバーで設定されているフォントサイズ
	 */
	private int font_size_in_menubar = -1;

	//private JMenu jMenu_window = null;

	private JMenu jMenu_file = null;
	private JMenuItem jMenuItem_close = null;
	private JMenuItem jMenuItem_save_conf = null;
	private JMenuItem jMenuItem_quit = null;
	private JMenuItem jMenuItem_quit_immediately = null;
	//
	private JMenu jMenu_open_view = null;
	private JMenuItem jMenuItem_browse_view = null;
	private JMenuItem jMenuItem_search_view = null;
	private JMenuItem jMenuItem_batch_view = null;
	//
	private JMenu jMenu_option = null;
	private JMenuItem jMenuItem_conf_view = null;
	private JMenu jMenu_corpus = null;
	private JRadioButtonMenuItem jRadioButtonMenuItem_corpus_mai = null;
	private JRadioButtonMenuItem jRadioButtonMenuItem_corpus_iwa = null;
	private JMenuItem jMenuItem_make_index = null;
	//
	//private JMenu jMenu_font = null;
	private JMenu jMenu_font_size = null;
	private JRadioButtonMenuItem jRadioButtonMenuItem_font_size8 = null;
	private JRadioButtonMenuItem jRadioButtonMenuItem_font_size10 = null;
	private JRadioButtonMenuItem jRadioButtonMenuItem_font_size12 = null;
	private JRadioButtonMenuItem jRadioButtonMenuItem_font_size14 = null;
	private JRadioButtonMenuItem jRadioButtonMenuItem_font_size16 = null;
	private JRadioButtonMenuItem jRadioButtonMenuItem_font_size18 = null;
	private JRadioButtonMenuItem jRadioButtonMenuItem_font_size20 = null;
	private JMenuItem jMenuItem_font_size_default = null;
	//
	private JMenu jMenu_help = null;
	private JMenuItem jMenuItem_help = null;
	private JMenuItem jMenuItem_version = null;

	//private JMenu jMenu_font_name = null;
	//private JMenuItem jMenuItem_font_ms_gothic = null;
	//private JMenuItem jMenuItem_font_ms_Pgothic = null;
	//private JMenuItem jMenuItem_font_hg_mintyo = null;
	//private JMenuItem jMenuItem_font_hg_soei = null;
	//private JMenuItem jMenuItem_font_dialog = null;
	
	//private JMenu jMenu_view = null;
	//private JRadioButtonMenuItem jRadioButtonMenuItem_browse_view = null;
	//private JRadioButtonMenuItem jRadioButtonMenuItem_search_view = null;
	//private JRadioButtonMenuItem jRadioButtonMenuItem_batch_view = null;
	//private JRadioButtonMenuItem jRadioButtonMenuItem_conf_view = null;

	/**
	 * This method initializes 
	 * 
	 */
	public MainMenuBar(WindowManager mgr,ViewFrame parent) {
		super();
		this.wm = mgr;
		this.pf = parent;
		initialize();
	}
	
	/**
	 * メニューバーの内容を保存するメソッド
	 */
	void save_parameters() {
		pf.conf_vf.changeFontSize( font_size_in_menubar );
	}
	/*
	 * フォントを更新する
	 *   recursiveUpdateUI()を使ってフォントをまとめて変更する方法は
	 *   メニューバーではうまく動作しない
	 *   メニューアイテムをひとつずつ指定してもダメ
	private void update_font (){
		ViewFrame.recursiveUpdateUI(this);

		//for(int i=0 ; i < this.getMenuCount() ; i++)
		//	ViewFrame.recursiveUpdateUI(this.getMenu(i));

		//ViewFrame.recursiveUpdateUI(jMenu_help);
		//ViewFrame.recursiveUpdateUI(jMenuItem_help);
		//ViewFrame.recursiveUpdateUI(jMenuItem_version);
	}
	 */
	/**
	 * バージョンを表示するメソッド
	 */
	private void aboutMe () {
		JOptionPane.showMessageDialog(pf,GDA_Corpus_Browser.about_me_msg,"Version", JOptionPane.PLAIN_MESSAGE);
	}
	/**
	 * メニューバーに現在のビューを設定するメソッド
	private void set_view_in_menu_bar () {
		if(p_mf.current_view == E_View.SEARCH){
			jRadioButtonMenuItem_search_view.setSelected(true);
		}else if(p_mf.current_view == E_View.BROWSE){
			jRadioButtonMenuItem_browse_view.setSelected(true);
		}else if(p_mf.current_view == E_View.BATCH){
			jRadioButtonMenuItem_batch_view.setSelected(true);
		}else if(p_mf.current_view == E_View.CONFIGURATION){
			jRadioButtonMenuItem_conf_view.setSelected(true);
		}
	}
	 */
	/**
	 * メニューバーに現在の対象コーパスを設定するメソッド
	 */
	private void set_corpus_in_menu_bar () {
		E_Corpus c = pf.current_target_corpus();
		if(c == E_Corpus.MAINITI){
			jRadioButtonMenuItem_corpus_mai.setSelected(true);
		}else if(c == E_Corpus.IWANAMI){
			jRadioButtonMenuItem_corpus_iwa.setSelected(true);
		}
	}
	/**
	 * メニューバーに現在のフォントサイズを設定するメソッド
	 */
	private void set_font_size_in_menu_bar () {
		int size = pf.conf_vf.font_size.get();
		font_size_in_menubar = size;
		if(size == 8){
			jRadioButtonMenuItem_font_size8.setSelected(true);
		}else if(size == 10){
			jRadioButtonMenuItem_font_size10.setSelected(true);
		}else if(size == 12){
			jRadioButtonMenuItem_font_size12.setSelected(true);
		}else if(size == 14){
			jRadioButtonMenuItem_font_size14.setSelected(true);
		}else if(size == 16){
			jRadioButtonMenuItem_font_size16.setSelected(true);
		}else if(size == 18){
			jRadioButtonMenuItem_font_size18.setSelected(true);
		}else if(size == 20){
			jRadioButtonMenuItem_font_size20.setSelected(true);
		}else{
			font_size_in_menubar = -1;
		}
	}
	/**
	 * フォントサイズのメニューボタンに割り当てるアクション
	 */
	private void action_font_size (java.awt.event.ItemEvent e){
		JRadioButtonMenuItem item = (JRadioButtonMenuItem) e.getSource();
		if(! item.isSelected()) return;

		int prev_font_size = pf.conf_vf.font_size.get();
		int font_size = Integer.parseInt( item.getText() );
		pf.conf_vf.changeFontSize( font_size );
		if(prev_font_size != font_size) pf.updateFont();
		font_size_in_menubar = font_size;
	}
	/**
	 * フォントを変更するメニューアイテムに割り当てるアクション
	private void action_font_family (java.awt.event.ActionEvent e){
		JMenuItem jmi = (JMenuItem) e.getSource();
		pf.conf_vf.changeFont(jmi.getText());
		pf.updateFont();	
	}
	 */
	
	/**
	 * This method initializes this
	 */
	private void initialize() {
        this.add(getJMenu_file());
        this.add(getJMenu_open_view());
        this.add(create_jMenu_window());
        this.add(getJMenu_option());
        this.add(getJMenu_help());
        
        ButtonGroup font_size_group = new ButtonGroup();
        font_size_group.add(jRadioButtonMenuItem_font_size8);
        font_size_group.add(jRadioButtonMenuItem_font_size10);
        font_size_group.add(jRadioButtonMenuItem_font_size12);
        font_size_group.add(jRadioButtonMenuItem_font_size14);
        font_size_group.add(jRadioButtonMenuItem_font_size16);
        font_size_group.add(jRadioButtonMenuItem_font_size18);
        font_size_group.add(jRadioButtonMenuItem_font_size20);
        
        ButtonGroup corpus_group = new ButtonGroup();
        corpus_group.add(jRadioButtonMenuItem_corpus_mai);
        corpus_group.add(jRadioButtonMenuItem_corpus_iwa);

        //ButtonGroup view_group = new ButtonGroup();
        //view_group.add(jRadioButtonMenuItem_browse_view);
        //view_group.add(jRadioButtonMenuItem_search_view);
        //view_group.add(jRadioButtonMenuItem_batch_view);
        //view_group.add(jRadioButtonMenuItem_conf_view);
        
        set_corpus_in_menu_bar();
        set_font_size_in_menu_bar();
	}

	/**
	 * ウィンドウのリストを選択するためのメニュー
	 */
	private JMenu create_jMenu_window() {
		JMenu menu = new JMenu();
		menu.setText("Window");
		//for(int i=wm.view_list.size()-1 ; i >= 0 ; i--){
		menu.setMnemonic(KeyEvent.VK_W);
		for(int i=0 ; i < wm.view_list.size() ; i++){
			menu.add(new JMenuItem_ViewFrame(wm.view_list.get_view_frame(i),wm.view_list.get_menu_title(i)));
		}
		return menu;
	}

	/**
	 * This method initializes jMenu_file	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenu_file() {
		if (jMenu_file == null) {
			jMenu_file = new JMenu();
			jMenu_file.setText("File");
			jMenu_file.setMnemonic(KeyEvent.VK_F);
			jMenu_file.add(getJMenuItem_close());
			jMenu_file.add(getJMenuItem_save_conf());
			jMenu_file.add(getJMenuItem_quit());
			jMenu_file.add(getJMenuItem_quit_immediately());
		}
		return jMenu_file;
	}

	/**
	 * This method initializes jMenuItem_save_conf	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_save_conf() {
		if (jMenuItem_save_conf == null) {
			jMenuItem_save_conf = new JMenuItem();
			jMenuItem_save_conf.setText("設定保存");
			jMenuItem_save_conf.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
			jMenuItem_save_conf.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean flag = pf.save_conf_file();
					if(flag) wm.show_notice_message_in_dialog("設定をファイルに保存しました",pf);
				}
			});
		}
		return jMenuItem_save_conf;
	}

	/**
	 * This method initializes jMenuItem_quit	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_quit() {
		if (jMenuItem_quit == null) {
			jMenuItem_quit = new JMenuItem();
			jMenuItem_quit.setText("終了");
			jMenuItem_quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
			jMenuItem_quit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					wm.quit_with_confirmation(pf);
				}
			});
		}
		return jMenuItem_quit;
	}

	/**
	 * This method initializes jMenu_option	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenu_option() {
		if (jMenu_option == null) {
			jMenu_option = new JMenu();
			jMenu_option.setText("Option");
			jMenu_option.setMnemonic(KeyEvent.VK_O);
			jMenu_option.add(getJMenuItem_conf_view());
			jMenu_option.add(getJMenu_corpus());
			jMenu_option.add(getJMenu_font_size());
			jMenu_option.addSeparator();
			jMenu_option.add(getJMenuItem_make_index());
		}
		return jMenu_option;
	}

	/**
	 * This method initializes jMenuItem_make_index	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_make_index() {
		if (jMenuItem_make_index == null) {
			jMenuItem_make_index = new JMenuItem();
			jMenuItem_make_index.setText("インデックス作成");
			jMenuItem_make_index.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					wm.open_merge_gda_frame();
				}
			});
		}
		return jMenuItem_make_index;
	}

	/**
	 * This method initializes jMenu_help	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenu_help() {
		if (jMenu_help == null) {
			jMenu_help = new JMenu();
			jMenu_help.setText("Help");
			jMenu_help.setMnemonic(KeyEvent.VK_H);
			jMenu_help.add(getJMenuItem_help());
			jMenu_help.add(getJMenuItem_version());
		}
		return jMenu_help;
	}

	/**
	 * This method initializes jMenuItem_help	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_help() {
		if (jMenuItem_help == null) {
			jMenuItem_help = new JMenuItem();
			jMenuItem_help.setText("ヘルプ(h)");
			jMenuItem_help.setMnemonic(KeyEvent.VK_H);
			jMenuItem_help.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					wm.show_help_by_web_browser();
				}
			});
		}
		return jMenuItem_help;
	}

	/**
	 * This method initializes jMenuItem_version	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_version() {
		if (jMenuItem_version == null) {
			jMenuItem_version = new JMenuItem();
			jMenuItem_version.setMnemonic(KeyEvent.VK_V);
			jMenuItem_version.setText("バージョン(v)");
			jMenuItem_version.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					aboutMe();
				}
			});
		}
		return jMenuItem_version;
	}

	/**
	 * This method initializes jMenu_font_size	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenu_font_size() {
		if (jMenu_font_size == null) {
			jMenu_font_size = new JMenu();
			jMenu_font_size.setText("フォントサイズ(s)");
			jMenu_font_size.setMnemonic(KeyEvent.VK_S);
			jMenu_font_size.add(getJRadioButtonMenuItem_font_size8());
			jMenu_font_size.add(getJRadioButtonMenuItem_font_size10());
			jMenu_font_size.add(getJRadioButtonMenuItem_font_size12());
			jMenu_font_size.add(getJRadioButtonMenuItem_font_size14());
			jMenu_font_size.add(getJRadioButtonMenuItem_font_size16());
			jMenu_font_size.add(getJRadioButtonMenuItem_font_size18());
			jMenu_font_size.add(getJRadioButtonMenuItem_font_size20());
			jMenu_font_size.addSeparator();
			jMenu_font_size.add(getJMenuItem_font_size_default());
		}
		return jMenu_font_size;
	}

	/**
	 * This method initializes jRadioButtonMenuItem_font_size8	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getJRadioButtonMenuItem_font_size8() {
		if (jRadioButtonMenuItem_font_size8 == null) {
			jRadioButtonMenuItem_font_size8 = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_font_size8.setText("8");
			jRadioButtonMenuItem_font_size8
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							action_font_size(e);
						}
					});
		}
		return jRadioButtonMenuItem_font_size8;
	}

	/**
	 * This method initializes jRadioButtonMenuItem_font_size10	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getJRadioButtonMenuItem_font_size10() {
		if (jRadioButtonMenuItem_font_size10 == null) {
			jRadioButtonMenuItem_font_size10 = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_font_size10.setText("10");
			jRadioButtonMenuItem_font_size10
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							action_font_size(e);
						}
					});
		}
		return jRadioButtonMenuItem_font_size10;
	}

	/**
	 * This method initializes jRadioButtonMenuItem_font_size12	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getJRadioButtonMenuItem_font_size12() {
		if (jRadioButtonMenuItem_font_size12 == null) {
			jRadioButtonMenuItem_font_size12 = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_font_size12.setText("12");
			jRadioButtonMenuItem_font_size12
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							action_font_size(e);
						}
					});
		}
		return jRadioButtonMenuItem_font_size12;
	}

	/**
	 * This method initializes jRadioButtonMenuItem_font_size14	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getJRadioButtonMenuItem_font_size14() {
		if (jRadioButtonMenuItem_font_size14 == null) {
			jRadioButtonMenuItem_font_size14 = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_font_size14.setText("14");
			jRadioButtonMenuItem_font_size14
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							action_font_size(e);
						}
					});
		}
		return jRadioButtonMenuItem_font_size14;
	}

	/**
	 * This method initializes jRadioButtonMenuItem_font_size16	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getJRadioButtonMenuItem_font_size16() {
		if (jRadioButtonMenuItem_font_size16 == null) {
			jRadioButtonMenuItem_font_size16 = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_font_size16.setText("16");
			jRadioButtonMenuItem_font_size16
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							action_font_size(e);
						}
					});
		}
		return jRadioButtonMenuItem_font_size16;
	}

	/**
	 * This method initializes jRadioButtonMenuItem_font_size18	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getJRadioButtonMenuItem_font_size18() {
		if (jRadioButtonMenuItem_font_size18 == null) {
			jRadioButtonMenuItem_font_size18 = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_font_size18.setText("18");
			jRadioButtonMenuItem_font_size18
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							action_font_size(e);
						}
					});
		}
		return jRadioButtonMenuItem_font_size18;
	}

	/**
	 * This method initializes jRadioButtonMenuItem_font_size20	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getJRadioButtonMenuItem_font_size20() {
		if (jRadioButtonMenuItem_font_size20 == null) {
			jRadioButtonMenuItem_font_size20 = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_font_size20.setText("20");
			jRadioButtonMenuItem_font_size20
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							action_font_size(e);
						}
					});
		}
		return jRadioButtonMenuItem_font_size20;
	}

	/**
	 * This method initializes jMenuItem_quit_immediately	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_quit_immediately() {
		if (jMenuItem_quit_immediately == null) {
			jMenuItem_quit_immediately = new JMenuItem();
			jMenuItem_quit_immediately.setText("直ちに終了(q)");
			jMenuItem_quit_immediately.setMnemonic(KeyEvent.VK_Q);
			jMenuItem_quit_immediately
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							wm.quit(pf);
						}
					});
		}
		return jMenuItem_quit_immediately;
	}

	/**
	 * This method initializes jMenuItem_font_size_default	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_font_size_default() {
		if (jMenuItem_font_size_default == null) {
			jMenuItem_font_size_default = new JMenuItem();
			jMenuItem_font_size_default.setText("デフォルト");
			jMenuItem_font_size_default
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							jRadioButtonMenuItem_font_size14.setSelected(true);
						}
					});
		}
		return jMenuItem_font_size_default;
	}

	/**
	 * This method initializes jMenuItem_browse_view	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_browse_view() {
		if (jMenuItem_browse_view == null) {
			jMenuItem_browse_view = new JMenuItem();
			jMenuItem_browse_view.setText("閲覧ビュー(b)");
			jMenuItem_browse_view.setMnemonic(KeyEvent.VK_B);
			jMenuItem_browse_view.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					wm.create_new_browse_view(pf);
				}
			});
		}
		return jMenuItem_browse_view;
	}

	/**
	 * This method initializes jMenuItem_search_view	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_search_view() {
		if (jMenuItem_search_view == null) {
			jMenuItem_search_view = new JMenuItem();
			jMenuItem_search_view.setText("検索ビュー(s)");
			jMenuItem_search_view.setMnemonic(KeyEvent.VK_S);
			//jMenuItem_search_view.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
			jMenuItem_search_view.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					wm.create_new_kwic_view(pf);
				}
			});
		}
		return jMenuItem_search_view;
	}

	/**
	 * This method initializes jMenuItem_batch_view	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_batch_view() {
		if (jMenuItem_batch_view == null) {
			jMenuItem_batch_view = new JMenuItem();
			jMenuItem_batch_view.setText("一括検索ビュー(t)");
			jMenuItem_batch_view.setMnemonic(KeyEvent.VK_T);
			jMenuItem_batch_view.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					wm.create_new_batch_view(pf);
				}
			});
		}
		return jMenuItem_batch_view;
	}

	/**
	 * This method initializes jMenuItem_conf_view	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_conf_view() {
		if (jMenuItem_conf_view == null) {
			jMenuItem_conf_view = new JMenuItem();
			jMenuItem_conf_view.setText("設定(o)");
			jMenuItem_conf_view.setMnemonic(KeyEvent.VK_O);
			jMenuItem_conf_view.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					wm.open_conf_view();
				}
			});
		}
		return jMenuItem_conf_view;
	}

	/**
	 * This method initializes jMenu_open_view	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenu_open_view() {
		if (jMenu_open_view == null) {
			jMenu_open_view = new JMenu();
			jMenu_open_view.setText("View");
			jMenu_open_view.setMnemonic(KeyEvent.VK_V);
			jMenu_open_view.add(getJMenuItem_browse_view());
			jMenu_open_view.add(getJMenuItem_search_view());
			jMenu_open_view.add(getJMenuItem_batch_view());
		}
		return jMenu_open_view;
	}

	/**
	 * This method initializes jMenuItem_close	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem_close() {
		if (jMenuItem_close == null) {
			jMenuItem_close = new JMenuItem();
			jMenuItem_close.setText("閉じる");
			jMenuItem_close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
			jMenuItem_close.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					wm.close_view(pf);
				}
			});
		}
		return jMenuItem_close;
	}

	/**
	 * This method initializes jMenu_font	
	 * 	
	 * @return javax.swing.JMenu	
	private JMenu getJMenu_font() {
		if (jMenu_font == null) {
			jMenu_font = new JMenu();
			jMenu_font.setToolTipText("Font");
			jMenu_font.setText("Font");
			jMenu_font.setMnemonic(KeyEvent.VK_N);
			jMenu_font.add(getJMenu_font_size());
		}
		return jMenu_font;
	}
	 */

	/**
	 * This method initializes jMenu_corpus	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenu_corpus() {
		if (jMenu_corpus == null) {
			jMenu_corpus = new JMenu();
			jMenu_corpus.setText("コーパス(c)");
			jMenu_corpus.setMnemonic(KeyEvent.VK_C);
			jMenu_corpus.add(getJRadioButtonMenuItem_corpus_mai());
			jMenu_corpus.add(getJRadioButtonMenuItem_corpus_iwa());
		}
		return jMenu_corpus;
	}

	/**
	 * This method initializes jRadioButtonMenuItem_corpus_mai	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getJRadioButtonMenuItem_corpus_mai() {
		if (jRadioButtonMenuItem_corpus_mai == null) {
			jRadioButtonMenuItem_corpus_mai = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_corpus_mai.setText("新聞GDAコーパス");
			if(SYSTEM_CORPUS == E_Corpus.IWANAMI) jRadioButtonMenuItem_corpus_mai.setEnabled(false);
			jRadioButtonMenuItem_corpus_mai
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							if(jRadioButtonMenuItem_corpus_mai.isSelected()){
								pf.change_target_corpus(E_Corpus.MAINITI);
							}else{
								pf.change_target_corpus(E_Corpus.IWANAMI);
							}
						}
					});
		}
		return jRadioButtonMenuItem_corpus_mai;
	}

	/**
	 * This method initializes jRadioButtonMenuItem_corpus_iwa	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getJRadioButtonMenuItem_corpus_iwa() {
		if (jRadioButtonMenuItem_corpus_iwa == null) {
			jRadioButtonMenuItem_corpus_iwa = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_corpus_iwa.setText("岩波GDAコーパス");
			if(SYSTEM_CORPUS == E_Corpus.MAINITI) jRadioButtonMenuItem_corpus_iwa.setEnabled(false);
		}
		return jRadioButtonMenuItem_corpus_iwa;
	}

	/**
	 * This method initializes jMenu_font_name	
	 * 	
	 * @return javax.swing.JMenu	
	private JMenu getJMenu_font_name() {
		if (jMenu_font_name == null) {
			jMenu_font_name = new JMenu();
			jMenu_font_name.setText("Font");
			jMenu_font_name.add(getJMenuItem_font_ms_gothic());
			jMenu_font_name.add(getJMenuItem_font_ms_Pgothic());
			jMenu_font_name.add(getJMenuItem_font_hg_mintyo());
			jMenu_font_name.add(getJMenuItem_font_hg_soei());
			jMenu_font_name.add(getJMenuItem_font_dialog());
		}
		return jMenu_font_name;
	}
	 */
	/**
	 * This method initializes jMenuItem_font_ms_gothic	
	 * 	
	 * @return javax.swing.JMenuItem	
	private JMenuItem getJMenuItem_font_ms_gothic() {
		if (jMenuItem_font_ms_gothic == null) {
			jMenuItem_font_ms_gothic = new JMenuItem();
			jMenuItem_font_ms_gothic.setText("ＭＳ ゴシック");
			jMenuItem_font_ms_gothic.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					action_font_family(e);
				}
			});
		}
		return jMenuItem_font_ms_gothic;
	}
	 */
	/**
	 * This method initializes jMenuItem_font_ms_Pgothic	
	 * 	
	 * @return javax.swing.JMenuItem	
	private JMenuItem getJMenuItem_font_ms_Pgothic() {
		if (jMenuItem_font_ms_Pgothic == null) {
			jMenuItem_font_ms_Pgothic = new JMenuItem();
			jMenuItem_font_ms_Pgothic.setText("ＭＳ Ｐゴシック");
			jMenuItem_font_ms_Pgothic
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							action_font_family(e);
						}
					});
		}
		return jMenuItem_font_ms_Pgothic;
	}
	 */
	/**
	 * This method initializes jMenuItem_font_hg_mintyo	
	 * 	
	 * @return javax.swing.JMenuItem	
	private JMenuItem getJMenuItem_font_hg_mintyo() {
		if (jMenuItem_font_hg_mintyo == null) {
			jMenuItem_font_hg_mintyo = new JMenuItem();
			jMenuItem_font_hg_mintyo.setText("HGS明朝E");
			jMenuItem_font_hg_mintyo.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					action_font_family(e);
				}
			});
		}
		return jMenuItem_font_hg_mintyo;
	}
	 */
	/**
	 * This method initializes jMenuItem_font_hg_soei	
	 * 	
	 * @return javax.swing.JMenuItem	
	private JMenuItem getJMenuItem_font_hg_soei() {
		if (jMenuItem_font_hg_soei == null) {
			jMenuItem_font_hg_soei = new JMenuItem();
			jMenuItem_font_hg_soei.setText("HGS創英ﾌﾟﾚｾﾞﾝｽEB");
			jMenuItem_font_hg_soei.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					action_font_family(e);
				}
			});
		}
		return jMenuItem_font_hg_soei;
	}
	 */
	/**
	 * This method initializes jMenuItem_font_dialog	
	 * 	
	 * @return javax.swing.JMenuItem	
	private JMenuItem getJMenuItem_font_dialog() {
		if (jMenuItem_font_dialog == null) {
			jMenuItem_font_dialog = new JMenuItem();
			jMenuItem_font_dialog.setText("Dialog");
			jMenuItem_font_dialog.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					action_font_family(e);
				}
			});
		}
		return jMenuItem_font_dialog;
	}
	 */
	/*
	 * This method initializes jMenu_view	
	 * 	
	 * @return javax.swing.JMenu	
	private JMenu getJMenu_view() {
		if (jMenu_view == null) {
			jMenu_view = new JMenu();
			jMenu_view.setText("View");
			jMenu_view.add(getJRadioButtonMenuItem_browse_view());
			jMenu_view.add(getJRadioButtonMenuItem_search_view());
			jMenu_view.add(getJRadioButtonMenuItem_batch_view());
			jMenu_view.add(getJRadioButtonMenuItem_conf_view());
		}
		return jMenu_view;
	}
	 */
	/*
	 * This method initializes jRadioButtonMenuItem_browse_view	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	private JRadioButtonMenuItem getJRadioButtonMenuItem_browse_view() {
		if (jRadioButtonMenuItem_browse_view == null) {
			jRadioButtonMenuItem_browse_view = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_browse_view.setText("ファイル閲覧ビュー(f)");
			jRadioButtonMenuItem_browse_view.setMnemonic(KeyEvent.VK_F);
			jRadioButtonMenuItem_browse_view
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							if(! jRadioButtonMenuItem_browse_view.isSelected()) return;
							if(p_mf.current_view != E_View.BROWSE) p_mf.switch_view(E_View.BROWSE);
						}
					});
		}
		return jRadioButtonMenuItem_browse_view;
	}
	 */
	/*
	 * This method initializes jRadioButtonMenuItem_search_view	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	private JRadioButtonMenuItem getJRadioButtonMenuItem_search_view() {
		if (jRadioButtonMenuItem_search_view == null) {
			jRadioButtonMenuItem_search_view = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_search_view.setText("検索ビュー(s)");
			jRadioButtonMenuItem_search_view.setMnemonic(KeyEvent.VK_S);
			jRadioButtonMenuItem_search_view
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							if(! jRadioButtonMenuItem_search_view.isSelected()) return;
							if(p_mf.current_view != E_View.SEARCH) p_mf.switch_view(E_View.SEARCH);
						}
					});
		}
		return jRadioButtonMenuItem_search_view;
	}
	 */
	/*
	 * This method initializes jRadioButtonMenuItem_batch_view	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	private JRadioButtonMenuItem getJRadioButtonMenuItem_batch_view() {
		if (jRadioButtonMenuItem_batch_view == null) {
			jRadioButtonMenuItem_batch_view = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_batch_view.setText("一括検索ビュー(b)");
			jRadioButtonMenuItem_batch_view.setMnemonic(KeyEvent.VK_B);
			jRadioButtonMenuItem_batch_view
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							if(! jRadioButtonMenuItem_batch_view.isSelected()) return;
							if(p_mf.current_view != E_View.BATCH) p_mf.switch_view(E_View.BATCH);
						}
					});
		}
		return jRadioButtonMenuItem_batch_view;
	}
	 */
	/*
	 * This method initializes jRadioButtonMenuItem_conf_view	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	private JRadioButtonMenuItem getJRadioButtonMenuItem_conf_view() {
		if (jRadioButtonMenuItem_conf_view == null) {
			jRadioButtonMenuItem_conf_view = new JRadioButtonMenuItem();
			jRadioButtonMenuItem_conf_view.setText("設定ビュー(c)");
			jRadioButtonMenuItem_conf_view.setMnemonic(KeyEvent.VK_C);
			jRadioButtonMenuItem_conf_view
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							//if(! jRadioButtonMenuItem_conf_view.isSelected()) return;
							//if(p_mf.current_view != E_View.CONFIGURATION) p_mf.switch_view(E_View.CONFIGURATION);
							
						}
					});
		}
		return jRadioButtonMenuItem_conf_view;
	}
	 */

	/**
	 * ウィンドウを登録するための拡張メニューアイテムの内部クラス
	 */
	private class JMenuItem_ViewFrame extends JMenuItem {
		private static final long serialVersionUID = 1L;
		ViewFrame frame = null;
		JMenuItem_ViewFrame(ViewFrame fr,String t){
			super();
			this.frame = fr;
			this.setText(t);
			if(pf == this.frame){
				URL url = this.getClass().getResource("icon/check_rd.gif");
				if(url != null){
					this.setIcon(new ImageIcon(url));
					this.setHorizontalTextPosition(SwingConstants.LEADING);
				}
			}
			this.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					frame.toFront();
				}
			});			
		}
	}
}
