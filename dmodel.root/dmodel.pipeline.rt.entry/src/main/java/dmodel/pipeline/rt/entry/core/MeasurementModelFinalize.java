package dmodel.pipeline.rt.entry.core;

import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;

public class MeasurementModelFinalize extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts(ports = { @InputPort(id = "finalize") })
	public void finalizeMeasurementModel() {
		System.out.println(getBlackboard().getMeasurementModel());
	}

}
