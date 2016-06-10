package jp.or.gsk.gdacb.gui.parts;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.CardLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import java.awt.Color;
import javax.swing.BorderFactory;

public class JTextArea_FlexibleSize extends JPanel {

	private static final long serialVersionUID = 1L;
	private Dimension fixed_textarea_size;
	private boolean flexible_text_area_flag = true;
	private boolean show_check_box_flag;
	private CardLayout card_layout_of_switch_panel = null;  //  @jve:decl-index=0:

	private JPanel jPanel_card = null;
	private JScrollPane jScrollPane_fix = null;
	private JTextArea jTextArea_fix = null;
	private JTextArea jTextArea_var = null;
	private JCheckBox jCheckBox_switch = null;
	/**
	 * Constructor
	 * @param w		幅
	 * @param h		高さ
	 * @param show_check_box	高さ自動調節のon/offを切り換えるチェックボックスを表示するフラグ
	 */
	public JTextArea_FlexibleSize(int w,int h,boolean show_check_box) {
		super();
		this.fixed_textarea_size = new Dimension(w,h);
		this.flexible_text_area_flag = false;
		this.show_check_box_flag = show_check_box;
		initialize();
	}
	/**
	 * 表示するテキストを設定するメソッド
	 * @param s	テキスト
	 */
	public void setText(String s){
		if(flexible_text_area_flag){
			jTextArea_var.setText(s);
			jTextArea_var.setCaretPosition(0);
		}else{
			jTextArea_fix.setText(s);
			jTextArea_fix.setCaretPosition(0);
		}
	}
	/**
	 * 表示されているテキストを得るメソッド
	 * @return	テキスト
	 */
	public String getText(){
		if(flexible_text_area_flag){
			return jTextArea_var.getText();
		}else{
			return jTextArea_fix.getText();
		}
	}
	/**
	 * 現在表示しているエリアが高さを自動調整できるか否かを返す
	 * @return	自動調整できるときに真
	 */
	public boolean isShowFlexibleTextArea(){
		return flexible_text_area_flag;
	}
	/**
	 * 現在のカーソルの位置を得るメソッド 
	 */
	public int getCaretPosition(){
		if(flexible_text_area_flag){
			return jTextArea_var.getCaretPosition();
		}else{
			return jTextArea_fix.getCaretPosition();
		}
	}
	//
	/**
	 * (外部から)テキストエリアの切り替えを行うメソッド
	 */
	public void setShowFlexibleTextArea(boolean b){
		if(b == flexible_text_area_flag) return;
		if(show_check_box_flag){
			jCheckBox_switch.setSelected(b);
		}else{
			switch_text_area(b);
		}
	}
	/**
	 * (内部で)テキストエリアの切り替えを行うメソッド
	 */
	private void switch_text_area(boolean show_flexible_area){
		if(show_flexible_area){
			jTextArea_var.setText( jTextArea_fix.getText() );
			jTextArea_var.setCaretPosition(0);
			jTextArea_fix.setText("");
			card_layout_of_switch_panel.show(jPanel_card, "var_area");
			flexible_text_area_flag = true;
		}else{
			jTextArea_fix.setText( jTextArea_var.getText() );
			jTextArea_fix.setCaretPosition(0);
			jTextArea_var.setText("");
			card_layout_of_switch_panel.show(jPanel_card, "fix_area");
			flexible_text_area_flag = false;
		}
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gBC_CB_switch = new GridBagConstraints();
		gBC_CB_switch.gridx = 1;
		gBC_CB_switch.weightx = 0.0D;
		gBC_CB_switch.gridy = 0;
		GridBagConstraints gBC_P_card = new GridBagConstraints();
		gBC_P_card.gridx = 0;
		gBC_P_card.weightx = 1.0D;
		gBC_P_card.fill = GridBagConstraints.HORIZONTAL;
		gBC_P_card.gridy = 0;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getJPanel_card(), gBC_P_card);
		if(show_check_box_flag)
			this.add(getJCheckBox_switch(), gBC_CB_switch);
	}

	/**
	 * This method initializes jPanel_card	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_card() {
		if (jPanel_card == null) {
			card_layout_of_switch_panel = new CardLayout();
			jPanel_card = new JPanel();
			jPanel_card.setLayout(card_layout_of_switch_panel);
			jPanel_card.add(getJScrollPane_fix(), "fix_area");
			jPanel_card.add(getJTextArea_var(), "var_area");
			card_layout_of_switch_panel.show(jPanel_card, "fix_area");
		}
		return jPanel_card;
	}

	/**
	 * This method initializes jScrollPane_fix	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane_fix() {
		if (jScrollPane_fix == null) {
			jScrollPane_fix = new JScrollPane();
			jScrollPane_fix.setName("fix_area");
			jScrollPane_fix.setPreferredSize(fixed_textarea_size);
			jScrollPane_fix.setMinimumSize(fixed_textarea_size);
//			jScrollPane_fix.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			jScrollPane_fix.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane_fix.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			jScrollPane_fix.setViewportView(getJTextArea_fix());
		}
		return jScrollPane_fix;
	}

	/**
	 * This method initializes jTextArea_fix	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea_fix() {
		if (jTextArea_fix == null) {
			jTextArea_fix = new JTextArea();
			jTextArea_fix.setLineWrap(true);
//			jTextArea_fix.setMinimumSize(fixed_textarea_size);
		}
		return jTextArea_fix;
	}

	/**
	 * This method initializes jTextArea_var	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea_var() {
		if (jTextArea_var == null) {
			jTextArea_var = new JTextArea();
			jTextArea_var.setName("var_area");
			jTextArea_var.setLineWrap(true);
			jTextArea_var.setBorder(BorderFactory.createLineBorder(Color.red, 1));
			// 内容に応じてサイズを変更するためには PreferredSize は設定してはいけない
//			jTextArea_var.setPreferredSize(new Dimension(50, 16));
		}
		return jTextArea_var;
	}

	/**
	 * This method initializes jCheckBox_switch	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox_switch() {
		if (jCheckBox_switch == null) {
			jCheckBox_switch = new JCheckBox();
			jCheckBox_switch.setText("FLEX");
			jCheckBox_switch.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					switch_text_area( jCheckBox_switch.isSelected() );
				}
			});
		}
		return jCheckBox_switch;
	}
}
