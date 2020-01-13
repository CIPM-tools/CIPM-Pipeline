package dmodel.pipeline.evaluation.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExecutionData {
	// EXECUTION TIMES
	private long executionTimeCumulated;

	private long preFilter;
	private long serviceCallTree;

	private long resourceEnvironmentUpdates;
	private long systemUpdates;

	private long calibration1;
	private long calibration2;

	private long usage1;
	private long usage2;

	private long validation1;
	private long validation2;
	private long validation3;
	private long validation4;

	private long instrumentationModel;

	private long crossValidation;

	// LOGIC
	private long recordCount;
	private int usageScenarios;

	private boolean path;

	@JsonProperty
	public long validationCumulated() {
		return validation1 + validation2 + validation3 + validation4;
	}

	@JsonProperty
	public long calibrationCumulated() {
		return calibration1 + calibration2;
	}

	@JsonProperty
	public long usageCumulated() {
		return usage1 + usage2;
	}
}
