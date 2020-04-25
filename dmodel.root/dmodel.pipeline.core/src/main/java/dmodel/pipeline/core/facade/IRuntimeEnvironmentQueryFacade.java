package dmodel.pipeline.core.facade;

import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainerConnection;

/**
 * Sub-facade which makes it possible to easily access and modify elements in
 * the Runtime Environment Model (REM).
 * 
 * @see RuntimeEnvironmentModel
 * @author David Monschein
 *
 */
public interface IRuntimeEnvironmentQueryFacade extends IResettableQueryFacade {

	/**
	 * Gets the runtime container with a given host ID
	 * 
	 * @param hostId the host ID
	 * @return the runtime container corresponding to the host ID - or null if it
	 *         does not exist
	 */
	public RuntimeResourceContainer getContainerById(String hostId);

	/**
	 * Gets a link between runtime containers by their IDs.
	 * 
	 * @param fromId the host ID of the link source
	 * @param toId   the host ID of the link target
	 * @return the link between the runtime containers - or null if it does not
	 *         exist
	 */
	public RuntimeResourceContainerConnection getLinkByIds(String fromId, String toId);

	/**
	 * Determines whether there is a runtime container with a given host ID.
	 * 
	 * @param hostId the host ID to search for
	 * @return true if a corresponding runtime container exists, false if not
	 */
	public boolean containsHostId(String hostId);

	/**
	 * Determines whether a link between two runtime containers exists.
	 * 
	 * @param fromId the host ID of the link source
	 * @param toId   the host ID of the link target
	 * @return true if the link exists, false otherwise
	 */
	public boolean containsLink(String fromId, String toId);

	/**
	 * Creates a resource container with a given host ID and a given host name.
	 * 
	 * @param hostId   host ID of the container to create
	 * @param hostName host name of the container to create
	 */
	public void createResourceContainer(String hostId, String hostName);

	/**
	 * Creates a link between two resource containers.
	 * 
	 * @param fromId host ID of the link source
	 * @param toId   host ID of the link target
	 */
	public void createResourceContainerLink(String fromId, String toId);

}
