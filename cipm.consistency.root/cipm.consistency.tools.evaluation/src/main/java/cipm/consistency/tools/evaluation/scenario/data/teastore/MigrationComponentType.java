package cipm.consistency.tools.evaluation.scenario.data.teastore;

import lombok.Getter;

public enum MigrationComponentType {

	AUTH("teastore-auth"), PERSISTENCE("teastore-persistence"), IMAGE("teastore-image"),
	RECOMMENDER("teastore-recommender");

	@Getter
	private String name;

	MigrationComponentType(String string) {
		this.name = string;
	}

}
