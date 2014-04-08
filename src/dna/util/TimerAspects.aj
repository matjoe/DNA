package dna.util;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.generators.IGraphGenerator;
import dna.metrics.Metric;
import dna.profiler.HotSwap;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.SeriesStats;
import dna.profiler.Profiler;
import dna.series.data.BatchData;
import dna.series.data.RunData;
import dna.series.data.RunTime;
import dna.series.data.SeriesData;
import dna.series.lists.RunTimeList;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.Update;

public aspect TimerAspects {
	private HashSet<String> resetList = new HashSet<>();
	private HashSet<String> metricList = new HashSet<>();
	private HashSet<String> additionalNotInTotalRuntimesList = new HashSet<>();
	private TimerMap map = new TimerMap();

	pointcut seriesGeneration() : call(* SeriesGeneration.generate(Series, int, int, boolean, boolean));
	pointcut runGeneration(): call(* SeriesGeneration.generateRun(Series, int, int,..));
	pointcut graphGeneration(): call(* IGraphGenerator.generate(..));

	pointcut initialBatchData(): call(* SeriesGeneration.computeInitialData(..)) || call(* SeriesGeneration.computeNextBatch(..));
	pointcut initialBatchDataGeneration(): call(* SeriesGeneration.generateInitialData(..)) || call(* SeriesGeneration.generateNextBatch(..));
	pointcut initialMetricData(): call(* SeriesGeneration.computeInitialMetrics(..));

	pointcut batchGeneration(): call(* BatchGenerator+.generate(..));
	pointcut batchApplication(): call(* Update+.apply(..));

	pointcut metricApplicationInInitialization(Metric metric) : (call(* Metric+.init()) || call(* Metric+.compute())) && target(metric) && cflow(initialMetricData());
	pointcut metricApplicationPerBatch(Metric metric, Batch b) : (call(* Metric+.applyBeforeBatch(Batch+))
			 || call(* Metric+.applyAfterBatch(Batch+))) && args(b) && target(metric);
	pointcut metricApplicationPerUpdate(Metric metric, Update update) : (call(* Metric+.applyBeforeUpdate(Update+))
			 || call(* Metric+.applyAfterUpdate(Update+))) && args(update) && target(metric);

	pointcut profilerExecution(): execution(* Profiler.start*(..)) || execution(* Profiler.finish*(..));
	pointcut hotswappingExecution(): call(* HotSwap.trySwap(..));

	SeriesData around(): seriesGeneration() {
		map = new TimerMap();
		Timer timer = new Timer("seriesGeneration");
		SeriesData res = proceed();
		timer.end();
		Log.info("total time for seriesGeneration: " + timer.toString());
		Log.infoSep();

		return res;
	}

	RunData around(): runGeneration() {
		Timer timer = new Timer("runGeneration");
		RunData res = proceed();
		timer.end();
		Log.info(timer.toString());

		return res;
	}

	Graph around(): graphGeneration() {
		resetList.add(SeriesStats.graphGenerationRuntime);
		Timer graphGenerationTimer = new Timer(
				SeriesStats.graphGenerationRuntime);
		Graph res = proceed();
		graphGenerationTimer.end();
		map.put(graphGenerationTimer);
		return res;
	}
	
	BatchData around(): initialBatchDataGeneration() {
		for (String resetTimerName : resetList) {
			map.remove(resetTimerName);
		}
		BatchData res = proceed();

		RunTimeList generalRuntimes = res.getGeneralRuntimes();

		generalRuntimes.add(map.get(SeriesStats.graphGenerationRuntime, true)
				.getRuntime());
		generalRuntimes.add(map.get(SeriesStats.batchGenerationRuntime, true)
				.getRuntime());
		generalRuntimes.add(map.get(SeriesStats.graphUpdateRuntime, true)
				.getRuntime());
		generalRuntimes.add(map.get(SeriesStats.metricsRuntime).getRuntime());

		// add metric runtimes
		for (String m : metricList) {
			res.getMetricRuntimes().add(map.get(m).getRuntime());
		}

		/**
		 * Add other runtimes that might occur off-site, as in: not counted in
		 * total. Stuff like the profiler and hotswapping is done after the
		 * total counter has stopped!
		 */
		long notInTotalRt = 0;
		for (String m : additionalNotInTotalRuntimesList) {
			Timer singleTimer = map.get(m);
			generalRuntimes.add(singleTimer.getRuntime());
			notInTotalRt += singleTimer.getRuntime().getRuntime();
		}

		// Be sure to add additional stuff to the total counter!
		double total = map.get(SeriesStats.totalRuntime).getRuntime()
				.getRuntime()
				+ notInTotalRt;
		generalRuntimes.add(new RunTime("total", total));

		double metrics = generalRuntimes.get(SeriesStats.metricsRuntime)
				.getRuntime();

		long sumRt = 0;
		for (RunTime rt : generalRuntimes.getList()) {
			sumRt += rt.getRuntime();
		}
		for (RunTime rt : res.getMetricRuntimes().getList()) {
			sumRt += rt.getRuntime();
		}

		double sum = sumRt - total - metrics;
		double overhead = total - sum;// -notInTotalRt;

		generalRuntimes.add(new RunTime("sum", sum));
		generalRuntimes.add(new RunTime("overhead", overhead));

		return res;
	}

	BatchData around(): initialBatchData() {
		Timer t = new Timer(SeriesStats.totalRuntime);
		BatchData res = proceed();
		t.end();
		map.put(t);
		return res;
	}

	BatchData around(): initialMetricData() {
		Timer t = new Timer(SeriesStats.metricsRuntime);
		BatchData res = proceed();
		t.end();
		map.put(t);
		
		for (String metricName: metricList) {
			res.getMetricRuntimes().add(
					map.get(metricName).getRuntime());
		}
		
		return res;
	}

	Batch around(): batchGeneration() {
		Timer t = new Timer(SeriesStats.batchGenerationRuntime);
		Batch res = proceed();
		t.end();
		map.put(t);
		return res;
	}

	boolean around(): batchApplication() {
		resetList.add(SeriesStats.graphUpdateRuntime);
		Timer t = map.get(SeriesStats.graphUpdateRuntime);
		if (t == null) {
			t = new Timer(SeriesStats.graphUpdateRuntime);
		}
		t.restart();
		boolean res = proceed();
		t.end();
		map.put(t);
		return res;
	}

	Object around(Metric metric): metricApplicationInInitialization(metric) {
		String metricName = metric.getName();
		metricList.add(metricName);
		Timer t = new Timer(metricName);
		Object res = proceed(metric);
		t.end();
		map.put(t);
		return res;
	}

	boolean around(Metric metric, Batch b): metricApplicationPerBatch(metric, b) {
		resetList.add(metric.getName());
		Timer singleMetricTimer = map.get(metric.getName());
		if (singleMetricTimer == null) {
			singleMetricTimer = new Timer(metric.getName());
		}

		resetList.add(SeriesStats.metricsRuntime);
		Timer wholeMetricsTimer = map.get(SeriesStats.metricsRuntime);
		if (wholeMetricsTimer == null) {
			wholeMetricsTimer = new Timer(SeriesStats.metricsRuntime);
		}

		singleMetricTimer.restart();
		wholeMetricsTimer.restart();
		boolean res = proceed(metric, b);
		singleMetricTimer.end();
		wholeMetricsTimer.end();
		map.put(singleMetricTimer);
		map.put(wholeMetricsTimer);
		return res;
	}

	boolean around(Metric metric, Update u): metricApplicationPerUpdate(metric, u) {
		resetList.add(metric.getName());
		Timer singleMetricTimer = map.get(metric.getName());
		if (singleMetricTimer == null) {
			singleMetricTimer = new Timer(metric.getName());
		}

		resetList.add(SeriesStats.metricsRuntime);
		Timer wholeMetricsTimer = map.get(SeriesStats.metricsRuntime);
		if (wholeMetricsTimer == null) {
			wholeMetricsTimer = new Timer(SeriesStats.metricsRuntime);
		}

		singleMetricTimer.restart();
		wholeMetricsTimer.restart();
		boolean res = proceed(metric, u);
		singleMetricTimer.end();
		wholeMetricsTimer.end();
		map.put(singleMetricTimer);
		map.put(wholeMetricsTimer);
		return res;
	}

	Object around(): profilerExecution() {
		resetList.add(SeriesStats.profilerRuntime);
		Timer t = map.get(SeriesStats.profilerRuntime);
		if (t == null) {
			t = new Timer(SeriesStats.profilerRuntime);
		}

		t.restart();
		Object res = proceed();
		t.end();
		map.put(t);
		additionalNotInTotalRuntimesList.add(SeriesStats.profilerRuntime);

		return res;
	}

	void around(): hotswappingExecution() {
		resetList.add(SeriesStats.hotswapRuntime);
		Timer profilerTimer = map.get(SeriesStats.profilerRuntime);
		if (profilerTimer != null) {
			profilerTimer.end();
		}
		Timer t = new Timer(SeriesStats.hotswapRuntime);
		proceed();
		t.end();
		if (profilerTimer != null) {
			profilerTimer.restart();
		}
		map.put(t);
		additionalNotInTotalRuntimesList.add(SeriesStats.hotswapRuntime);
	}
}