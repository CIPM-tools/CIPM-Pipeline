package dmodel.pipeline.core.facade.pcm;

import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import dmodel.pipeline.core.facade.IResettableQueryFacade;

public interface IAllocationQueryFacade extends IResettableQueryFacade {

	void deployAssembly(AssemblyContext ret, ResourceContainer host);

	void undeployAssembly(AssemblyContext ctx);

	boolean isDeployed(AssemblyContext ac);

	ResourceContainer getContainerByAssembly(AssemblyContext asCtx);

	List<AssemblyContext> getDeployedAssembly(BasicComponent componentType, ResourceContainer container);

	void deleteAllocation(AssemblyContext da);

	boolean hasDeployments(ResourceContainer presentContainer);

}
