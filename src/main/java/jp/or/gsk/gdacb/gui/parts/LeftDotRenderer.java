package jp.or.gsk.gdacb.gui.parts;

/**
 * テキストの長さが表示幅を超えるとき、右寄せして左側に...を表示する TableCellRenderer
 * 情報源: http://forums.sun.com/thread.jspa?threadID=634798
 */

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LeftDotRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
 
		int availableWidth = table.getColumnModel().getColumn(column).getWidth();
		availableWidth -= table.getIntercellSpacing().getWidth();
		Insets borderInsets = getBorder().getBorderInsets((Component)this);
		availableWidth -= (borderInsets.left + borderInsets.right);
		String cellText = getText();
		FontMetrics fm = getFontMetrics( getFont() );
 
		if (fm.stringWidth(cellText) > availableWidth) {
			String dots = "...";
			int textWidth = fm.stringWidth( dots );
			int nChars = cellText.length() - 1;
			for (; nChars > 0; nChars--) {
				textWidth += fm.charWidth(cellText.charAt(nChars));
 
				if (textWidth > availableWidth) break;
			}
 
			setText( dots + cellText.substring(nChars + 1) );
		}
 
		return this;
	}
}
