/**
 * ビュー共通のフレーム(ウィンドウ)のクラス
 */

package jp.or.gsk.gdacb.gui;

import jp.or.gsk.gdacb.*;
import jp.or.gsk.gdacb.gui.parts.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.JLabel;

public class ViewFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	static final Color bg_color_of_message_area = new Color(255, 255, 237);	// 薄い黄色
	static final Color bg_color_of_tab_area = new Color(245, 245, 255);	// 薄紫

	E_View view_type;
	WindowManager wm;
	ViewFrame source = null;
	Conf_ViewFrame conf_vf;
	
    MainMenuBar menu_bar = null;
    BrowseView browse_view = null;
	KWIC_View kwic_view = null;
    BatchView batch_view = null;
    
    private CardLayout card = null;
//    private Clipboard clipboard = null;

	private JPanel jPanel_view = null;
	private JPanel jPanel_body = null;
	private JLabel jLabel_status_view = null;
	private JPanel jPanel_status_bar = null;
	private JLabel jLabel_status_corpus = null;
	private JLabel jLabel_padding_status = null;

	/**
	 * constructor
	 * @param mgr	ウィンドウマネージャー
	 * @param view	ビューの種別
	 * @param src	このウィンドウを生成する基となるウィンドウ 
	 */
	public ViewFrame(WindowManager mgr,E_View view,ViewFrame src) {
		super();
		this.wm = mgr;
		this.view_type = view;
		this.source = src;
		//this.conf_vf = (src == null) ? wm.conf.conf_vf.clone() : source.conf_vf.clone();
		this.conf_vf = wm.conf.conf_vf.clone();
		initialize();

		this.setLocationRelativeTo(this.source);
		if(this.source != null){
			Point pt = this.getLocation();
			this.setLocation(pt.x+20, pt.y+20);
		}
//		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * このウィンドウの設定を保存するメソッド
	 */
	void save_parameter(){
		System.out.println("here");
		if(this.view_type == E_View.BROWSE){
			this.browse_view.save_parameters();
		}else if(this.view_type == E_View.SEARCH){
			this.kwic_view.save_parameters();
		}else if(this.view_type == E_View.BATCH){
			this.batch_view.save_parameters();
		}
	}
	/**
	 * このウィンドウの設定をツール全体の設定に反映させるメソッド
	 */
	void save_parameter_global(){
		// ビューの設定項目を保存する
		// clone()を使うのは、wm.conf.* に単純に値をセットすると、ポインタが保持されるため、
		// ビューを閉じてもそのビューがメモリから解放されないような気がするため
		if(this.view_type == E_View.BROWSE){
			this.browse_view.save_parameters();
			wm.conf.conf_browse = this.browse_view.conf_browse.clone();
		}else if(this.view_type == E_View.SEARCH){
			this.kwic_view.save_parameters();
			wm.conf.conf_kwic = this.kwic_view.conf_kwic.clone();
		}else if(this.view_type == E_View.BATCH){
			this.batch_view.save_parameters();
			wm.conf.conf_batch = this.batch_view.conf_batch.clone();
		}
		wm.conf.conf_vf = this.conf_vf.clone();
	}
	/**
	 * 設定ファイルへの保存
	 */
	boolean save_conf_file(){
		save_parameter_global();
		return wm.save_conf_file();
	}
	/**
	 * 検索対象となるコーパスを切り換える
	 * @param new_corpus	対象コーパス
	 */
	void change_target_corpus(E_Corpus new_corpus){
		if(this.view_type == E_View.BROWSE){
			if(this.browse_view.conf_browse.target_corpus.get() == new_corpus) return;
			browse_view.save_parameters();	// 変更前のコーパスにおける設定項目を保存
			this.browse_view.conf_browse.target_corpus.set(new_corpus);
			browse_view.change_target_corpus();
		}else if(this.view_type == E_View.SEARCH){
			if(this.kwic_view.conf_kwic.target_corpus.get() == new_corpus) return;
			kwic_view.save_parameters();	// 変更前のコーパスにおける設定項目を保存
			this.kwic_view.conf_kwic.target_corpus.set(new_corpus);
			kwic_view.change_target_corpus();
		}else if(this.view_type == E_View.BATCH){
			if(this.batch_view.conf_batch.target_corpus.get() == new_corpus) return;
			batch_view.save_parameters();	// 変更前のコーパスにおける設定項目を保存
			this.batch_view.conf_batch.target_corpus.set(new_corpus);
			batch_view.change_target_corpus();
		}
		update_status_bar();
	}

	/**
	 * ステータスバーを更新するメソッド
	 */
	void update_status_bar(){
		E_Corpus c = current_target_corpus();
		String corpus_name = (c == null) ? "不明" : c.corpus_name();

		jLabel_status_view.setText(view_type.toString());
		jLabel_status_corpus.setText("["+corpus_name+"]");
	}
	/**
	 * このビューの現在の対象コーパスを返す 
	 */
	E_Corpus current_target_corpus (){
		if(this.view_type == E_View.BROWSE){
			return this.browse_view.conf_browse.target_corpus.get();
		}else if(this.view_type == E_View.SEARCH){
			return this.kwic_view.conf_kwic.target_corpus.get();
		}else if(this.view_type == E_View.BATCH){
			return this.batch_view.conf_batch.target_corpus.get();
		}else{
			return null;
		}
	}
	/**
	 * メニューバーを再構築するメソッド
	 */
	void reconstruct_menu_bar (){
		Dimension prev_window_size = this.getSize();
		this.setJMenuBar(new MainMenuBar(wm,this));
		this.pack();
		this.setSize(prev_window_size);
	}

	/**
	 * フォントサイズを変更するメソッド
	 * 出典: http://terai.xrea.jp/Swing/FontChange.html
	 */
	void updateFont() {
		Dimension prev_window_size = this.getSize();
		
		FontUIResource fontUIResource = new FontUIResource(this.conf_vf.font);
		for(Object o: UIManager.getLookAndFeelDefaults().keySet()) {
			if(o.toString().toLowerCase().endsWith("font")) {
				// To show all font keys for debug
				//System.out.println(o.toString());
				UIManager.put(o, fontUIResource);
			}
		}
		recursiveUpdateUI(jPanel_body);
        Container c = jPanel_body.getTopLevelAncestor();
        if(c!=null && c instanceof Window) ((Window)c).pack();
        if(kwic_view != null) kwic_view.update_font();

        // メニューバーのフォントを変更する
        // バーのテキストは変更されるが、メニューの中のアイテムのテキストは更新されない
//		recursiveUpdateUI(menu_bar);
//		c = menu_bar.getTopLevelAncestor();
//		if(c!=null && c instanceof Window) ((Window)c).pack();
        // メニューバーのフォントの更新は上記の方法ではうまくいかないため、再構築する
        reconstruct_menu_bar();
        
        // ウィンドウのサイズを復元
        this.setSize(prev_window_size);
    }
	static void recursiveUpdateUI(JComponent p) {
		for(Component c: p.getComponents()) {
// 	JToolBar はフォントを変更しないとき
//			if(c instanceof JToolBar) continue;
			
			if(c instanceof JButtonHL) {
				JButtonHL b = (JButtonHL) c;
				b.updateUI();
				b.setUI(new RoundedCornerButtonUI());
				continue;
			}
			if(c instanceof JComponent) {
				JComponent jc = (JComponent)c;
				jc.updateUI();
				if(jc.getComponentCount()>0) recursiveUpdateUI(jc);
			}
		}
	}

	/**
	 * ビューを作成するメソッド
	 */
	private void create_view(E_View v) {
		if(v == E_View.BROWSE){
			jPanel_view.add(E_View.BROWSE.viewName(), get_browse_view());
			card.show(jPanel_view, E_View.BROWSE.viewName());
		}else if(v == E_View.SEARCH){
			jPanel_view.add(E_View.SEARCH.viewName(), get_kwic_view());
			card.show(jPanel_view, E_View.SEARCH.viewName());
		}else if(v == E_View.BATCH){
			jPanel_view.add(E_View.BATCH.viewName(), get_batch_view());				
			card.show(jPanel_view, E_View.BATCH.viewName());
			// batch_view.removeMsg();
		//}else if(v == E_View.CONFIGURATION){
		//	jPanel_view.add(E_View.BATCH.viewName(), get_conf_view());
		//	card.show(jPanel_view, E_View.CONFIGURATION.viewName());
		}
	}
	/*
	void set_clipboard(String s){
		clipboard.setContents(new StringSelection(s),null);
	}
	*/

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setContentPane(getJPanel_body());
		//this.setJMenuBar(getMainMenuBar());
		this.setTitle("GDA Corpus Browser");

		// ビューの作成
		create_view(view_type);
		// ステータスバーの設定
		update_status_bar();
		this.setSize(750,500);
		//this.setLocationRelativeTo(null);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				wm.close_view((ViewFrame)e.getSource());
			}
		});
	}
	/**
	 * タイミングを遅らせて実行する初期化の処理
	 *   new ViewFrame() を実行し、作成されたオブジェクトを
	 *   WindowManager.view_list に登録した後にこのメソッドを呼び出す
	 */
	void post_processing (){
		updateFont();
		this.setVisible(true);
	}

	/*
	 * This method initializes menu_bar	
	 * 	
	 * @return MainMenuBar
	private MainMenuBar getMainMenuBar() {
		if (menu_bar == null) {
			menu_bar = new MainMenuBar(wm,this);
		}
		return menu_bar;
	}
	 */

	/**
	 * This method initializes jPanel_view	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_view() {
		if (jPanel_view == null) {
			card = new CardLayout();
			jPanel_view = new JPanel();
			// カードレイアウトを使ってるのは、ウィンドウを1つ作成してビューを切り換えていたときの名残り
			jPanel_view.setLayout(card);
		}
		return jPanel_view;
	}

	/**
	 * This method initializes jPanel_body	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_body() {
		if (jPanel_body == null) {
			GridBagConstraints gBC_P_status_bar = new GridBagConstraints();
			gBC_P_status_bar.gridx = 0;
			gBC_P_status_bar.anchor = GridBagConstraints.EAST;
			gBC_P_status_bar.fill = GridBagConstraints.HORIZONTAL;
			gBC_P_status_bar.insets = new Insets(5, 0, 5, 0);
			gBC_P_status_bar.gridy = 0;
			GridBagConstraints gBC_P_view = new GridBagConstraints();
			gBC_P_view.gridx = 0;
			gBC_P_view.fill = GridBagConstraints.BOTH;
			gBC_P_view.weighty = 1.0D;
			gBC_P_view.weightx = 1.0D;
			gBC_P_view.gridy = 1;
			jPanel_body = new JPanel();
			jPanel_body.setLayout(new GridBagLayout());
			jPanel_body.setName("jPanel_body");
			jPanel_body.add(getJPanel_status_bar(), gBC_P_status_bar);
			//jPanel_body.add(getMainMenuBar(), gBC_P_status_bar);
			jPanel_body.add(getJPanel_view(), gBC_P_view);
		}
		return jPanel_body;
	}

	/**
	 * This method initializes jPanel_status_bar	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_status_bar() {
		if (jPanel_status_bar == null) {
			GridBagConstraints gBC_L_padding_status = new GridBagConstraints();
			gBC_L_padding_status.gridx = 1;
			gBC_L_padding_status.weightx = 1.0D;
			gBC_L_padding_status.gridy = 0;
			jLabel_padding_status = new JLabel();
			jLabel_padding_status.setText("");
			jLabel_padding_status.setPreferredSize(new Dimension(10, 10));
			GridBagConstraints gBC_L_status_corpus = new GridBagConstraints();
			gBC_L_status_corpus.gridx = 2;
			gBC_L_status_corpus.insets = new Insets(0, 5, 0, 5);
			gBC_L_status_corpus.anchor = GridBagConstraints.EAST;
			gBC_L_status_corpus.gridy = 0;
			jLabel_status_corpus = new JLabel();
			jLabel_status_corpus.setText("");
			jLabel_status_corpus.setToolTipText("検索対象となっているコーパスです");
			jLabel_status_corpus.setForeground(Color.white);
			GridBagConstraints gBC_L_status_view = new GridBagConstraints();
			gBC_L_status_view.anchor = GridBagConstraints.WEST;
			gBC_L_status_view.gridy = 0;
			gBC_L_status_view.insets = new Insets(0, 5, 0, 5);
			gBC_L_status_view.gridx = 0;
			jLabel_status_view = new JLabel();
			jLabel_status_view.setText("");
			jLabel_status_view.setToolTipText("現在表示中のビューです。");
			jLabel_status_view.setForeground(Color.white);
			//jLabel_status_view.setComponentPopupMenu(getJPopupMenu_switch_view());

			jPanel_status_bar = new JPanel();
			jPanel_status_bar.setLayout(new GridBagLayout());
			jPanel_status_bar.setBackground(new Color(0, 102, 102));
			jPanel_status_bar.add(jLabel_status_view, gBC_L_status_view);
			jPanel_status_bar.add(jLabel_padding_status, gBC_L_padding_status);
			jPanel_status_bar.add(jLabel_status_corpus, gBC_L_status_corpus);
		}
		return jPanel_status_bar;
	}
	private BrowseView get_browse_view () {
		if(browse_view == null){
			browse_view = new BrowseView(wm,this,wm.conf.conf_browse.clone());
		}
		return browse_view;
	}
	private KWIC_View get_kwic_view () {
		if(kwic_view == null){
			kwic_view = new KWIC_View(wm,this,wm.conf.conf_kwic.clone());
		}
		return kwic_view;
	}
	private BatchView get_batch_view () {
		if(batch_view == null){
			batch_view = new BatchView(wm,this,wm.conf.conf_batch.clone());
		}
		return batch_view;
	}
}
