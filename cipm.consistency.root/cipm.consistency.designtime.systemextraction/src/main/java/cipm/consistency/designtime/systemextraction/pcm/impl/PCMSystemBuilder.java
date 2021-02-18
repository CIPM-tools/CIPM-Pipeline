package cipm.consistency.designtime.systemextraction.pcm.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.system.SystemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import cipm.consistency.base.core.facade.pcm.IRepositoryQueryFacade;
import cipm.consistency.base.core.health.AbstractHealthStateComponent;
import cipm.consistency.base.core.health.HealthState;
import cipm.consistency.base.core.health.HealthStateObservedComponent;
import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph;
import cipm.consistency.base.shared.pcm.util.system.PCMSystemUtil;
import cipm.consistency.designtime.systemextraction.pcm.IAssemblySelectionListener;
import cipm.consistency.designtime.systemextraction.pcm.IConnectionConflictListener;
import cipm.consistency.designtime.systemextraction.pcm.data.AbstractConflict;
import cipm.consistency.designtime.systemextraction.pcm.data.AssemblyConflict;
import cipm.consistency.designtime.systemextraction.pcm.data.ConnectionConflict;
import cipm.consistency.designtime.systemextraction.pcm.impl.util.ConflictBuilder;
import cipm.consistency.designtime.systemextraction.pcm.impl.util.ServiceCallGraphProcessor;
import cipm.consistency.designtime.systemextraction.pcm.impl.util.Xor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;

@Component
@Log
/**
 * Main building block for creating system models at design-time. Implements a
 * user-guided procedure which is conflict driven. The conflicts are resolved
 * automatically by using a corresponding service call graph (SCG). If there are
 * conflicts which cannot be resolved at design-time with the help of the given
 * SCG, the user needs to resolve them manually.
 * 
 * @author David Monschein
 *
 */
public class PCMSystemBuilder extends AbstractHealthStateComponent {
	/**
	 * Listeners for events of the system building procedure.
	 */
	private List<IConnectionConflictListener> connectionConflictListener;
	private List<IAssemblySelectionListener> assemblySelectionListener;

	/**
	 * Excluded procedures for creating conflicts.
	 */
	@Autowired
	private ConflictBuilder conflictBuilder;

	// current data
	@Getter
	private AbstractConflict<?> currentConflict;

	/**
	 * Open required and provided roles of existing assembly contexts.
	 */
	private List<AssemblyProvidedRole> openProvidedRoles;
	private List<AssemblyRequiredRole> openRequiredRoles;

	/**
	 * Excluded procedures for processing the service call graph (SCG).
	 */
	private ServiceCallGraphProcessor scgProcessor;

	/**
	 * Simple counter to keep the entity names of the assembly contexts unique.
	 */
	private int assemblyNamingId;

	/**
	 * Current system that is built and the repository which is used.
	 */
	@Getter
	private System currentSystem;

	@Autowired
	private IRepositoryQueryFacade baseRepository;

	/**
	 * Used to iterate over the interfaces that should be provided by the resulting
	 * system in the end.
	 */
	private Iterator<OperationInterface> entryPoints;

	/**
	 * Creates a new instance and initializes empty listener lists.
	 */
	public PCMSystemBuilder() {
		super(HealthStateObservedComponent.DT_SYSTEM_BUILDER, HealthStateObservedComponent.MODEL_MANAGER);
		this.assemblySelectionListener = new ArrayList<>();
		this.connectionConflictListener = new ArrayList<>();
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// nothing to do here
	}

	/**
	 * Starts the System model building procedure.
	 * 
	 * @param serviceCallGraph an input SCG that represents the calls structure of
	 *                         the services
	 * @param systemInterfaces the interfaces that should be provided by the whole
	 *                         System in the end
	 * @return true if the building process finished, false if there is a conflict
	 *         that needs to be resolved
	 */
	public boolean startBuildingSystem(ServiceCallGraph serviceCallGraph, List<OperationInterface> systemInterfaces) {
		if (!checkPreconditions()) {
			return false;
		}

		// 0. create output system
		currentSystem = SystemFactory.eINSTANCE.createSystem();

		// 1.1. find entry points to the call graph
		entryPoints = systemInterfaces.iterator();

		// 1.2. containers
		openProvidedRoles = Lists.newArrayList();
		openRequiredRoles = Lists.newArrayList();

		// 1.3. default values
		assemblyNamingId = 0;
		currentConflict = null;
		scgProcessor = new ServiceCallGraphProcessor(serviceCallGraph);

		// 2. try to build a system and corresponding conflicts that need to be
		// resolved
		return buildingStep();
	}

	/**
	 * Continues the building process after a conflict.
	 * 
	 * @return true if the building process is finished, false if there is a another
	 *         conflict
	 */
	public boolean continueBuilding() {
		if (currentConflict != null && currentConflict.isSolved()) {
			if (currentConflict instanceof ConnectionConflict) {
				return resolveConnectionConflict((ConnectionConflict) currentConflict);
			} else if (currentConflict instanceof AssemblyConflict) {
				return resolveAssemblyConflict((AssemblyConflict) currentConflict);
			}
		} else {
			throw new IllegalStateException("Cannot continue if there is a conflict that is not resolved.");
		}
		return false;
	}

	/**
	 * Resolves the current {@link AssemblyConflict} if there is a solution and it
	 * is marked as solved. Afterwards, the building procedure is triggered again.
	 * 
	 * @param currentConflict the current conflict which should be resolved
	 * @return true, if the consecutive building procedure is finished, false if
	 *         there is another conflict that needs to be resolved
	 */
	private boolean resolveAssemblyConflict(AssemblyConflict currentConflict) {
		if (currentConflict.getTarget().anyPresent()) {
			if (currentConflict.getSolution() == null) {
				// => new assembly
				AssemblyContext ctx = createNewAssembly(currentConflict.getCorrespondingComponent());
				currentConflict.setSolution(ctx);
			}

			AssemblyProvidedRole provRole = pickOpenProvidedRole(currentConflict.getSolution(),
					getTargetInterface(currentConflict.getTarget()));
			connect(currentConflict.getTarget(), provRole);

			// conflict solved
			this.currentConflict = null;
			return buildingStep();
		}

		return false;
	}

	/**
	 * Resolves a {@link ConnectionConflict} if there is a solution and it is marked
	 * as solved. Afterwards the building procedure is resumed.
	 * 
	 * @param currentConflict the current conflict which should be resolved
	 * @return true, if the consecutive building procedure is finished, false if
	 *         there is another conflict that needs to be resolved
	 */
	private boolean resolveConnectionConflict(ConnectionConflict currentConflict) {
		if (currentConflict.getTarget().anyPresent()) {
			if (currentConflict.getSolution() != null) {
				// get inner provided role
				if (currentConflict.getTarget().anyPresent()) {
					AssemblyProvidedRole suppliedAssembly = supplyAssembly(currentConflict.getTarget(),
							currentConflict.getSolution(), getTargetInterface(currentConflict.getTarget()));

					if (suppliedAssembly != null) {
						connect(currentConflict.getTarget(), suppliedAssembly);
					}
				}
			} else {
				// link to outside -> required delegation
				if (currentConflict.getTarget().leftPresent()) {
					// right present does not make sense at this point
					OperationRequiredRole outerRequiredRole = PCMSystemUtil.createRequiredRole(currentSystem,
							getTargetInterface(currentConflict.getTarget()));
					PCMSystemUtil.createRequiredDelegation(currentConflict.getTarget().getLeft().ctx,
							currentConflict.getTarget().getLeft().role, currentSystem, outerRequiredRole);
				}
			}

			this.currentConflict = null;
			return buildingStep();
		}
		return false;
	}

	/**
	 * Triggers a single step in the system model building procedure. Consecutive
	 * steps are triggered if no conflict occurs. The procedure is stopped when
	 * there is a conflict.
	 * 
	 * @return true if the procedure finished without any conflict, false if there
	 *         is a conflict that needs to be resolved before the procedure can
	 *         continue
	 */
	private boolean buildingStep() {
		log.info("Executing a building step.");
		// if there is a conflict we cannot do anything
		if (currentConflict != null) {
			return false;
		}

		// open entry point?
		Optional<Boolean> entryPointResult = processEntryPoints();
		if (entryPointResult.isPresent()) {
			return entryPointResult.get();
		}

		// process open provided roles
		return processOpenRequiredRoles();
	}

	/**
	 * Processes an interface that should be provided by the system in the end. It
	 * creates a provided role of the system and tries to delegate it. If there is
	 * no interface left that should be provided by the system the method exits
	 * without doing anything.
	 * 
	 * @return an empty optional if there were no interfaces left that should be
	 *         provided by the system, otherwise an optional which contains a value
	 *         analogous to the return of {@link PCMSystemBuilder#buildingStep()}
	 */
	private Optional<Boolean> processEntryPoints() {
		if (entryPoints.hasNext()) {
			// pop an entry point
			OperationInterface entryPoint = entryPoints.next();

			// create the belonging role
			OperationProvidedRole outerProvidedRole = PCMSystemUtil.createProvidedRole(currentSystem, entryPoint);
			SystemProvidedRole outerProvidedRolePlain = new SystemProvidedRole(outerProvidedRole, currentSystem);

			// get inner provided role
			AssemblyProvidedRole innerProvidedRole = supplyProvidedRole(
					Xor.<AssemblyRequiredRole, SystemProvidedRole>right(outerProvidedRolePlain), null);

			if (innerProvidedRole != null) {
				// => no conflict
				createDelegation(outerProvidedRolePlain, innerProvidedRole);

				return Optional.of(buildingStep());
			} else {
				if (currentConflict != null) {
					// conflict has already been created
					return Optional.of(false);
				} else {
					// => next step
					return Optional.of(buildingStep());
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * Processes a required role that is not satisfied yet.
	 * 
	 * @return analogous to {@link PCMSystemBuilder#buildingStep()} it returns true
	 *         if the procedure finished without any conflict, false if there is a
	 *         conflict that needs to be resolved before the procedure can continue
	 */
	private boolean processOpenRequiredRoles() {
		if (!openRequiredRoles.isEmpty()) {
			// pop a required role
			AssemblyRequiredRole requiredRole = openRequiredRoles.remove(0);

			// get provided role
			AssemblyProvidedRole connectedProvidedRole = supplyProvidedRole(
					Xor.<AssemblyRequiredRole, SystemProvidedRole>left(requiredRole),
					requiredRole.ctx.getEncapsulatedComponent__AssemblyContext());

			if (connectedProvidedRole != null) {
				// no conflict
				createConnector(requiredRole, connectedProvidedRole);

				return buildingStep();
			} else {
				if (currentConflict != null) {
					// conflict has already been created
					return false;
				} else {
					// => next step
					return buildingStep();
				}
			}
		}

		super.removeAllProblems();
		super.updateState();
		return true;
	}

	/**
	 * Gets the target interface of a required role or a provided role by the
	 * system.
	 * 
	 * @param target a required role or a provided role by the system
	 * @return the interface that is required or provided by the parameter
	 */
	private OperationInterface getTargetInterface(Xor<AssemblyRequiredRole, SystemProvidedRole> target) {
		if (target.leftPresent()) {
			return target.getLeft().getRole().getRequiredInterface__OperationRequiredRole();
		} else if (target.rightPresent()) {
			return target.getRight().getRole().getProvidedInterface__OperationProvidedRole();
		}
		return null;
	}

	/**
	 * Either creates a connector between a required and a provided role or creates
	 * a delegation from a system provided role to a provided role of an assembly,
	 * depending on the parameter.
	 * 
	 * @param target   a required role of an assembly or a provided role of the
	 *                 surrounding system
	 * @param provRole inner provided role of an assembly the outer provided role or
	 *                 the inner required role should be connected to
	 */
	private void connect(Xor<AssemblyRequiredRole, SystemProvidedRole> target, AssemblyProvidedRole provRole) {
		if (target.leftPresent()) {
			createConnector(target.getLeft(), provRole);
		} else if (target.rightPresent()) {
			createDelegation(target.getRight(), provRole);
		}
	}

	/**
	 * Connect a required role of an assembly with a provided role of an assembly
	 * using a {@link AssemblyConnector}.
	 * 
	 * @param reqRole  required role of an assembly
	 * @param provRole provided role of an assembly
	 */
	private void createConnector(AssemblyRequiredRole reqRole, AssemblyProvidedRole provRole) {
		PCMSystemUtil.createAssemblyConnector(currentSystem, provRole.role, provRole.ctx, reqRole.role, reqRole.ctx);
	}

	/**
	 * Delegates a provided role of the system to a provided role of an assembly
	 * that is contained in the system using a {@link ProvidedDelegationConnector}.
	 * 
	 * @param provRole      provided role of the system
	 * @param provRoleInner provided role of an assembly
	 */
	private void createDelegation(SystemProvidedRole provRole, AssemblyProvidedRole provRoleInner) {
		PCMSystemUtil.createProvidedDelegation(provRole.system, provRole.role, provRoleInner.ctx, provRoleInner.role);
	}

	/**
	 * Supplies a provided role of an assembly for a given target (either a required
	 * role of an assembly or a provided role of the surrounding system). It creates
	 * a conflict if the information that is provided is not enough to determine the
	 * corresponding assembly and provided role.
	 * 
	 * @param target either a required role of an assembly or a provided role of the
	 *               surrounding system
	 * @param from   the component of the target assembly, or null if the target is
	 *               the system
	 * @return a provided role of an assembly context or null if there was a
	 *         conflict
	 */
	private AssemblyProvidedRole supplyProvidedRole(Xor<AssemblyRequiredRole, SystemProvidedRole> target,
			RepositoryComponent from) {
		OperationInterface targetInterface = getTargetInterface(target);
		RepositoryComponent comp = supplyComponent(target, targetInterface, from);

		if (comp == null) {
			if (currentConflict == null) {
				// no possible solution => delegate required role
				if (target.leftPresent()) {
					OperationRequiredRole outerRequiredRole = PCMSystemUtil.createRequiredRole(currentSystem,
							targetInterface);
					PCMSystemUtil.createRequiredDelegation(target.getLeft().ctx, target.getLeft().role, currentSystem,
							outerRequiredRole);
				} else {
					log.warning(
							"A provided role of the system can not be satisfied because there is no component that provides the corresponding interface.");
				}
			}

			return null;
		}

		return supplyAssembly(target, comp, targetInterface);
	}

	/**
	 * Supplies an assembly with a corresponding provided role for a given component
	 * and a given target (either a required role of an assembly or a provided role
	 * of the surrounding system).
	 * 
	 * @param target either a required role of an assembly or a provided role of the
	 *               surrounding system
	 * @param comp   a component which should be used to satisfy the target
	 * @param iface  the interface of the target and the role that should be
	 *               provided
	 * @return an assembly with a corresponding provided role for a given component
	 *         and a given target, null if there is a conflict
	 */
	private AssemblyProvidedRole supplyAssembly(Xor<AssemblyRequiredRole, SystemProvidedRole> target,
			RepositoryComponent comp, OperationInterface iface) {
		List<AssemblyProvidedRole> possibleRoles = openProvidedRoles.stream()
				.filter(opr -> opr.ctx.getEncapsulatedComponent__AssemblyContext().getId().equals(comp.getId()))
				.collect(Collectors.toList());

		if (possibleRoles.size() == 0) {
			// create one necessarily
			return pickOpenProvidedRole(createNewAssembly(comp), iface);
		} else {
			// collect possibilities and create conflict
			List<AssemblyContext> possibleACtxs = possibleRoles.stream().map(pr -> pr.ctx).collect(Collectors.toList());
			publishConflict(conflictBuilder.createAssemblyConflict(target, possibleACtxs, comp));
			return null;
		}
	}

	/**
	 * Supplies a component a given target (either a required role of an assembly or
	 * a provided role of the surrounding system), a given interface that should be
	 * provided and a given component that uses the component that should be
	 * selected. The third parameter (component) is used to filter the possible
	 * components using the service call graph (SCG).
	 * 
	 * @param target either a required role of an assembly or a provided role of the
	 *               surrounding system
	 * @param iface  interface that should be provided
	 * @param from   component that uses the component that should be selected
	 * @return component which provides the needed interface for the given target
	 */
	private RepositoryComponent supplyComponent(Xor<AssemblyRequiredRole, SystemProvidedRole> target,
			OperationInterface iface, RepositoryComponent from) {
		List<RepositoryComponent> possibleComponents = baseRepository.getComponentsProvidingInterface(iface);
		List<RepositoryComponent> filteredComponents = scgProcessor.filterComponents(possibleComponents, from, target);

		if (filteredComponents.size() > 1) {
			// => conflict
			publishConflict(conflictBuilder.createConnectionConflict(target, filteredComponents));
			return null;
		} else if (filteredComponents.size() == 1) {
			return filteredComponents.iterator().next();
		} else {
			log.warning(
					"A required role can not be satisfied because there is no component in the repository that provides the required interface.");
			return null;
		}
	}

	/**
	 * Provides an assembly and a corresponding provided role for a given
	 * {@link AssemblyContext} and a given {@link OperationInterface}.
	 * 
	 * @param ctx   the assembly context
	 * @param iface the interface to search for
	 * @return an assembly and a corresponding provided role for the given interface
	 *         or null if there is no open provided role for that interface of the
	 *         given assembly
	 */
	private AssemblyProvidedRole pickOpenProvidedRole(AssemblyContext ctx, OperationInterface iface) {
		AssemblyProvidedRole selectedRole = openProvidedRoles.stream().filter(opr -> {
			return opr.ctx.getId().equals(ctx.getId())
					&& iface.getId().equals(opr.role.getProvidedInterface__OperationProvidedRole().getId());
		}).findFirst().orElse(null);

		if (selectedRole != null) {
			// support multiple use of roles
			// openProvidedRoles.remove(selectedRole);
			return selectedRole;
		} else {
			log.severe(
					"There is no available open role, even after creating a specific assembly (this should never happen).");
			return null;
		}
	}

	/**
	 * Creates a new assembly context for a given component. Adds the provided and
	 * required roles to the corresponding internal lists.
	 * 
	 * @param comp the component
	 * @return a new assembly context for the given component type
	 */
	private AssemblyContext createNewAssembly(RepositoryComponent comp) {
		AssemblyContext ctx = PCMSystemUtil.createAssemblyContext(currentSystem, comp,
				comp.getEntityName() + "_" + (assemblyNamingId++));

		// add all provided and required roles
		ctx.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity()
				.forEach(provRole -> {
					if (provRole instanceof OperationProvidedRole) {
						openProvidedRoles.add(new AssemblyProvidedRole(ctx, (OperationProvidedRole) provRole));
					}
				});
		ctx.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().forEach(reqRole -> {
			if (reqRole instanceof OperationRequiredRole) {
				openRequiredRoles.add(new AssemblyRequiredRole(ctx, (OperationRequiredRole) reqRole));
			}
		});

		return ctx;
	}

	/**
	 * Publishes a {@link ConnectionConflict}, which notifies all listeners and
	 * marks the conflict as active.
	 * 
	 * @param conflict the conflict that should be published
	 */
	private void publishConflict(ConnectionConflict conflict) {
		this.currentConflict = conflict;
		this.connectionConflictListener.forEach(l -> l.conflict(conflict));
	}

	/**
	 * Publishes a {@link AssemblyConflict}, which notifies all listeners and marks
	 * the conflict as active.
	 * 
	 * @param conflict the conflict that should be published
	 */
	private void publishConflict(AssemblyConflict conflict) {
		this.currentConflict = conflict;
		this.assemblySelectionListener.forEach(l -> l.conflict(conflict));
	}

	/**
	 * Simple data structure which contains an assembly context and a corresponding
	 * provided role.
	 * 
	 * @author David Monschein
	 *
	 */
	@AllArgsConstructor
	@Getter
	public class AssemblyProvidedRole {
		private AssemblyContext ctx;
		private OperationProvidedRole role;
	}

	/**
	 * Simple data structure which contains an assembly context and a corresponding
	 * required role.
	 * 
	 * @author David Monschein
	 *
	 */
	@AllArgsConstructor
	@Getter
	public class AssemblyRequiredRole {
		private AssemblyContext ctx;
		private OperationRequiredRole role;
	}

	/**
	 * Simple data structure which contains a system and a corresponding provided
	 * role.
	 * 
	 * @author David Monschein
	 *
	 */
	@AllArgsConstructor
	@Getter
	public class SystemProvidedRole {
		private OperationProvidedRole role;
		private System system;
	}

}
