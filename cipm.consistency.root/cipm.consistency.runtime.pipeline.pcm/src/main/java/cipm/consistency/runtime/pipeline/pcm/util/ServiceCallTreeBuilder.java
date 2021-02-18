package cipm.consistency.runtime.pipeline.pcm.util;

import java.util.List;
import java.util.stream.Collectors;

import cipm.consistency.base.core.state.ExecutionMeasuringPoint;
import cipm.consistency.base.shared.pipeline.PortIDs;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.util.MonitoringDataUtil;
import cipm.consistency.runtime.pipeline.AbstractIterativePipelinePart;
import cipm.consistency.runtime.pipeline.annotation.InputPort;
import cipm.consistency.runtime.pipeline.annotation.InputPorts;
import cipm.consistency.runtime.pipeline.annotation.OutputPort;
import cipm.consistency.runtime.pipeline.annotation.OutputPorts;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.data.PartitionedMonitoringData;
import cipm.consistency.runtime.pipeline.pcm.resourceenv.ResourceEnvironmentTransformation;
import cipm.consistency.runtime.pipeline.pcm.router.AccuracySwitch;
import cipm.consistency.runtime.pipeline.pcm.system.RuntimeSystemTransformation;
import lombok.extern.java.Log;

@Log
public class ServiceCallTreeBuilder extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	public ServiceCallTreeBuilder() {
		super(ExecutionMeasuringPoint.T_SERVICE_CALL_TREE, null);
	}

	/* @formatter:off */
	@InputPorts(@InputPort(PortIDs.T_BUILD_SERVICECALL_TREE))
	@OutputPorts({
		@OutputPort(to = ResourceEnvironmentTransformation.class, async = false, id = PortIDs.T_SC_PCM_RESENV),
		@OutputPort(to = RuntimeSystemTransformation.class, async = false, id = PortIDs.T_SC_PCM_SYSTEM),
		@OutputPort(to = AccuracySwitch.class, async = false, id = PortIDs.T_SC_ROUTER)
	})
	/* @formatter:on */
	public List<Tree<ServiceCallRecord>> buildServiceCallTree(PartitionedMonitoringData<PCMContextRecord> records) {
		super.trackStart();

		log.info("Start building of service call trees.");
		List<Tree<ServiceCallRecord>> result = MonitoringDataUtil
				.buildServiceCallTree(records.getAllData().stream().filter(f -> f instanceof ServiceCallRecord)
						.map(ServiceCallRecord.class::cast).collect(Collectors.toList()));

		super.trackEnd();

		return result;
	}

}
