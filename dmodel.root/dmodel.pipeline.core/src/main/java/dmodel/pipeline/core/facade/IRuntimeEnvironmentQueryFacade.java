package dmodel.pipeline.core.facade;

import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainerConnection;

public interface IRuntimeEnvironmentQueryFacade extends IResettableQueryFacade {

	public RuntimeResourceContainer getContainerById(String hostId);

	public RuntimeResourceContainerConnection getLinkByIds(String fromId, String toId);

	public boolean containsHostId(String hostId);

	public boolean containsLink(String fromId, String toId);

	public void createResourceContainer(String hostId, String hostName);

	public void createResourceContainerLink(String fromId, String toId);

}
