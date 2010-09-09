package jebl.gui.trees.treeviewer.painters;

import org.virion.jam.controlpanels.ControlsProvider;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import jebl.evolution.graphs.Node;


/**
 * A painter draws a particular decoration onto the tree within a
 * rectangle.
 * @author Andrew Rambaut
 * @version $Id: Painter.java 989 2009-03-23 06:09:53Z stevensh $
 */
public interface Painter<T> extends ControlsProvider {

    public enum Orientation {
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    public enum Justification {
        FLUSH,
        LEFT,
        RIGHT,
        CENTER
    }

    boolean isVisible();

    //void calibrate(Graphics2D g2, T item);

    void calibrate(Graphics2D g2);

    // May change paint and stroke
    void paint(Graphics2D g2, T item, Justification justification, Rectangle2D bounds);

    double getWidth(Graphics2D g2, T item);
    double getPreferredHeight(Graphics2D g2, T item);
    double getHeightBound(Graphics2D g2, T item);

    void addPainterListener(PainterListener listener);
    void removePainterListener(PainterListener listener);

    void setPaintAsMirrorImage(boolean paintAsMirrorImage);

    void setForeground(Paint foreground);
    void setBackground(Paint background);
    void setBorder(Paint borderPaint, Stroke borderStroke);


}
