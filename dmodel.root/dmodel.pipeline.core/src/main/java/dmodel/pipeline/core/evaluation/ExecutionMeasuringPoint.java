package dmodel.pipeline.core.evaluation;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ExecutionMeasuringPoint {
	T_PRE_FILTER("preFilter"), T_SERVICE_CALL_TREE("serviceCallTree"),
	T_RESOURCE_ENVIRONMENT("resourceEnvironmentUpdates"), T_SYSTEM("systemUpdates"),
	T_DEMAND_CALIBRATION_1("demandCalibration1"), T_DEMAND_CALIBRATION_2("demandCalibration2"),
	T_USAGE_1("usageUpdates1"), T_USAGE_2("usageUpdates2"), T_VALIDATION_1("validation1"),
	T_VALIDATION_2("validation2"), T_VALIDATION_3("validation3"), T_VALIDATION_4("validation4"),
	T_INSTRUMENTATION_MODEL("instrumentationModelUpdates"), T_CROSS_VALIDATION("crossValidation");

	private String name;

	private ExecutionMeasuringPoint(String name) {
		this.name = name;
	}

	@JsonValue
	public String getName() {
		return name;
	}
}
