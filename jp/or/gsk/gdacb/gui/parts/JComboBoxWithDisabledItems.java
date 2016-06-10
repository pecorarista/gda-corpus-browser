package jp.or.gsk.gdacb.gui.parts;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;

/**
 * 一部のアイテムを選択不可にできるJComboBox
 * @author kshirai
 * 
 * 情報源: http://terai.xrea.jp/Swing/DisableItemComboBox.html
 *
 */

public class JComboBoxWithDisabledItems extends JComboBox {
	private static final long serialVersionUID = 1L;
	public JComboBoxWithDisabledItems() {
		super();
		final ListCellRenderer r = getRenderer();
		setRenderer(new ListCellRenderer() {
			@Override public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component c;
				if(disableIndexSet.contains(index)) {
					c = r.getListCellRendererComponent(list,value,index,false,false);
					c.setEnabled(false);
				}else{
					c = r.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					c.setEnabled(true);
				}
				return c;
			}
		});
		Action up = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override public void actionPerformed(ActionEvent e) {
				int si = getSelectedIndex();
				for(int i = si-1;i>=0;i--) {
					if(!disableIndexSet.contains(i)) {
						setSelectedIndex(i);
						break;
					}
				}
			}
		};
		Action down = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override public void actionPerformed(ActionEvent e) {
				int si = getSelectedIndex();
				for(int i = si+1;i<getModel().getSize();i++) {
					if(!disableIndexSet.contains(i)) {
						setSelectedIndex(i);
						break;
					}
				}
			}
		};
		ActionMap am = getActionMap();
		am.put("selectPrevious3", up);
		am.put("selectNext3", down);
		InputMap im = getInputMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),      "selectPrevious3");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0),   "selectPrevious3");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),    "selectNext3");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), "selectNext3");
	}
	private final HashSet<Integer> disableIndexSet = new HashSet<Integer>();
	private boolean isDisableIndex = false;
	public void setDisableIndex(HashSet<Integer> set) {
		disableIndexSet.clear();
		for(Integer i:set) {
			disableIndexSet.add(i);
		}
	}
	public void setDisableIndex(int i){
		disableIndexSet.add(i);
	}
	public void setEnableIndex(int i){
		disableIndexSet.remove(i);
	}
	@Override
    public void setPopupVisible(boolean v) {
    	if(!v && isDisableIndex) {
    		isDisableIndex = false;
    	}else{
    		super.setPopupVisible(v);
    	}
	}
	@Override
	public void setSelectedIndex(int index) {
		if(disableIndexSet.contains(index)) {
			isDisableIndex = true;
		}else{
			//isDisableIndex = false;
			super.setSelectedIndex(index);
		}
	}
}
