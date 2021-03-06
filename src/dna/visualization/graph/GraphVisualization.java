package dna.visualization.graph;

import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.Layout;

import dna.graph.IGraph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;
import dna.graph.weights.doubleW.Double2dWeight;
import dna.graph.weights.doubleW.Double3dWeight;
import dna.graph.weights.intW.Int2dWeight;
import dna.graph.weights.intW.Int3dWeight;
import dna.graph.weights.longW.Long2dWeight;
import dna.graph.weights.longW.Long3dWeight;
import dna.util.Config;
import dna.util.Log;
import dna.visualization.graph.GraphPanel.PositionMode;
import dna.visualization.graph.rules.NodeColorByDegree;
import dna.visualization.graph.rules.NodeSizeBy3dCoordinates;
import dna.visualization.graph.rules.NodeSizeByDegree;
import dna.visualization.graph.toolTipManager.DefaultToolTipManager;

/** The GraphVisualization class offers methods to visualize graphs used in DNA. **/
public class GraphVisualization {
	// statics
	public static final String positionKey = "xyz";
	public static final String weightKey = "dna.weight";
	public static final String labelKey = "ui.label";
	public static final String directedKey = "dna.directed";
	public static final String screenshotsKey = "ui.screenshot";
	public static final String qualityKey = "ui.quality";
	public static final String antialiasKey = "ui.antialias";
	public static final String colorKey = "dna.color";
	public static final String sizeKey = "dna.size";
	public static final String styleKey = "ui.style";
	public static final String updateKey = "dna.update";
	public static final String zKey = "dna.z";
	public static final String frozenKey = "layout.frozen";

	// GUI CONFIG
	protected static final Dimension size = new Dimension(
			Config.getInt("GRAPH_VIS_FRAME_WIDTH"),
			Config.getInt("GRAPH_VIS_FRAME_HEIGHT"));

	// graph map
	protected static HashMap<IGraph, GraphPanel> map = new HashMap<IGraph, GraphPanel>();

	// current GraphPanel
	protected static GraphPanel currentGraphPanel;
	protected static boolean init = false;

	/*
	 * GRAPH
	 */
	/** Init graph g. **/
	public static void init(IGraph g) {
		if (!init) {
			// init sophisticated renderer
			System.setProperty("org.graphstream.ui.renderer",
					"org.graphstream.ui.j2dviewer.J2DGraphRenderer");
			init = true;
		}

		Log.info("GraphVis - init graph: " + g);

		final String name = g.getName();

		// init graph
		final org.graphstream.graph.Graph graph = new MultiGraph(g.getName());

		// set if directed or undirected
		if (g.getGraphDatastructures().createsDirected())
			graph.addAttribute(directedKey, true);
		else
			graph.addAttribute(directedKey, false);

		// rendering options
		if (Config.getBoolean("GRAPH_VIS_RENDERING_HQ"))
			graph.addAttribute(GraphVisualization.qualityKey);
		if (Config.getBoolean("GRAPH_VIS_RENDERING_ANTIALIAS"))
			graph.addAttribute(GraphVisualization.antialiasKey);

		// check nodeweighttypes for position modes
		PositionMode mode = PositionMode.auto;
		Class<? extends Weight> nwt = g.getGraphDatastructures()
				.getNodeWeightType();
		if (nwt != null
				&& (nwt.equals(Int2dWeight.class)
						|| nwt.equals(Long2dWeight.class) || nwt
							.equals(Double2dWeight.class)))
			mode = PositionMode.twoDimension;
		if (nwt != null
				&& (nwt.equals(Int3dWeight.class)
						|| nwt.equals(Long3dWeight.class) || nwt
							.equals(Double3dWeight.class)))
			mode = PositionMode.threeDimension;

		// main frame
		JFrame mainFrame = new JFrame("Graph-Vis Mainframe");
		GraphPanel panel = new GraphPanel(mainFrame, graph, name, name, mode);

		// add style rules
		if (Config.getBoolean("GRAPH_VIS_SIZE_NODES_BY_DEGREE"))
			panel.addGraphStyleRule(new NodeSizeByDegree("NODE_SIZE_BY_DEGREE"));
		if (Config.getBoolean("GRAPH_VIS_3D_PROJECTION_ENABLED")
				&& Config.getBoolean("GRAPH_VIS_SIZE_NODES_BY_Z_COORDINATE"))
			panel.addGraphStyleRule(new NodeSizeBy3dCoordinates(
					"NODE_SIZE_BY_3D_COORDINATES"));
		if (Config.getBoolean("GRAPH_VIS_COLOR_NODES_BY_DEGREE"))
			panel.addGraphStyleRule(new NodeColorByDegree(
					"NODE_COLOR_BY_DEGREE"));
		if (Config.getBoolean("GRAPH_VIS_TOOLTIPS_ENABLED"))
			panel.addToolTipManager(new DefaultToolTipManager(panel));
		// panel.addGraphStyleRule(new
		// ToolTipUpdater(panel.getSpriteManager()));

		// create main frame
		mainFrame.add(panel);
		mainFrame.setName(name);
		mainFrame.setTitle(name);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setSize(size);
		mainFrame.setLocationRelativeTo(null);

		// set visible
		mainFrame.setVisible(true);

		// set as current frame
		currentGraphPanel = panel;

		// add to map
		map.put(g, panel);

		// start recording if automatic recording is set
		if (Config.getBoolean("GRAPH_VIS_VIDEO_AUTO_RECORD")) {
			if (!panel.isRecording())
				try {
					Thread.sleep(100);
					panel.captureVideo();
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
		}
	}

	/*
	 * NODE
	 */

	/** Adds node n to graph g. **/
	public static void addNode(IGraph g, Node n) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_NODE_ADDITION"));

		// add node
		map.get(g).addNode(n);
	}

	/** Removes node n from graph g. **/
	public static void removeNode(IGraph g, Node n) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_NODE_REMOVAL"));

		// get graph
		map.get(g).removeNode(n);
	}

	/** Changes node weight on node n IN CURRENT GRAPH!!. **/
	public static void changeNodeWeight(IWeightedNode n, Weight w) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_NODE_WEIGHT_CHANGE"));

		// get graph
		currentGraphPanel.changeNodeWeight(n, w);
	}

	/*
	 * EDGE
	 */

	/** Adds edge e to graph g. **/
	public static void addEdge(IGraph g, Edge e) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_EDGE_ADDITION"));

		// add edge
		map.get(g).addEdge(e);
	}

	/** Removes edge e from graph g. **/
	public static void removeEdge(IGraph g, Edge e) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_EDGE_REMOVAL"));

		// get graph
		map.get(g).removeEdge(e);
	}

	/** Changes edge weight on edge e IN CURRENT GRAPH!!. **/
	public static void changeEdgeWeight(IWeightedEdge e, Weight w) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_EDGE_WEIGHT_CHANGE"));

		// get graph
		currentGraphPanel.changeEdgeWeight(e, w);
	}

	/*
	 * MISC
	 */

	/** Wait for specified time in milliseconds. **/
	protected static void waitTime(long milliseconds) {
		if (Config.getBoolean("GRAPH_VIS_WAIT_ENABLED")) {
			try {
				TimeUnit.MILLISECONDS.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** Sets the description text for the given graph. **/
	public static void setText(IGraph g, String text) {
		map.get(g).setText(text);
	}

	/** Returns the GraphPanel belonging to graph g. **/
	public static GraphPanel getGraphPanel(IGraph g) {
		return GraphVisualization.map.get(g);
	}

	/** Returns the CURRENT GraphPanel. **/
	public static GraphPanel getCurrentGraphPanel() {
		return GraphVisualization.currentGraphPanel;
	}

	/** Sets the description text for the CURRENT GRAPH. **/
	public static void setText(String text) {
		currentGraphPanel.setText(text);
	}

	/** Returns the layouter belonging to graph g. **/
	public static Layout getLayouter(IGraph g) {
		return GraphVisualization.getGraphPanel(g).getLayouter();
	}

	/** Returns the CURRENT layouter. **/
	public static Layout getLayouter() {
		return GraphVisualization.getCurrentGraphPanel().getLayouter();
	}

	/**
	 * Returns the stabilization of the layouter belonging to the graph g. <br>
	 * 
	 * Returned values will be between 0.0 and 1.0, where 0.0 is unstable and
	 * 1.0 means its completely stabilized and not moving.
	 **/
	public static double getStabilization(IGraph g) {
		return GraphVisualization.getGraphPanel(g).getStabilization();
	}

	/**
	 * Returns the stabilization of the CURRENT layouter. <br>
	 * 
	 * Returned values will be between 0.0 and 1.0, where 0.0 is unstable and
	 * 1.0 means its completely stabilized and not moving.
	 **/
	public static double getStabilization() {
		return GraphVisualization.getCurrentGraphPanel().getStabilization();
	}

	/** Enable graph visualization. **/
	public static void enable() {
		Config.overwrite("GRAPH_VIS_ENABLED", "true");
	}

	/** Disable graph visualization. **/
	public static void disable() {
		Config.overwrite("GRAPH_VIS_ENABLED", "false");
	}

	/** Checks if graph visualization is enabled. **/
	public static boolean isEnabled() {
		return Config.getBoolean("GRAPH_VIS_ENABLED");
	}

	/**
	 * Gets the coords from the weight, casts it to float and returns it as
	 * float[] = {x, y, z}
	 **/
	public static float[] getCoordsFromWeight(Weight w) {
		float x = 0;
		float y = 0;
		float z = 0;
		if (w instanceof Int2dWeight) {
			x = ((Int2dWeight) w).getX();
			y = ((Int2dWeight) w).getY();
		}
		if (w instanceof Int3dWeight) {
			x = ((Int3dWeight) w).getX();
			y = ((Int3dWeight) w).getY();
			z = ((Int3dWeight) w).getZ();
		}
		if (w instanceof Long2dWeight) {
			x = ((Long2dWeight) w).getX();
			y = ((Long2dWeight) w).getY();
		}
		if (w instanceof Long3dWeight) {
			x = ((Long3dWeight) w).getX();
			y = ((Long3dWeight) w).getY();
			z = ((Long3dWeight) w).getZ();
		}
		if (w instanceof Double2dWeight) {
			x = (float) ((Double2dWeight) w).getX();
			y = (float) ((Double2dWeight) w).getY();
		}
		if (w instanceof Double3dWeight) {
			x = (float) ((Double3dWeight) w).getX();
			y = (float) ((Double3dWeight) w).getY();
			z = (float) ((Double3dWeight) w).getZ();
		}

		return new float[] { x, y, z };
	}
}
