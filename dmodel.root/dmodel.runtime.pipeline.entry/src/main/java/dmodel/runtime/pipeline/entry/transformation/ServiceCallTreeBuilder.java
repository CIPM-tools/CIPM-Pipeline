package dmodel.runtime.pipeline.entry.transformation;

import java.util.List;
import java.util.stream.Collectors;

import dmodel.base.core.evaluation.ExecutionMeasuringPoint;
import dmodel.base.shared.pipeline.PortIDs;
import dmodel.base.shared.structure.Tree;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.designtime.monitoring.util.MonitoringDataUtil;
import dmodel.runtime.pipeline.AbstractIterativePipelinePart;
import dmodel.runtime.pipeline.annotation.InputPort;
import dmodel.runtime.pipeline.annotation.InputPorts;
import dmodel.runtime.pipeline.annotation.OutputPort;
import dmodel.runtime.pipeline.annotation.OutputPorts;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipeline.data.PartitionedMonitoringData;
import dmodel.runtime.pipeline.pcm.resourceenv.ResourceEnvironmentTransformation;
import dmodel.runtime.pipeline.pcm.router.AccuracySwitch;
import dmodel.runtime.pipeline.pcm.system.RuntimeSystemTransformation;
import lombok.extern.java.Log;

// TODO maybe the project and the package are not the correct location
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
