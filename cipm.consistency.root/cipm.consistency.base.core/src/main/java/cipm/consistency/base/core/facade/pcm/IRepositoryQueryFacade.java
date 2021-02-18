package cipm.consistency.base.core.facade.pcm;

import java.util.List;

import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import cipm.consistency.base.core.facade.IResettableQueryFacade;
import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph;
import de.uka.ipd.sdq.identifier.Identifier;

/**
 * Facade for accessing the elements in the repository model. Should provide
 * caching functionalities.
 * 
 * @author David Monschein
 *
 */
public interface IRepositoryQueryFacade extends IResettableQueryFacade {

	/**
	 * Gets the element within the repository with a given ID.
	 * 
	 * @param <T>  the type of the element to find (needs to extend
	 *             {@link Identifier}
	 * @param id   the ID of the element to find
	 * @param type the class corresponding to the type of the element to find
	 * @return the element with the given ID and type or null if it does not exist
	 */
	public <T extends Identifier> T getElementById(String id, Class<T> type);

	/**
	 * Higher order function which builds a service call graph out of a repository
	 * as a whole.
	 * 
	 * @return {@link ServiceCallGraph} which describes the "calls" relationship
	 *         between services
	 */
	public ServiceCallGraph getServiceCallGraph();

	/**
	 * Gets all components that provide a specific interface.
	 * 
	 * @param iface the interface
	 * @return all components that provide the specific interface
	 */
	public List<RepositoryComponent> getComponentsProvidingInterface(OperationInterface iface);

	/**
	 * Gets a service ({@link ResourceDemandingSEFF} by a given ID.
	 * 
	 * @param id ID of the service
	 * @return the service or null if it does not exist
	 */
	default public ResourceDemandingSEFF getServiceById(String id) {
		return this.getElementById(id, ResourceDemandingSEFF.class);
	}

	/**
	 * Gets a internal action ({@link InternalAction} by a given ID.
	 * 
	 * @param key the ID of the internal action
	 * @return the corresponding internal action or null if it does not exist
	 */
	default public InternalAction getInternalAction(String key) {
		return this.getElementById(key, InternalAction.class);
	}

	/**
	 * Gets an interface ({@link OperationInterface} by a given ID.
	 * 
	 * @param id the ID of the interface
	 * @return the corresponding interface or null if it does not exist
	 */
	default public OperationInterface getOperationInterface(String id) {
		return this.getElementById(id, OperationInterface.class);
	}

	/**
	 * Gets a branch transition ({@link AbstractBranchTransition} by a given ID.
	 * 
	 * @param id the ID of the branch transition
	 * @return the corresponding branch transition or null if it does not exist
	 */
	default public AbstractBranchTransition getBranchTransition(String id) {
		return this.getElementById(id, AbstractBranchTransition.class);
	}

}
