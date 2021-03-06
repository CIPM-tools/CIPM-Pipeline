package cipm.consistency.base.core.facade.pcm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingEntity;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.core.facade.pcm.IAllocationQueryFacade;
import cipm.consistency.base.core.facade.pcm.ISystemQueryFacade;
import cipm.consistency.base.shared.pcm.util.deprecation.IDeprecationProcessor;
import cipm.consistency.base.shared.pcm.util.system.PCMSystemUtil;
import cipm.consistency.base.shared.structure.DirectedGraph;
import de.uka.ipd.sdq.identifier.Identifier;
import lombok.Setter;

/**
 * Implementation of the {@link ISystemQueryFacade} interface. Uses a lot of
 * caches to guarantee fast accesses.
 * 
 * @author David Monschein
 *
 */
@Component
public class SystemQueryFacadeImpl implements ISystemQueryFacade {
	/**
	 * Provider of the underlying models.
	 */
	@Autowired
	@Setter
	private IPcmModelProvider pcmModelProvider;

	/**
	 * Facade for accessing the allocation model.
	 */
	@Autowired
	@Setter
	private IAllocationQueryFacade allocationQuery;

	/**
	 * Set of IDs of the assembly contexts within the system model.
	 */
	private Set<String> assemblyIdsCache = Sets.newHashSet();

	/**
	 * Mapping of ID string to elements in the system model.
	 */
	private Map<String, Identifier> idElementCache = Maps.newHashMap();

	/**
	 * A list of all assembly connectors in the system model.
	 */
	private List<AssemblyConnector> assemblyConnectors = Lists.newArrayList();

	/**
	 * A list of all provided delegation connectors in the system model.
	 */
	private List<ProvidedDelegationConnector> providedDelegationConnectors = Lists.newArrayList();

	/**
	 * A list of all required delegation connectors in the system model.
	 */
	private List<RequiredDelegationConnector> requiredDelegationConnectors = Lists.newArrayList();

	/**
	 * A list of all provided roles of the enclosing system.
	 */
	private List<OperationProvidedRole> systemProvidedRoles = Lists.newArrayList();

	/**
	 * All required roles of the system that are not linked yet.
	 */
	private Set<OperationRequiredRole> openOuterRequiredRoles = Sets.newHashSet();

	/**
	 * All provided roles of the system that are not linked yet.
	 */
	private Set<OperationProvidedRole> openOuterProvidedRoles = Sets.newHashSet();

	/**
	 * Caches the mapping between service IDs and whether they are entry calls.
	 */
	private Map<String, Boolean> isEntryCallIdCache = Maps.newHashMap();

	/**
	 * Set of pairs (assembly context, required role) of required roles that are not
	 * satisfied yet.
	 */
	private Set<Pair<AssemblyContext, OperationRequiredRole>> openInnerRequiredRoles = Sets.newHashSet();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset(boolean hard) {
		clearCaches();
		buildCaches();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return pcmModelProvider.getSystem().getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getAssemblyIds() {
		return assemblyIdsCache;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<AssemblyConnector> getAssemblyConnectors() {
		return assemblyConnectors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<AssemblyContext> getAssemblyContexts() {
		return pcmModelProvider.getSystem().getAssemblyContexts__ComposedStructure();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<ProvidedDelegationConnector> getProvidedDelegationConnectors() {
		return providedDelegationConnectors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<RequiredDelegationConnector> getRequiredDelegationConnectors() {
		return requiredDelegationConnectors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OperationInterface> getProvidedInterfaces() {
		return systemProvidedRoles.stream().map(r -> r.getProvidedInterface__OperationProvidedRole())
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OperationProvidedRole> getProvidedRoles() {
		return systemProvidedRoles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OperationProvidedRole> getProvidedRoleBySignature(Signature describedService__SEFF) {
		return getProvidedRoleBySignature(describedService__SEFF, pcmModelProvider.getSystem());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OperationProvidedRole> getProvidedRoleBySignature(Signature describedService__SEFF,
			InterfaceProvidingEntity entity) {
		return entity.getProvidedRoles_InterfaceProvidingEntity().stream()
				.filter(r -> r instanceof OperationProvidedRole).map(OperationProvidedRole.class::cast)
				.filter(opr -> opr.getProvidedInterface__OperationProvidedRole().getSignatures__OperationInterface()
						.stream().anyMatch(sig -> sig.getId().equals(describedService__SEFF.getId())))
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AssemblyContext getAssemblyById(String assembly) {
		return (AssemblyContext) idElementCache.get(assembly);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized AssemblyContext createAssemblyContext(RepositoryComponent component) {
		long timestamp = System.currentTimeMillis() / 1000;
		String name = "Assembly-" + component.getEntityName() + "-" + String.valueOf(timestamp);
		String staticNameCopy = name;

		long count = idElementCache.entrySet().stream().filter(f -> f.getValue() instanceof AssemblyContext)
				.map(e -> e.getValue()).map(AssemblyContext.class::cast)
				.filter(ctx -> ctx.getEntityName().equals(staticNameCopy)).count();

		if (count > 0) {
			long nId = idElementCache.entrySet().stream().filter(f -> f.getValue() instanceof AssemblyContext)
					.map(e -> e.getValue()).map(AssemblyContext.class::cast)
					.filter(ctx -> ctx.getEncapsulatedComponent__AssemblyContext().getId().equals(component.getId()))
					.count();

			name += "-" + String.valueOf(nId + 1);
		}

		AssemblyContext nCtx = PCMSystemUtil.createAssemblyContext(pcmModelProvider.getSystem(), component, name);
		cacheAssembly(nCtx);
		return nCtx;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeConnectors(List<Connector> connectors) {
		for (Connector connector : connectors) {
			removeConnector(connector);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasConnector(AssemblyContext correspondingACtx, OperationRequiredRole requiredRole,
			AssemblyContext correspondingACtxTarget, OperationProvidedRole correspondingProvidedRole) {
		return assemblyConnectors.stream().anyMatch(
				cn -> cn.getRequiringAssemblyContext_AssemblyConnector().getId().equals(correspondingACtx.getId())
						&& cn.getRequiredRole_AssemblyConnector().getId().equals(requiredRole.getId())
						&& cn.getProvidingAssemblyContext_AssemblyConnector().getId()
								.equals(correspondingACtxTarget.getId())
						&& cn.getProvidedRole_AssemblyConnector().getId().equals(correspondingProvidedRole.getId()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasConnector(OperationProvidedRole outerProvidedRole, AssemblyContext ctx,
			OperationProvidedRole innerProvidedRole) {
		return providedDelegationConnectors.stream().anyMatch(c -> c.getOuterProvidedRole_ProvidedDelegationConnector()
				.getId().equals(outerProvidedRole.getId())
				&& c.getAssemblyContext_ProvidedDelegationConnector().getId().equals(ctx.getId())
				&& c.getInnerProvidedRole_ProvidedDelegationConnector().getId().equals(innerProvidedRole.getId()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasConnector(OperationRequiredRole outerRequiredRole, AssemblyContext ctx,
			OperationRequiredRole innerRequiredRole) {
		return requiredDelegationConnectors.stream().anyMatch(c -> c.getOuterRequiredRole_RequiredDelegationConnector()
				.getId().equals(outerRequiredRole.getId())
				&& c.getAssemblyContext_RequiredDelegationConnector().getId().equals(ctx.getId())
				&& c.getInnerRequiredRole_RequiredDelegationConnector().getId().equals(innerRequiredRole.getId()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AssemblyConnector createConnector(AssemblyContext correspondingACtx, OperationRequiredRole requiredRole,
			AssemblyContext correspondingACtxTarget, OperationProvidedRole correspondingProvidedRole) {
		AssemblyConnector nConnector = PCMSystemUtil.createAssemblyConnector(pcmModelProvider.getSystem(),
				correspondingProvidedRole, correspondingACtxTarget, requiredRole, correspondingACtx);
		cacheConnector(nConnector);
		return nConnector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reconnectOuterProvidedRole(OperationProvidedRole systemProvidedRole, AssemblyContext ctx,
			OperationProvidedRole role) {
		if (!hasConnector(systemProvidedRole, ctx, role)) {
			// delete old
			providedDelegationConnectors.stream().filter(conn -> conn.getOuterProvidedRole_ProvidedDelegationConnector()
					.getId().equals(systemProvidedRole.getId())).findFirst().ifPresent(this::removeConnector);

			// create new & cache
			ProvidedDelegationConnector nConnector = PCMSystemUtil
					.createProvidedDelegation(pcmModelProvider.getSystem(), systemProvidedRole, ctx, role);
			cacheConnector(nConnector);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reconnectOuterRequiredRole(OperationRequiredRole selectedOuter, AssemblyContext left,
			OperationRequiredRole right) {
		if (!hasConnector(selectedOuter, left, right)) {
			// delete old
			requiredDelegationConnectors.stream()
					.filter(conn -> conn.getOuterRequiredRole_RequiredDelegationConnector().equals(selectedOuter))
					.findFirst().ifPresent(this::removeConnector);

			// create new & cache
			RequiredDelegationConnector nConnector = PCMSystemUtil.createRequiredDelegation(left, right,
					pcmModelProvider.getSystem(), selectedOuter);
			cacheConnector(nConnector);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Pair<AssemblyContext, OperationRequiredRole>> getUnsatisfiedInnerRequiredRoles() {
		return new ArrayList<>(openInnerRequiredRoles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OperationProvidedRole> getUnsatisfiedOuterProvidedRoles() {
		return new ArrayList<>(openOuterProvidedRoles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OperationRequiredRole> getNonLinkedOuterRequiredRoles() {
		return new ArrayList<>(openOuterRequiredRoles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeInconsistentConnectors(AssemblyConnector connector) {
		List<Connector> inconsistentConnectors = Lists.newArrayList();

		assemblyConnectors.stream().filter(
				c -> !c.equals(connector) && (providingEntityEqual(c, connector) || requiringEntityEqual(c, connector)))
				.forEach(inconsistentConnectors::add);

		requiredDelegationConnectors.stream()
				.filter(c -> c.getAssemblyContext_RequiredDelegationConnector()
						.equals(connector.getRequiringAssemblyContext_AssemblyConnector())
						&& c.getInnerRequiredRole_RequiredDelegationConnector()
								.equals(connector.getRequiredRole_AssemblyConnector()))
				.forEach(inconsistentConnectors::add);

		providedDelegationConnectors.stream()
				.filter(c -> c.getAssemblyContext_ProvidedDelegationConnector()
						.equals(connector.getProvidingAssemblyContext_AssemblyConnector())
						&& c.getInnerProvidedRole_ProvidedDelegationConnector()
								.equals(connector.getProvidedRole_AssemblyConnector()))
				.forEach(inconsistentConnectors::add);

		for (Connector conn : inconsistentConnectors) {
			removeConnector(conn);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processUnreachableAssemblys(IDeprecationProcessor deprecationProcessor) {
		DirectedGraph<String, Integer> assemblyInvocationGraph = new DirectedGraph<>();
		assemblyConnectors.forEach(
				conn -> assemblyInvocationGraph.addEdge(conn.getRequiringAssemblyContext_AssemblyConnector().getId(),
						conn.getProvidingAssemblyContext_AssemblyConnector().getId(), 0));
		providedDelegationConnectors.forEach(conn -> assemblyInvocationGraph.addEdge(getId(),
				conn.getAssemblyContext_ProvidedDelegationConnector().getId(), 0));
		requiredDelegationConnectors.forEach(conn -> assemblyInvocationGraph
				.addEdge(conn.getAssemblyContext_RequiredDelegationConnector().getId(), getId(), 0));

		Set<String> marked = getReachableNodes(assemblyInvocationGraph);

		// process all assembly context that are not needed anymore
		List<AssemblyContext> deprecatedAssemblys = getAssemblyContexts().stream().filter(ac -> {
			return !marked.contains(ac.getId());
		}).collect(Collectors.toList());

		deprecatedAssemblys.forEach(da -> {
			if (deprecationProcessor.shouldDelete(da)) {
				removeAssembly(da);
			}
		});
		deprecationProcessor.iterationFinished();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEntryCall(ResourceDemandingSEFF seff) {
		if (isEntryCallIdCache.containsKey(seff.getId())) {
			return isEntryCallIdCache.get(seff.getId());
		} else {
			for (OperationInterface pi : getProvidedInterfaces()) {
				for (OperationSignature sig : pi.getSignatures__OperationInterface()) {
					if (sig.getId().equals(seff.getDescribedService__SEFF().getId())) {
						isEntryCallIdCache.put(seff.getId(), true);
						return true;
					}
				}
			}
			isEntryCallIdCache.put(seff.getId(), false);
			return false;
		}
	}

	/**
	 * Determines whether the requiring entity of two assembly connectors are equal,
	 * based on their IDs.
	 * 
	 * @param c         the first connector
	 * @param connector the second connector
	 * @return true, if the requiring entity of two assembly connectors are equal
	 *         (according to their IDs), false otherwise
	 */
	private boolean requiringEntityEqual(AssemblyConnector c, AssemblyConnector connector) {
		return c.getRequiredRole_AssemblyConnector().getId()
				.equals(connector.getRequiredRole_AssemblyConnector().getId())
				&& c.getRequiringAssemblyContext_AssemblyConnector().getId()
						.equals(connector.getRequiringAssemblyContext_AssemblyConnector().getId());
	}

	/**
	 * Determines whether the providing entity of two assembly connectors are equal,
	 * based on their IDs.
	 * 
	 * @param c         the first connector
	 * @param connector the second connector
	 * @return true, if the providing entity of two assembly connectors are equal
	 *         (according to their IDs), false otherwise
	 */
	private boolean providingEntityEqual(AssemblyConnector c, AssemblyConnector connector) {
		return c.getProvidedRole_AssemblyConnector().getId()
				.equals(connector.getProvidedRole_AssemblyConnector().getId())
				&& c.getProvidingAssemblyContext_AssemblyConnector().getId()
						.equals(connector.getRequiringAssemblyContext_AssemblyConnector().getId());
	}

	/**
	 * Determines all nodes that are reachable within a graph. This enables the
	 * finding of unused assembly contexts within the system.
	 * 
	 * @param assemblyInvocationGraph a graph that contains the IDs of the assembly
	 *                                contexts as nodes and the edges represent that
	 *                                two nodes are connected with a
	 *                                {@link Connector}.
	 * @return set of IDs of all reachable assembly contexts
	 */
	private Set<String> getReachableNodes(DirectedGraph<String, Integer> assemblyInvocationGraph) {
		Set<String> marked = Sets.newHashSet();
		// create transitive closure
		Stack<String> currentNodes = new Stack<>();
		currentNodes.push(getId());
		while (currentNodes.size() > 0) {
			String node = currentNodes.pop();
			marked.add(node);

			List<Pair<String, Integer>> outgoingEdges = assemblyInvocationGraph.getOutgoingEdges(node);
			if (outgoingEdges != null) {
				for (Pair<String, Integer> child : assemblyInvocationGraph.getOutgoingEdges(node)) {
					if (!marked.contains(child.getLeft())) {
						currentNodes.add(child.getLeft());
					}
				}
			}
		}
		return marked;
	}

	/**
	 * Clears all caches within this facade.
	 */
	private void clearCaches() {
		assemblyIdsCache.clear();
		idElementCache.clear();

		assemblyConnectors.clear();
		providedDelegationConnectors.clear();
		requiredDelegationConnectors.clear();
		systemProvidedRoles.clear();

		openOuterRequiredRoles.clear();
		openInnerRequiredRoles.clear();
		isEntryCallIdCache.clear();
	}

	/**
	 * Rebuilds all caches within this facade.
	 */
	private void buildCaches() {
		// process system as it
		pcmModelProvider.getSystem().getProvidedRoles_InterfaceProvidingEntity().stream()
				.filter(p -> p instanceof OperationProvidedRole).map(OperationProvidedRole.class::cast)
				.forEach(systemProvidedRoles::add);

		pcmModelProvider.getSystem().getRequiredRoles_InterfaceRequiringEntity().stream()
				.filter(p -> p instanceof OperationRequiredRole).map(OperationRequiredRole.class::cast)
				.forEach(this.openOuterRequiredRoles::add);

		// process assemblies
		for (AssemblyContext actx : pcmModelProvider.getSystem().getAssemblyContexts__ComposedStructure()) {
			cacheAssembly(actx);
		}

		// process connectors
		for (Connector connector : pcmModelProvider.getSystem().getConnectors__ComposedStructure()) {
			cacheConnector(connector);
		}
	}

	/**
	 * Introduces a new connector into the facade internal caches.
	 * 
	 * @param connector the connector that should be cached
	 */
	private void cacheConnector(Connector connector) {
		if (connector instanceof AssemblyConnector) {
			AssemblyConnector cConnector = (AssemblyConnector) connector;

			openInnerRequiredRoles.remove(Pair.of(cConnector.getRequiringAssemblyContext_AssemblyConnector(),
					cConnector.getRequiredRole_AssemblyConnector()));

			assemblyConnectors.add(cConnector);
		} else if (connector instanceof ProvidedDelegationConnector) {
			ProvidedDelegationConnector cConnector = (ProvidedDelegationConnector) connector;
			providedDelegationConnectors.add(cConnector);
			openOuterProvidedRoles.remove(cConnector.getOuterProvidedRole_ProvidedDelegationConnector());
		} else if (connector instanceof RequiredDelegationConnector) {
			RequiredDelegationConnector cConnector = (RequiredDelegationConnector) connector;
			requiredDelegationConnectors.add(cConnector);

			openOuterRequiredRoles.remove(cConnector.getOuterRequiredRole_RequiredDelegationConnector());
			openInnerRequiredRoles.remove(Pair.of(cConnector.getAssemblyContext_RequiredDelegationConnector(),
					cConnector.getInnerRequiredRole_RequiredDelegationConnector()));
		}
	}

	/**
	 * Introduces a new assembly context into the facade internal caches.
	 * 
	 * @param actx the assembly context that should be cached
	 */
	private void cacheAssembly(AssemblyContext actx) {
		assemblyIdsCache.add(actx.getId());
		idElementCache.put(actx.getId(), actx);

		actx.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().stream()
				.filter(r -> r instanceof OperationRequiredRole).map(OperationRequiredRole.class::cast)
				.map(r -> Pair.of(actx, r)).forEach(openInnerRequiredRoles::add);
	}

	/**
	 * Removes a connector from the internal caches.
	 * 
	 * @param connector the connector that should be removed from all internal
	 *                  caches
	 */
	private void removeConnector(Connector connector) {
		assemblyConnectors.remove(connector);
		requiredDelegationConnectors.remove(connector);
		providedDelegationConnectors.remove(connector);

		if (connector instanceof AssemblyConnector) {
			AssemblyConnector cConnector = (AssemblyConnector) connector;
			openInnerRequiredRoles.add(Pair.of(cConnector.getRequiringAssemblyContext_AssemblyConnector(),
					cConnector.getRequiredRole_AssemblyConnector()));
		} else if (connector instanceof RequiredDelegationConnector) {
			RequiredDelegationConnector cConnector = (RequiredDelegationConnector) connector;
			openOuterRequiredRoles.add(cConnector.getOuterRequiredRole_RequiredDelegationConnector());
			openInnerRequiredRoles.add(Pair.of(cConnector.getAssemblyContext_RequiredDelegationConnector(),
					cConnector.getInnerRequiredRole_RequiredDelegationConnector()));
		} else if (connector instanceof ProvidedDelegationConnector) {
			ProvidedDelegationConnector cConnector = (ProvidedDelegationConnector) connector;
			openOuterProvidedRoles.add(cConnector.getOuterProvidedRole_ProvidedDelegationConnector());
		}

		pcmModelProvider.getSystem().getConnectors__ComposedStructure().remove(connector);
	}

	/**
	 * Removes an assembly context from the internal caches.
	 * 
	 * @param ctx assembly context that should be removed from all internal caches.
	 */
	private void removeAssembly(AssemblyContext ctx) {
		assemblyIdsCache.remove(ctx.getId());
		idElementCache.remove(ctx.getId());

		pcmModelProvider.getSystem().getAssemblyContexts__ComposedStructure().remove(ctx);

		// delete corresponding connectors
		List<Connector> toDelete = Lists.newArrayList();
		providedDelegationConnectors.stream()
				.filter(c -> c.getAssemblyContext_ProvidedDelegationConnector().getId().equals(ctx.getId()))
				.forEach(toDelete::add);
		requiredDelegationConnectors.stream()
				.filter(c -> c.getAssemblyContext_RequiredDelegationConnector().getId().equals(ctx.getId()))
				.forEach(toDelete::add);
		assemblyConnectors.stream()
				.filter(c -> c.getProvidingAssemblyContext_AssemblyConnector().getId().equals(ctx.getId())
						|| c.getRequiringAssemblyContext_AssemblyConnector().getId().equals(ctx.getId()))
				.forEach(toDelete::add);

		for (Connector conn : toDelete) {
			removeConnector(conn);
		}

		// delete corresponding roles
		openInnerRequiredRoles.stream().filter(oir -> oir.getLeft().getId().equals(ctx.getId()))
				.collect(Collectors.toList()).forEach(openInnerRequiredRoles::remove);

		// undeploy this one
		allocationQuery.undeployAssembly(ctx);
	}

}
