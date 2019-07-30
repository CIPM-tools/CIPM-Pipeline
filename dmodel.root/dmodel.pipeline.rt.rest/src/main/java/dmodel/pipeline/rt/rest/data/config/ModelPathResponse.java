package dmodel.pipeline.rt.rest.data.config;

public class ModelPathResponse {
	private boolean repo;
	private boolean sys;
	private boolean res;
	private boolean alloc;
	private boolean usage;

	public boolean isRepo() {
		return repo;
	}

	public void setRepo(boolean repo) {
		this.repo = repo;
	}

	public boolean isSys() {
		return sys;
	}

	public void setSys(boolean sys) {
		this.sys = sys;
	}

	public boolean isRes() {
		return res;
	}

	public void setRes(boolean res) {
		this.res = res;
	}

	public boolean isAlloc() {
		return alloc;
	}

	public void setAlloc(boolean alloc) {
		this.alloc = alloc;
	}

	public boolean isUsage() {
		return usage;
	}

	public void setUsage(boolean usage) {
		this.usage = usage;
	}

}
