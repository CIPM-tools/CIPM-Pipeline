package dmodel.pipeline.rt.entry.core;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import dmodel.pipeline.rt.pipeline.AbstractIterativePipeline;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import kieker.common.record.IMonitoringRecord;
import lombok.extern.java.Log;

@Component
@Log
public class IterativeRuntimePipeline extends
		AbstractIterativePipeline<List<IMonitoringRecord>, RuntimePipelineBlackboard> implements InitializingBean {

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private ApplicationContext applicationContext;

	public IterativeRuntimePipeline() {
		super();
	}

	@Override
	public void initBlackboard() {
		blackboard.reset();
		blackboard.getValidationFeedbackComponent().clearSimulationData();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.blackboard = blackboard;
		this.buildPipeline(IterativePipelineEntryPoint.class, new SpringContextClassProvider(applicationContext));
	}

	@Override
	protected void onIterationFinished() {
		log.info("Finished execution of the pipeline.");
	}

}
