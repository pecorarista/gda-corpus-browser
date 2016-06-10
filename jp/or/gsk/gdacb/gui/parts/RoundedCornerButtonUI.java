package jp.or.gsk.gdacb.gui.parts;

// 角丸ボタン
// 使い方: button = new JButton();
//        button.setUI(new RoundedCornerButtonUI());
// 以下のウェブページで公開されていたものをカスタマイズした
// http://terai.xrea.jp/Swing/RoundButton.html

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

public class RoundedCornerButtonUI extends BasicButtonUI{
    private static final float arcwidth  = 24.0f;	// 角の円の大きさ (元は16)
    private static final float archeight = 24.0f;
    protected static final int focusstroke = 2;
    //protected final Color fc = new Color(100,150,255,200);	// focus時の色
    //protected final Color ac = new Color(230,230,230);		// isArmed (ボタンが作動準備状態にある) ときの色
    //protected final Color rc = Color.ORANGE;				// Roll Over(マウスがボタンの上にある)ときの色
    protected final Color fc = new Color(255, 241, 0);	// 黄色と紫色の組み合わせ
    protected final Color ac = new Color(96, 25, 134);
    protected final Color rc = new Color(255, 241, 0);
    //protected final Color fc = new Color(234, 85, 4); // オレンジと薄い緑の組み合わせ
    //protected final Color ac = new Color(116, 198, 192);
    //protected final Color rc = new Color(234, 85, 4);
    protected Shape shape;
    protected Shape border;
    protected Shape base;
    protected final Color bg_color = new Color(96, 25, 134);
    //protected final Color bg_color = new Color(116, 198, 192);
    
    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        //b.setBackground(new Color(250, 250, 250));
        b.setBackground(bg_color);
        initShape(b);
    }
    @Override
    protected void installListeners(AbstractButton b) {
        BasicButtonListener listener = new BasicButtonListener(b) {
            @Override public void mousePressed(MouseEvent e) {
                AbstractButton b = (AbstractButton) e.getSource();
                initShape(b);
                if(shape.contains(e.getX(), e.getY())) {
                    super.mousePressed(e);
                }
            }
            @Override public void mouseEntered(MouseEvent e) {
                if(shape.contains(e.getX(), e.getY())) {
                    super.mouseEntered(e);
                }
            }
            @Override public void mouseMoved(MouseEvent e) {
                if(shape.contains(e.getX(), e.getY())) {
                    super.mouseEntered(e);
                }else{
                    super.mouseExited(e);
                }
            }
        };
        if(listener != null) {
            b.addMouseListener(listener);
            b.addMouseMotionListener(listener);
            b.addFocusListener(listener);
            b.addPropertyChangeListener(listener);
            b.addChangeListener(listener);
        }
    }
    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D)g;
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        initShape(b);

        //ContentArea
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(model.isArmed()) {
            g2.setColor(ac);
            g2.fill(shape);
        }else if(b.isRolloverEnabled() && model.isRollover()) {
            paintFocusAndRollover(g2, c, rc);
        }else if(b.hasFocus()) {
        	// フォーカス時は通常時と表示を変えないようにする
        	//paintFocusAndRollover(g2, c, fc);
            g2.setColor(ac);
            g2.fill(shape);
        }else{
            g2.setColor(c.getBackground());
            g2.fill(shape);
        }
        //Border
//        g2.setStroke(new BasicStroke(2));	// 参考: border の線の幅を指定
        // Borderは表示しないようにする
//        g2.setPaint(c.getForeground());
//        g2.draw(shape);

        g2.setColor(c.getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        super.paint(g2, c);
    }
    
    private void initShape(JComponent c) {
        if(!c.getBounds().equals(base)) {
            base = c.getBounds();
            shape = new RoundRectangle2D.Float(0, 0, c.getWidth()-1, c.getHeight()-1, arcwidth, archeight);
            border = new RoundRectangle2D.Float(focusstroke, focusstroke,
                                                c.getWidth()-1-focusstroke*2,
                                                c.getHeight()-1-focusstroke*2,
                                                arcwidth, archeight);
        }
    }
    private void paintFocusAndRollover(Graphics2D g2, JComponent c, Color color) {
        g2.setPaint(new GradientPaint(0, 0, color, c.getWidth()-1, c.getHeight()-1, color.brighter(), true));
        g2.fill(shape);
        g2.setColor(c.getBackground());
        g2.fill(border);
    }
}    
