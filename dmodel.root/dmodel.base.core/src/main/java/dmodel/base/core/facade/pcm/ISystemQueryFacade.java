package dmodel.base.core.facade.pcm;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingEntity;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.system.System;

import dmodel.base.core.facade.IResettableQueryFacade;
import dmodel.base.shared.pcm.util.deprecation.IDeprecationProcessor;

/**
 * Facade for accessing the underlying system model ({@link System}). It
 * supports read and write operations.
 * 
 * @author David Monschein
 *
 */
public interface ISystemQueryFacade extends IResettableQueryFacade {
	/**
	 * Gets the ID of the system model.
	 * 
	 * @return ID of the system model
	 */
	String getId();

	/**
	 * Gets a set that contains all IDs of the assembly contexts within the system
	 * model.
	 * 
	 * @return IDs of all assembly contexts within the system model
	 */
	Set<String> getAssemblyIds();

	/**
	 * Gets all assembly connectors within the enclosing system.
	 * 
	 * @return all assembly connectors contained by the enclosing system
	 */
	Collection<AssemblyConnector> getAssemblyConnectors();

	/**
	 * Gets all assembly contexts within the enclosing system.
	 * 
	 * @return all assembly contexts contained by the enclosing system
	 */
	Collection<AssemblyContext> getAssemblyContexts();

	/**
	 * Gets all {@link ProvidedDelegationConnector} within the enclosing system.
	 * 
	 * @return all {@link ProvidedDelegationConnector} contained by the enclosing
	 *         system
	 */
	Collection<ProvidedDelegationConnector> getProvidedDelegationConnectors();

	/**
	 * Gets all {@link RequiredDelegationConnector} within the enclosing system.
	 * 
	 * @return all {@link RequiredDelegationConnector} contained by the enclosing
	 *         system
	 */
	Collection<RequiredDelegationConnector> getRequiredDelegationConnectors();

	/**
	 * Gets all provided interfaces of the enclosing system.
	 * 
	 * @return provided interfaces of the system
	 */
	List<OperationInterface> getProvidedInterfaces();

	/**
	 * Gets all provided roles of the enclosing system.
	 * 
	 * @return provided roles of the system
	 */
	List<OperationProvidedRole> getProvidedRoles();

	/**
	 * Gets the provided roles of the enclosing system for a specific service
	 * signature.
	 * 
	 * @param describedService__SEFF the service signature
	 * @return a list of provided roles of the enclosing system which provide the
	 *         specified signature
	 */
	List<OperationProvidedRole> getProvidedRoleBySignature(Signature describedService__SEFF);

	/**
	 * Gets the provided roles of a {@link InterfaceProvidingEntity} for a specific
	 * service signature.
	 * 
	 * @param describedService__SEFF the service signature
	 * @param entity                 the {@link InterfaceProvidingEntity}
	 * @return all provided roles of the specified {@link InterfaceProvidingEntity}
	 *         that provide the signature
	 */
	List<OperationProvidedRole> getProvidedRoleBySignature(Signature describedService__SEFF,
			InterfaceProvidingEntity entity);

	/**
	 * Gets an assembly context for a given ID.
	 * 
	 * @param assembly the ID of the assembly context
	 * @return the assembly context with the given ID in the system model, or null
	 *         if it does not exist
	 */
	AssemblyContext getAssemblyById(String assembly);

	/**
	 * Creates an assembly context of a given component type and returns it.
	 * 
	 * @param component component type for the assembly context
	 * @return the created assembly context
	 */
	AssemblyContext createAssemblyContext(RepositoryComponent component);

	/**
	 * Removes a list of connectors that should be removed from the system model.
	 * 
	 * @param connectors list of connectors to remove
	 */
	void removeConnectors(List<Connector> connectors);

	/**
	 * Determines whether an {@link AssemblyConnector} with specific properties
	 * exists.
	 * 
	 * @param fromCtx     the source assembly context
	 * @param fromReqRole the required role of the source assembly context
	 * @param toCtx       the target assembly context
	 * @param toProvRole  the provided role of the target assembly context
	 * @return true, if a connector with the specified properties exists, false
	 *         otherwise
	 */
	boolean hasConnector(AssemblyContext fromCtx, OperationRequiredRole fromReqRole, AssemblyContext toCtx,
			OperationProvidedRole toProvRole);

	/**
	 * Determines whether an {@link ProvidedDelegationConnector} with specific
	 * properties exists.
	 * 
	 * @param outerProvidedRole the provided role of the enclosing system
	 * @param ctx               the inner assembly context that provides the role
	 * @param innerProvidedRole the provided role of the inner assembly context
	 * @return true, if a connector with the specified properties exists, false
	 *         otherwise
	 */
	boolean hasConnector(OperationProvidedRole outerProvidedRole, AssemblyContext ctx,
			OperationProvidedRole innerProvidedRole);

	/**
	 * Determines whether an {@link RequiredDelegationConnector} with specific
	 * properties exists.
	 * 
	 * @param outerRequiredRole the required role of the enclosing system
	 * @param ctx               the inner assembly context that requires the role
	 * @param innerRequiredRole the required role of the inner assembly context
	 * @return true, if a connector with the specified properties exists, false
	 *         otherwise
	 */
	boolean hasConnector(OperationRequiredRole outerRequiredRole, AssemblyContext ctx,
			OperationRequiredRole innerRequiredRole);

	/**
	 * Creates an {@link AssemblyConnector} with given properties.
	 * 
	 * @param sourceCtx      the source assembly context
	 * @param sourceReqRole  the required role of the source assembly context
	 * @param targetCtx      the target assembly context
	 * @param targetProvRole the provided role of the target assembly context
	 * @return the newly created connector with the specified characteristics
	 */
	AssemblyConnector createConnector(AssemblyContext sourceCtx, OperationRequiredRole sourceReqRole,
			AssemblyContext targetCtx, OperationProvidedRole targetProvRole);

	/**
	 * Removes an existing connector of a provided role delegation and replaces it
	 * with a new one.
	 * 
	 * @param systemProvidedRole the provided role of the system
	 * @param ctx                the inner assembly context
	 * @param role               the corresponding provided role of the inner
	 *                           assembly context
	 */
	void reconnectOuterProvidedRole(OperationProvidedRole systemProvidedRole, AssemblyContext ctx,
			OperationProvidedRole role);

	/**
	 * Removes an existing connector of a required role delegation and replaces it
	 * with a new one.
	 * 
	 * @param selectedOuter the required role of the system
	 * @param left          the inner assembly context
	 * @param right         the corresponding required role of the inner assembly
	 *                      context
	 */
	void reconnectOuterRequiredRole(OperationRequiredRole selectedOuter, AssemblyContext left,
			OperationRequiredRole right);

	/**
	 * Gets a list of pairs (assembly context, required role) of inner roles that
	 * are not satisfied/connected yet.
	 * 
	 * @return list of pairs (assembly context, required role) of inner roles that
	 *         are not satisfied/connected yet
	 */
	List<Pair<AssemblyContext, OperationRequiredRole>> getUnsatisfiedInnerRequiredRoles();

	/**
	 * Gets a list of provided roles of the system that are not delegated yet.
	 * 
	 * @return list of provided roles of the system that are not delegated yet
	 */
	List<OperationProvidedRole> getUnsatisfiedOuterProvidedRoles();

	/**
	 * Gets a list of outer required roles of the system that are not used
	 * 
	 * @return list of required roles of the system that are not used
	 */
	List<OperationRequiredRole> getNonLinkedOuterRequiredRoles();

	// more logic
	/**
	 * Removes all connectors that are inconsistent with the given one. The target
	 * is to ensure that the OCL constraints of the system model are fulfilled after
	 * adding the new connector.
	 * 
	 * @param connector the connector for which all inconsistent other connectors
	 *                  should be removed
	 */
	void removeInconsistentConnectors(AssemblyConnector connector);

	/**
	 * Processes all assembly contexts that are not reachable in the system (for
	 * example if they are not connected). Depending on the return values of the
	 * deprecation processor, they are removed or not.
	 * 
	 * @param deprecationProcessor determines whether a specific assembly should be
	 *                             removed or not
	 */
	void processUnreachableAssemblys(IDeprecationProcessor deprecationProcessor);

	/**
	 * Determines whether a specific service is an entry call (at the border of the
	 * system). An entry call is an action where the user directly triggers a
	 * function of the system (which is provided via a provided role).
	 * 
	 * @param seff the service to check whether it is an entry call
	 * @return true, if it is an entry call, false otherwise
	 */
	boolean isEntryCall(ResourceDemandingSEFF seff);

}
