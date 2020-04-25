package dmodel.runtime.pipeline.pcm.resourceenv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import dmodel.base.core.evaluation.ExecutionMeasuringPoint;
import dmodel.base.core.state.EPipelineTransformation;
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
import dmodel.runtime.pipeline.pcm.resourceenv.data.EnvironmentData;
import dmodel.runtime.pipeline.pcm.resourceenv.data.Host;
import dmodel.runtime.pipeline.pcm.resourceenv.data.HostLink;
import dmodel.runtime.pipeline.pcm.resourceenv.finalize.IResourceEnvironmentDeduction;
import dmodel.runtime.pipeline.pcm.resourceenv.finalize.ResourceEnvironmentTransformer;
import dmodel.runtime.pipeline.pcm.system.RuntimeSystemTransformation;
import lombok.extern.java.Log;

@Log
public class ResourceEnvironmentTransformation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	private IResourceEnvironmentDeduction transformer;

	public ResourceEnvironmentTransformation() {
		super(ExecutionMeasuringPoint.T_RESOURCE_ENVIRONMENT, EPipelineTransformation.T_RESOURCEENV);
		this.transformer = new ResourceEnvironmentTransformer();
	}

	@InputPorts({ @InputPort(PortIDs.T_SC_PCM_RESENV) })
	@OutputPorts(@OutputPort(to = RuntimeSystemTransformation.class, async = false, id = PortIDs.T_RESENV_PCM_SYSTEM))
	public void deriveResourceEnvironment(List<Tree<ServiceCallRecord>> entryCalls) {
		log.info("Deriving resource environment.");

		super.trackStart();

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
		transformer.processEnvironmentData(getBlackboard().getPcmQuery(), getBlackboard().getRemQuery(),
				getBlackboard().getVsumQuery(), data);

		super.trackEnd();
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
