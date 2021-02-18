package cipm.consistency.runtime.pipeline.instrumentation.predicate.config;

import lombok.Getter;

public enum ELogicalOperator {
	AND("AND"), OR("OR");

	@Getter
	private String name;

	private ELogicalOperator(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static ELogicalOperator fromString(String text) {
		for (ELogicalOperator b : ELogicalOperator.values()) {
			if (b.name.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
