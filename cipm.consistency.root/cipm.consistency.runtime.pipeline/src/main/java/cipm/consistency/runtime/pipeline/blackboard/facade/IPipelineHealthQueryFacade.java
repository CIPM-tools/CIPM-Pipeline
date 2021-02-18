package cipm.consistency.runtime.pipeline.blackboard.facade;

import cipm.consistency.base.core.health.HealthStateProblemSeverity;

public interface IPipelineHealthQueryFacade {

	public long reportProblem(HealthStateProblemSeverity severity, String message);

	public void removeProblem(long id);

}
