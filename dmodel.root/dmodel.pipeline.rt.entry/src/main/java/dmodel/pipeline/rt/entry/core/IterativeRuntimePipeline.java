package dmodel.pipeline.rt.entry.core;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import dmodel.pipeline.rt.pipeline.AbstractIterativePipeline;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import kieker.common.record.IMonitoringRecord;

@Component
public class IterativeRuntimePipeline extends
		AbstractIterativePipeline<List<IMonitoringRecord>, RuntimePipelineBlackboard> implements InitializingBean {

	public IterativeRuntimePipeline() {
		super();
		this.blackboard = new RuntimePipelineBlackboard();
	}

	@Override
	public void initBlackboard() {
		this.blackboard.reset();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.buildPipeline(IterativePipelineEntryPoint.class);
	}

	@Override
	protected void onIterationFinished() {
		System.out.println(this.blackboard.getMeasurementModel().getEnvironmentData().getHostNames().size());
	}

}