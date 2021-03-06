package dna.metrics.weights;

import dna.graph.IGraph;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.doubleW.Double2dWeight;
import dna.graph.weights.doubleW.Double3dWeight;
import dna.graph.weights.doubleW.DoubleWeight;
import dna.graph.weights.intW.Int2dWeight;
import dna.graph.weights.intW.Int3dWeight;
import dna.graph.weights.intW.IntWeight;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.DataUtils;
import dna.util.parameters.Parameter;

/**
 * 
 * Root Mean Square Deviation of the position changes of nodes in a
 * 3-dimensional space. For each node, the difference between their position
 * from one batch to the other is taken as distance / movement of the node. For
 * the first snapshot, all nodes are initialize dwith their current position.
 * Since there is no point of reference to compute the distance to, their
 * deviation in this first step is 0.
 * http://en.wikipedia.org/wiki/Root_mean_square_deviation
 * 
 * @author benni
 * 
 */
public abstract class RootMeanSquareDeviation extends Metric {

	protected int changes;

	protected double rmsd;

	protected BinnedDoubleDistr distr;

	public RootMeanSquareDeviation(String name, Parameter... p) {
		super(name, MetricType.exact, p);
	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("RootMeanSquareDeviation", this.rmsd);
		Value v2 = new Value("Changes", this.changes);
		return new Value[] { v1, v2 };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr<?, ?>[] { this.distr };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof RootMeanSquareDeviation;
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof RootMeanSquareDeviation)) {
			return false;
		}
		RootMeanSquareDeviation m2 = (RootMeanSquareDeviation) m;
		boolean success = true;
		success &= DataUtils.equals(this.rmsd, m2.rmsd,
				"RootMeanSquareDeviation");
		success &= this.distr.equalsVerbose(m2.distr);
		return success;
	}

	@Override
	public boolean isApplicable(IGraph g) {
		return g.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& g.getGraphDatastructures().isNodeWeightType(
						DoubleWeight.class, Double2dWeight.class,
						Double3dWeight.class, IntWeight.class,
						Int2dWeight.class, Int3dWeight.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& b.getGraphDatastructures().isNodeWeightType(
						DoubleWeight.class, Double2dWeight.class,
						Double3dWeight.class, IntWeight.class,
						Int2dWeight.class, Int3dWeight.class);
	}

	protected void initDistr() {
		this.distr = new BinnedDoubleDistr("DistanceDistribution", 0.05);
	}

}
