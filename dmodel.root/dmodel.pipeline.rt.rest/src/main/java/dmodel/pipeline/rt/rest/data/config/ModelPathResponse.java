package dmodel.pipeline.rt.rest.data.config;

import lombok.Data;

@Data
public class ModelPathResponse {
	private boolean repo;
	private boolean sys;
	private boolean res;
	private boolean alloc;
	private boolean usage;

	private boolean instrumentation;
	private boolean correspondences;
	private boolean runtimeenv;

}
