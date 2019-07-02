package dmodel.pipeline.rt.entry.contracts.core;

import java.util.List;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.entry.contracts.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.entry.contracts.annotation.InputPort;
import dmodel.pipeline.rt.entry.contracts.annotation.OutputPort;
import dmodel.pipeline.rt.entry.contracts.blackboard.RuntimePipelineBlackboard;

public class ServiceCallEntryPoint extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPort(from = IterativePipelineEntryPoint.class)
	@OutputPort(to = MeasurementModelFinalize.class, async = false)
	public void processServiceCalls(List<ServiceCallRecord> record) {
	}

}
