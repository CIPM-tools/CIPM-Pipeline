package cipm.consistency.base.core.facade;

/**
 * Describes that a facade can be reset (e.g. rebuild of the caches).
 * 
 * @author David Monschein
 *
 */
public interface IResettableQueryFacade {

	/**
	 * Resets the facade, thereby a distinction can be made between a hard and a
	 * soft reset (e.g. hard reset rebuilds all caches and soft reset only removes
	 * data from cache that is older than a certain date).
	 * 
	 * @param hard whether it is a hard reset or not
	 */
	public void reset(boolean hard);

}
