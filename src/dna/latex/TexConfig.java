package dna.latex;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import dna.latex.TexTable.TableFlag;
import dna.plot.PlottingConfig.PlotFlag;
import dna.util.Config;

/** A TexConfig object is used to configure the tex-output process. **/
public class TexConfig {

	// directories
	private String dstDir;
	private String srcDir;
	private String plotDir;

	// output interval
	private String scaling;

	// scaling
	private HashMap<Long, Long> map;

	// date format
	private SimpleDateFormat dateFormat;

	// flags
	private PlotFlag[] plotFlags;
	private TableFlag[] tableFlags;

	// constructor
	public TexConfig(String dstDir, String srcDir, String plotDir,
			PlotFlag[] plotFlags, TableFlag... tableFlags) {
		this.dstDir = dstDir;
		this.srcDir = srcDir;
		this.plotDir = plotDir;
		this.plotFlags = plotFlags;
		this.tableFlags = tableFlags;

		// if default datetime is set in config, set it here
		if (Config.get("LATEX_DEFAULT_DATETIME") != null) {
			String tempDateTime = Config.get("LATEX_DEFAULT_DATETIME");
			if (!tempDateTime.equals("null"))
				this.dateFormat = new SimpleDateFormat(tempDateTime);
		}
	}

	// getters & setters
	public void setPlotFlags(PlotFlag... flags) {
		this.plotFlags = flags;
	}

	public void setTableFlags(TableFlag... flags) {
		this.tableFlags = flags;
	}

	public String getDstDir() {
		return dstDir;
	}

	public String getSrcDir() {
		return srcDir;
	}

	public String getPlotDir() {
		return plotDir;
	}

	public PlotFlag[] getPlotFlags() {
		return plotFlags;
	}

	public TableFlag[] getTableFlags() {
		return tableFlags;
	}

	public boolean isIncludeRuntimes() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotRuntimes:
				return true;
			}
		}
		return false;
	}

	public boolean isIncludeStatistics() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotSingleScalarValues:
				return true;
			case plotStatistics:
				return true;
			}
		}
		return false;
	}

	public boolean isIncludeDistributions() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotMultiScalarValues:
				return true;
			case plotMetricEntirely:
				return true;
			case plotDistributions:
				return true;
			}
		}
		return false;
	}

	public boolean isIncludeNodeValueLists() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotMultiScalarValues:
				return true;
			case plotMetricEntirely:
				return true;
			case plotNodeValueLists:
				return true;
			}
		}
		return false;
	}

	public boolean isIncludeMetricValues() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotSingleScalarValues:
				return true;
			case plotMetricEntirely:
				return true;
			case plotMetricValues:
				return true;
			}
		}
		return false;
	}

	public boolean isIncludeMetrics() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotSingleScalarValues:
				return true;
			case plotMultiScalarValues:
				return true;
			case plotMetricEntirely:
				return true;
			case plotMetricValues:
				return true;
			case plotDistributions:
				return true;
			case plotNodeValueLists:
				return true;
			}
		}
		return false;
	}

	public void setDateFormat(String pattern) {
		this.dateFormat = new SimpleDateFormat(pattern);
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public SimpleDateFormat getDateFormat() {
		return this.dateFormat;
	}

	public void setScaling(String scaling) {
		this.scaling = scaling;
	}

	public String getScaling() {
		return this.scaling;
	}

	public void setMapping(HashMap<Long, Long> map) {
		this.map = map;
	}

	public HashMap<Long, Long> getMapping() {
		return this.map;
	}

}
