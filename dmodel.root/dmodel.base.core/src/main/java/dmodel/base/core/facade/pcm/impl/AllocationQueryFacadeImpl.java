package dmodel.base.core.facade.pcm.impl;

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

import dmodel.base.core.IPcmModelProvider;
import dmodel.base.core.facade.pcm.IAllocationQueryFacade;

@Component
public class AllocationQueryFacadeImpl implements IAllocationQueryFacade {
	@Autowired
	private IPcmModelProvider pcmModelProvider;

	private Cache<String, AllocationContext> allocationContextIdCache = new Cache2kBuilder<String, AllocationContext>() {
	}.eternal(true).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	private Cache<String, AllocationContext> assemblyDeploymentCache = new Cache2kBuilder<String, AllocationContext>() {
	}.eternal(true).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	private Map<String, Integer> hasDeploymentCache = Maps.newHashMap();

	// this cache is used very often so it is designed to be high-performance
	private Map<Pair<String, String>, List<AssemblyContext>> componentContainerQueryCache = Maps.newHashMap();

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
	public void undeployAssembly(AssemblyContext ctx) {
		if (assemblyDeploymentCache.containsKey(ctx.getId())) {
			this.removeAllocationContext(assemblyDeploymentCache.get(ctx.getId()));
		}
	}

	@Override
	public boolean isDeployed(AssemblyContext ac) {
		return assemblyDeploymentCache.containsKey(ac.getId());
	}

	@Override
	public ResourceContainer getContainerByAssembly(AssemblyContext asCtx) {
		if (assemblyDeploymentCache.containsKey(asCtx.getId())) {
			return assemblyDeploymentCache.get(asCtx.getId()).getResourceContainer_AllocationContext();
		}
		return null;
	}

	@Override
	public List<AssemblyContext> getDeployedAssembly(BasicComponent componentType, ResourceContainer container) {
		Pair<String, String> queryPair = Pair.of(componentType.getId(), container.getId());
		if (componentContainerQueryCache.containsKey(queryPair)) {
			return componentContainerQueryCache.get(queryPair);
		} else {
			return Lists.newArrayList();
		}
	}

	@Override
	public void deleteAllocation(AssemblyContext da) {
		if (assemblyDeploymentCache.containsKey(da.getId())) {
			AllocationContext correspondingCtx = assemblyDeploymentCache.get(da.getId());
			removeAllocationContext(correspondingCtx);
		}
	}

	@Override
	public boolean hasDeployments(ResourceContainer presentContainer) {
		return hasDeploymentCache.containsKey(presentContainer.getId())
				? hasDeploymentCache.get(presentContainer.getId()) > 0
				: false;
	}

	@Override
	public void reset(boolean hard) {
		assemblyDeploymentCache.clear();
		allocationContextIdCache.clear();
		componentContainerQueryCache.clear();
		hasDeploymentCache.clear();

		// fill cache
		for (AllocationContext actx : pcmModelProvider.getAllocation().getAllocationContexts_Allocation()) {
			importAllocationContext(actx);
		}
	}

	private void importAllocationContext(AllocationContext ctx) {
		allocationContextIdCache.put(ctx.getId(), ctx);
		assemblyDeploymentCache.put(ctx.getAssemblyContext_AllocationContext().getId(), ctx);

		if (hasDeploymentCache.containsKey(ctx.getResourceContainer_AllocationContext().getId())) {
			hasDeploymentCache.put(ctx.getResourceContainer_AllocationContext().getId(),
					hasDeploymentCache.get(ctx.getResourceContainer_AllocationContext().getId()) + 1);
		} else {
			hasDeploymentCache.put(ctx.getResourceContainer_AllocationContext().getId(), 1);
		}

		Pair<String, String> queryPair = generateQueryPair(ctx);
		if (componentContainerQueryCache.containsKey(queryPair)) {
			componentContainerQueryCache.get(queryPair).add(ctx.getAssemblyContext_AllocationContext());
		} else {
			componentContainerQueryCache.put(queryPair, Lists.newArrayList(ctx.getAssemblyContext_AllocationContext()));
		}
	}

	private void removeAllocationContext(AllocationContext ctx) {
		pcmModelProvider.getAllocation().getAllocationContexts_Allocation().remove(ctx);

		allocationContextIdCache.remove(ctx.getId());
		assemblyDeploymentCache.remove(ctx.getAssemblyContext_AllocationContext().getId());
		componentContainerQueryCache.get(generateQueryPair(ctx)).remove(ctx.getAssemblyContext_AllocationContext());

		if (hasDeploymentCache.containsKey(ctx.getResourceContainer_AllocationContext().getId())) {
			int oldValue = hasDeploymentCache.get(ctx.getResourceContainer_AllocationContext().getId());
			if (oldValue - 1 <= 0) {
				hasDeploymentCache.remove(ctx.getResourceContainer_AllocationContext().getId());
			} else {
				hasDeploymentCache.put(ctx.getResourceContainer_AllocationContext().getId(), oldValue - 1);
			}
		}
	}

	private Pair<String, String> generateQueryPair(AllocationContext ctx) {
		return Pair.of(ctx.getAssemblyContext_AllocationContext().getEncapsulatedComponent__AssemblyContext().getId(),
				ctx.getResourceContainer_AllocationContext().getId());
	}

}
