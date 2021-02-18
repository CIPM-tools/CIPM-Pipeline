package cipm.consistency.tools.evaluation.scenario.data.teastore;

import lombok.Getter;

public enum LoadProfileType {
	DEFAULT_20USER("20user_default.jmx"), DEFAULT_40USER("40user_default.jmx"), BIG_CART_20USER("20user_cart_big.jmx"),
	BIG_CART_40USER("40user_cart_big.jmx"), NONE("none");

	@Getter
	private String name;

	LoadProfileType(String string) {
		this.name = string;
	}

}
