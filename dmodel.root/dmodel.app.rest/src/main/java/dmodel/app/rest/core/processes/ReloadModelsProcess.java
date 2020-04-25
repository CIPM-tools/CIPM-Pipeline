package dmodel.app.rest.core.processes;

import dmodel.base.core.config.ModelConfiguration;
import dmodel.base.core.impl.CentralModelAdminstrator;

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
