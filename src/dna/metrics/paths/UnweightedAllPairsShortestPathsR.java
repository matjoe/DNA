package dna.metrics.paths;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.metrics.algorithms.IRecomputation;
import dna.series.data.distr.BinnedIntDistr;

public class UnweightedAllPairsShortestPathsR extends
		UnweightedAllPairsShortestPaths implements IRecomputation {

	public UnweightedAllPairsShortestPathsR() {
		super("UnweightedAllPairsShortestPathsR");
	}

	public UnweightedAllPairsShortestPathsR(String[] nodeTypes) {
		super("UnweightedAllPairsShortestPathsR", nodeTypes);
	}

	@Override
	public boolean recompute() {
		this.apsp = new BinnedIntDistr("APSP");
		for (IElement n_ : this.getNodesOfAssignedTypes()) {
			this.compute((Node) n_);
		}
		return true;
	}

}
