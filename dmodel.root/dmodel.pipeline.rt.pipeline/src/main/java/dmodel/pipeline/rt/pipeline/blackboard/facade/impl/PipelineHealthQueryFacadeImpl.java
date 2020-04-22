package dmodel.pipeline.rt.pipeline.blackboard.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.core.health.HealthStateManager;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import dmodel.pipeline.core.health.HealthStateProblem;
import dmodel.pipeline.core.health.HealthStateProblemSeverity;
import dmodel.pipeline.rt.pipeline.blackboard.facade.IPipelineHealthQueryFacade;

@Component
public class PipelineHealthQueryFacadeImpl implements IPipelineHealthQueryFacade {

	@Autowired
	private HealthStateManager healthStateManager;

	@Override
	public long reportProblem(HealthStateProblemSeverity severity, String message) {
		return healthStateManager.addProblem(HealthStateProblem.builder().description(message).severity(severity)
				.source(HealthStateObservedComponent.PIPELINE).build());
	}

	@Override
	public void removeProblem(long id) {
		healthStateManager.removeProblem(id);
	}

}
