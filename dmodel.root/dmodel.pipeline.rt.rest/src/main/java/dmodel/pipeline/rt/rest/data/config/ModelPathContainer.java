package dmodel.pipeline.rt.rest.data.config;

import dmodel.pipeline.core.config.ConfigurationContainer;
import lombok.Data;

@Data
public class ModelPathContainer {
	private String repo;
	private String sys;
	private String res;
	private String alloc;
	private String usage;

	private String instrumentation;
	private String correspondences;
	private String runtimeenv;

	public static ModelPathContainer from(ConfigurationContainer config) {
		ModelPathContainer req = new ModelPathContainer();
		req.alloc = config.getModels().getAllocationPath();
		req.repo = config.getModels().getRepositoryPath();
		req.res = config.getModels().getEnvPath();
		req.sys = config.getModels().getSystemPath();
		req.usage = config.getModels().getUsagePath();
		req.instrumentation = config.getModels().getInstrumentationModelPath();
		req.correspondences = config.getModels().getCorrespondencePath();
		req.runtimeenv = config.getModels().getRuntimeEnvironmentPath();

		return req;
	}

}
