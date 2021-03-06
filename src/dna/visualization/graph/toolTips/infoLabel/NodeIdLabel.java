package dna.visualization.graph.toolTips.infoLabel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import dna.graph.weights.Weight;

/** The NodeIdLabel displays the NodeId of the Node it is attached to. **/
public class NodeIdLabel extends InfoLabel {

	// constructor
	public NodeIdLabel(Sprite s, String name, String attachementId) {
		super(s, name, attachementId, LabelValueType.INT, attachementId);
	}

	@Override
	public ToolTipType getType() {
		return ToolTipType.INFO_NODE_ID;
	}

	@Override
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
		// DO NOTHING
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
		// DO NOTHING
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		// DO NOTHING
	}
}
