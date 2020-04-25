package dmodel.runtime.pipeline.entry.core;

import java.util.List;
import java.util.stream.Collectors;

import dmodel.base.core.evaluation.ExecutionMeasuringPoint;
import dmodel.base.shared.pipeline.PortIDs;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.runtime.pipeline.AbstractIterativePipelinePart;
import dmodel.runtime.pipeline.annotation.EntryInputPort;
import dmodel.runtime.pipeline.annotation.OutputPort;
import dmodel.runtime.pipeline.annotation.OutputPorts;
import dmodel.runtime.pipeline.annotation.PipelineEntryPoint;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipeline.data.PCMPartionedMonitoringData;
import dmodel.runtime.pipeline.data.PartitionedMonitoringData;
import dmodel.runtime.pipeline.entry.transformation.PrePipelineValidationTask;
import dmodel.runtime.pipeline.pcm.router.AccuracySwitch;
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

		return new PCMPartionedMonitoringData(result, monitoringData.getValidationSplit());
	}

}
