package dna.updates.generators.zalando;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.zalando.ZalandoGraphDataStructure;
import dna.graph.generators.zalando.data.EventColumn;
import dna.graph.generators.zalando.parser.EventFilter;

public class ProductsAbsoluteBatchGenerator extends
		ZalandoEqualityBatchGenerator {

	/**
	 * Initializes the {@link ProductsAbsoluteBatchGenerator}.
	 * 
	 * @param gds
	 *            The {@link GraphDataStructure} of the graph to generate.
	 * @param timestampInit
	 *            The time right before start creating the graph.
	 * @param numberOfLinesPerBatch
	 *            The maximum number of {@code Event}s used for each batch. It
	 *            is the <u>maximum</u> number because the log file may have
	 *            fewer lines.
	 * @param eventsFilepath
	 *            The full path of the Zalando log file. Will be passed to
	 *            {@link Old_EventReader}.
	 */
	public ProductsAbsoluteBatchGenerator(ZalandoGraphDataStructure gds,
			long timestampInit, String filterProperties,
			int numberOfLinesPerBatch, String pathProducts,
			boolean isGzippedProducts, String pathLog, boolean isGzippedLog,
			int omitFirstEvents) {
		super("ProductsAbsolute", gds, timestampInit, EventFilter
				.fromFile(filterProperties)
		/* new DefaultEventFilter() /* null */, numberOfLinesPerBatch,
				pathProducts, isGzippedProducts, pathLog, isGzippedLog,
				new EventColumn[] { EventColumn.PRODUCTFAMILYID }, true,
				new EventColumn[] { EventColumn.USER, EventColumn.ACTION },
				true, true, omitFirstEvents);
	}

}
