package jp.or.gsk.gdacb.gui.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.geom.Ellipse2D;
import javax.swing.Icon;
import javax.swing.JLabel;

public class AnimatedLabel extends JLabel implements ActionListener, HierarchyListener {
	private static final long serialVersionUID = 1L;

	private final javax.swing.Timer animator;
    private final AnimeIcon icon = new AnimeIcon();
    public AnimatedLabel() {
    	super();
    	animator = new javax.swing.Timer(100, this);
    	setIcon(icon);
    	addHierarchyListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        icon.next();
        repaint();
    }
    @Override
    public void hierarchyChanged(HierarchyEvent e) {
    	if((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED)!=0 && !isDisplayable()) {
    		animator.stop();
        }
    }
    public void startAnimation() {
        icon.setRunning(true);
        animator.start();
    }
    public void stopAnimation() {
        icon.setRunning(false);
        animator.stop();
        repaint();	// added by SHIRAI
    }
    
    private class AnimeIcon implements Icon {
    	private final Color cColor = new Color(0.5f,0.5f,0.5f);
        private static final double r  = 2.0d;
        private static final double sx = 1.0d;
        private static final double sy = 1.0d;
        private final Dimension dim = new Dimension((int)(r*8+sx*2),(int)(r*8+sy*2));
        private final java.util.List<Shape> list = new ArrayList<Shape>(Arrays.asList(
        	new Ellipse2D.Double(sx+3*r, sy+0*r, 2*r, 2*r),
            new Ellipse2D.Double(sx+5*r, sy+1*r, 2*r, 2*r),
            new Ellipse2D.Double(sx+6*r, sy+3*r, 2*r, 2*r),
            new Ellipse2D.Double(sx+5*r, sy+5*r, 2*r, 2*r),
            new Ellipse2D.Double(sx+3*r, sy+6*r, 2*r, 2*r),
            new Ellipse2D.Double(sx+1*r, sy+5*r, 2*r, 2*r),
            new Ellipse2D.Double(sx+0*r, sy+3*r, 2*r, 2*r),
            new Ellipse2D.Double(sx+1*r, sy+1*r, 2*r, 2*r)));

        private boolean isRunning = false;
        public void next() {
        	if(isRunning) list.add(list.remove(0));
        }
        public void setRunning(boolean isRunning) {
        	this.isRunning = isRunning;
        }
        @Override public int getIconWidth()  { return dim.width;  }
        @Override public int getIconHeight() { return dim.height; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint((c!=null)?c.getBackground():Color.WHITE);
            g2d.fillRect(x, y, getIconWidth(), getIconHeight());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(cColor);
            float alpha = 0.0f;
            g2d.translate(x, y);
            for(Shape s: list) {
            	alpha = isRunning?alpha+0.1f:0.5f;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
                g2d.fill(s);
            }
            g2d.translate(-x, -y);
        }
    }
}
