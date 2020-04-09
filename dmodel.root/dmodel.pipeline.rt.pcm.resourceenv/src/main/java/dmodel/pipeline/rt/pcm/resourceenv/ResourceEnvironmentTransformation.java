package dmodel.pipeline.rt.pcm.resourceenv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.core.state.EPipelineTransformation;
import dmodel.pipeline.core.state.ETransformationState;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.resourceenv.data.EnvironmentData;
import dmodel.pipeline.rt.pcm.resourceenv.data.Host;
import dmodel.pipeline.rt.pcm.resourceenv.data.HostLink;
import dmodel.pipeline.rt.pcm.resourceenv.finalize.IResourceEnvironmentDeduction;
import dmodel.pipeline.rt.pcm.resourceenv.finalize.ResourceEnvironmentTransformer;
import dmodel.pipeline.rt.pcm.system.RuntimeSystemDerivation;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
import dmodel.pipeline.shared.structure.Tree.TreeNode;
import lombok.extern.java.Log;

@Log
public class ResourceEnvironmentTransformation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	private IResourceEnvironmentDeduction transformer;

	public ResourceEnvironmentTransformation() {
		this.transformer = new ResourceEnvironmentTransformer();
	}

	@InputPorts({ @InputPort(PortIDs.T_SC_PCM_RESENV) })
	@OutputPorts(@OutputPort(to = RuntimeSystemDerivation.class, async = false, id = PortIDs.T_RESENV_PCM_SYSTEM))
	public void deriveResourceEnvironment(List<Tree<ServiceCallRecord>> entryCalls) {
		getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_RESOURCE_ENVIRONMENT);

		log.info("Deriving resource environment.");
		getBlackboard().getQuery().updateState(EPipelineTransformation.T_RESOURCEENV, ETransformationState.RUNNING);

		Set<String> hostIds = new HashSet<>();
		Map<String, String> hostIdMapping = new HashMap<>();
		List<Pair<String, String>> hostConnections = new ArrayList<>();

		// traverse trees
		for (Tree<ServiceCallRecord> trace : entryCalls) {
			traverseNode(trace.getRoot(), hostIds, hostIdMapping, hostConnections);
		}

		// apply new data
		EnvironmentData data = new EnvironmentData();
		hostIds.forEach(id -> {
			data.getHosts().add(Host.builder().id(id).name(hostIdMapping.get(id)).build());
		});
		hostConnections.forEach(c -> {
			data.getConnections().add(HostLink.builder().fromId(c.getLeft()).toId(c.getRight()).build());
		});

		// trigger deduction
		transformer.processEnvironmentData(getBlackboard().getRemQuery(), getBlackboard().getVsumQuery(), data);

		getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_RESOURCE_ENVIRONMENT);
		getBlackboard().getQuery().updateState(EPipelineTransformation.T_RESOURCEENV, ETransformationState.FINISHED);
	}

	private void traverseNode(TreeNode<ServiceCallRecord> node, Set<String> hosts, Map<String, String> mapping,
			List<Pair<String, String>> conns) {
		// put mapping and child
		hosts.add(node.getData().getHostId());
		mapping.put(node.getData().getHostId(), node.getData().getHostName());

		for (TreeNode<ServiceCallRecord> rec : node.getChildren()) {
			if (!rec.getData().getHostId().equals(node.getData().getHostId())) {
				// there is a transition
				conns.add(Pair.of(node.getData().getHostId(), rec.getData().getHostId()));
			}
			// recursive traversion
			traverseNode(rec, hosts, mapping, conns);
		}
	}

}
