package dmodel.pipeline.core.state;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ETransformationState {
	NOT_REACHED("not_reached"), RUNNING("running"), FINISHED("finished");

	private final String name;

	private ETransformationState(String name) {
		this.name = name;
	}

	@Override
	@JsonValue
	public String toString() {
		return name;
	}

}
