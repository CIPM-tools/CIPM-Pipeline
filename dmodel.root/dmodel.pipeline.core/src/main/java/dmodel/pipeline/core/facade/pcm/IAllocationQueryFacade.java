package dmodel.pipeline.core.facade.pcm;

import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import dmodel.pipeline.core.facade.IResettableQueryFacade;

public interface IAllocationQueryFacade extends IResettableQueryFacade {

	void deployAssembly(AssemblyContext ret, ResourceContainer host);

	boolean isDeployed(AssemblyContext ac);

	ResourceContainer getContainerByAssembly(AssemblyContext asCtx);

	List<AssemblyContext> getDeployedAssembly(BasicComponent componentType, ResourceContainer container);
	/*
	 * PCMUtils .getElementsByType(getBlackboard().getQuery().getAllocation(),
	 * AllocationContext.class).stream() .filter(a -> { return belongingContainer !=
	 * null &&
	 * a.getResourceContainer_AllocationContext().getId().equals(belongingContainer.
	 * getId()) && a.getAssemblyContext_AllocationContext().
	 * getEncapsulatedComponent__AssemblyContext()
	 * .getId().equals(belComponent.getId()); }).map(a ->
	 * a.getAssemblyContext_AllocationContext()).collect(Collectors.toList());
	 */

	void deleteAllocation(AssemblyContext da);

}
