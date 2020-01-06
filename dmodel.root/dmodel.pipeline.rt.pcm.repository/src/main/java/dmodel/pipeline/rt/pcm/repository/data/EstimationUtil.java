package dmodel.pipeline.rt.pcm.repository.data;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.system.System;
import org.pcm.headless.api.util.PCMUtil;

import dmodel.pipeline.shared.ModelUtil;

public class EstimationUtil {

	/**
	 * This method is fully senseless and only here because we need to extend the
	 * monitoring later.
	 */
	public static AssemblyContext getAssemblyBySeff(Repository repo, System system, String serviceId) {
		ResourceDemandingSEFF seff = PCMUtil.getElementById(repo, ResourceDemandingSEFF.class, serviceId);
		BasicComponent ownComponent = seff.getBasicComponent_ServiceEffectSpecification();

		return ModelUtil.getObjects(system, AssemblyContext.class).stream().filter(
				assembly -> assembly.getEncapsulatedComponent__AssemblyContext().getId().equals(ownComponent.getId()))
				.findFirst().orElse(null);
	}

	public static ResourceContainer getContainerByAssemblyContext(AssemblyContext ctx, Allocation allocationModel) {
		AllocationContext allocation = allocationModel.getAllocationContexts_Allocation().stream().filter(alloc -> {
			return alloc.getAssemblyContext_AllocationContext().getId().equals(ctx.getId());
		}).findFirst().orElse(null);

		return allocation.getResourceContainer_AllocationContext();
	}

	public static ProcessingResourceSpecification getResourceSpecificationByContainer(ResourceContainer container,
			String resourceId) {
		return container.getActiveResourceSpecifications_ResourceContainer().parallelStream()
				.filter(res -> res.getActiveResourceType_ActiveResourceSpecification().getId().equals(resourceId))
				.findFirst().orElse(null);
	}

}
