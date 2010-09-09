package jebl.gui.trees.treeviewer.painters;

import jebl.util.NumberFormatter;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

/**
 * @author Andrew Rambaut
 * @version $Id: AbstractPainter.java 989 2009-03-23 06:09:53Z stevensh $
 */
public abstract class AbstractPainter<T> implements Painter<T> {

    public static final String TAXON_NAMES = "Names";
    public static final String NODE_HEIGHTS = "Node Heights";
    public static final String BRANCH_LENGTHS = "Substitutions per Site";

    protected boolean paintAsMirrorImage = true;
    protected Paint foreground = Color.BLACK;
    protected Paint background = null;
    protected Paint borderPaint = null;
    protected Stroke borderStroke = null;

    protected NumberFormatter formatter = new NumberFormatter(4);

    public void addPainterListener(PainterListener listener) {
        listeners.add(listener);
    }

    public void removePainterListener(PainterListener listener) {
        listeners.remove(listener);
    }

    public void firePainterChanged() {
        for (PainterListener listener : listeners) {
            listener.painterChanged();
        }
    }
    private final List<PainterListener> listeners = new ArrayList<PainterListener>();

    public void setPaintAsMirrorImage(boolean paintAsMirrorImage) {
        this.paintAsMirrorImage = paintAsMirrorImage;
    }

    public void setForeground(Paint foreground) {
        this.foreground = foreground;
    }

    public void setBackground(Paint background) {
        this.background = background;
    }

    public void setBorder(Paint borderPaint, Stroke borderStroke) {
        this.borderPaint = borderPaint;
        this.borderStroke = borderStroke;
        firePainterChanged();
    }

    protected void setSignificantDigits(int digits) {
        assert formatter != null;

        formatter.setSignificantFigures(digits);
        firePainterChanged();
    }

    protected String getFormattedValue(double d){
        if(d == 0)
            return "0";
        return formatter.getFormattedValue(d);
    }
}
