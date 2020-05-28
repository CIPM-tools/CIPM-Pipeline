package dmodel.runtime.pipeline.pcm.system;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dmodel.base.core.evaluation.ExecutionMeasuringPoint;
import dmodel.base.core.state.EPipelineTransformation;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphEdge;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphFactory;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphNode;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.base.shared.pipeline.PortIDs;
import dmodel.base.shared.structure.Tree;
import dmodel.base.shared.structure.Tree.TreeNode;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.runtime.pipeline.AbstractIterativePipelinePart;
import dmodel.runtime.pipeline.annotation.InputPort;
import dmodel.runtime.pipeline.annotation.InputPorts;
import dmodel.runtime.pipeline.annotation.OutputPort;
import dmodel.runtime.pipeline.annotation.OutputPorts;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipeline.pcm.router.AccuracySwitch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Service
public class RuntimeSystemTransformation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	@Autowired
	private RuntimeSystemUpdater systemUpdater;

	public RuntimeSystemTransformation() {
		super(ExecutionMeasuringPoint.T_SYSTEM, EPipelineTransformation.T_SYSTEM);
	}

	@InputPorts({ @InputPort(PortIDs.T_SC_PCM_SYSTEM), @InputPort(PortIDs.T_RESENV_PCM_SYSTEM) })
	@OutputPorts({ @OutputPort(async = false, id = PortIDs.T_SYSTEM_ROUTER, to = AccuracySwitch.class) })
	public void transformSystem(List<Tree<ServiceCallRecord>> entryCalls) {
		super.trackStart();

		log.info("Deriving system refinements at runtime.");
		List<ServiceCallGraph> runtimeGraphs = buildGraphsFromMonitoringData(entryCalls);
		Pair<ServiceCallGraph, CallGraphMergeMetadata> mergeResult = mergeCallGraphs(runtimeGraphs);
		if (mergeResult != null && mergeResult.getLeft() != null) {
			systemUpdater.applyCallGraph(mergeResult);
		}

		// finish
		super.trackEnd();
	}

	private Pair<ServiceCallGraph, CallGraphMergeMetadata> mergeCallGraphs(List<ServiceCallGraph> callGraphs) {
		// the later the scg in the list, the newer it is
		// therefore we prefer information that is present towards the end of the list
		// (in case of conflicts)
		if (callGraphs.size() == 0) {
			return null;
		}

		// priority list
		CallGraphMergeMetadata metadata = new CallGraphMergeMetadata();

		// merge graphs
		ServiceCallGraph currentCallGraph = ServiceCallGraphFactory.eINSTANCE.createServiceCallGraph();
		for (ServiceCallGraph scg : callGraphs) {
			scg.rebuild();
			updateEntryCallPriorities(scg, metadata);
			mergeCallGraphs(currentCallGraph, scg, metadata);
		}
		currentCallGraph.rebuild();

		// clean unreachable nodes
		final ServiceCallGraph finalCallGraph = currentCallGraph;
		List<ServiceCallGraphNode> unreachableNodes = currentCallGraph.getNodes().stream()
				.filter(n -> finalCallGraph.getOutgoingEdges().get(n).size() == 0
						&& finalCallGraph.getIncomingEdges().get(n).size() == 0)
				.collect(Collectors.toList());
		unreachableNodes.forEach(un -> finalCallGraph.removeNode(un));

		return Pair.of(finalCallGraph, metadata);
	}

	private void updateEntryCallPriorities(ServiceCallGraph scg, CallGraphMergeMetadata metadata) {
		for (ServiceCallGraphNode node : scg.getNodes()) {
			if (scg.getIncomingEdges().get(node).size() == 0) {
				// => entry point
				metadata.entryNodePriorities.remove(node);
				metadata.entryNodePriorities.addFirst(node);
			}
		}
	}

	private void mergeCallGraphs(ServiceCallGraph parent, ServiceCallGraph inherit, CallGraphMergeMetadata metadata) {
		for (ServiceCallGraphEdge inheritEdge : inherit.getEdges()) {
			ServiceCallGraphNode existingNodeFrom = parent.hasNode(inheritEdge.getFrom().getSeff(),
					inheritEdge.getFrom().getHost());

			// process
			OperationRequiredRole correspondingReqRole = inheritEdge.getExternalCall().getRole_ExternalService();
			BasicComponent componentTo = inheritEdge.getTo().getSeff().getBasicComponent_ServiceEffectSpecification();

			if (existingNodeFrom != null) {
				removeInconsistentEdges(parent, correspondingReqRole, componentTo, existingNodeFrom);
			}

			// add new edge
			parent.incrementEdge(inheritEdge.getFrom().getSeff(), inheritEdge.getTo().getSeff(),
					inheritEdge.getFrom().getHost(), inheritEdge.getTo().getHost(), inheritEdge.getExternalCall());

			// add priority
			ComponentInterfaceBinding binding = new ComponentInterfaceBinding(componentTo,
					inheritEdge.getTo().getHost(), correspondingReqRole.getRequiredInterface__OperationRequiredRole());
			metadata.insertPriority(binding, correspondingReqRole);
		}
	}

	private void removeInconsistentEdges(ServiceCallGraph parent, OperationRequiredRole correspondingReqRole,
			BasicComponent componentTo, ServiceCallGraphNode existingNodeFrom) {
		// this edges are inconsistent with the new one
		List<ServiceCallGraphEdge> oldEdges = parent.getOutgoingEdges().get(existingNodeFrom).stream().filter(e -> {
			return e.getExternalCall().getRole_ExternalService().equals(correspondingReqRole)
					&& !e.getTo().getSeff().getBasicComponent_ServiceEffectSpecification().equals(componentTo);
		}).collect(Collectors.toList());
		oldEdges.forEach(oe -> parent.removeEdge(oe));
	}

	private List<ServiceCallGraph> buildGraphsFromMonitoringData(List<Tree<ServiceCallRecord>> entryCalls) {
		return entryCalls.stream().map(ec -> {
			ServiceCallGraph ret = ServiceCallGraphFactory.eINSTANCE.createServiceCallGraph();
			processTreeRecursive(ret, ec.getRoot());
			return ret;
		}).collect(Collectors.toList());
	}

	private void processTreeRecursive(ServiceCallGraph ret, TreeNode<ServiceCallRecord> root) {
		for (TreeNode<ServiceCallRecord> node : root.getChildren()) {
			// get container
			ResourceContainer fromContainer = getMappedResourceContainer(root.getData().getHostId());
			ResourceContainer toContainer = getMappedResourceContainer(node.getData().getHostId());

			ResourceDemandingSEFF fromSeff = getBlackboard().getPcmQuery().getRepository()
					.getServiceById(root.getData().getServiceId());
			ResourceDemandingSEFF toSeff = getBlackboard().getPcmQuery().getRepository()
					.getServiceById(node.getData().getServiceId());

			String externalCallId = node.getData().getExternalCallId();
			ExternalCallAction externalCallAction = getBlackboard().getPcmQuery().getRepository()
					.getElementById(externalCallId, ExternalCallAction.class);
			if (fromSeff != null && toSeff != null && fromContainer != null && toContainer != null
					&& externalCallAction != null) {
				// connect in scg
				ret.incrementEdge(fromSeff, toSeff, fromContainer, toContainer, externalCallAction);

				// recursive processing
				processTreeRecursive(ret, node);
			} else {
				log.warning(
						"System derivation could not build call graphs successfully. This occurs due to an inconsistent state of other models.");
			}
		}
	}

	private ResourceContainer getMappedResourceContainer(String hostId) {
		RuntimeResourceContainer remContainer = getBlackboard().getRemQuery().getContainerById(hostId);
		if (remContainer != null) {
			return getBlackboard().getVsumQuery().getCorrespondingResourceContainer(remContainer).orElse(null);
		}
		return null;
	}

	@Data
	@AllArgsConstructor
	protected static class ComponentInterfaceBinding {
		private BasicComponent component;
		private ResourceContainer host;
		private OperationInterface iface;

		@Override
		public boolean equals(Object other) {
			if (other instanceof ComponentInterfaceBinding) {
				ComponentInterfaceBinding _other = (ComponentInterfaceBinding) other;
				return _other.component.getId().equals(component.getId()) && _other.host.getId().equals(host.getId())
						&& _other.iface.getId().equals(iface.getId());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(component.getId(), host.getId(), iface.getId());
		}
	}

	@Data
	@AllArgsConstructor
	protected static class CallGraphMergeMetadata {
		private Map<ComponentInterfaceBinding, LinkedList<OperationRequiredRole>> bindingPriorityMap;
		private LinkedList<ServiceCallGraphNode> entryNodePriorities;

		protected CallGraphMergeMetadata() {
			this.bindingPriorityMap = Maps.newHashMap();
			this.entryNodePriorities = Lists.newLinkedList();
		}

		protected void insertPriority(ComponentInterfaceBinding binding, OperationRequiredRole role) {
			if (bindingPriorityMap.containsKey(binding)) {
				bindingPriorityMap.get(binding).addFirst(role);
			} else {
				LinkedList<OperationRequiredRole> nList = Lists.newLinkedList();
				nList.add(role);
				bindingPriorityMap.put(binding, nList);
			}
		}
	}

}
