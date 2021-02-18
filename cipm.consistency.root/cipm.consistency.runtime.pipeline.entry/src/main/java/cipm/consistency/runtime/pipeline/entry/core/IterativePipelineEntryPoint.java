package cipm.consistency.runtime.pipeline.entry.core;

import java.util.List;
import java.util.stream.Collectors;

import cipm.consistency.base.core.state.ExecutionMeasuringPoint;
import cipm.consistency.base.shared.pipeline.PortIDs;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.AbstractIterativePipelinePart;
import cipm.consistency.runtime.pipeline.annotation.EntryInputPort;
import cipm.consistency.runtime.pipeline.annotation.OutputPort;
import cipm.consistency.runtime.pipeline.annotation.OutputPorts;
import cipm.consistency.runtime.pipeline.annotation.PipelineEntryPoint;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.data.PartitionedMonitoringData;
import cipm.consistency.runtime.pipeline.data.SessionPartionedMonitoringData;
import cipm.consistency.runtime.pipeline.entry.transformation.PrePipelineValidationTask;
import cipm.consistency.runtime.pipeline.pcm.router.AccuracySwitch;
import kieker.common.record.IMonitoringRecord;
import lombok.extern.java.Log;

@PipelineEntryPoint
@Log
public class IterativePipelineEntryPoint extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	public IterativePipelineEntryPoint() {
		super(ExecutionMeasuringPoint.T_PRE_FILTER, null);
	}

	@EntryInputPort
	@OutputPorts({ @OutputPort(id = PortIDs.T_VAL_PRE, to = PrePipelineValidationTask.class, async = false),
			@OutputPort(id = PortIDs.T_RAW_ROUTER, to = AccuracySwitch.class, async = false) })
	public PartitionedMonitoringData<PCMContextRecord> filterMonitoringData(
			PartitionedMonitoringData<IMonitoringRecord> monitoringData) {
		List<IMonitoringRecord> records = monitoringData.getAllData();

		// EVALUATION
		getBlackboard().getQuery().trackStartPipelineExecution();
		getBlackboard().getQuery().trackRecordCount(records.size());

		super.trackStart();
		// original logic
		log.info("Reached entry (size = " + records.size() + ").");
		List<PCMContextRecord> result = records.stream().filter(r -> r instanceof PCMContextRecord)
				.map(PCMContextRecord.class::cast).collect(Collectors.toList());

		super.trackEnd();

		return new SessionPartionedMonitoringData(result, monitoringData.getValidationSplit());
	}

}
