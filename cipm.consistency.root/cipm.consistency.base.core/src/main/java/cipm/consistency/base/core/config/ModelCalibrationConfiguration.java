package cipm.consistency.base.core.config;

import lombok.Data;

@Data
public class ModelCalibrationConfiguration {

	private int powDepth = 4;

	private boolean lnFunction = true;
	private boolean sqrtFunction = true;
	private boolean exponentialFunction = false;

	private double parameterSignificanceThreshold = 0.0005;

	private long regressionHorizon = 300 * 3; // 15 minutes
	private float outlierPercentile = 1;
	private float noiseOutlierPercentile = 5;
	private int noiseStrategy = 0; // 0 = normal distr, 1 = deviation
	
	private double maxParameterCorrelation = 0.8;

}
