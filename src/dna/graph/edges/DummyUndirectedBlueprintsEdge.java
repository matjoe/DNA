package dna.graph.edges;

import dna.graph.nodes.UndirectedBlueprintsNode;

public class DummyUndirectedBlueprintsEdge extends UndirectedBlueprintsEdge implements IEdgeDummy {

	public DummyUndirectedBlueprintsEdge(UndirectedBlueprintsNode src, UndirectedBlueprintsNode dst) {
		super(src, dst);
	}

	public void setNodes(int n1, int n2) {
		this.n1.setIndex(Math.min(n1, n2));
		this.n2.setIndex(Math.max(n1, n2));
	}
}
