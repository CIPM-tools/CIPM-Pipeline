package dmodel.pipeline.rt.pcm.validation;

import java.util.List;
import java.util.stream.Collectors;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pcm.resourceenv.ResourceEnvironmentTransformation;
import dmodel.pipeline.rt.pcm.system.RuntimeSystemTransformation;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.data.PartitionedMonitoringData;
import dmodel.pipeline.rt.router.AccuracySwitch;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
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
