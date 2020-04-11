package dmodel.pipeline.rt.rest.data.config;

import dmodel.pipeline.core.config.ConfigurationContainer;

public class ModelPathContainer {
	private String repo;
	private String sys;
	private String res;
	private String alloc;
	private String usage;

	public String getRepo() {
		return repo;
	}

	public void setRepo(String repo) {
		this.repo = repo;
	}

	public String getSys() {
		return sys;
	}

	public void setSys(String sys) {
		this.sys = sys;
	}

	public String getRes() {
		return res;
	}

	public void setRes(String res) {
		this.res = res;
	}

	public String getAlloc() {
		return alloc;
	}

	public void setAlloc(String alloc) {
		this.alloc = alloc;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public static ModelPathContainer from(ConfigurationContainer config) {
		ModelPathContainer req = new ModelPathContainer();
		req.alloc = config.getModels().getAllocationPath();
		req.repo = config.getModels().getRepositoryPath();
		req.res = config.getModels().getEnvPath();
		req.sys = config.getModels().getSystemPath();
		req.usage = config.getModels().getUsagePath();
		return req;
	}

}
