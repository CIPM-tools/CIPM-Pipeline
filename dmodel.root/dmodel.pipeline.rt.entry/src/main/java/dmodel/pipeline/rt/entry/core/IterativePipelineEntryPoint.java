package dmodel.pipeline.rt.entry.core;

import java.util.List;
import java.util.stream.Collectors;

import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.pcm.validation.PrePipelineValidationTask;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.EntryInputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.annotation.PipelineEntryPoint;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.router.AccuracySwitch;
import dmodel.pipeline.shared.pipeline.PortIDs;
import kieker.common.record.IMonitoringRecord;
import lombok.extern.java.Log;

@PipelineEntryPoint
@Log
public class IterativePipelineEntryPoint extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@EntryInputPort
	@OutputPorts({ @OutputPort(id = PortIDs.T_VAL_PRE, to = PrePipelineValidationTask.class, async = false),
			@OutputPort(id = PortIDs.T_RAW_ROUTER, to = AccuracySwitch.class, async = false) })
	public List<PCMContextRecord> filterMonitoringData(List<IMonitoringRecord> records) {
		// EVALUATION
		getBlackboard().getPerformanceEvaluation().enterPipelineExecution();
		getBlackboard().getPerformanceEvaluation().trackRecordCount(records.size());

		long start = getBlackboard().getPerformanceEvaluation().getTime();

		// original logic
		log.info("Reached entry (size = " + records.size() + ").");
		List<PCMContextRecord> result = records.stream().filter(r -> r instanceof PCMContextRecord)
				.map(PCMContextRecord.class::cast).collect(Collectors.toList());

		getBlackboard().getPerformanceEvaluation().trackPreFilter(start);

		return result;
	}

}
