package cipm.consistency.tools.evaluation.accuracy.models.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.core.entity.ComposedProvidingRequiringEntity;
import org.palladiosimulator.pcm.repository.InfrastructureProvidedRole;
import org.palladiosimulator.pcm.repository.InfrastructureRequiredRole;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.system.System;

import com.google.common.collect.Sets;

import de.uka.ipd.sdq.identifier.Identifier;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

// USES THE JACCARD COEFFICIENT
// AND THE STRUCTURE OF THE SYSTEM MODEL
@Log
public class PCMSystemComparator {

	// JACCARD COEFFICIENT
	public double compareSystems(System actual, System expected) {
		SystemComparatorState result = processComposition(actual, expected);
		return (double) result.matches / (double) (result.comparisons + result.additionals);
	}

	private SystemComparatorState processComposition(ComposedProvidingRequiringEntity actual,
			ComposedProvidingRequiringEntity expected) {
		Map<String, List<Identifier>> conformingObjects = new HashMap<>();

		// 1. get conforming objects for all
		for (AssemblyContext ac : expected.getAssemblyContexts__ComposedStructure()) {
			conformingObjects.put(ac.getId(), actual.getAssemblyContexts__ComposedStructure().stream()
					.filter(acc -> assemblysEqual(ac, acc)).collect(Collectors.toList()));
		}

		for (ProvidedRole provRole : expected.getProvidedRoles_InterfaceProvidingEntity()) {
			conformingObjects.put(provRole.getId(), actual.getProvidedRoles_InterfaceProvidingEntity().stream()
					.filter(provRoleI -> providingRolesEqual(provRoleI, provRole)).collect(Collectors.toList()));
		}

		for (RequiredRole reqRole : expected.getRequiredRoles_InterfaceRequiringEntity()) {
			conformingObjects.put(reqRole.getId(), actual.getRequiredRoles_InterfaceRequiringEntity().stream()
					.filter(reqRoleI -> requiredRolesEqual(reqRoleI, reqRole)).collect(Collectors.toList()));
		}

		for (Connector conn : expected.getConnectors__ComposedStructure()) {
			conformingObjects.put(conn.getId(), actual.getConnectors__ComposedStructure().stream()
					.filter(connI -> connectorEqual(connI, conn)).collect(Collectors.toList()));
		}

		// 2. calculate max matching set
		SystemComparatorState state = new SystemComparatorState(0, 0, 0, new HashSet<>());
		conformingObjects.entrySet().forEach(e -> {
			state.comparisons++;
			if (e.getValue().size() > 0) {
				Identifier selected = e.getValue().stream().filter(i -> !state.consumedElements.contains(i.getId()))
						.findFirst().orElse(null);
				if (selected != null) {
					state.matches++;
					state.consumedElements.add(selected.getId());
				} else {
					log.info("Failed to find a corresponding [equal] model element for element with ID: '" + e.getKey()
							+ "'");
				}
			}
		});

		// 3. set additionals
		state.additionals = (state.comparisons - state.matches) * 2;

		// 4. log not conforming elements
		conformingObjects.entrySet().stream().filter(f -> f.getValue().size() == 0).forEach(e -> {
			log.info("Failed to find a corresponding [equal] model element for element with ID: '" + e.getKey() + "'");
		});

		return state;
	}

	private boolean assemblysEqual(AssemblyContext ac, AssemblyContext acc) {
		return acc.getEncapsulatedComponent__AssemblyContext().getId()
				.equals(ac.getEncapsulatedComponent__AssemblyContext().getId());
	}

	private boolean connectorEqual(Connector connI, Connector conn) {
		if (connI.getClass().equals(conn.getClass())) {
			if (connI instanceof ProvidedDelegationConnector && conn instanceof ProvidedDelegationConnector) {
				ProvidedDelegationConnector pdr1 = (ProvidedDelegationConnector) connI;
				ProvidedDelegationConnector pdr2 = (ProvidedDelegationConnector) conn;
				return providingRolesEqual(pdr1.getInnerProvidedRole_ProvidedDelegationConnector(),
						pdr2.getInnerProvidedRole_ProvidedDelegationConnector())
						&& providingRolesEqual(pdr1.getOuterProvidedRole_ProvidedDelegationConnector(),
								pdr2.getOuterProvidedRole_ProvidedDelegationConnector())
						&& assemblysEqual(pdr1.getAssemblyContext_ProvidedDelegationConnector(),
								pdr2.getAssemblyContext_ProvidedDelegationConnector());
			} else if (connI instanceof RequiredDelegationConnector && conn instanceof RequiredDelegationConnector) {
				RequiredDelegationConnector rdr1 = (RequiredDelegationConnector) connI;
				RequiredDelegationConnector rdr2 = (RequiredDelegationConnector) conn;
				return requiredRolesEqual(rdr1.getInnerRequiredRole_RequiredDelegationConnector(),
						rdr2.getInnerRequiredRole_RequiredDelegationConnector())
						&& requiredRolesEqual(rdr1.getOuterRequiredRole_RequiredDelegationConnector(),
								rdr2.getOuterRequiredRole_RequiredDelegationConnector())
						&& assemblysEqual(rdr1.getAssemblyContext_RequiredDelegationConnector(),
								rdr2.getAssemblyContext_RequiredDelegationConnector());
			} else if (connI instanceof AssemblyConnector && conn instanceof AssemblyConnector) {
				AssemblyConnector ac1 = (AssemblyConnector) connI;
				AssemblyConnector ac2 = (AssemblyConnector) conn;

				return providingRolesEqual(ac1.getProvidedRole_AssemblyConnector(),
						ac2.getProvidedRole_AssemblyConnector())
						&& requiredRolesEqual(ac1.getRequiredRole_AssemblyConnector(),
								ac2.getRequiredRole_AssemblyConnector())
						&& assemblysEqual(ac1.getProvidingAssemblyContext_AssemblyConnector(),
								ac2.getProvidingAssemblyContext_AssemblyConnector())
						&& assemblysEqual(ac1.getRequiringAssemblyContext_AssemblyConnector(),
								ac2.getRequiringAssemblyContext_AssemblyConnector());
			}
		}
		return false;
	}

	private boolean requiredRolesEqual(RequiredRole r, RequiredRole rr) {
		if (r.getClass().equals(rr.getClass())) {
			if (rr instanceof OperationRequiredRole) {
				return ((OperationRequiredRole) rr).getRequiredInterface__OperationRequiredRole().getId()
						.equals(((OperationRequiredRole) r).getRequiredInterface__OperationRequiredRole().getId());
			} else if (rr instanceof InfrastructureProvidedRole) {
				return ((InfrastructureRequiredRole) rr).getRequiredInterface__InfrastructureRequiredRole().getId()
						.equals(((InfrastructureRequiredRole) r).getRequiredInterface__InfrastructureRequiredRole()
								.getId());
			}
		}
		return false;
	}

	private boolean providingRolesEqual(ProvidedRole p, ProvidedRole pr) {
		if (pr.getClass().equals(p.getClass())) {
			if (pr instanceof OperationProvidedRole) {
				return ((OperationProvidedRole) pr).getProvidedInterface__OperationProvidedRole().getId()
						.equals(((OperationProvidedRole) p).getProvidedInterface__OperationProvidedRole().getId());
			} else if (pr instanceof InfrastructureProvidedRole) {
				return ((InfrastructureProvidedRole) pr).getProvidedInterface__InfrastructureProvidedRole().getId()
						.equals(((InfrastructureProvidedRole) p).getProvidingEntity_ProvidedRole().getId());
			}
		}
		return false;
	}

	@NoArgsConstructor
	private static class SystemComparatorState {
		int matches = 0;
		int comparisons = 0;
		int additionals = 0;
		Set<String> consumedElements = Sets.newHashSet();

		public SystemComparatorState(int matches, int comparisons, int additionals, Set<String> consumedElements) {
			super();
			this.matches = matches;
			this.comparisons = comparisons;
			this.consumedElements = consumedElements;
			this.additionals = additionals;
		}

		protected SystemComparatorState clone() {
			return new SystemComparatorState(matches, comparisons, additionals, new HashSet<>(consumedElements));
		}
	}

}