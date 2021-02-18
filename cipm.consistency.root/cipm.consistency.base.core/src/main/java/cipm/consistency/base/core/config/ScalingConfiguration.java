package cipm.consistency.base.core.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScalingConfiguration {

	private boolean scalingEnabled = true;
	private boolean constantDetermination = true;

	private boolean treeScaling = true;
	private boolean feedbackBasedScaling = false;

	private double adjustmentFactor = 0.1d;
	private double baseGradient = 0.04d;

	private double constantDeterminationProportion = 0.15d;

}
