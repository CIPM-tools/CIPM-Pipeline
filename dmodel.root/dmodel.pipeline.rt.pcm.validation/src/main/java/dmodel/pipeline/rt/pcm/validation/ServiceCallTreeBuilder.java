package dmodel.pipeline.rt.pcm.validation;

import java.util.List;
import java.util.stream.Collectors;

import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pcm.allocation.AllocationDerivation;
import dmodel.pipeline.rt.pcm.resourceenv.ResourceEnvironmentTransformation;
import dmodel.pipeline.rt.pcm.system.RuntimeSystemDerivation;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.router.AccuracySwitch;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
import lombok.extern.java.Log;

// TODO maybe the project and the package are not the correct location
@Log
public class ServiceCallTreeBuilder extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	/* @formatter:off */
	@InputPorts(@InputPort(PortIDs.T_BUILD_SERVICECALL_TREE))
	@OutputPorts({
		@OutputPort(to = ResourceEnvironmentTransformation.class, async = false, id = PortIDs.T_SC_PCM_RESENV),
		@OutputPort(to = AllocationDerivation.class, async = false, id = PortIDs.T_SC_PCM_ALLOCATION),
		@OutputPort(to = RuntimeSystemDerivation.class, async = false, id = PortIDs.T_SC_PCM_SYSTEM),
		@OutputPort(to = AccuracySwitch.class, async = false, id = PortIDs.T_SC_ROUTER)
	})
	/* @formatter:on */
	public List<Tree<ServiceCallRecord>> buildServiceCallTree(List<PCMContextRecord> records) {
		log.info("Start building of service call trees.");
		return MonitoringDataUtil.buildServiceCallTree(records.stream().filter(f -> f instanceof ServiceCallRecord)
				.map(ServiceCallRecord.class::cast).collect(Collectors.toList()));
	}

}
