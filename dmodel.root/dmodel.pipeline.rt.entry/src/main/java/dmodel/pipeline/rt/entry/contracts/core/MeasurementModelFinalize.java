package dmodel.pipeline.rt.entry.contracts.core;

import dmodel.pipeline.rt.entry.contracts.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.entry.contracts.annotation.InputPort;
import dmodel.pipeline.rt.entry.contracts.blackboard.RuntimePipelineBlackboard;

public class MeasurementModelFinalize extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPort(from = ServiceCallEntryPoint.class)
	public void finalizeMeasurementModel() {
		System.out.println(getBlackboard().getMeasurementModel());
	}

}
