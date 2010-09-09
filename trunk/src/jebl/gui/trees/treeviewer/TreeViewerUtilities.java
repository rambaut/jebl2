package jebl.gui.trees.treeviewer;

import java.awt.*;

/**
 * @author Steven Stones-Havas
 * @version $Id$
 *          <p/>
 *          Created on 20/03/2009 8:19:25 PM
 */
public class TreeViewerUtilities {

    public static final Font DEFAULT_FONT = new Font("sansserif", Font.PLAIN, 12);

    public static Font fontFromString(String fontRepresentation) {
        String[] attributes = fontRepresentation.split(":");
        if(attributes.length != 4) {
            throw new IllegalArgumentException("The font descriptor \""+fontRepresentation+"\" is invalid or corrupt");
        }
        String family = attributes[0];
        int size = 0;
        try {
            size = Integer.parseInt(attributes[1]);
        }
        catch(NumberFormatException e) {
            throw new IllegalArgumentException("The size marker in the font descriptor \""+fontRepresentation+"\" is invalid or corrupt");
        }

        boolean bold = Boolean.parseBoolean(attributes[2]);
        boolean italic = Boolean.parseBoolean(attributes[3]);

        int style = 0;
        if(bold) {
            style += Font.BOLD;
        }
        if(italic) {
            style += Font.ITALIC;
        }

        return new Font(family, style, size);
    }


    public static String fontToString(Font font) {
        return font.getFamily() + ":" + font.getSize() + ":" + font.isBold() + ":" + font.isItalic();
    }


}
