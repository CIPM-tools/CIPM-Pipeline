package cipm.consistency.app.rest.core.processes;

import cipm.consistency.base.core.config.ModelConfiguration;
import cipm.consistency.base.core.impl.CentralModelProviderImpl;

public class ReloadModelsProcess implements Runnable {

	private CentralModelProviderImpl modelContainer;
	private ModelConfiguration config;

	public ReloadModelsProcess(CentralModelProviderImpl modelContainer, ModelConfiguration config) {
		this.modelContainer = modelContainer;
		this.config = config;
	}

	@Override
	public void run() {
		this.modelContainer.loadArchitectureModel(config);
	}

}
