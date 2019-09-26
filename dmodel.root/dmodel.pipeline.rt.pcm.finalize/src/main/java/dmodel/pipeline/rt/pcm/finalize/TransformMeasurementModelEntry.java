package dmodel.pipeline.rt.pcm.finalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;

@Component
public class TransformMeasurementModelEntry {
	private static final Logger LOG = LoggerFactory.getLogger(TransformMeasurementModelEntry.class);

	public void transformMeasurementModel(RuntimePipelineBlackboard blackboard) {
		// TODO
		LOG.info("Using Measurement Model to refine the architectural model parts.");
	}

}
