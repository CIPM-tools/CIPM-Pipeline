package dmodel.pipeline.shared.health;

import lombok.Getter;

public enum HealthStateObservedComponents {
	VSUM_MANAGER("VSUM Manager"), CONFIGURATION("Configuration");

	@Getter
	private String name;

	private HealthStateObservedComponents(String name) {
		this.name = name;
	}
}
