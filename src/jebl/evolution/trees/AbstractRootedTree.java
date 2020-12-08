package jebl.evolution.trees;

import jebl.evolution.graphs.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andrew Rambaut
 * @version $
 */
public abstract class AbstractRootedTree implements RootedTree {
    @Override
    public int getExternalNodeCount(Node node) {
        if (isExternal(node)) return 1;

        int externalNodeCount = 0;
        for (Node child :  getChildren(node)) {
            externalNodeCount += getExternalNodeCount(child);
        }

        return externalNodeCount;
    }

    /**
     * @param node the node whose external nodes are being requested.
     * @return the list of external nodes descendent of the given node.
     * The set may be empty for a terminal node (a tip).
     */
    @Override
    public List<Node> getExternalNodes(Node node) {
        if (isExternal(node)) return Collections.singletonList(node);

        List<Node> tips = new ArrayList<Node>();
        for (Node child :  getChildren(node)) {
            tips.addAll(getExternalNodes(child));
        }

        return tips;
    }
}
