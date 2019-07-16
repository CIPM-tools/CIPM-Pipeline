package dmodel.pipeline.rt.pcm.resourceenv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import dmodel.pipeline.dt.mmmodel.HostIdPair;
import dmodel.pipeline.dt.mmmodel.MmmodelFactory;
import dmodel.pipeline.dt.mmmodel.ResourceContainer;
import dmodel.pipeline.dt.mmmodel.ResourceEnvironmentData;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
import dmodel.pipeline.shared.structure.Tree.TreeNode;

public class ResourceEnvironmentDerivation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts({ @InputPort(PortIDs.T_PCM_RESENV) })
	public void deriveResourceEnvironmentData(List<Tree<ServiceCallRecord>> entryCalls) {
		Set<String> hostIds = new HashSet<>();
		Map<String, String> hostIdMapping = new HashMap<>();
		List<Pair<String, String>> hostConnections = new ArrayList<>();

		// traverse trees
		for (Tree<ServiceCallRecord> trace : entryCalls) {
			traverseNode(trace.getRoot(), hostIds, hostIdMapping, hostConnections);
		}

		// apply new data
		ResourceEnvironmentData data = MmmodelFactory.eINSTANCE.createResourceEnvironmentData();
		hostIds.forEach(id -> {
			ResourceContainer container = MmmodelFactory.eINSTANCE.createResourceContainer();
			container.setHostId(id);
			container.setHostName(hostIdMapping.get(id));
			data.getHosts().add(container);
		});
		hostConnections.forEach(c -> {
			HostIdPair pair = MmmodelFactory.eINSTANCE.createHostIdPair();
			pair.setLeft(c.getLeft());
			pair.setRight(c.getRight());
			data.getConnections().add(pair);
		});
		getBlackboard().getMeasurementModel().setEnvironmentData(data);
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
