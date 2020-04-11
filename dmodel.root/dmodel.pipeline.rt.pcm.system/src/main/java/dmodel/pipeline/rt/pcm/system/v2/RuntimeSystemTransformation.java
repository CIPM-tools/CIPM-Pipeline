package dmodel.pipeline.rt.pcm.system.v2;

import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.core.state.EPipelineTransformation;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphFactory;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.router.AccuracySwitch;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
import dmodel.pipeline.shared.structure.Tree.TreeNode;
import lombok.extern.java.Log;

@Log
public class RuntimeSystemTransformation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	public RuntimeSystemTransformation() {
		super(ExecutionMeasuringPoint.T_SYSTEM, EPipelineTransformation.T_SYSTEM);
	}

	@InputPorts({ @InputPort(PortIDs.T_SC_PCM_SYSTEM), @InputPort(PortIDs.T_RESENV_PCM_SYSTEM) })
	@OutputPorts({ @OutputPort(async = false, id = PortIDs.T_SYSTEM_ROUTER, to = AccuracySwitch.class) })
	public void transformSystem(List<Tree<ServiceCallRecord>> entryCalls) {
		super.trackStart();

		log.info("Deriving system refinements at runtime.");
		List<ServiceCallGraph> runtimeGraphs = buildGraphsFromMonitoringData(entryCalls);
		ServiceCallGraph actualGraph = mergeCallGraphs(runtimeGraphs);
		if (actualGraph != null) {
			// TODO apply the current graph
		}

		// finish
		super.trackEnd();
	}

	private ServiceCallGraph mergeCallGraphs(List<ServiceCallGraph> callGraphs) {
		// the later the scg in the list, the newer it is
		// therefore we prefer information that is present towards the end of the list
		// (in case of conflicts)
		if (callGraphs.size() == 0) {
			return null;
		}

		// merge graphs
		ServiceCallGraph currentCallGraph = null;
		for (ServiceCallGraph scg : callGraphs) {
			if (currentCallGraph == null) {
				currentCallGraph = scg;
			} else {
				mergeCallGraphs(currentCallGraph, scg);
			}
		}

		// clean unreachable nodes
		// TODO

		return currentCallGraph;
	}

	private void mergeCallGraphs(ServiceCallGraph parent, ServiceCallGraph inherit) {
		// TODO multiple provided role ranking

		for (ServiceCallGraphEdge inheritEdge : inherit.getEdges()) {
			ServiceCallGraphNode existingNodeFrom = parent.hasNode(inheritEdge.getFrom().getSeff(),
					inheritEdge.getFrom().getHost());

			// process
			OperationRequiredRole correspondingReqRole = inheritEdge.getExternalCall().getRole_ExternalService();
			BasicComponent componentTo = inheritEdge.getTo().getSeff().getBasicComponent_ServiceEffectSpecification();

			if (existingNodeFrom != null) {
				// this edges are inconsistent with the new one
				List<ServiceCallGraphEdge> oldEdges = parent.getOutgoingEdges().get(existingNodeFrom).stream()
						.filter(e -> {
							return e.getExternalCall().getRole_ExternalService().equals(correspondingReqRole)
									&& !e.getTo().getSeff().getBasicComponent_ServiceEffectSpecification()
											.equals(componentTo);
						}).collect(Collectors.toList());
				oldEdges.forEach(oe -> parent.removeEdge(oe));
			}

			// add new edge
			parent.incrementEdge(inheritEdge.getFrom().getSeff(), inheritEdge.getTo().getSeff(),
					inheritEdge.getFrom().getHost(), inheritEdge.getTo().getHost(), inheritEdge.getExternalCall());

		}
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

			List<AssemblyContext> fromAssembly = getBlackboard().getPcmQuery().getAllocation()
					.getDeployedAssembly(fromSeff.getBasicComponent_ServiceEffectSpecification(), fromContainer);
			List<AssemblyContext> toAssembly = getBlackboard().getPcmQuery().getAllocation()
					.getDeployedAssembly(toSeff.getBasicComponent_ServiceEffectSpecification(), toContainer);

			String externalCallId = node.getData().getExternalCallId();
			ExternalCallAction externalCallAction = getBlackboard().getPcmQuery().getRepository()
					.getElementById(externalCallId, ExternalCallAction.class);

			if (fromAssembly != null && toAssembly != null && externalCallAction != null) {
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

}
