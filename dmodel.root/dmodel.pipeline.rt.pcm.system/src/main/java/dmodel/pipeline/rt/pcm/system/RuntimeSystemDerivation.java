package dmodel.pipeline.rt.pcm.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import com.beust.jcommander.internal.Lists;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphFactory;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
import dmodel.pipeline.shared.structure.Tree.TreeNode;
import lombok.extern.java.Log;

// TODO refactor and outsource
@Log
public class RuntimeSystemDerivation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	// caches
	private Cache<String, ResourceDemandingSEFF> cache = new Cache2kBuilder<String, ResourceDemandingSEFF>() {
	}.expireAfterWrite(5, TimeUnit.MINUTES).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(true).build();

	private Cache<String, ResourceContainer> cacheResEnv = new Cache2kBuilder<String, ResourceContainer>() {
	}.expireAfterWrite(5, TimeUnit.MINUTES).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(true).build();

	// references
	private RuntimeSystemBuilder runtimeSystemBuilder;
	private Map<Pair<String, String>, AssemblyContext> creationCache;

	public RuntimeSystemDerivation() {
		this.runtimeSystemBuilder = new RuntimeSystemBuilder();
		this.creationCache = new HashMap<>();
	}

	@InputPorts({ @InputPort(PortIDs.T_PCM_SYSTEM) })
	public void deriveSystemData(List<Tree<ServiceCallRecord>> entryCalls) {
		log.info("Deriving system refinements at runtime.");
		creationCache.clear();

		ServiceCallGraph runtimeGraph = buildGraphFromMonitoringData(entryCalls);
		List<Tree<Pair<AssemblyContext, ResourceDemandingSEFF>>> assemblyTrees = transformCallGraph(runtimeGraph);
	}

	private List<Tree<Pair<AssemblyContext, ResourceDemandingSEFF>>> transformCallGraph(ServiceCallGraph callGraph) {
		List<Tree<Pair<AssemblyContext, ResourceDemandingSEFF>>> trees = Lists.newArrayList();
		callGraph.getNodes().forEach(n -> {
			if (callGraph.getIncomingEdges().get(n) == null) {
				// root node
				Tree<Pair<AssemblyContext, ResourceDemandingSEFF>> nTree = new Tree<>(
						Pair.of(resolveOrCreateAssembly(n), n.getSeff()));
				transformCallGraphRecursive(nTree.getRoot(), callGraph.getOutgoingEdges().get(n), callGraph);
				trees.add(nTree);
			}
		});
		return trees;
	}

	private void transformCallGraphRecursive(TreeNode<Pair<AssemblyContext, ResourceDemandingSEFF>> parent,
			EList<ServiceCallGraphEdge> eList, ServiceCallGraph graph) {
		if (eList == null) {
			return;
		}
		eList.forEach(e -> {
			TreeNode<Pair<AssemblyContext, ResourceDemandingSEFF>> nNode = parent
					.addChildren(Pair.of(resolveOrCreateAssembly(e.getTo()), e.getTo().getSeff()));
			transformCallGraphRecursive(nNode, graph.getOutgoingEdges().get(e.getTo()), graph);
		});
	}

	private AssemblyContext resolveOrCreateAssembly(ServiceCallGraphNode rec) {
		AssemblyContext ctx = resolveCorrespondingAssembly(rec);
		if (ctx != null) {
			return ctx;
		} else {
			// build it
			AssemblyContext ret = CompositionFactory.eINSTANCE.createAssemblyContext();
			ret.setEncapsulatedComponent__AssemblyContext(rec.getSeff().getBasicComponent_ServiceEffectSpecification());
			creationCache.put(Pair.of(rec.getHost().getId(),
					rec.getSeff().getBasicComponent_ServiceEffectSpecification().getId()), ret);

			return ret;
		}
	}

	private AssemblyContext resolveCorrespondingAssembly(ServiceCallGraphNode data) {
		ResourceContainer belongingContainer = data.getHost();
		BasicComponent belComponent = data.getSeff().getBasicComponent_ServiceEffectSpecification();

		Pair<String, String> resolvingPair = Pair.of(belongingContainer.getId(), belComponent.getId());
		if (creationCache.containsKey(resolvingPair)) {
			return creationCache.get(resolvingPair);
		}

		if (belongingContainer != null && belComponent != null) {
			return resolveAssemblyOnContainer(belComponent, belongingContainer);
		} else {
			log.warning("Failed to resolve corresponding assembly for service ID '" + data.getSeff().getId()
					+ "' and host name '" + data.getHost().getEntityName() + "'.");
		}

		return null;
	}

	private AssemblyContext resolveAssemblyOnContainer(BasicComponent belComponent,
			ResourceContainer belongingContainer) {
		// here we use the assumption that only one assembly of one component can be
		// deployed on the same container
		List<AssemblyContext> matches = PCMUtils
				.getElementsByType(getBlackboard().getArchitectureModel().getAllocationModel(), AllocationContext.class)
				.stream().filter(a -> {
					return a.getResourceContainer_AllocationContext().getId().equals(belongingContainer.getId())
							&& a.getAssemblyContext_AllocationContext().getEncapsulatedComponent__AssemblyContext()
									.getId().equals(belComponent.getId());
				}).map(a -> a.getAssemblyContext_AllocationContext()).collect(Collectors.toList());

		if (matches.size() == 0) {
			return null;
		} else if (matches.size() == 1) {
			return matches.get(0);
		} else {
			log.warning(
					"The assumption that only one assembly context per type can be deployed on a container does not seem to be valid.");
			return null;
		}
	}

	private ServiceCallGraph buildGraphFromMonitoringData(List<Tree<ServiceCallRecord>> entryCalls) {
		ServiceCallGraph ret = ServiceCallGraphFactory.eINSTANCE.createServiceCallGraph();

		entryCalls.stream().forEach(ec -> {
			processTreeRecursive(ret, ec.getRoot());
		});

		return ret;
	}

	private void processTreeRecursive(ServiceCallGraph ret, TreeNode<ServiceCallRecord> root) {
		for (TreeNode<ServiceCallRecord> node : root.getChildren()) {
			createConnectionInSCG(ret, root.getData(), node.getData());
			node.getChildren().forEach(n -> processTreeRecursive(ret, n));
		}
	}

	private void createConnectionInSCG(ServiceCallGraph ret, ServiceCallRecord anchor, ServiceCallRecord data) {
		ResourceDemandingSEFF from = resolveServiceWithCache(anchor.getServiceId());
		ResourceDemandingSEFF to = resolveServiceWithCache(data.getServiceId());

		ResourceContainer containerFrom = resolveResourceContainerWithCache(anchor.getHostId());
		ResourceContainer containerTo = resolveResourceContainerWithCache(data.getHostId());

		ret.incrementEdge(from, to, containerFrom, containerTo);
	}

	private ResourceDemandingSEFF resolveServiceWithCache(String id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		} else {
			ResourceDemandingSEFF ret = PCMUtils.getElementById(
					this.getBlackboard().getArchitectureModel().getRepository(), ResourceDemandingSEFF.class, id);

			cache.put(id, ret);
			return ret;
		}
	}

	private ResourceContainer resolveResourceContainerWithCache(String id) {
		if (cacheResEnv.containsKey(id)) {
			return cacheResEnv.get(id);
		} else {
			ResourceContainer container = PCMUtils.getElementById(
					this.getBlackboard().getArchitectureModel().getResourceEnvironmentModel(), ResourceContainer.class,
					id);
			if (container != null) {
				cacheResEnv.put(id, container);
			} else {
				log.warning("Failed to resolve container with ID '" + id
						+ "'. The resource environment model seems to be inconsistent.");
			}
			return container;
		}
	}

}
