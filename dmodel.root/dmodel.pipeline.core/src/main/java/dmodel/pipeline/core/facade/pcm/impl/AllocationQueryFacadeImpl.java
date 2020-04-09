package dmodel.pipeline.core.facade.pcm.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.allocation.AllocationFactory;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.core.facade.pcm.IAllocationQueryFacade;

@Component
public class AllocationQueryFacadeImpl implements IAllocationQueryFacade {
	@Autowired
	private IPcmModelProvider pcmModelProvider;

	private Cache<String, AllocationContext> allocationContextIdCache = new Cache2kBuilder<String, AllocationContext>() {
	}.eternal(true).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	private Cache<AssemblyContext, AllocationContext> assemblyDeploymentCache = new Cache2kBuilder<AssemblyContext, AllocationContext>() {
	}.eternal(true).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	// this cache is used very often so it is designed to be high-performance
	Map<Pair<String, String>, List<AssemblyContext>> componentContainerQueryCache = Maps.newHashMap();

	@Override
	public void deployAssembly(AssemblyContext ret, ResourceContainer host) {
		AllocationContext nAllocation = AllocationFactory.eINSTANCE.createAllocationContext();
		nAllocation.setAssemblyContext_AllocationContext(ret);
		nAllocation.setResourceContainer_AllocationContext(host);
		nAllocation.setEntityName(ret.getEntityName() + " -> " + host.getEntityName());

		pcmModelProvider.getAllocation().getAllocationContexts_Allocation().add(nAllocation);

		importAllocationContext(nAllocation);
	}

	@Override
	public boolean isDeployed(AssemblyContext ac) {
		return assemblyDeploymentCache.containsKey(ac);
	}

	@Override
	public ResourceContainer getContainerByAssembly(AssemblyContext asCtx) {
		if (assemblyDeploymentCache.containsKey(asCtx)) {
			return assemblyDeploymentCache.get(asCtx).getResourceContainer_AllocationContext();
		}
		return null;
	}

	@Override
	public List<AssemblyContext> getDeployedAssembly(BasicComponent componentType, ResourceContainer container) {
		return componentContainerQueryCache.get(Pair.of(componentType.getId(), container.getId()));
	}

	@Override
	public void deleteAllocation(AssemblyContext da) {
		if (assemblyDeploymentCache.containsKey(da)) {
			AllocationContext correspondingCtx = assemblyDeploymentCache.get(da);
			pcmModelProvider.getAllocation().getAllocationContexts_Allocation().remove(correspondingCtx);
			removeAllocationContext(correspondingCtx);
		}
	}

	@Override
	public void reset(boolean hard) {
		assemblyDeploymentCache.clear();
		allocationContextIdCache.clear();
		componentContainerQueryCache.clear();

		// fill cache
		for (AllocationContext actx : pcmModelProvider.getAllocation().getAllocationContexts_Allocation()) {
			importAllocationContext(actx);
		}
	}

	private void importAllocationContext(AllocationContext ctx) {
		allocationContextIdCache.put(ctx.getId(), ctx);
		assemblyDeploymentCache.put(ctx.getAssemblyContext_AllocationContext(), ctx);

		Pair<String, String> queryPair = generateQueryPair(ctx);
		if (componentContainerQueryCache.containsKey(queryPair)) {
			componentContainerQueryCache.get(queryPair).add(ctx.getAssemblyContext_AllocationContext());
		} else {
			componentContainerQueryCache.put(queryPair, Lists.newArrayList(ctx.getAssemblyContext_AllocationContext()));
		}
	}

	private void removeAllocationContext(AllocationContext ctx) {
		allocationContextIdCache.remove(ctx.getId());
		assemblyDeploymentCache.remove(ctx.getAssemblyContext_AllocationContext());
		componentContainerQueryCache.get(generateQueryPair(ctx)).remove(ctx.getAssemblyContext_AllocationContext());
	}

	private Pair<String, String> generateQueryPair(AllocationContext ctx) {
		return Pair.of(ctx.getAssemblyContext_AllocationContext().getEncapsulatedComponent__AssemblyContext().getId(),
				ctx.getResourceContainer_AllocationContext().getId());
	}

}
