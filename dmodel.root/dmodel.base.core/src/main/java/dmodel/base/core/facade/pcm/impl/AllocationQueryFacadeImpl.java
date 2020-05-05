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

/**
 * Implementation of the {@link IAllocationQueryFacade} interface. Uses caching
 * mechanisms to speedup the accesses to model elements.
 * 
 * @author David Monschein
 *
 */
@Component
public class AllocationQueryFacadeImpl implements IAllocationQueryFacade {
	/**
	 * Provider for the underlying models.
	 */
	@Autowired
	private IPcmModelProvider pcmModelProvider;

	/**
	 * Cache of the mapping between ID -> allocation context.
	 */
	private Cache<String, AllocationContext> allocationContextIdCache = new Cache2kBuilder<String, AllocationContext>() {
	}.eternal(true).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	/**
	 * Caching between assembly context ID -> allocation context.
	 */
	private Cache<String, AllocationContext> assemblyDeploymentCache = new Cache2kBuilder<String, AllocationContext>() {
	}.eternal(true).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	/**
	 * Caches whether a specific assembly context with a given ID is deployed.
	 */
	private Map<String, Integer> hasDeploymentCache = Maps.newHashMap();

	// this cache is used very often so it is designed to be high-performance
	/**
	 * Mapping between pair (component ID, container ID) -> assembly context.
	 */
	private Map<Pair<String, String>, List<AssemblyContext>> componentContainerQueryCache = Maps.newHashMap();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deployAssembly(AssemblyContext ret, ResourceContainer host) {
		AllocationContext nAllocation = AllocationFactory.eINSTANCE.createAllocationContext();
		nAllocation.setAssemblyContext_AllocationContext(ret);
		nAllocation.setResourceContainer_AllocationContext(host);
		nAllocation.setEntityName(ret.getEntityName() + " -> " + host.getEntityName());

		pcmModelProvider.getAllocation().getAllocationContexts_Allocation().add(nAllocation);

		importAllocationContext(nAllocation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undeployAssembly(AssemblyContext ctx) {
		if (assemblyDeploymentCache.containsKey(ctx.getId())) {
			this.removeAllocationContext(assemblyDeploymentCache.get(ctx.getId()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDeployed(AssemblyContext ac) {
		return assemblyDeploymentCache.containsKey(ac.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResourceContainer getContainerByAssembly(AssemblyContext asCtx) {
		if (assemblyDeploymentCache.containsKey(asCtx.getId())) {
			return assemblyDeploymentCache.get(asCtx.getId()).getResourceContainer_AllocationContext();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AssemblyContext> getDeployedAssembly(BasicComponent componentType, ResourceContainer container) {
		Pair<String, String> queryPair = Pair.of(componentType.getId(), container.getId());
		if (componentContainerQueryCache.containsKey(queryPair)) {
			return componentContainerQueryCache.get(queryPair);
		} else {
			return Lists.newArrayList();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDeployments(ResourceContainer presentContainer) {
		return hasDeploymentCache.containsKey(presentContainer.getId())
				? hasDeploymentCache.get(presentContainer.getId()) > 0
				: false;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * Imports an allocation context into this facade and introduces it to all
	 * internal caches.
	 * 
	 * @param ctx the allocation context that should be imported
	 */
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

	/**
	 * Removes an allocation context from the underlying model and updates all
	 * caches.
	 * 
	 * @param ctx the allocation context that should be removed
	 */
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

	/**
	 * Generates a pair (component ID, container ID) for a given allocation context.
	 * 
	 * @param ctx the allocation context
	 * @return the generated pair (component ID, container ID)
	 */
	private Pair<String, String> generateQueryPair(AllocationContext ctx) {
		return Pair.of(ctx.getAssemblyContext_AllocationContext().getEncapsulatedComponent__AssemblyContext().getId(),
				ctx.getResourceContainer_AllocationContext().getId());
	}

}
