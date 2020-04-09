package dmodel.pipeline.rt.pcm.repository.estimation;

import dmodel.pipeline.rt.pcm.repository.RepositoryStoexChanges;

public interface ITimelineAnalysis {

	public RepositoryStoexChanges analyze(IResourceDemandTimeline timeline);

}
