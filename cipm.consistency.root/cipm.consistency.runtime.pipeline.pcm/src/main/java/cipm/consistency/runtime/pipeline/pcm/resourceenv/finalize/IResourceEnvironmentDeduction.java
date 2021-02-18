package cipm.consistency.runtime.pipeline.pcm.resourceenv.finalize;

import cipm.consistency.base.core.facade.IPCMQueryFacade;
import cipm.consistency.base.core.facade.IRuntimeEnvironmentQueryFacade;
import cipm.consistency.base.vsum.facade.ISpecificVsumFacade;
import cipm.consistency.runtime.pipeline.pcm.resourceenv.data.EnvironmentData;

public interface IResourceEnvironmentDeduction {

	public void processEnvironmentData(IPCMQueryFacade pcm, IRuntimeEnvironmentQueryFacade rem,
			ISpecificVsumFacade mapping, EnvironmentData data);

}
