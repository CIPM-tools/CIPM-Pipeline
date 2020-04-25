package dmodel.runtime.pipeline.blackboard.facade;

import dmodel.base.core.health.HealthStateProblemSeverity;

public interface IPipelineHealthQueryFacade {

	public long reportProblem(HealthStateProblemSeverity severity, String message);

	public void removeProblem(long id);

}
