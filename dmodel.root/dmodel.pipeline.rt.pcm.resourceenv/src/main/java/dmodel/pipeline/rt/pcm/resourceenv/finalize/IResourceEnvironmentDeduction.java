package dmodel.pipeline.rt.pcm.resourceenv.finalize;

import dmodel.pipeline.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.pipeline.rt.pcm.resourceenv.data.EnvironmentData;
import dmodel.pipeline.vsum.facade.ISpecificVsumFacade;

public interface IResourceEnvironmentDeduction {

	public void processEnvironmentData(IRuntimeEnvironmentQueryFacade rem, ISpecificVsumFacade mapping,
			EnvironmentData data);

}
