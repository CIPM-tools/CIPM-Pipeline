package dmodel.pipeline.core;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import dmodel.pipeline.shared.pcm.InMemoryPCM;

public interface IPcmModelProvider {

	public Repository getRepository();

	public System getSystem();

	public ResourceEnvironment getResourceEnvironment();

	public Allocation getAllocation();

	public UsageModel getUsage();

	public InMemoryPCM getRaw();

}
