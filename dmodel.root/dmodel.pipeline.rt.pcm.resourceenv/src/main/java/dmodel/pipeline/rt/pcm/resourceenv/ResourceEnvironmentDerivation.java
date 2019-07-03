package dmodel.pipeline.rt.pcm.resourceenv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import dmodel.pipeline.dt.mmmodel.HostNamePair;
import dmodel.pipeline.dt.mmmodel.MmmodelFactory;
import dmodel.pipeline.dt.mmmodel.ResourceEnvironmentData;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;

public class ResourceEnvironmentDerivation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts({ @InputPort(PortIDs.TO_PCM_RESENV) })
	public void deriveResourceEnvironmentData(List<ServiceCallRecord> records) {
		Set<String> hostNames = new HashSet<>();
		List<Pair<String, String>> hostConnections = new ArrayList<>();

		List<Tree<ServiceCallRecord>> entryCalls = MonitoringDataUtil.buildServiceCallTree(records);

		// traverse trees
		for (Tree<ServiceCallRecord> trace : entryCalls) {
			// TODO recursive parts
		}

		// apply new data
		ResourceEnvironmentData data = MmmodelFactory.eINSTANCE.createResourceEnvironmentData();
		data.getHostNames().addAll(hostNames);
		hostConnections.forEach(c -> {
			HostNamePair pair = MmmodelFactory.eINSTANCE.createHostNamePair();
			pair.setLeft(c.getLeft());
			pair.setRight(c.getRight());
			data.getConnections().add(pair);
		});
		getBlackboard().getMeasurementModel().setEnvironmentData(data);
	}

}
