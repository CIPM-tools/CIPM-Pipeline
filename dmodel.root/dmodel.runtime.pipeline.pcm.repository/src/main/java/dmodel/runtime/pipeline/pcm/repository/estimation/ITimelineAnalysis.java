package dmodel.runtime.pipeline.pcm.repository.estimation;

import dmodel.runtime.pipeline.pcm.repository.RepositoryStoexChanges;

public interface ITimelineAnalysis {

	public RepositoryStoexChanges analyze(IResourceDemandTimeline timeline);

}
