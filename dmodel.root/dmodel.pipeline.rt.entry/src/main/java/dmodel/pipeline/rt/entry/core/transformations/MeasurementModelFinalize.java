package dmodel.pipeline.rt.entry.core.transformations;

import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;

public class MeasurementModelFinalize extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts({ @InputPort(PortIDs.T_MM_FINALIZE) })
	public void finalizeMeasurementModel() {
	}

}
