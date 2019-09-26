package dmodel.pipeline.rt.entry.core;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.rt.pcm.finalize.TransformMeasurementModelEntry;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipeline;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import kieker.common.record.IMonitoringRecord;

@Component
public class IterativeRuntimePipeline extends
		AbstractIterativePipeline<List<IMonitoringRecord>, RuntimePipelineBlackboard> implements InitializingBean {

	@Autowired
	private TransformMeasurementModelEntry measurementModelTransformer;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	public IterativeRuntimePipeline() {
		super();
	}

	@Override
	public void initBlackboard() {
		this.blackboard.reset();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.blackboard = blackboard;
		this.buildPipeline(IterativePipelineEntryPoint.class);
	}

	@Override
	protected void onIterationFinished() {
		measurementModelTransformer.transformMeasurementModel(this.blackboard);
	}

}
