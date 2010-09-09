package jebl.gui.trees.treeviewer.painters;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.TransformedRootedTree;
import jebl.evolution.taxa.Taxon;
import jebl.util.NumberFormatter;
import org.virion.jam.controlpanels.ControlPalette;
import org.virion.jam.controlpanels.Controls;
import org.virion.jam.controlpanels.ControlsSettings;
import org.virion.jam.panels.OptionsPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * @author Andrew Rambaut
 * @version $Id: BasicLabelPainter.java 1049 2010-02-26 04:14:15Z stevensh $
 */
public class BasicLabelPainter extends AbstractPainter<Node> {


    final String fontMinSizePrefKey;
    final String fontSizePrefKey;

    private final PainterIntent intent;
    private int consensusSupportIndex;
    private boolean containsConsensusSupport;
    protected static final String[] attributesToNotDisplay = new String[] {"nodeColor", "labelFont", "size", "first residues"};

    private final String isOpenKey;

    public BasicLabelPainter(String title, RootedTree tree, PainterIntent intent) {
        this(title, tree, intent, 6);
    }


    public enum PainterIntent {
        NODE,
        BRANCH,
        TIP
    }

    public BasicLabelPainter(String title, RootedTree tree, PainterIntent intent, int defaultSize) {
        this.title = title;
        this.intent = intent;

        fontMinSizePrefKey = getTitle() + "_fontminsize";
        fontSizePrefKey = getTitle() + "_fontsize";

        this.defaultFontSize = defaultSize;
        taxonLabelFont = new Font("sansserif", Font.PLAIN, defaultSize);
        taxonExponentFont = taxonLabelFont.deriveFont(taxonLabelFont.getSize2D()*0.66f);

        this.tree = tree;

        hasNumericAttributes = false;

        Set<String> names = new TreeSet<String>();

        // by default, node properties are on nodes for rooted trees, on branches for unrooted trees
        this.attribute = null;

        List<String> sources = new ArrayList<String>();
        boolean wantHeightsIfPossible = false;
        boolean wantBranchesIfPossible = false;
        boolean addNodeAttributes = false;
        switch( intent ) {
            case TIP: {
                sources.add(TAXON_NAMES);
                wantHeightsIfPossible = true;
                addNodeAttributes = true;
                break;
            }
            case NODE: {
                wantHeightsIfPossible = true;
                addNodeAttributes = !tree.conceptuallyUnrooted();
                break;
            }
            case BRANCH: {
                wantBranchesIfPossible = !(tree instanceof TransformedRootedTree);
                addNodeAttributes = tree.conceptuallyUnrooted();
                break;
            }
        }

        if( addNodeAttributes ) {
            Set<Node> nodes;
            switch( intent ) {
                case TIP: {
                    nodes = tree.getExternalNodes();
                    break;
                }
                default : {
                    nodes = tree.getInternalNodes();
                }
            }
            for(Node n : nodes) {
                Set<String> attributeNames = new LinkedHashSet<String>();
                attributeNames.addAll(n.getAttributeNames());
                final Taxon nodeTaxon = tree.getTaxon(n);
                if(nodeTaxon != null) {
                    attributeNames.addAll(nodeTaxon.getAttributeNames());
                }

                aroundTheAttributeNamesLoop:
                for(String s : attributeNames) {
                    for(int i=0; i < attributesToNotDisplay.length; i++) {
                        if(attributesToNotDisplay[i].equalsIgnoreCase(s)) {
                            continue aroundTheAttributeNamesLoop;
                        }
                    }
                    names.add(s);
                }
            }
        }

        if( wantHeightsIfPossible && tree.hasHeights() && !tree.conceptuallyUnrooted() ) {
            sources.add(NODE_HEIGHTS);
            hasNumericAttributes = true;
        }

        if( wantBranchesIfPossible && tree.hasLengths()) {
            sources.add(BRANCH_LENGTHS);
            hasNumericAttributes = true;
        }

        sources.addAll(names);

        if (this.attribute == null && sources.size() > 0) {
            this.attribute = sources.get(0);
        } else {
            this.attribute = "";
        }

        this.attributes = new String[sources.size()];
        sources.toArray(this.attributes);

        formatter = new NumberFormatter(4);

        consensusSupportIndex = getConsensusSupportIndex();
        containsConsensusSupport = consensusSupportIndex >= 0;
        isOpenKey = getTitle() + "_isopen" + (containsConsensusSupport ? "_withconsensusSupport" : "");
    }

    public void setTree(RootedTree tree) {
        this.tree = tree;
    }

    public Font getLabelFont(){
        return taxonLabelFont;
    }

    protected String getLabel(Node node) {
        String prefix = " ";
        String suffix = " ";
        if (attribute.equalsIgnoreCase(TAXON_NAMES)) {
            return prefix+tree.getTaxon(node).getName()+suffix;
        }

        if( tree instanceof RootedTree ) {
            final RootedTree rtree = (RootedTree) tree;

            if (attribute.equalsIgnoreCase(NODE_HEIGHTS) ) {
                return prefix+getFormattedValue(rtree.getHeight(node))+suffix;
            } else if (attribute.equalsIgnoreCase(BRANCH_LENGTHS) ) {
                return prefix+getFormattedValue(rtree.getLength(node))+suffix;
            }
        }

        Object value = node.getAttribute(attribute);
        final Taxon nodeTaxon = tree.getTaxon(node);
        if(value == null && nodeTaxon != null) {
            value = nodeTaxon.getAttribute(attribute);
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


    public float getFontSize() {
        return defaultFontSize;
    }

    public float getFontMinSize() {
        return defaultMinFontSize;
    }

    private float defaultFontSize;
    private float defaultMinFontSize;
    private int defaultDigits = 4;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        firePainterChanged();
        PREFS.putBoolean(isOpenKey, visible);
    }

//    public void calibrate(Graphics2D g2, Node item) {
//        final Font oldFont = g2.getFont();
//        g2.setFont(taxonLabelFont);
//
//        final FontMetrics fm = g2.getFontMetrics();
//        preferredHeight = fm.getHeight();
//        preferredWidth = 0;
//
//        String label = getLabel(item);
//        if (label != null) {
//            Rectangle2D rect = fm.getStringBounds(label, g2);
//            preferredWidth = rect.getWidth();
//        }
//
//        yOffset = (float)fm.getAscent();
//
//        g2.setFont(oldFont);
//    }

     public void calibrate(Graphics2D g2) {
        final Font oldFont = g2.getFont();
        g2.setFont(taxonLabelFont);

        final FontMetrics fm = g2.getFontMetrics();
        preferredHeight = fm.getHeight()-5;

        yOffset = (float)fm.getAscent();

        g2.setFont(oldFont);
    }

    public double getWidth(Graphics2D g2, Node item) {
        final String label = getLabel(item);
        if( label != null ) {
            final Font oldFont = g2.getFont();
            g2.setFont(taxonLabelFont);

            final FontMetrics fm = g2.getFontMetrics();
            Rectangle2D rect = fm.getStringBounds(label, g2);
            g2.setFont(oldFont);
            return rect.getWidth();
        }

        return 0.0;
    }

    public double getPreferredHeight(Graphics2D g2, Node item) {
        return preferredHeight;
    }

    public double getHeightBound(Graphics2D g2, Node item) {
        return preferredHeight + yOffset;
    }

    public void resetFontSizes(boolean fire) {
        float fontSize = PREFS.getFloat(fontSizePrefKey, defaultFontSize);
        float minFontSize = PREFS.getFloat(fontMinSizePrefKey, defaultMinFontSize);
        setFontSize(fontSize, fire);
        setFontMinSize(minFontSize, fire);
    }

    public boolean setFontSize(float size, boolean fire) {
        if( defaultFontSize != size ) {
            taxonLabelFont = taxonLabelFont.deriveFont(size);
            taxonExponentFont = taxonLabelFont.deriveFont(taxonLabelFont.getSize2D()*0.66f);
            defaultFontSize = size;
            if( fire ) firePainterChanged();
            return true;
        }
        return false;
    }

    private boolean setFontMinSize(float fontsize, boolean fire) {
        if( defaultMinFontSize != fontsize ) {
            defaultMinFontSize = fontsize;
            if( fire ) firePainterChanged();
            return true;
        }
        return false;
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


                //valueString = valueString.replace("E", "x10");
            }

            Rectangle2D rect = g2.getFontMetrics().getStringBounds(prefix, g2);

            float xOffset = 0;
            float y = yOffset + (float) bounds.getY();
            switch (justification) {
                case CENTER:
                    //xOffset = (float)(-rect.getWidth()/2.0);
                    //y = yOffset + (float) rect.getY();
                   // y = (float)bounds.getHeight()/2;
                    //xOffset = (float) (bounds.getX() + (bounds.getWidth() - rect.getWidth()) / 2.0);
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
            //g2.draw(bounds);
        }

        g2.setFont(oldFont);

        if(paintAsMirrorImage) {
            g2.translate(bounds.getWidth()+2*bounds.getX(),0);
            g2.scale(-1,1);
        }
    }

    public String[] getAttributes() {
        return attributes;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
        firePainterChanged();
    }

    public void setControlPalette(ControlPalette controlPalette) {
        // nothing to do
    }

    private static Preferences PREFS = Preferences.userNodeForPackage(BasicLabelPainter.class);

    public List<Controls> getControls(boolean detachPrimaryCheckbox) {

        List<Controls> controlsList = new ArrayList<Controls>();

        if (controls == null) {
            OptionsPanel optionsPanel = new OptionsPanel();

            final JCheckBox showTextCHeckBox = new JCheckBox("Show " + getTitle());
            if (! detachPrimaryCheckbox) {
                optionsPanel.addComponent(showTextCHeckBox);
            }

            visible = PREFS.getBoolean(isOpenKey, containsConsensusSupport || intent == PainterIntent.TIP);
            showTextCHeckBox.setSelected(visible);

            showTextCHeckBox.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent changeEvent) {
                    final boolean selected = showTextCHeckBox.isSelected();
                    if( isVisible() != selected ) {
                        setVisible(selected);
                    }
                }
            });


            final String whatPrefKey = getTitle() + "_whatToDisplay" + (containsConsensusSupport ? "_withconsensusSupport" : "");
            String[] attributes = getAttributes();
            final JComboBox combo1 = new JComboBox(attributes);
            combo1.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent itemEvent) {
                    String attribute = (String) combo1.getSelectedItem();
                    setAttribute(attribute);
                    PREFS.put(whatPrefKey, attribute);
                }
            });

            final String whatToDisplay = PREFS.get(whatPrefKey, consensusSupportIndex >= 0 ? attributes[consensusSupportIndex] : null);
            if( whatToDisplay != null ) {
                int i = Arrays.asList(attributes).indexOf(whatToDisplay);
                if( i >= 0 ) {
                    combo1.setSelectedIndex(i);
                }
            }

            optionsPanel.addComponentWithLabel("Display:", combo1);
            final JSpinner fontSizeSpinner = new JSpinner(new SpinnerNumberModel(defaultFontSize, 0.01, 48, 1));

            //optionsPanel.addComponentWithLabel("Font Size:", fontSizeSpinner);
            //final boolean xselected = showTextCHeckBox.isSelected();
            //label1.setEnabled(selected);
            //fontSizeSpinner.setEnabled(selected);


            final float fontsize = PREFS.getFloat(fontSizePrefKey, taxonLabelFont.getSize());
            setFontSize(fontsize, false);
            fontSizeSpinner.setValue((double)fontsize);



            //-----------------------------------------



            //final boolean xselected = showTextCHeckBox.isSelected();
            //label1.setEnabled(selected);
            //fontSizeSpinner.setEnabled(selected);

            final float size = PREFS.getFloat(fontMinSizePrefKey, 8);
            setFontMinSize(size, false);

            final JSpinner fontMinSizeSpinner = new JSpinner(new SpinnerNumberModel(defaultMinFontSize, 0.01, 48, 1));
            //optionsPanel.addComponentWithLabel("Minimum Size:", fontMinSizeSpinner);
            JPanel fontSizePanel = new JPanel(new FlowLayout());
            fontSizePanel.add(fontMinSizeSpinner);
            fontSizePanel.add(new JLabel("to"));
            fontSizePanel.add(fontSizeSpinner);
            optionsPanel.addComponentWithLabel("Font Size:", fontSizePanel);
            //fontMinSizeSpinner.setValue(size);

            fontSizeSpinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent changeEvent) {
                    float size = ((Double) fontSizeSpinner.getValue()).floatValue();
                    final float minSize = ((Number) fontMinSizeSpinner.getValue()).floatValue();
                    if (size < minSize) {
                        fontMinSizeSpinner.setValue((double) size);
                    }

                    setFontMinSize(minSize, true);
                    setFontSize(size, true);
                    PREFS.putFloat(fontSizePrefKey, size);
                }
            });

            fontMinSizeSpinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent changeEvent) {
                    float size = ((Double) fontMinSizeSpinner.getValue()).floatValue();
                    final float maxSize = ((Number) fontSizeSpinner.getValue()).floatValue();
                    if (size > maxSize) {
                        fontSizeSpinner.setValue((double) size);
                    }
                    setFontMinSize(size, true);
                    setFontSize(maxSize, true);
                    PREFS.putFloat(fontMinSizePrefKey, size);
                }
            });
            //-------------------------
            final JSpinner digitsSpinner = new JSpinner(new SpinnerNumberModel(defaultDigits, 2, 14, 1));

            if( hasNumericAttributes ) {
                final JLabel label2 = optionsPanel.addComponentWithLabel("Significant Digits:", digitsSpinner);
                // label2.setEnabled(selected);
                //  digitsSpinner.setEnabled(selected);

                final String digitsPrefKey = getTitle() + "_sigDigits";
                final int digits = PREFS.getInt(digitsPrefKey, defaultDigits);
                setSignificantDigits(digits);
                digitsSpinner.setValue(digits);

                digitsSpinner.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent changeEvent) {
                        final int digits = (Integer)digitsSpinner.getValue();
                        setSignificantDigits(digits);
                        PREFS.putInt(digitsPrefKey, digits);
                    }
                });
            }

            controls = new Controls(getTitle(), optionsPanel, false, false, detachPrimaryCheckbox ? showTextCHeckBox : null);
        }

        controlsList.add(controls);

        return controlsList;
    }

    private int getConsensusSupportIndex() {

        //we can put other names that represent consensus support here as we discover them
        String[] valuesToTest = new String[]{
            "Consensus support(%)",
            "Clade Support",
            "bootstrap proportion",
            "Posterior Probability"
        };
        List<String> attributesList = Arrays.asList(getAttributes());
        for(String s : valuesToTest) {
            int index = attributesList.indexOf(s);
            if(index >= 0) {
                return index;
            }
        }
        return -1;
    }


    public void setSettings(ControlsSettings settings) {
    }

    public void getSettings(ControlsSettings settings) {
    }

    private Controls controls = null;

    public String getTitle() {
        return title;
    }

    private final String title;


    private Font taxonLabelFont;
    private Font taxonExponentFont;
    //private double preferredWidth;
    private double preferredHeight;
    private float yOffset;

    private boolean visible = true;

    private boolean hasNumericAttributes = false;

    protected RootedTree tree;
    protected String attribute;
    protected String[] attributes;
}
