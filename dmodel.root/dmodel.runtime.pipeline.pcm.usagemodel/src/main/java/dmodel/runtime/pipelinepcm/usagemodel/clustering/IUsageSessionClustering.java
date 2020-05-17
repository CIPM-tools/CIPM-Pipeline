package dmodel.runtime.pipelinepcm.usagemodel.clustering;

import java.util.List;

import dmodel.runtime.pipelinepcm.usagemodel.ServiceCallSession;

public interface IUsageSessionClustering {

	public List<List<ServiceCallSession>> clusterSessions(List<ServiceCallSession> initial);

}
