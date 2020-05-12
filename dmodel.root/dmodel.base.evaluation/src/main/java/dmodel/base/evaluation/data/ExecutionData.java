package dmodel.base.evaluation.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import dmodel.base.core.evaluation.ExecutionMeasuringPoint;
import dmodel.base.core.health.HealthState;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Performance data about a single pipeline execution.
 * 
 * @author David Monschein
 *
 */
@Data
public class ExecutionData {
	/**
	 * Maps execution measuring points to a time interval.
	 */
	@Getter(AccessLevel.PUBLIC)
	@Setter(AccessLevel.NONE)
	private Map<ExecutionMeasuringPoint, StartStopInterval> measuringPoints;

	/**
	 * Timestamp of pipeline start.
	 */
	private long startTime;

	/**
	 * Execution time of all pipeline parts cumulated.
	 */
	private long executionTimeCumulated;

	/**
	 * Creates a new instance for monitoring a single pipeline execution.
	 */
	public ExecutionData() {
		this.measuringPoints = Maps.newHashMap();
	}

	// LOGIC
	/**
	 * Number of records passed to the pipeline.
	 */
	private long recordCount;

	/**
	 * Number of extracted usage scenarios.
	 */
	private int usageScenarios;

	/**
	 * Executed path within the pipeline.
	 */
	private boolean executedPath;

	/**
	 * Health state of the pipeline after the execution.
	 */
	private HealthState success;

	/**
	 * Tracks the execution of a certain point in the pipeline.
	 * 
	 * @param point the pipeline to track
	 */
	public void trackPoint(ExecutionMeasuringPoint point) {
		if (!measuringPoints.containsKey(point)) {
			measuringPoints.put(point, new StartStopInterval());
		} else {
			measuringPoints.get(point).stop();
		}
	}

	/**
	 * Returns the sum of the execution times of the validations.
	 * 
	 * @return the sum of execution times of the validation
	 */
	@JsonProperty
	public long validationCumulated() {
		return resolveOrZero(ExecutionMeasuringPoint.T_VALIDATION_1)
				+ resolveOrZero(ExecutionMeasuringPoint.T_VALIDATION_2)
				+ resolveOrZero(ExecutionMeasuringPoint.T_VALIDATION_3)
				+ resolveOrZero(ExecutionMeasuringPoint.T_VALIDATION_4);
	}

	/**
	 * Returns the sum of the execution times of the repository model
	 * transformations.
	 * 
	 * @return sum of the execution times of the repository model transformations
	 */
	@JsonProperty
	public long calibrationCumulated() {
		return resolveOrZero(ExecutionMeasuringPoint.T_DEMAND_CALIBRATION_1)
				+ resolveOrZero(ExecutionMeasuringPoint.T_DEMAND_CALIBRATION_2);
	}

	/**
	 * Returns the sum of the execution times of the usage model transformations.
	 * 
	 * @return Returns the sum of the execution times of the usage model
	 *         transformations.
	 */
	@JsonProperty
	public long usageCumulated() {
		return resolveOrZero(ExecutionMeasuringPoint.T_USAGE_1) + resolveOrZero(ExecutionMeasuringPoint.T_USAGE_2);
	}

	/**
	 * Gets the time interval duration for a specific measuring point or returns
	 * zero if it does not exist.
	 * 
	 * @param measuringPoint the measuring point
	 * @return time interval duration or zero if it does not exist
	 */
	private long resolveOrZero(ExecutionMeasuringPoint measuringPoint) {
		if (measuringPoints.containsKey(measuringPoint)) {
			StartStopInterval ival = measuringPoints.get(measuringPoint);
			if (ival.completed()) {
				return ival.getDuration();
			}
		}
		return 0;
	}
}
