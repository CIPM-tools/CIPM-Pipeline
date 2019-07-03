package dmodel.pipeline.rt.entry.core.transformations;

import java.util.List;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pcm.resourceenv.ResourceEnvironmentDerivation;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;

public class ServiceCallTreeBuilder extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts(@InputPort(PortIDs.T_BUILD_SERVICECALL_TREE))
	@OutputPorts(@OutputPort(to = ResourceEnvironmentDerivation.class, async = true, id = PortIDs.T_PCM_RESENV))
	public List<Tree<ServiceCallRecord>> buildServiceCallTree(List<ServiceCallRecord> records) {
		return MonitoringDataUtil.buildServiceCallTree(records);
	}

}
