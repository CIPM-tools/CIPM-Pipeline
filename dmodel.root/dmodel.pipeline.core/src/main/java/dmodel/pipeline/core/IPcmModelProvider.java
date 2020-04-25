package dmodel.pipeline.core;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import dmodel.pipeline.shared.pcm.InMemoryPCM;

/**
 * Describes the interface for accessing PCM model parts.
 * 
 * @author David Monschein
 *
 */
public interface IPcmModelProvider {

	/**
	 * Gets the repository model of the PCM.
	 * 
	 * @see Repository
	 * @return repository model of the PCM
	 */
	public Repository getRepository();

	/**
	 * Gets the system model of the PCM.
	 * 
	 * @see System
	 * @return system model of the PCM
	 */
	public System getSystem();

	/**
	 * Gets the resource environment of the PCM.
	 * 
	 * @see ResourceEnvironment
	 * @return resource environment of the PCM
	 */
	public ResourceEnvironment getResourceEnvironment();

	/**
	 * Gets the allocation model of the PCM.
	 * 
	 * @see Allocation
	 * @return allocation model of the PCM
	 */
	public Allocation getAllocation();

	/**
	 * Gets the usage model of the PCM.
	 * 
	 * @see UsageModel
	 * @return usage model of the PCM
	 */
	public UsageModel getUsage();

	/**
	 * Gets the raw PCM model container which refers all parts of the PCM.
	 * 
	 * @return the raw PCM model container
	 */
	public InMemoryPCM getRaw();

}
