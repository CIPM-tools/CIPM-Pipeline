package dmodel.pipeline.core.facade.pcm.impl;

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
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.Signature;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.uka.ipd.sdq.identifier.Identifier;
import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.core.facade.pcm.IAllocationQueryFacade;
import dmodel.pipeline.core.facade.pcm.ISystemQueryFacade;
import dmodel.pipeline.shared.pcm.util.deprecation.IDeprecationProcessor;
import dmodel.pipeline.shared.pcm.util.system.PCMSystemUtil;
import dmodel.pipeline.shared.structure.DirectedGraph;

public class SystemQueryFacadeImpl implements ISystemQueryFacade {
	@Autowired
	private IPcmModelProvider pcmModelProvider;

	@Autowired
	private IAllocationQueryFacade allocationQuery;

	private Set<String> assemblyIdsCache = Sets.newHashSet();

	private Map<String, Identifier> idElementCache = Maps.newHashMap();

	private List<AssemblyConnector> assemblyConnectors = Lists.newArrayList();
	private List<ProvidedDelegationConnector> providedDelegationConnectors = Lists.newArrayList();
	private List<RequiredDelegationConnector> requiredDelegationConnectors = Lists.newArrayList();

	private List<OperationProvidedRole> systemProvidedRoles = Lists.newArrayList();

	private Set<OperationRequiredRole> openOuterRequiredRoles = Sets.newHashSet();
	private Set<Pair<AssemblyContext, OperationRequiredRole>> openInnerRequiredRoles = Sets.newHashSet();

	@Override
	public void reset(boolean hard) {
		clearCaches();
		buildCaches();
	}

	@Override
	public String getId() {
		return pcmModelProvider.getSystem().getId();
	}

	@Override
	public Set<String> getAssemblyIds() {
		return assemblyIdsCache;
	}

	@Override
	public Collection<AssemblyConnector> getAssemblyConnectors() {
		return assemblyConnectors;
	}

	@Override
	public Collection<AssemblyContext> getAssemblyContexts() {
		return pcmModelProvider.getSystem().getAssemblyContexts__ComposedStructure();
	}

	@Override
	public Collection<ProvidedDelegationConnector> getProvidedDelegationConnectors() {
		return providedDelegationConnectors;
	}

	@Override
	public Collection<RequiredDelegationConnector> getRequiredDelegationConnectors() {
		return requiredDelegationConnectors;
	}

	@Override
	public List<OperationInterface> getProvidedInterfaces() {
		return systemProvidedRoles.stream().map(r -> r.getProvidedInterface__OperationProvidedRole())
				.collect(Collectors.toList());
	}

	@Override
	public List<OperationProvidedRole> getProvidedRoles() {
		return systemProvidedRoles;
	}

	@Override
	public List<OperationProvidedRole> getProvidedRoleBySignature(Signature describedService__SEFF) {
		return getProvidedRoleBySignature(describedService__SEFF, pcmModelProvider.getSystem());
	}

	@Override
	public List<OperationProvidedRole> getProvidedRoleBySignature(Signature describedService__SEFF,
			InterfaceProvidingEntity entity) {
		return entity
				.getProvidedRoles_InterfaceProvidingEntity().stream().filter(r -> r instanceof OperationProvidedRole)
				.map(OperationProvidedRole.class::cast).filter(opr -> opr.getProvidedInterface__OperationProvidedRole()
						.getSignatures__OperationInterface().contains(describedService__SEFF))
				.collect(Collectors.toList());
	}

	@Override
	public AssemblyContext getAssemblyById(String assembly) {
		return (AssemblyContext) idElementCache.get(assembly);
	}

	@Override
	public AssemblyContext createAssemblyContext(RepositoryComponent component) {
		long timestamp = System.currentTimeMillis() / 1000;
		String name = "Assembly[" + component.getEntityName() + "][" + String.valueOf(timestamp) + "]";
		AssemblyContext nCtx = PCMSystemUtil.createAssemblyContext(pcmModelProvider.getSystem(), component, name);
		cacheAssembly(nCtx);
		return nCtx;
	}

	@Override
	public void removeConnectors(List<Connector> connectors) {
		for (Connector connector : connectors) {
			removeConnector(connector);
		}
	}

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

	@Override
	public AssemblyConnector createConnector(AssemblyContext correspondingACtx, OperationRequiredRole requiredRole,
			AssemblyContext correspondingACtxTarget, OperationProvidedRole correspondingProvidedRole) {
		AssemblyConnector nConnector = PCMSystemUtil.createAssemblyConnector(pcmModelProvider.getSystem(),
				correspondingProvidedRole, correspondingACtxTarget, requiredRole, correspondingACtx);
		cacheConnector(nConnector);
		return nConnector;
	}

	@Override
	public void reconnectOuterProvidedRole(OperationProvidedRole systemProvidedRole, AssemblyContext ctx,
			OperationProvidedRole role) {
		// delete old
		providedDelegationConnectors.stream().filter(conn -> conn.getOuterProvidedRole_ProvidedDelegationConnector()
				.getId().equals(systemProvidedRole.getId())).findFirst().ifPresent(this::removeConnector);

		// create new & cache
		ProvidedDelegationConnector nConnector = PCMSystemUtil.createProvidedDelegation(pcmModelProvider.getSystem(),
				systemProvidedRole, ctx, role);
		cacheConnector(nConnector);
	}

	@Override
	public void reconnectOuterRequiredRole(OperationRequiredRole selectedOuter, AssemblyContext left,
			OperationRequiredRole right) {
		// delete old
		requiredDelegationConnectors.stream()
				.filter(conn -> conn.getOuterRequiredRole_RequiredDelegationConnector().equals(selectedOuter))
				.findFirst().ifPresent(this::removeConnector);

		// create new & cache
		RequiredDelegationConnector nConnector = PCMSystemUtil.createRequiredDelegation(left, right,
				pcmModelProvider.getSystem(), selectedOuter);
		cacheConnector(nConnector);
	}

	@Override
	public List<Pair<AssemblyContext, OperationRequiredRole>> getUnsatisfiedInnerRequiredRoles() {
		return new ArrayList<>(openInnerRequiredRoles);
	}

	@Override
	public List<OperationRequiredRole> getNonLinkedOuterRequiredRoles() {
		return new ArrayList<>(openOuterRequiredRoles);
	}

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

	private boolean requiringEntityEqual(AssemblyConnector c, AssemblyConnector connector) {
		return c.getRequiredRole_AssemblyConnector().getId()
				.equals(connector.getRequiredRole_AssemblyConnector().getId())
				&& c.getRequiringAssemblyContext_AssemblyConnector().getId()
						.equals(connector.getRequiringAssemblyContext_AssemblyConnector().getId());
	}

	private boolean providingEntityEqual(AssemblyConnector c, AssemblyConnector connector) {
		return c.getProvidedRole_AssemblyConnector().getId()
				.equals(connector.getProvidedRole_AssemblyConnector().getId())
				&& c.getProvidingAssemblyContext_AssemblyConnector().getId()
						.equals(connector.getRequiringAssemblyContext_AssemblyConnector().getId());
	}

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

	private void clearCaches() {
		assemblyIdsCache.clear();
		idElementCache.clear();

		assemblyConnectors.clear();
		providedDelegationConnectors.clear();
		requiredDelegationConnectors.clear();
		systemProvidedRoles.clear();

		openOuterRequiredRoles.clear();
		openInnerRequiredRoles.clear();
	}

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

	private void cacheConnector(Connector connector) {
		if (connector instanceof AssemblyConnector) {
			AssemblyConnector cConnector = (AssemblyConnector) connector;

			openInnerRequiredRoles.remove(Pair.of(cConnector.getRequiringAssemblyContext_AssemblyConnector(),
					cConnector.getRequiredRole_AssemblyConnector()));

			assemblyConnectors.add(cConnector);
		} else if (connector instanceof ProvidedDelegationConnector) {
			providedDelegationConnectors.add((ProvidedDelegationConnector) connector);
		} else if (connector instanceof RequiredDelegationConnector) {
			RequiredDelegationConnector cConnector = (RequiredDelegationConnector) connector;
			requiredDelegationConnectors.add(cConnector);

			openOuterRequiredRoles.remove(cConnector.getOuterRequiredRole_RequiredDelegationConnector());
			openInnerRequiredRoles.remove(Pair.of(cConnector.getAssemblyContext_RequiredDelegationConnector(),
					cConnector.getInnerRequiredRole_RequiredDelegationConnector()));
		}
	}

	private void cacheAssembly(AssemblyContext actx) {
		assemblyIdsCache.add(actx.getId());
		idElementCache.put(actx.getId(), actx);

		actx.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().stream()
				.filter(r -> r instanceof OperationRequiredRole).map(OperationRequiredRole.class::cast)
				.map(r -> Pair.of(actx, r)).forEach(openInnerRequiredRoles::add);
	}

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
		}

		pcmModelProvider.getSystem().getConnectors__ComposedStructure().remove(connector);
	}

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
				.forEach(openInnerRequiredRoles::remove);

		// undeploy this one
		allocationQuery.undeployAssembly(ctx);
	}

}
