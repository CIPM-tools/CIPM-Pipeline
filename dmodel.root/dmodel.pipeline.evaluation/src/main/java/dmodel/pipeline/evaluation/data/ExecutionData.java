package dmodel.pipeline.evaluation.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ExecutionData {
	@Getter(AccessLevel.PUBLIC)
	@Setter(AccessLevel.NONE)
	private Map<ExecutionMeasuringPoint, StartStopInterval> measuringPoints;

	private long startTime;
	private long executionTimeCumulated;

	public ExecutionData() {
		this.measuringPoints = Maps.newHashMap();
	}

	// LOGIC
	private long recordCount;
	private int usageScenarios;

	private boolean executedPath;

	public void trackPoint(ExecutionMeasuringPoint point) {
		if (!measuringPoints.containsKey(point)) {
			measuringPoints.put(point, new StartStopInterval());
		} else {
			measuringPoints.get(point).stop();
		}
	}

	@JsonProperty
	public long validationCumulated() {
		return resolveOrZero(ExecutionMeasuringPoint.T_VALIDATION_1)
				+ resolveOrZero(ExecutionMeasuringPoint.T_VALIDATION_2)
				+ resolveOrZero(ExecutionMeasuringPoint.T_VALIDATION_3)
				+ resolveOrZero(ExecutionMeasuringPoint.T_VALIDATION_4);
	}

	@JsonProperty
	public long calibrationCumulated() {
		return resolveOrZero(ExecutionMeasuringPoint.T_DEMAND_CALIBRATION_1)
				+ resolveOrZero(ExecutionMeasuringPoint.T_DEMAND_CALIBRATION_2);
	}

	@JsonProperty
	public long usageCumulated() {
		return resolveOrZero(ExecutionMeasuringPoint.T_USAGE_1) + resolveOrZero(ExecutionMeasuringPoint.T_USAGE_2);
	}

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
