package cipm.consistency.base.core.mocks;

import java.util.List;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.springframework.beans.factory.annotation.Autowired;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.core.facade.IResettableQueryFacade;
import cipm.consistency.base.shared.pcm.InMemoryPCM;

public class StaticModelProviderImpl implements IPcmModelProvider {
	private InMemoryPCM pcm;

	@Autowired
	private List<IResettableQueryFacade> facades;

	@Override
	public Repository getRepository() {
		return pcm.getRepository();
	}

	@Override
	public System getSystem() {
		return pcm.getSystem();
	}

	@Override
	public ResourceEnvironment getResourceEnvironment() {
		return pcm.getResourceEnvironmentModel();
	}

	@Override
	public Allocation getAllocation() {
		return pcm.getAllocationModel();
	}

	@Override
	public UsageModel getUsage() {
		return pcm.getUsageModel();
	}

	@Override
	public InMemoryPCM getRaw() {
		return pcm;
	}

	public void setPCM(InMemoryPCM pcm) {
		this.pcm = pcm;

		for (IResettableQueryFacade fac : facades) {
			fac.reset(true);
		}
	}

}
