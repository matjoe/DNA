package dna.visualization.graph.toolTipManager;

import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.toolTips.ToolTip.ToolTipType;
import dna.visualization.graph.toolTips.button.FreezeButton;
import dna.visualization.graph.toolTips.button.HighlightButton;
import dna.visualization.graph.toolTips.infoLabel.NodeDegreeLabel;
import dna.visualization.graph.toolTips.infoLabel.NodeIdLabel;

/**
 * The default ToolTipManager used in the GraphVisualization.
 * 
 * @author Rwilmes
 * @date 16.12.2015
 */
public class DefaultToolTipManager extends ToolTipManager {

	private static int distance = 30;
	private static int offset = 30;
	private static double angle = 270;

	// constructor
	public DefaultToolTipManager(GraphPanel panel) {
		super("DefaultToolTipManager", panel.getSpriteManager(), panel,
				distance, offset, angle);

		// add additional tooltips here
		addToolTip("Node", ToolTipType.INFO_NODE_ID);
		addToolTip("Degree", ToolTipType.INFO_NODE_DEGREE);
		addToolTip("Freeze", ToolTipType.BUTTON_FREEZE);
		addToolTip("Highlight", ToolTipType.BUTTON_HIGHLIGHT);
	}

}
