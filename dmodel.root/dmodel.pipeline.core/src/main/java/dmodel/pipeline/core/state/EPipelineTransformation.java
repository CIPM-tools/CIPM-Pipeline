package dmodel.pipeline.core.state;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EPipelineTransformation {
	PRE_VALIDATION("pre-validation"), T_RESOURCEENV("t-resourceenv"), T_ALLOCATION("t-allocation"),
	T_SYSTEM("t-system"), T_VALIDATION21("t-validation2-1"), T_VALIDATION22("t-validation2-2"),
	T_USAGEMODEL1("t-usagemodel1"), T_REPOSITORY1("t-repository1"), T_USAGEMODEL2("t-usagemodel2"),
	T_REPOSITORY2("t-repository2"), T_VALIDATION3("t-validation3");

	private final String name;

	private EPipelineTransformation(String name) {
		this.name = name;
	}

	@Override
	@JsonValue
	public String toString() {
		return name;
	}

}
