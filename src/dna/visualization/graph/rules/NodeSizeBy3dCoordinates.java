package dna.visualization.graph.rules;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.Config;
import dna.visualization.graph.GraphVisualization;

public class NodeSizeBy3dCoordinates extends GraphStyleRule {

	protected double baseGrowth;
	protected double shrinkFactor;

	public NodeSizeBy3dCoordinates(String name) {
		this(name, Config.getDouble("GRAPH_VIS_3D_PROJECTION_NODE_GROWTH"),
				Config.getDouble("GRAPH_VIS_3D_PROJECTION_NODE_SHRINK_FACTOR"));
	}

	public NodeSizeBy3dCoordinates(String name, double growthFactor,
			double shrinkFactor) {
		this.name = name;
		this.baseGrowth = growthFactor;
		this.shrinkFactor = shrinkFactor;
	}

	@Override
	public void onNodeAddition(Node n) {
		// increase size by base growth and reduze by z*shrinkfactor
		float z = n.getAttribute(GraphVisualization.zKey);

		GraphStyleUtils.increaseSize(n,
				this.baseGrowth - Math.floor(z * this.shrinkFactor));
	}

	@Override
	public void onNodeRemoval(Node n) {
	}

	@Override
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
		// get z-coords
		float zNew = GraphVisualization.getCoordsFromWeight(wNew)[2];
		float zOld = GraphVisualization.getCoordsFromWeight(wOld)[2];

		// calc shrink based on z-diff
		float zDiff = zNew - zOld;
		GraphStyleUtils.decreaseSize(n, Math.floor(zDiff * this.shrinkFactor));
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
	}

	@Override
	public void onEdgeWeightChange(Edge e, Weight wNew, Weight wOld) {
	}

	@Override
	public String toString() {
		return "NodeSizeBy3dCoordinates-Rule: '" + this.name + "'";
	}
}
