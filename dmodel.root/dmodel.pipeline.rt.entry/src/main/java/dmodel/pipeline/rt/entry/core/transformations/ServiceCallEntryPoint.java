package dmodel.pipeline.rt.entry.core.transformations;

import java.util.List;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;

public class ServiceCallEntryPoint extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts({ @InputPort(PortIDs.T_DEFAULT) })
	@OutputPorts({
			@OutputPort(to = MeasurementModelFinalize.class, async = false, id = PortIDs.T_MM_FINALIZE)
	})
	public void processServiceCalls(List<ServiceCallRecord> record) {
	}

}
