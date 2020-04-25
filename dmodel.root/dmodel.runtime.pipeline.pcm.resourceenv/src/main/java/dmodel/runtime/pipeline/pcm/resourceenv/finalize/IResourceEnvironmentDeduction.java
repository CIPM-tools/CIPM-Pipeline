package dmodel.runtime.pipeline.pcm.resourceenv.finalize;

import dmodel.base.core.facade.IPCMQueryFacade;
import dmodel.base.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.base.vsum.facade.ISpecificVsumFacade;
import dmodel.runtime.pipeline.pcm.resourceenv.data.EnvironmentData;

public interface IResourceEnvironmentDeduction {

	public void processEnvironmentData(IPCMQueryFacade pcm, IRuntimeEnvironmentQueryFacade rem,
			ISpecificVsumFacade mapping, EnvironmentData data);

}
