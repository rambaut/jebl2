package jebl.evolution.trees;

import jebl.evolution.graphs.Edge;
import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.BaseNode;
import jebl.evolution.trees.BaseEdge;
import jebl.util.AttributableHelper;

import java.util.*;

/**
 * A simple, immutable rooted tree implementation that is a subtree of an existing tree
 * subtending a specified set of taxa..
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @version $Id: SimpleRootedTree.java 935 2008-07-22 16:52:04Z rambaut $
 */
final public class RootedSubtree extends AbstractRootedTree {

    /**
     * Make a copy of the given rooted tree
     * @param tree a rooted tree
     * @param includedTaxa
     */
    public RootedSubtree(RootedTree tree, Set<Taxon> includedTaxa) {
        createNodes(tree, tree.getRootNode(), includedTaxa);
    }

    /**
     * Clones the entire tree structure from the given RootedTree.
     * @param tree
     * @param node
     * @param includedTaxa may be empty
     * @return
     */
    private RootedSubtreeNode createNodes(RootedTree tree, Node node, Set<Taxon> includedTaxa) {

        RootedSubtreeNode newNode;

        if (tree.isExternal(node)) {                                                
            //System.out.println(node);
            newNode = createExternalNode(tree.getTaxon(node), includedTaxa);

        } else {
            List<RootedSubtreeNode> children = new ArrayList<RootedSubtreeNode>();
            for (Node child : tree.getChildren(node)) {
                RootedSubtreeNode newChild = createNodes(tree, child, includedTaxa);
                if (newChild != null) {
                    children.add(newChild);
                }
            }
            if (children.size() > 1) {
                newNode = createInternalNode(children);

            } else if (children.size() == 1) {

                // just return the single child
                return children.get(0);
            } else {

                newNode = null;
            }
        }

        if (newNode != null) {
            for( Map.Entry<String, Object> e : node.getAttributeMap().entrySet() ) {
                newNode.setAttribute(e.getKey(), e.getValue());
            }
            setHeight(newNode, tree.getHeight(node));
        }

        return newNode;
    }

    /**
     * Creates a new external node with the given taxon. See createInternalNode
     * for a description of how to use these methods.
     * @param taxon the taxon associated with this node
     * @return the created node reference
     */
    private RootedSubtreeNode createExternalNode(Taxon taxon, Set<Taxon> includedTaxa) {
        if( getTaxa().contains(taxon) ) {
            throw new IllegalArgumentException("duplicate taxon "+taxon.getName());
        }

        if (!includedTaxa.contains(taxon)) {
            return null;
        }

        RootedSubtreeNode node = new RootedSubtreeNode(taxon);
        externalNodes.put(taxon, node);
        return node;
    }

    /**
     * Once a SimpleRootedTree has been created, the node stucture can be created by
     * calling createExternalNode and createInternalNode. First of all createExternalNode
     * is called giving Taxon objects for the external nodes. Then these are put into
     * sets and passed to createInternalNode to create a parent of these nodes. The
     * last node created using createInternalNode is automatically the root so when
     * all the nodes are created, the tree is complete.
     *
     * @param children the child nodes of this nodes
     * @return the created node reference
     */
    private RootedSubtreeNode createInternalNode(List<? extends Node> children) {
        RootedSubtreeNode node = new RootedSubtreeNode(children);

        for (Node child : children) {
            ((RootedSubtreeNode)child).setParent(node);
        }

        internalNodes.add(node);

        rootNode = node;
        return node;
    }

    /**
     * @param node the node whose height is being set
     * @param height the height
     */
    public void setHeight(Node node, double height) {
        lengthsKnown = false;
        heightsKnown = true;

        // If a single height of a single node is set then
        // assume that all nodes have heights and by extension,
        // branch lengths as well as these will be calculated
        // from the heights
        hasLengths = true;
        hasHeights = true;

        ((RootedSubtreeNode)node).setHeight(height);
    }

    /**
     * @param node the node whose branch length (to its parent) is being set
     * @param length the length
     */
    public void setLength(Node node, double length) {
        heightsKnown = false;
        lengthsKnown = true;

        // If a single length of a single branch is set then
        // assume that all branch have lengths and by extension,
        // node heights as well as these will be calculated
        // from the lengths
        hasLengths = true;
        hasHeights = true;

        ((RootedSubtreeNode)node).setLength(length);
    }

    /**
     * @param node the node whose children are being requested.
     * @return the list of nodes that are the children of the given node.
     *         The list may be empty for a terminal node (a tip).
     */
    public List<Node> getChildren(Node node) {
        return new ArrayList<Node>(((RootedSubtreeNode)node).getChildren());
    }

    /**
     * @return Whether this tree has node heights available
     */
    public boolean hasHeights() {
        return hasHeights;
    }

    /**
     * @param node the node whose height is being requested.
     * @return the height of the given node. The height will be
     *         less than the parent's height and greater than it children's heights.
     */
    public double getHeight(Node node) {
        if (!hasHeights) throw new IllegalArgumentException("This tree has no node heights");
        if (!heightsKnown) calculateNodeHeights();
        return ((RootedSubtreeNode)node).getHeight();
    }

    /**
     * @return Whether this tree has branch lengths available
     */
    public boolean hasLengths() {
        return hasLengths;
    }

    /**
     * @param node the node whose branch length (to its parent) is being requested.
     * @return the length of the branch to the parent node (0.0 if the node is the root).
     */
    public double getLength(Node node) {
        if (!hasLengths) throw new IllegalArgumentException("This tree has no branch lengths");
        if (!lengthsKnown) calculateBranchLengths();
        return ((RootedSubtreeNode)node).getLength();
    }

    /**
     * @return Whether the node heights are known or need to be recalculated from the lengths
     */
    public boolean isHeightsKnown() {
        return heightsKnown;
    }

    /**
     * @return Whether the branch lengths are known or need to be recalculated from the heights
     */
    public boolean isLengthsKnown() {
        return lengthsKnown;
    }

    /**
     * @param node the node whose parent is requested
     * @return the parent node of the given node, or null
     *         if the node is the root node.
     */
    public Node getParent(Node node) {
        if (!(node instanceof RootedSubtreeNode)) {
            throw new IllegalArgumentException("Node, " + node.toString() + " is not an instance of RootedSubtreeNode");
        }
        return ((RootedSubtreeNode)node).getParent();
    }

    public Edge getParentEdge(Node node) {
        if (!(node instanceof RootedSubtreeNode)) {
            throw new IllegalArgumentException("Node, " + node.toString() + " is not an instance of RootedSubtreeNode");
        }
        return ((RootedSubtreeNode)node).getEdge();
    }

    /**
     * The root of the tree has the largest node height of
     * all nodes in the tree.
     *
     * @return the root of the tree.
     */
    public Node getRootNode() {
        return rootNode;
    }


    /**
     * @return a set of all nodes that have degree 1.
     *         These nodes are often refered to as 'tips'.
     */
    public Set<Node> getExternalNodes() {
        return new LinkedHashSet<Node>(externalNodes.values());
    }

    /**
     * @return a set of all nodes that have degree 2 or more.
     *         These nodes are often refered to as internal nodes.
     */
    public Set<Node> getInternalNodes() {
        return new LinkedHashSet<Node>(internalNodes);
    }

    /**
     * @return the set of taxa associated with the external
     *         nodes of this tree. The size of this set should be the
     *         same as the size of the external nodes set.
     */
    public Set<Taxon> getTaxa() {
        return new LinkedHashSet<Taxon>(externalNodes.keySet());
    }

    /**
     * @param node the node whose associated taxon is being requested.
     * @return the taxon object associated with the given node, or null
     *         if the node is an internal node.
     */
    public Taxon getTaxon(Node node) {
        if (!(node instanceof RootedSubtreeNode)) {
            throw new IllegalArgumentException("Node, " + node.toString() + " is not an instance of RootedSubtreeNode");
        }
        return ((RootedSubtreeNode)node).getTaxon();
    }

    /**
     * @param node the node
     * @return true if the node is of degree 1.
     */
    public boolean isExternal(Node node) {
        if (!(node instanceof RootedSubtreeNode)) {
            throw new IllegalArgumentException("Node, " + node.toString() + " is not an instance of RootedSubtreeNode");
        }
        boolean result= ((RootedSubtreeNode)node).getChildren().size() == 0;
        return  result;//((RootedSubtreeNode)node).getChildren().size() == 0;
    }

    /**
     * @param taxon the taxon
     * @return the external node associated with the given taxon, or null
     *         if the taxon is not a member of the taxa set associated with this tree.
     */
    public Node getNode(Taxon taxon) {
        return externalNodes.get(taxon);
    }

    public void renameTaxa(Taxon from, Taxon to) {
        RootedSubtreeNode node = (RootedSubtreeNode)externalNodes.get(from);

        // TT: The javadoc doesn't specify whether renameTaxa() should fail or silently do nothing
        // if Taxon from doesn't exist. But the code already threw a NullPointerException before (bug 4824),
        // so it's probably ok to throw a more informative IllegalArgumentException instead.
        if (node == null) {
            throw new IllegalArgumentException("Unknown taxon " + from + "; can't rename to " + to);
        }

        node.setTaxa(to);

        externalNodes.remove(from);
        externalNodes.put(to, node);
    }

    /**
     * Returns a list of edges connected to this node
     *
     * @param node
     * @return the set of nodes that are attached by edges to the given node.
     */
    public List<Edge> getEdges(Node node) {
        List<Edge> edges = new ArrayList<Edge>();
        for (Node adjNode : getAdjacencies(node)) {
            edges.add(((RootedSubtreeNode)adjNode).getEdge());

        }
        return edges;
    }

    /**
     * @param node
     * @return the set of nodes that are attached by edges to the given node.
     */
    public List<Node> getAdjacencies(Node node) {
        return ((RootedSubtreeNode)node).getAdjacencies();
    }

    /**
     * Returns the Edge that connects these two nodes
     *
     * @param node1
     * @param node2
     * @return the edge object.
     * @throws jebl.evolution.graphs.Graph.NoEdgeException
     *          if the nodes are not directly connected by an edge.
     */
    public Edge getEdge(Node node1, Node node2) throws NoEdgeException {
        if (((RootedSubtreeNode)node1).getParent() == node2) {
            return ((RootedSubtreeNode)node1).getEdge();
        } else if (((RootedSubtreeNode)node2).getParent() == node1) {
            return ((RootedSubtreeNode)node2).getEdge();
        } else {
            throw new NoEdgeException();
        }
    }

    /**
     * @param node1
     * @param node2
     * @return the length of the edge connecting node1 and node2.
     * @throws jebl.evolution.graphs.Graph.NoEdgeException
     *          if the nodes are not directly connected by an edge.
     */
    public double getEdgeLength(Node node1, Node node2) throws NoEdgeException {
        if (((RootedSubtreeNode)node1).getParent() == node2) {
            if (heightsKnown) {
                return ((RootedSubtreeNode)node2).getHeight() - ((RootedSubtreeNode)node1).getHeight();
            } else {
                return ((RootedSubtreeNode)node1).getLength();
            }
        } else if (((RootedSubtreeNode)node2).getParent() == node1) {
            if (heightsKnown) {
                return ((RootedSubtreeNode)node1).getHeight() - ((RootedSubtreeNode)node2).getHeight();
            } else {
                return ((RootedSubtreeNode)node2).getLength();
            }
        } else {
            throw new NoEdgeException();
        }
    }

    /**
     * Returns an array of 2 nodes which are the nodes at either end of the edge.
     *
     * @param edge
     * @return an array of 2 edges
     */
    public Node[] getNodes(Edge edge) {
        for (Node node : getNodes()) {
            if (((RootedSubtreeNode)node).getEdge() == edge) {
                return new Node[] { node, ((RootedSubtreeNode)node).getParent() };
            }
        }
        return null;
    }

    /**
     * @return the set of all nodes in this graph.
     */
    public Set<Node> getNodes() {
        Set<Node> nodes = new LinkedHashSet<Node>(internalNodes);
        nodes.addAll(externalNodes.values());
        return nodes;
    }

    /**
     * @return the set of all edges in this graph.
     */
    public Set<Edge> getEdges() {
        Set<Edge> edges = new LinkedHashSet<Edge>();
        for (Node node : getNodes()) {
            if (node != getRootNode()) {
                edges.add(((RootedSubtreeNode)node).getEdge());
            }

        }
        return edges;
    }

    /**
     * The set of external edges. This is a pretty inefficient implementation because
     * a new set is constructed each time this is called.
     * @return the set of external edges.
     */
    public Set<Edge> getExternalEdges() {
        Set<Edge> edges = new LinkedHashSet<Edge>();
        for (Node node : getExternalNodes()) {
            edges.add(((RootedSubtreeNode)node).getEdge());
        }
        return edges;
    }

    /**
     * The set of internal edges. This is a pretty inefficient implementation because
     * a new set is constructed each time this is called.
     * @return the set of internal edges.
     */
    public Set<Edge> getInternalEdges() {
        Set<Edge> edges = new LinkedHashSet<Edge>();
        for (Node node : getInternalNodes()) {
            if (node != getRootNode()) {
                edges.add(((RootedSubtreeNode)node).getEdge());
            }
        }
        return edges;
    }

    /**
     * @param degree the number of edges connected to a node
     * @return a set containing all nodes in this graph of the given degree.
     */
    public Set<Node> getNodes(int degree) {
        Set<Node> nodes = new LinkedHashSet<Node>();
        for (Node node : getNodes()) {
            // Account for no anncesstor of root, assumed by default in getDegree
            final int deg = node.getDegree() ;
            if (deg == degree) nodes.add(node);
        }
        return nodes;
    }

    /**
     * Set the node heights from the current branch lengths.
     */
    private void calculateNodeHeights() {

        if (!lengthsKnown) {
            throw new IllegalArgumentException("Can't calculate node heights because branch lengths not known");
        }

        nodeLengthsToHeights(rootNode, 0.0);

        double maxHeight = 0.0;
        for (Node externalNode : getExternalNodes()) {
            if (((RootedSubtreeNode)externalNode).getHeight() > maxHeight) {
                maxHeight = ((RootedSubtreeNode)externalNode).getHeight();
            }
        }

        for (Node node : getNodes()) {
            ((RootedSubtreeNode)node).setHeight(maxHeight - ((RootedSubtreeNode)node).getHeight());
        }

        heightsKnown = true;
    }

    /**
     * Set the node heights from the current node branch lengths. Actually
     * sets distance from root so the heights then need to be reversed.
     */
    private void nodeLengthsToHeights(RootedSubtreeNode node, double height) {

        double newHeight = height;

        if (node.getLength() > 0.0) {
            newHeight += node.getLength();
        }

        node.setHeight(newHeight);

        for (Node child : node.getChildren()) {
            nodeLengthsToHeights((RootedSubtreeNode)child, newHeight);
        }
    }

    /**
     * Calculate branch lengths from the current node heights.
     */
    protected void calculateBranchLengths() {

        if (!hasLengths) {
            throw new IllegalArgumentException("Can't calculate branch lengths because node heights not known");
        }

        nodeHeightsToLengths(rootNode, getHeight(rootNode));

        lengthsKnown = true;
    }

    /**
     * Calculate branch lengths from the current node heights.
     */
    private void nodeHeightsToLengths(RootedSubtreeNode node, double height) {
        final double h = node.getHeight();
        node.setLength(h >= 0 ? height - h : 1);

        for (Node child : node.getChildren()) {
            nodeHeightsToLengths((RootedSubtreeNode)child, node.getHeight());
        }

    }

    public void setConceptuallyUnrooted(boolean intent) {
        conceptuallyUnrooted = intent;
    }

    public boolean conceptuallyUnrooted() {
        return conceptuallyUnrooted;
    }

    public boolean isRoot(Node node) {
        return node == rootNode;
    }

    // Attributable IMPLEMENTATION

    public void setAttribute(String name, Object value) {
        if (helper == null) {
            helper = new AttributableHelper();
        }
        helper.setAttribute(name, value);
    }

    public Object getAttribute(String name) {
        if (helper == null) {
            return null;
        }
        return helper.getAttribute(name);
    }

    public void removeAttribute(String name) {
        if( helper != null ) {
            helper.removeAttribute(name);
        }
    }

    public Set<String> getAttributeNames() {
        if (helper == null) {
            return Collections.emptySet();
        }
        return helper.getAttributeNames();
    }

    public Map<String, Object> getAttributeMap() {
        if (helper == null) {
            return Collections.emptyMap();
        }
        return helper.getAttributeMap();
    }

    // PRIVATE members

    private AttributableHelper helper = null;

    protected RootedSubtreeNode rootNode = null;
    protected final Set<Node> internalNodes = new LinkedHashSet<Node>();
    private final Map<Taxon, Node> externalNodes = new LinkedHashMap<Taxon, Node>();

    private boolean heightsKnown = false;
    private boolean lengthsKnown = false;

    private boolean hasHeights = false;
    private boolean hasLengths = false;

    private boolean conceptuallyUnrooted = false;

    private class RootedSubtreeNode extends BaseNode {
        public RootedSubtreeNode(Taxon taxon) {
            this.children = Collections.unmodifiableList(new ArrayList<Node>());
            this.taxon = taxon;
        }

        public RootedSubtreeNode(List<? extends Node> children) {
            this.children = Collections.unmodifiableList(new ArrayList<Node>(children));
            this.taxon = null;
        }

        public void removeChild(Node node) {
            List<Node> c = new ArrayList<Node>(children);
            c.remove(node);
            children = Collections.unmodifiableList(c);
        }

        public void addChild(RootedSubtreeNode node) {
            List<Node> c = new ArrayList<Node>(children);
            c.add(node);
            node.setParent(this);
            children = Collections.unmodifiableList(c);
        }

        public void replaceChildren(List<RootedSubtreeNode> nodes) {
            for( RootedSubtreeNode n : nodes ) {
                n.setParent(this);
            }
            children = Collections.unmodifiableList(new ArrayList<Node>(nodes));
        }

        void swapChildren(int i0, int i1) {
            ArrayList<Node> nc = new ArrayList<Node>(children);
            //there was a user reported crash where i0 was > size of the array of children nodes
            if (i0 < 0 || i0 >= nc.size() || i1 < 0 || i1 >= nc.size()) {
                throw new IllegalArgumentException("Tried to swap children ("+i0+","+i1+") on node with " + nc.size() + " children");
            }
            final Node ni0 = nc.get(i0);
            nc.set(i0, nc.get(i1));
            nc.set(i1, ni0);
            children = Collections.unmodifiableList(nc);
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public List<Node> getChildren() {
            return children;
        }

        public double getHeight() {
            return height;
        }

        // height above latest tip
        public void setHeight(double height) {
            this.height = height;
        }

        // length of branch to parent
        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        public int getDegree() {
            return children.size() +(this==rootNode?0:1);
        }

        public void setTaxa(Taxon to) {
            taxon = to;
        }

        /**
         * returns the edge connecting this node to the parent node
         * @return the edge
         */
        public Edge getEdge() {
            if (edge == null) {
                edge = new BaseEdge() {
                    public double getLength() {
                        return length;
                    }
                };
            }

            return edge;
        }

        /**
         * For a rooted tree, getting the adjacencies is not the most efficient
         * operation as it makes a new set containing the children and the parent.
         * @return the adjacaencies
         */
        public List<Node> getAdjacencies() {
            List<Node> adjacencies = new ArrayList<Node>();
            if (children != null) adjacencies.addAll(children);
            if (parent != null) adjacencies.add(parent);
            return adjacencies;
        }

        public Taxon getTaxon() {
            return taxon;
        }

        private List<Node> children;
        private Taxon taxon;

        private Node parent;
        private double height;
        private double length;

        private Edge edge = null;
    }
}