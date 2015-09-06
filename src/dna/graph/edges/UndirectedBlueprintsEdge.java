package dna.graph.edges;

import java.util.HashMap;

import com.tinkerpop.blueprints.Edge;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedBlueprintsNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.IGraph;
import dna.updates.update.Update;
import dna.util.MathHelper;

/**
 * The Class UndirectedBlueprintsEdge.
 * 
 * @author Matthias
 */
public class UndirectedBlueprintsEdge extends UndirectedEdge implements IGDBEdge<Edge> {
	
	/** The edge. */
	protected Edge edge;

	/**
	 * 
	 * The node with the lower index is stored as the first node. In case
	 * node1.index > node2.index, they are stored in changed order.
	 * 
	 * @param node1
	 *            first node connected by this edge
	 * @param node2
	 *            second node connected by this edge
	 */
	public UndirectedBlueprintsEdge(UndirectedBlueprintsNode node1, UndirectedBlueprintsNode node2) {
		super(node1, node2);
	}

	/**
	 * Instantiates a new undirected blueprints edge.
	 *
	 * @param s            String representation of an undirected edge
	 * @param g            graph this undirected edge is belonging to (required to obtain
	 *            node object pointers)
	 */
	public UndirectedBlueprintsEdge(String s, IGraph g) {
		super((UndirectedNode) getNodeFromStr(0, s, g),
				(UndirectedNode) getNodeFromStr(1, s, g));
	}

	/**
	 * Gets the node from str.
	 *
	 * @param index the index
	 * @param s the s
	 * @param g the g
	 * @return the node from str
	 */
	private static Node getNodeFromStr(int index, String s, IGraph g) {
		if (index < 0 || index > 1) {
			throw new IndexOutOfBoundsException(
					"The index must be 0 or 1, but was " + index + ".");
		}

		String[] temp = s.contains(UndirectedBlueprintsEdge.separator) ? s
				.split(UndirectedBlueprintsEdge.separator) : s
				.split(Update.EdgeSeparator);
		if (temp.length != 2) {
			throw new IllegalArgumentException("Cannot parse " + s
					+ " into an undirected edge");
		}

		return (UndirectedBlueprintsNode) g.getNode(MathHelper.parseInt(temp[index]));
	}

	/**
	 * Instantiates a new undirected blueprints edge.
	 *
	 * @param s the s
	 * @param g the g
	 * @param addedNodes the added nodes
	 */
	public UndirectedBlueprintsEdge(String s, IGraph g,
			HashMap<Integer, Node> addedNodes) {
		super((UndirectedNode) getNodeFromStr(0, s, g, addedNodes),
				(UndirectedNode) getNodeFromStr(1, s, g, addedNodes));		
	}

	/**
	 * Gets the node from str.
	 *
	 * @param index the index
	 * @param s the s
	 * @param g the g
	 * @param addedNodes the added nodes
	 * @return the node from str
	 */
	private static Node getNodeFromStr(int index, String s, IGraph g,
			HashMap<Integer, Node> addedNodes) {
		if (index < 0 || index > 1) {
			throw new IndexOutOfBoundsException(
					"The index must be 0 or 1, but was " + index + ".");
		}
		
		String[] temp = s.contains(UndirectedBlueprintsEdge.separator) ? s
				.split(UndirectedBlueprintsEdge.separator) : s
				.split(Update.EdgeSeparator);
		if (temp.length != 2) {
			throw new IllegalArgumentException("Cannot parse " + s
					+ " into an undirected edge");
		}
		
		int idx = MathHelper.parseInt(temp[index]);
		if (addedNodes.containsKey(idx)) {
			return (UndirectedNode) addedNodes.get(idx);
		} else {
			return (UndirectedNode) g.getNode(idx);
		}		
	}

	/**
	 * Instantiates a new undirected blueprints edge.
	 *
	 * @param src the src
	 * @param dst the dst
	 * @param e the e
	 */
	public UndirectedBlueprintsEdge(UndirectedBlueprintsNode src, UndirectedBlueprintsNode dst,
			com.tinkerpop.blueprints.Edge e) {
		super(src, dst);
		this.setGDBEdge(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dna.graph.edges.IGDBEdge#setEdge(com.tinkerpop.blueprints.Edge)
	 */
	@Override
	public void setGDBEdge(Edge edge) {
		this.edge = edge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dna.graph.edges.IGDBEdge#getEdge()
	 */
	@Override
	public Edge getGDBEdge() {
		return this.edge;
	}

	/**
	 * Gets the node1.
	 *
	 * @return first node connected by this edge (the node with the lower index)
	 */
	public UndirectedBlueprintsNode getNode1() {
		return (UndirectedBlueprintsNode) this.getN1();
	}

	/**
	 * Gets the node2.
	 *
	 * @return second node connected by this edge (the node with the higher
	 *         index)
	 */
	public UndirectedBlueprintsNode getNode2() {
		return (UndirectedBlueprintsNode) this.getN2();
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.UndirectedEdge#connectToNodes()
	 */
	@Override
	public boolean connectToNodes() {
		if (this.getNode1().hasEdge(this) && this.getNode2().hasEdge(this))
			return true;

//		boolean added = this.getNode1().addEdge(this);
//		return added;
		
		return false;
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.UndirectedEdge#disconnectFromNodes()
	 */
	@Override
	public boolean disconnectFromNodes() {
		if (!this.getNode1().hasEdge(this) && !this.getNode2().hasEdge(this))
			return true;

//		boolean removed = this.getNode1().removeEdge(this);
//		return removed;
		
		return false;
	}
	
	@Override
	public String toString()
	{
		return this.getN1().getIndex() + " " + separator + " " + this.getN2().getIndex();
	}
}
