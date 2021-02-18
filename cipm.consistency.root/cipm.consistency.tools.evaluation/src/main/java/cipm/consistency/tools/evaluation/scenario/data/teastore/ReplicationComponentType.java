package cipm.consistency.tools.evaluation.scenario.data.teastore;

import lombok.Getter;

public enum ReplicationComponentType {
	AUTH("auth", "_AiuxcDVdEeqPG_FgW3bi6Q"), PERSISTENCE("persistence", "_lnx1oDVWEeqPG_FgW3bi6Q"),
	IMAGE("image", "_h7nF4DVWEeqPG_FgW3bi6Q");

	@Getter
	private String name;

	@Getter
	private String id;

	ReplicationComponentType(String string, String id) {
		this.name = string;
		this.id = id;
	}

}
