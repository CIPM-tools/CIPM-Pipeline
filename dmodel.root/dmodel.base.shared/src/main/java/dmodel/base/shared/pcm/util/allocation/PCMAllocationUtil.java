package dmodel.base.shared.pcm.util.allocation;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.allocation.AllocationFactory;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

public class PCMAllocationUtil {

	public static AllocationContext deployAssemblyOnContainer(Allocation allocationModel,
			AssemblyContext freeAlternative, ResourceContainer belongingContainer) {
		AllocationContext ctx = AllocationFactory.eINSTANCE.createAllocationContext();
		ctx.setResourceContainer_AllocationContext(belongingContainer);
		ctx.setAssemblyContext_AllocationContext(freeAlternative);

		allocationModel.getAllocationContexts_Allocation().add(ctx);
		return ctx;
	}

}
