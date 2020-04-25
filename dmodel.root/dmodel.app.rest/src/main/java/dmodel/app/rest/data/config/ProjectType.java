package dmodel.app.rest.data.config;

public enum ProjectType {
	GRADLE("Gradle"), MAVEN("Maven"), UNKNOWN("Unknown"), PLAIN_JAVA("Plain Java");

	private String name;

	private ProjectType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
