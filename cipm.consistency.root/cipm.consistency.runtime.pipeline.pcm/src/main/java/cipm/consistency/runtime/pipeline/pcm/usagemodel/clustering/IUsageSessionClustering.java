package cipm.consistency.runtime.pipeline.pcm.usagemodel.clustering;

import java.util.List;

import cipm.consistency.runtime.pipeline.pcm.usagemodel.ServiceCallSession;

public interface IUsageSessionClustering {

	public List<List<ServiceCallSession>> clusterSessions(List<ServiceCallSession> initial);

}
