package jp.or.gsk.gdacb.gui.parts;

/* テキストが数値かどうかを判定する FormatterFactory
 * 情報元: http://terai.xrea.jp/Swing/NumericTextField.html
 * 
 * テキストフィールドに数値のみを入れたいときに使うが、
 * 10a のような文字列の入力を受け付けてしまう
 */

import java.text.NumberFormat;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

class NumberFormatterFactory extends DefaultFormatterFactory {
	private static final long serialVersionUID = 1L;
	private static NumberFormatter numberFormatter = new NumberFormatter();
	static{
		numberFormatter.setValueClass(Integer.class);
        ((NumberFormat)numberFormatter.getFormat()).setGroupingUsed(false);
	}
    public NumberFormatterFactory() {
    	super(numberFormatter, numberFormatter, numberFormatter);
    }
}