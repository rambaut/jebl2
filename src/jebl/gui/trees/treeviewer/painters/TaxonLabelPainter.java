package jebl.gui.trees.treeviewer.painters;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.taxa.Taxon;
import jebl.gui.trees.treeviewer.TreeViewer;
import jebl.gui.trees.treeviewer.TreeViewerUtilities;
import org.virion.jam.controlpanels.ControlPalette;
import org.virion.jam.controlpanels.Controls;
import org.virion.jam.controlpanels.ControlsSettings;
import org.virion.jam.panels.OptionsPanel;
import org.virion.jam.util.IconUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * @author Steven Stones-Havas
 * @version $Id$
 *          <p/>
 *          Created on 24/03/2009 11:35:41 AM
 */
public class TaxonLabelPainter extends BasicLabelPainter{

    private static final Preferences PREFS = Preferences.userNodeForPackage(TaxonLabelPainter.class);

    private Controls controls;
    private boolean visible;
    private String[] selectedAttributes = new String[] {TAXON_NAMES};
    private static final String SELECTED_FIELDS_SERIALIZATION_SEPARATOR = "|";


    public TaxonLabelPainter(RootedTree tree) {
        super("Tip Labels", tree, PainterIntent.TIP);

        visible = PREFS.getBoolean("Tip Labels_isopoen", true);
    }


    public boolean isVisible() {
        return visible;
    }

    public void calibrate(Graphics2D g2) {

    }

    public String getLabel(Node node){
        List<String> attributeValues = new ArrayList<String>();
        for(String s : selectedAttributes) {
            String value = getLabel(node, s);
            if(value != null) {
                attributeValues.add(value);
            }
        }

        if(attributeValues.size() == 0) {
            return null;
        }
        return join(", ", attributeValues);
    }

    public String getLabel(Node node, String attributeName){
        String prefix = " ";
        String suffix = " ";
        if (attributeName.equalsIgnoreCase(TAXON_NAMES)) {
            return prefix+tree.getTaxon(node).getName()+suffix;
        }

        if (attributeName.equalsIgnoreCase(NODE_HEIGHTS) ) {
            return prefix+getFormattedValue(tree.getHeight(node))+suffix;
        } else if (attributeName.equalsIgnoreCase(BRANCH_LENGTHS) ) {
            return prefix+getFormattedValue(tree.getLength(node))+suffix;
        }

        Object value = node.getAttribute(attributeName);
        final Taxon nodeTaxon = tree.getTaxon(node);
        if(value == null && nodeTaxon != null) {
            value = nodeTaxon.getAttribute(attributeName);
        }
        if (value != null) {
            if (value instanceof Double) {
                return prefix+formatter.getFormattedValue((Double) value)+suffix;
            }
            if(value instanceof Date){
                DateFormat format = new SimpleDateFormat("dd MMM yyyy h:mm a");
                return  prefix+format.format((Date)value)+suffix;
            }
            String s = value.toString();
            //limit node labels to 15 chars (plus ...)
            //if(s.length() > 15)
            //    return s.substring(0,15)+"...";
            return prefix+s+suffix;
        }
        return null;    
    }

    public void paint(Graphics2D g2, Node item, Justification justification, Rectangle2D bounds) {
        final Font oldFont = g2.getFont();

        if(paintAsMirrorImage) {
            g2.scale(-1,1);
            g2.translate(-bounds.getWidth()-2*bounds.getX(),0);
        }

        if (background != null) {
            g2.setPaint(background);
            g2.fill(bounds);
        }

        if (borderPaint != null && borderStroke != null) {
            g2.setPaint(borderPaint);
            g2.setStroke(borderStroke);
            g2.draw(bounds);
        }

        g2.setPaint(foreground);
        Object fontString = item.getAttribute("labelFont");
        Font taxonLabelFont = TreeViewerUtilities.DEFAULT_FONT;
        if(fontString != null) {
            try {
                taxonLabelFont = TreeViewerUtilities.fontFromString(fontString.toString());
            }
            catch(IllegalArgumentException ex) {
                System.err.println("Invalid font string: "+fontString);
            }
        }
        Font taxonExponentFont = taxonLabelFont.deriveFont(taxonLabelFont.getSize2D()*0.66f);
        g2.setFont(taxonLabelFont);


        final String label = getLabel(item);
        if (label != null) {
            String prefix = label;
            String suffix = "";

            int exponentIndex = label.indexOf("E");
            if(exponentIndex >= 0){
                try{
                    Double.parseDouble(label.substring(0, exponentIndex).trim());
                    Integer.parseInt(label.substring(exponentIndex+1, label.length()).trim());
                    prefix = label.substring(0, exponentIndex)+"x10";
                    suffix = label.substring(exponentIndex+1, label.length());
                }
                catch(NumberFormatException ex){} //skip out
                catch(IndexOutOfBoundsException ex){} //skip out
            }

            Rectangle2D rect = g2.getFontMetrics().getStringBounds(prefix, g2);

            float xOffset = 0;
            float yOffset = g2.getFontMetrics().getAscent();
            float y = yOffset + (float) bounds.getY();
            switch (justification) {
                case CENTER:
                    break;
                case FLUSH:
                case LEFT:
                    xOffset = (float) bounds.getX();
                    break;
                case RIGHT:
                    xOffset = (float) (bounds.getX() + bounds.getWidth() - rect.getWidth());
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized alignment enum option");
            }

            g2.drawString(prefix, xOffset, y);
            if(suffix.length() > 0){
                g2.setFont(taxonExponentFont);
                g2.drawString(suffix, xOffset+(float)rect.getWidth(), y-(float)rect.getHeight()/2);
            }
        }

        g2.setFont(oldFont);

        if(paintAsMirrorImage) {
            g2.translate(bounds.getWidth()+2*bounds.getX(),0);
            g2.scale(-1,1);
        }
    }

    public double getWidth(Graphics2D g2, Node item) {
        FontMetrics fm = getCurrentFontMetricsForGraphicsAndNode(g2, item);
        String label = getLabel(item);
        return label == null ? 0 : fm.getStringBounds(label, g2).getWidth();
    }

    public double getPreferredHeight(Graphics2D g2, Node item) {
        FontMetrics fm = getCurrentFontMetricsForGraphicsAndNode(g2, item);
        return fm.getHeight();
    }

    public double getHeightBound(Graphics2D g2, Node item) {
        FontMetrics fm = getCurrentFontMetricsForGraphicsAndNode(g2, item);
        return fm.getHeight()+fm.getAscent();
    }

    private FontMetrics getCurrentFontMetricsForGraphicsAndNode(Graphics2D g2, Node item) {
        Font oldFont = g2.getFont();
        Object fontValue = item.getAttribute("labelFont");
        g2.setFont(TreeViewerUtilities.DEFAULT_FONT);
        if(fontValue != null) {
            try {
                g2.setFont(TreeViewerUtilities.fontFromString(fontValue.toString()));
            }
            catch(IllegalArgumentException ex){
                System.err.println("Invalid font string: "+fontValue);
            }
        }
        FontMetrics fm = g2.getFontMetrics();
        g2.setFont(oldFont);
        return fm;
    }

    public void setControlPalette(ControlPalette controlPalette) {}

    public List<Controls> getControls(boolean detachPrimaryCheckbox) {
        List<Controls> controlsList = new ArrayList<Controls>();

        if(controls == null) {
            OptionsPanel panel = new OptionsPanel();

            final JCheckBox visibleCheckBox = new JCheckBox("Show Tip Labels", visible);
            visibleCheckBox.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e) {
                    visible = visibleCheckBox.isSelected();
                    PREFS.putBoolean("Tip Labels_isopoen", visible);
                    firePainterChanged();
                }
            });

            final JList attributeBox = new JList(attributes){
                @Override
                public Dimension getPreferredScrollableViewportSize() {
                    Insets insets = getBorder() != null ? getBorder().getBorderInsets(this) : new Insets(0,0,0,0);
                    int maximumHeight = System.getProperty("os.name").toLowerCase().contains("windows") ? 50 : 70; //needs to be a bit taller on macos because the scrollbar buttons are bigger
                    return new Dimension(super.getPreferredSize().width, Math.min(getPreferredSize().height+ insets.top+insets.bottom, maximumHeight));
                }
            };;
            String[] prefsValue = PREFS.get("Tip Labels_whatToDisplay", TAXON_NAMES).split("\\" + SELECTED_FIELDS_SERIALIZATION_SEPARATOR);
            attributeBox.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    int[] selectedIndicies = attributeBox.getSelectedIndices();
                    ArrayList<String> selectedAttributes = new ArrayList<String>();
                    for(int i=0; i < selectedIndicies.length; i++) {
                        selectedAttributes.add(attributes[selectedIndicies[i]]);
                    }
                    TaxonLabelPainter.this.selectedAttributes = selectedAttributes.toArray(new String[selectedAttributes.size()]);
                    PREFS.put("Tip Labels_whatToDisplay", join(SELECTED_FIELDS_SERIALIZATION_SEPARATOR,selectedAttributes));
                    firePainterChanged();
                }
            });
            List<Integer> selectedIndicies = new ArrayList<Integer>();
            for(int i=0; i < attributes.length; i++) {
                for(int j=0; j < prefsValue.length; j++) {
                    if(prefsValue[j].equals(attributes[i])) {
                        //attributeBox.setSelectedIndex(i);
                        selectedIndicies.add(i);
                    }
                }
            }
            if(selectedIndicies.size() > 0) {
                int[] selectedIndiciesArray = new int[selectedIndicies.size()];
                for(int i=0; i < selectedIndicies.size(); i++) {
                    selectedIndiciesArray[i] = selectedIndicies.get(i);
                }
                attributeBox.setSelectedIndices(selectedIndiciesArray);
            }
            else {
                attributeBox.setSelectedIndex(0);
            }


            JScrollPane attributesScroller = new JScrollPane(attributeBox);
            panel.addComponentWithLabel("Display:", attributesScroller);

            final JSpinner significantDigitSpinner = new JSpinner(new SpinnerNumberModel(PREFS.getInt("Tip Labels_sigDigits", formatter.getSignificantFigures()), 2, 14, 1));
            panel.addComponentWithLabel("Significant Digits:", significantDigitSpinner);
            ChangeListener sigFigListener = new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    Integer value = (Integer) significantDigitSpinner.getValue();
                    formatter.setSignificantFigures(value);
                    PREFS.putInt("Tip Labels_sigDigits", value);
                    firePainterChanged();
                }
            };
            significantDigitSpinner.addChangeListener(sigFigListener);
            sigFigListener.stateChanged(null);

            Icon infoIcon = IconUtils.getIcon(TreeViewer.class, "/jebl/gui/trees/treeviewer/images/info16.png");
            JLabel infoLabel = new JLabel("Set font sizes in the toolbar above", infoIcon, JLabel.CENTER);
            infoLabel.setBorder(new EmptyBorder(5,0,5,0));
            panel.addSpanningComponent(infoLabel);

            controls = new Controls("Tip Labels", panel, true, true, visibleCheckBox);
            
        }
        controlsList.add(controls);


        return controlsList;
    }

    private String join(String separator, Collection<String> items) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<String> it = items.iterator(); it.hasNext();) {
            builder.append(it.next());
            if(it.hasNext()) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    public void setSettings(ControlsSettings settings) {}

    public void getSettings(ControlsSettings settings) {}
}
