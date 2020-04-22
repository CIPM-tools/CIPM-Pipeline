package dmodel.pipeline.rt.pipeline.blackboard.facade;

import dmodel.pipeline.core.health.HealthStateProblemSeverity;

public interface IPipelineHealthQueryFacade {

	public long reportProblem(HealthStateProblemSeverity severity, String message);

	public void removeProblem(long id);

}
