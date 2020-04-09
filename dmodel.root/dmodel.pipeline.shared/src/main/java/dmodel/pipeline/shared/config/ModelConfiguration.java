package dmodel.pipeline.shared.config;

import lombok.Data;

@Data
public class ModelConfiguration {

	private String repositoryPath = null;
	private String systemPath = null;
	private String usagePath = null;
	private String allocationPath = null;
	private String envPath = null;

	private String instrumentationModelPath = null;
	private String correspondencePath = null;
	private String runtimeEnvironmentPath = null;

}
