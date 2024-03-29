package jebl.evolution.trees;

import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.MissingTaxonException;
import jebl.evolution.taxa.Taxon;

import java.util.*;

/**
 * Static utility functions for rooted trees.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 *
 * @version $Id: RootedTreeUtils.java 889 2008-02-27 01:13:21Z matt_kearse $
 */

public class RootedTreeUtils {
    private RootedTreeUtils() { }  // make class uninstantiable

    /**
	 * Return the number of leaves under this node.
	 * @param tree
	 * @param node
	 * @return the number of leaves under this node.
	 */
	public static final int getTipCount(RootedTree tree, Node node) {
		return tree.getExternalNodeCount(node);
	}

    public static double getMinTipHeight(RootedTree tree, Node node) {
        if (tree.isExternal(node)) {
            return tree.getHeight(node);
        }

        double minTipHeight = Double.MAX_VALUE;
        for (Node child : tree.getChildren(node)) {
           double h = getMinTipHeight(tree, child);
            if (h < minTipHeight) {
                minTipHeight = h;
            }
        }

        return minTipHeight;
    }

    public static double getMaxTipHeight(RootedTree tree, Node node) {
        if (tree.isExternal(node)) {
            return tree.getHeight(node);
        }

        double maxTipHeight = -Double.MAX_VALUE;
        for (Node child : tree.getChildren(node)) {
           double h = getMaxTipHeight(tree, child);
            if (h > maxTipHeight) {
                maxTipHeight = h;
            }
        }

        return maxTipHeight;
    }

	/**
	 * returns the average distance from the given node to all the tips below it
	 * @param tree
	 * @param node
	 * @return
	 */
	public static double getAverageTipDistance(RootedTree tree, Node node) {
		if (tree.isExternal(node)) {
			return 0.0;
		}

		double nodeHeight = tree.getHeight(node);
		double sumTipDistance = 0.0;
		for (Node tip : tree.getExternalNodes(node)) {
			sumTipDistance += nodeHeight - tree.getHeight(tip);
		}

		return sumTipDistance / tree.getExternalNodeCount(node);
	}


	/**
	 * @return true only if all tips have height 0.0
	 */
	public static boolean isUltrametric(RootedTree tree, double tolerance) {
		for (Node node : tree.getExternalNodes()) {
			if (Math.abs(tree.getHeight(node)) > tolerance) return false;
		}
		return true;
	}

	/**
	 * @return true only if internal nodes have 2 children
	 */
	public static boolean isBinary(RootedTree tree) {
		for (Node node : tree.getInternalNodes()) {
			if (tree.getChildren(node).size() > 2) return false;
		}
		return true;
	}

	/**
	 * Gets a set of external nodes that correspond to the given taxa.
	 */
	public static Set<Node> getTipsForTaxa(RootedTree tree, Collection<Taxon> taxa) throws MissingTaxonException {

		Set<Node> tipNodes = new LinkedHashSet<Node>();

		for (Taxon taxon : taxa) {

			Node node = tree.getNode(taxon);

			if (node == null) {
				throw new MissingTaxonException(taxon);
			}

			tipNodes.add(node);
		}

		return tipNodes;
	}

	/**
	 * Gets a set of tip nodes descended from the given node.
	 */
	public static Set<Node> getDescendantTips(RootedTree tree, Node node) {

		Set<Node> tipNodes = new LinkedHashSet<Node>();
		getDescendantTips(tree, node, tipNodes);
		return tipNodes;
	}

	/**
	 * Private recursive function used by getDescendantTips.
	 */
	private static void getDescendantTips(RootedTree tree, Node node, Set<Node> tipNodes) {

		for (Node child : tree.getChildren(node)) {
			if (tree.isExternal(child)) {
				tipNodes.add(child);
			} else {
				getDescendantTips(tree, child, tipNodes);
			}
		}
	}

	/**
	 * Gets the most recent common ancestor (MRCA) node of a set of tip nodes.
	 * @param tree the Tree
	 * @param tipNodes a set of tip nodes
	 * @return the Node of the MRCA
	 */
	public static Node getCommonAncestorNode(RootedTree tree, Set<Node> tipNodes) {

		if (tipNodes.size() == 0) {
			throw new IllegalArgumentException("No leaf nodes selected");
		}

		if (tipNodes.size() == 1) return tipNodes.iterator().next();

		Node[] mrca = new Node[] { null };
		getCommonAncestorNode(tree, tree.getRootNode(), tipNodes, mrca);

		return mrca[0];
	}

	/**
	 * Private recursive function used by getCommonAncestorNode.
	 */
	private static int getCommonAncestorNode(RootedTree tree, Node node,
	                                         Set<Node> tipNodes,
	                                         Node[] mrca) {


		int matches = 0;

		for (Node child : tree.getChildren(node)) {
            // AR - I see no reason to restrict this to external nodes.
            if (tipNodes.contains(child)) {
                matches ++;
            }

            if (!tree.isExternal(child)) {
				matches += getCommonAncestorNode(tree, child, tipNodes, mrca);

				if (mrca[0] != null) {
					return matches;
				}
            }
		}

		// If we haven't already found the MRCA, test this node
		if (matches == tipNodes.size()) {
			mrca[0] = node;
		}

		return matches;
	}

	/**
	 * Performs the a monophyly test on a set of tip nodes. The nodes are monophyletic
	 * if there is a node in the tree which subtends all the tips in the set (and
	 * only those tips).
	 * @param tree a tree object to perform test on
	 * @param tipNodes a set containing the tip node.
	 * @return boolean is monophyletic?
	 */
	public static boolean isMonophyletic(RootedTree tree, Set<Node> tipNodes) {

		if (tipNodes.size() == 0) {
			throw new IllegalArgumentException("No tip nodes selected");
		}

		if (tipNodes.size() == 1) {
			// A single selected leaf is always monophyletic
			return true;
		}

		if (tipNodes.size() == tree.getExternalNodes().size()) {
			// All leaf nodes are selected
			return true;
		}

		int[] matchCount = new int[] { 0 };
		int[] tipCount = new int[] { 0 };

		Boolean result = isMonophyletic(tree, tree.getRootNode(), tipNodes, matchCount, tipCount);

		if (result != null) return result;

		return false;
	}

	/**
	 * Private recursive function used by isMonophyletic.
	 */
	private static Boolean isMonophyletic(RootedTree tree, Node node,
	                                      Set<Node> tipNodes,
	                                      int[] matchCount, int[] tipCount) {

		int mc = 0;
		int tc = 0;

		for (Node child : tree.getChildren(node)) {
			if (tree.isExternal(child)) {
				if (tipNodes.contains(child)) {
					mc ++;
				}
				tc ++;
			} else {

				Boolean result = isMonophyletic(tree, child, tipNodes, matchCount, tipCount);

				if (result != null) {
					return result;
				}

				mc += matchCount[0];
				tc += tipCount[0];
			}
		}


		matchCount[0] = mc;
		tipCount[0] = tc;

		// If we haven't already found the MRCA, test this node
		if (mc == tc && tc == tipNodes.size()) {
			// monophyletic
			return Boolean.TRUE;
		}

		if (mc != 0 && mc != tc) {
			// not monophyletic
			return Boolean.FALSE;
		}

		// no result yet
		return null;
	}

	/**
	 * Recursive function for constructing a newick tree representation in the given buffer.
	 */
	public static String uniqueNewick(RootedTree tree, Node node) {
		if (tree.isExternal(node)) {
			return tree.getTaxon(node).getName();
		} else {
			StringBuffer buffer = new StringBuffer("(");

			List<String> subtrees = new ArrayList<String>();

			for (Node child : tree.getChildren(node)) {
				subtrees.add(uniqueNewick(tree, child));
			}
			Collections.sort(subtrees);

			for (int i = 0; i < subtrees.size(); i++) {
				buffer.append(subtrees.get(i));
				if (i < subtrees.size() - 1) {
					buffer.append(",");
				}
			}
			buffer.append(")");

			return buffer.toString();
		}
	}

	/**
	 * Compares 2 trees and returns true if they have the same topology.
	 */
	public static boolean equal(RootedTree tree1, RootedTree tree2) {

		return uniqueNewick(tree1, tree1.getRootNode()).equals(uniqueNewick(tree2, tree2.getRootNode()));
	}


}

