package dmodel.pipeline.rt.entry.core;

import java.util.List;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;

public class ServiceCallEntryPoint extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts(ports = { @InputPort(id = "toentry") })
	@OutputPorts(ports = { @OutputPort(to = MeasurementModelFinalize.class, async = false, id = "finalize") })
	public void processServiceCalls(List<ServiceCallRecord> record) {
	}

}
