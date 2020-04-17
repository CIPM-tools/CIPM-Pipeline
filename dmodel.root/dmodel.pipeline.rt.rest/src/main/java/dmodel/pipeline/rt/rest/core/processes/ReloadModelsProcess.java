package dmodel.pipeline.rt.rest.core.processes;

import dmodel.pipeline.core.config.ModelConfiguration;
import dmodel.pipeline.core.impl.CentralModelAdminstrator;

public class ReloadModelsProcess implements Runnable {

	private CentralModelAdminstrator modelContainer;
	private ModelConfiguration config;

	public ReloadModelsProcess(CentralModelAdminstrator modelContainer, ModelConfiguration config) {
		this.modelContainer = modelContainer;
		this.config = config;
	}

	@Override
	public void run() {
		this.modelContainer.loadArchitectureModel(config);
	}

}
