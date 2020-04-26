package dmodel.runtime.pipeline.inm.transformation.predicate.config;

import lombok.Getter;

public enum ENumericalComparator {
	GREATER("greater"), LOWER("less"), EQUAL("equal"), GREATER_EQUAL("greater_or_equal"), LOWER_EQUAL("less_or_equal");

	@Getter
	private String name;

	private ENumericalComparator(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static ENumericalComparator fromString(String text) {
		for (ENumericalComparator b : ENumericalComparator.values()) {
			if (b.name.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}

}
