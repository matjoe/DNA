package dna.depr.metrics.motifsNew;

import dna.metrics.IMetricNew;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class UndirectedMotifsR extends UndirectedMotifs {

	public UndirectedMotifsR() {
		super("UndirectedMotifsR", ApplicationType.Recomputation,
				IMetricNew.MetricType.exact);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

}