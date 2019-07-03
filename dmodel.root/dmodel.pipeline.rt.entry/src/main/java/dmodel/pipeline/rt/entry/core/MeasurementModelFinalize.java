package dmodel.pipeline.rt.entry.core;

import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;

public class MeasurementModelFinalize extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts({ @InputPort(PortIDs.TO_MM_FINALIZE) })
	public void finalizeMeasurementModel() {
		System.out.println(getBlackboard().getMeasurementModel());
	}

}
