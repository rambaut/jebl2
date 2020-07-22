package jebl.evolution.io;

import jebl.evolution.trees.Tree;
import jebl.evolution.trees.Utils;
import jebl.evolution.trees.RootedTree;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.Collection;

/**
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @version $Id: NewickExporter.java 429 2006-08-26 18:17:39Z rambaut $
 */
public class NewickExporter implements TreeExporter {
    public NewickExporter(Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    /**
     * Export a single tree
     *
     * @param tree
     * @throws java.io.IOException
     */
    public void exportTree(Tree tree) {
        writeTree(tree);
    }

    /**
     * Export a collection of trees
     *
     * @param trees
     * @throws java.io.IOException
     */
    public void exportTrees(Collection<? extends Tree> trees) {
        for (Tree tree : trees) {
            writeTree(tree);
        }
    }

    @Override
    public void close() {
        writer.close();
    }

    private void writeTree(Tree tree) {
        writer.println(Utils.toNewick(Utils.rootTheTree(tree)));
    }

    private final PrintWriter writer;
}
