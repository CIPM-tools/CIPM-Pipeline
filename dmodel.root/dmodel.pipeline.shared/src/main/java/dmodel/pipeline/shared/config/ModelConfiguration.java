package dmodel.pipeline.shared.config;

public class ModelConfiguration {

	private String repositoryPath = "";
	private String systemPath = "";
	private String usagePath = "";
	private String allocationPath = "";
	private String envPath = "";

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public String getSystemPath() {
		return systemPath;
	}

	public void setSystemPath(String systemPath) {
		this.systemPath = systemPath;
	}

	public String getUsagePath() {
		return usagePath;
	}

	public void setUsagePath(String usagePath) {
		this.usagePath = usagePath;
	}

	public String getAllocationPath() {
		return allocationPath;
	}

	public void setAllocationPath(String allocationPath) {
		this.allocationPath = allocationPath;
	}

	public String getEnvPath() {
		return envPath;
	}

	public void setEnvPath(String envPath) {
		this.envPath = envPath;
	}

}
