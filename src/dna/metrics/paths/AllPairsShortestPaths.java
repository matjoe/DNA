package dna.metrics.paths;

import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.BinnedDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.parameters.Parameter;

public abstract class AllPairsShortestPaths extends Metric {

	// TODO INIT!!!
	// this.apsp = new DistributionLong("APSP");

	protected BinnedDistr apsp;

	public AllPairsShortestPaths(String name) {
		super(name);
	}

	public AllPairsShortestPaths(String name, Parameter... p) {
		super(name, p);
	}

	protected abstract double getCharacteristicPathLength();

	@Override
	public Value[] getValues() {
		this.apsp.truncate();

		Value v1 = new Value("existingPaths", this.apsp.getDenominator());
		Value v2 = new Value("possiblePaths", this.g.getNodeCount()
				* (this.g.getNodeCount() - 1));
		Value v3 = new Value("characteristicPathLength",
				this.getCharacteristicPathLength());
		Value v4 = new Value("diameter", this.apsp.getMaxNonZeroIndex());

		return new Value[] { v1, v2, v3, v4 };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr<?, ?>[] { this.apsp };
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
	public boolean equals(IMetric m) {
		return this.isComparableTo(m)
				&& this.apsp.equalsVerbose(((AllPairsShortestPaths) m).apsp);
	}

}
