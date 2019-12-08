package dmodel.pipeline.rt.pcm.validation;

import java.util.List;

import dmodel.pipeline.monitoring.records.ServiceContextRecord;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.validation.data.ValidationMetric;
import dmodel.pipeline.shared.pipeline.PortIDs;
import lombok.extern.java.Log;

@Log
public class PrePipelineValidationTask extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts({ @InputPort(PortIDs.T_VAL_PRE) })
	@OutputPorts(@OutputPort(to = ServiceCallTreeBuilder.class, id = PortIDs.T_BUILD_SERVICECALL_TREE, async = false))
	public List<ServiceContextRecord> prePipelineValidation(List<ServiceContextRecord> recs) {
		log.info("Start simulation of the current models.");
		// simulate
		List<ValidationMetric> metrics = getBlackboard().getValidationFeedbackComponent()
				.process(getBlackboard().getArchitectureModel(), recs, "Pipeline-PreValidation");

		// set results
		getBlackboard().getValidationResultContainer().setPreValidationResults(metrics);

		// pass data
		return recs;
	}

}
