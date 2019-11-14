package dmodel.pipeline.rt.pcm.finalize;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;

@Component
public class TransformMeasurementModelEntry {
	private static final Logger LOG = LoggerFactory.getLogger(TransformMeasurementModelEntry.class);

	@Autowired
	private List<IMeasurementModelProcessor> mmmProcessors;

	public void transformMeasurementModel(RuntimePipelineBlackboard blackboard) {
		LOG.info("Using Measurement Model to refine the architectural model parts.");

		mmmProcessors.stream().forEach(proc -> {
			proc.processMeasurementModel(blackboard.getMeasurementModel(), blackboard.getArchitectureModel(),
					blackboard.getBorder().getRuntimeMapping());
		});
	}

}
