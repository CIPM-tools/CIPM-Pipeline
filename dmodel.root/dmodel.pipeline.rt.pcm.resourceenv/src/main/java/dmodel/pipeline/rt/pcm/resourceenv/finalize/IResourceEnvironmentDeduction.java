package dmodel.pipeline.rt.pcm.resourceenv.finalize;

import dmodel.pipeline.models.mapping.PalladioRuntimeMapping;
import dmodel.pipeline.rt.pcm.resourceenv.data.EnvironmentData;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

public interface IResourceEnvironmentDeduction {

	public void processEnvironmentData(InMemoryPCM pcm, PalladioRuntimeMapping mapping, EnvironmentData data);

}
