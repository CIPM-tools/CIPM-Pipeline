package cipm.consistency.runtime.pipeline.pcm.repository.outlier;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import cipm.consistency.runtime.pipeline.pcm.repository.IRegressionOutlierDetection;
import cipm.consistency.runtime.pipeline.pcm.repository.calibration.ExecutionTimesExtractor.RegressionDataset;

public class NumericOutlierDetection implements IRegressionOutlierDetection {
	private Percentile upperPercentile;
	private Percentile lowerPercentile;
	private boolean none = false;

	public NumericOutlierDetection(float percentile) {
		if (percentile > 0) {
			this.upperPercentile = new Percentile(100d - percentile);
			this.lowerPercentile = new Percentile(percentile);
		} else {
			none = true;
		}
	}

	@Override
	public void filterOutliers(RegressionDataset dataset) {
		if (none) {
			return;
		}

		double[] values = dataset.getRecords().stream().map(r -> r.getValue()).mapToDouble(d -> d).toArray();
		double valueUpperPercentile = upperPercentile.evaluate(values);
		double valueLowerPercentile = lowerPercentile.evaluate(values);

		dataset.setRecords(dataset.getRecords().stream().filter(r -> {
			return r.getValue() >= valueLowerPercentile && r.getValue() <= valueUpperPercentile;
		}).collect(Collectors.toList()));
	}

	@Override
	public double[] filterOutliers(double[] data) {
		if (none) {
			return data;
		}

		double valueUpperPercentile = upperPercentile.evaluate(data);
		double valueLowerPercentile = lowerPercentile.evaluate(data);
		return Arrays.stream(data).filter(d -> {
			return d >= valueLowerPercentile && d <= valueUpperPercentile;
		}).toArray();
	}

}
