package dmodel.pipeline.rt.pcm.system;

import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ComposedStructure;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.system.System;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;

import dmodel.pipeline.shared.pcm.util.PCMUtils;
import dmodel.pipeline.shared.pcm.util.deprecation.IDeprecationProcessor;
import dmodel.pipeline.shared.pcm.util.system.PCMSystemUtil;
import dmodel.pipeline.shared.structure.DirectedGraph;
import dmodel.pipeline.shared.structure.Tree;
import dmodel.pipeline.shared.structure.Tree.TreeNode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
// TODO refactor
// TODO linking to outside wrong if there is a back change laterly -> use a timestamp to the roles which is used to calculate their oder
public class RuntimeSystemBuilder {
	private System system;
	private Set<String> currentlyContainingAssemblyIds;
	private Set<String> addedAssemblyIds;

	private IDeprecationProcessor deprecationProcessor;

	public RuntimeSystemBuilder(IDeprecationProcessor deprecationProcessor) {
		this.deprecationProcessor = deprecationProcessor;
	}

	public void mergeSystem(Allocation allocationModel, System currentSystem,
			List<Tree<Pair<AssemblyContext, ResourceDemandingSEFF>>> trees) {
		system = currentSystem;
		currentlyContainingAssemblyIds = currentSystem.getAssemblyContexts__ComposedStructure().stream()
				.map(asmbly -> asmbly.getId()).collect(Collectors.toSet());
		addedAssemblyIds = Sets.newHashSet();

		// add all necessary assembly contexts
		trees.forEach(tree -> {
			addTreeAssemblysRecursive(tree.getRoot());
		});

		// merge assemblys
		trees.forEach(tree -> {
			List<String> added = Lists.newArrayList();
			List<String> removed = Lists.newArrayList();
			processTreeNodeRecursive(tree.getRoot(), removed, added);

			currentlyContainingAssemblyIds.removeAll(removed);
			currentlyContainingAssemblyIds.addAll(added);
		});

		// expose open roles
		exposeProvidedRoles();

		// remove unnecessary (soft)
		findAndProcessUnnecessary(allocationModel);
	}

	private void findAndProcessUnnecessary(Allocation allocation) {
		DirectedGraph<String, Integer> assemblyInvocationGraph = new DirectedGraph<>();

		// process connectors
		// this is only linear and therefore better
		system.getConnectors__ComposedStructure().stream().forEach(c -> {
			if (c instanceof AssemblyConnector) {
				assemblyInvocationGraph.addEdge(
						((AssemblyConnector) c).getRequiringAssemblyContext_AssemblyConnector().getId(),
						((AssemblyConnector) c).getProvidingAssemblyContext_AssemblyConnector().getId(), 0);
			} else if (c instanceof ProvidedDelegationConnector) {
				assemblyInvocationGraph.addEdge(
						((ProvidedDelegationConnector) c).getOuterProvidedRole_ProvidedDelegationConnector()
								.getProvidingEntity_ProvidedRole().getId(),
						((ProvidedDelegationConnector) c).getAssemblyContext_ProvidedDelegationConnector().getId(), 0);
			} else if (c instanceof RequiredDelegationConnector) {
				assemblyInvocationGraph.addEdge(
						((RequiredDelegationConnector) c).getAssemblyContext_RequiredDelegationConnector().getId(),
						((RequiredDelegationConnector) c).getOuterRequiredRole_RequiredDelegationConnector()
								.getRequiringEntity_RequiredRole().getId(),
						0);
			}
		});

		Set<String> marked = Sets.newHashSet();
		// create transitive closure
		Stack<String> currentNodes = new Stack<>();
		currentNodes.push(system.getId());
		while (currentNodes.size() > 0) {
			String node = currentNodes.pop();
			marked.add(node);

			List<Pair<String, Integer>> outgoingEdges = assemblyInvocationGraph.getOutgoingEdges(node);
			if (outgoingEdges != null) {
				for (Pair<String, Integer> child : assemblyInvocationGraph.getOutgoingEdges(node)) {
					if (!marked.contains(child.getLeft())) {
						currentNodes.add(child.getLeft());
					}
				}
			}
		}

		// process all assembly context that are not needed anymore
		List<AssemblyContext> deprecatedAssemblys = PCMUtils.getElementsByType(system, AssemblyContext.class).stream()
				.filter(ac -> {
					return !marked.contains(ac.getId());
				}).collect(Collectors.toList());

		deprecatedAssemblys.forEach(da -> {
			if (deprecationProcessor.shouldDelete(da)) {
				// remove all structures that are involved too!
				ComposedStructure cs = (ComposedStructure) da.eContainer();
				cs.getAssemblyContexts__ComposedStructure().remove(da);

				List<Connector> connectors = resolveCorrespondingConnectors(da);
				system.getConnectors__ComposedStructure().removeAll(connectors);

				// remove all allocations
				PCMUtils.getElementsByType(allocation, AllocationContext.class).forEach(ac -> {
					if (ac.getAssemblyContext_AllocationContext().getId().equals(da.getId())) {
						allocation.getAllocationContexts_Allocation().remove(ac);
					}
				});
			}
		});
		deprecationProcessor.iterationFinished();
	}

	private List<Connector> resolveCorrespondingConnectors(AssemblyContext da) {
		return system.getConnectors__ComposedStructure().stream().filter(conn -> {
			if (conn instanceof AssemblyConnector) {
				return ((AssemblyConnector) conn).getProvidingAssemblyContext_AssemblyConnector().getId()
						.equals(da.getId())
						|| ((AssemblyConnector) conn).getRequiringAssemblyContext_AssemblyConnector().getId()
								.equals(da.getId());
			} else if (conn instanceof ProvidedDelegationConnector) {
				return ((ProvidedDelegationConnector) conn).getAssemblyContext_ProvidedDelegationConnector().getId()
						.equals(da.getId());
			} else if (conn instanceof RequiredDelegationConnector) {
				return ((RequiredDelegationConnector) conn).getAssemblyContext_RequiredDelegationConnector().getId()
						.equals(da.getId());
			}
			return false;
		}).collect(Collectors.toList());
	}

	private void exposeProvidedRoles() {
		List<AssemblyProvidedRole> allProvidedRoles = system.getAssemblyContexts__ComposedStructure().stream()
				.map(asmbly -> asmbly.getEncapsulatedComponent__AssemblyContext()
						.getProvidedRoles_InterfaceProvidingEntity().stream()
						.map(t -> new AssemblyProvidedRole(asmbly, t)).collect(Collectors.toList()))
				.flatMap(List::stream).collect(Collectors.toList());

		system.getConnectors__ComposedStructure().forEach(conn -> {
			if (conn instanceof AssemblyConnector) {
				allProvidedRoles.remove(new AssemblyProvidedRole(
						((AssemblyConnector) conn).getProvidingAssemblyContext_AssemblyConnector(),
						((AssemblyConnector) conn).getProvidedRole_AssemblyConnector()));
			}
		});

		// process the remaining roles
		List<ProvidedRole> systemProvidingRoles = system.getProvidedRoles_InterfaceProvidingEntity();
		systemProvidingRoles.forEach(spr -> {
			if (spr instanceof OperationProvidedRole) {
				List<AssemblyProvidedRole> matchingInnerRoles = allProvidedRoles.stream().filter(pr -> {
					if (pr.role instanceof OperationProvidedRole) {
						return ((OperationProvidedRole) pr.role).getProvidedInterface__OperationProvidedRole().getId()
								.equals(((OperationProvidedRole) spr).getProvidedInterface__OperationProvidedRole()
										.getId());
					}
					return false;
				}).collect(Collectors.toList());

				if (matchingInnerRoles.size() == 0) {
					log.warning("There is no inner role which matches the exposed role of the system.");
				} else {
					AssemblyProvidedRole innerProvided = matchingInnerRoles.stream().min((a, b) -> {
						if (addedAssemblyIds.contains(a.ctx.getId())) {
							if (addedAssemblyIds.contains(b.ctx.getId())) {
								return 0;
							} else {
								return -1;
							}
						} else if (addedAssemblyIds.contains(b.ctx.getId())) {
							return 1;
						} else {
							return 0;
						}
					}).get();

					if (!system.getConnectors__ComposedStructure().stream().anyMatch(c -> {
						if (c instanceof ProvidedDelegationConnector) {
							ProvidedDelegationConnector pdc = (ProvidedDelegationConnector) c;
							return pdc.getAssemblyContext_ProvidedDelegationConnector().getId()
									.equals(innerProvided.ctx.getId())
									&& pdc.getInnerProvidedRole_ProvidedDelegationConnector().getId()
											.equals(innerProvided.role.getId())
									&& pdc.getOuterProvidedRole_ProvidedDelegationConnector().getId()
											.equals(spr.getId());
						}
						return false;
					})) {
						// delete all old delegations that contain this role
						List<ProvidedDelegationConnector> oldConnectors = PCMUtils
								.getElementsByType(system, ProvidedDelegationConnector.class).stream().filter(d -> {
									return d.getOuterProvidedRole_ProvidedDelegationConnector().getId()
											.equals(spr.getId());
								}).collect(Collectors.toList());
						system.getConnectors__ComposedStructure().removeAll(oldConnectors);

						// add
						PCMSystemUtil.createProvidedDelegation(system, (OperationProvidedRole) innerProvided.role,
								innerProvided.ctx, (OperationProvidedRole) spr);
					}
				}
			}
		});
	}

	private void addTreeAssemblysRecursive(TreeNode<Pair<AssemblyContext, ResourceDemandingSEFF>> root) {
		if (!currentlyContainingAssemblyIds.contains(root.getData().getLeft().getId())) {
			root.getData().getLeft().setEntityName(
					"Assembly_" + root.getData().getLeft().getEncapsulatedComponent__AssemblyContext().getEntityName());
			system.getAssemblyContexts__ComposedStructure().add(root.getData().getLeft());
			addedAssemblyIds.add(root.getData().getLeft().getId());
		}

		root.getChildren().forEach(child -> {
			addTreeAssemblysRecursive(child);
		});
	}

	private void processTreeNodeRecursive(TreeNode<Pair<AssemblyContext, ResourceDemandingSEFF>> parent,
			List<String> removedAssemblys, List<String> addedAssemblys) {
		Pair<AssemblyContext, ResourceDemandingSEFF> parentData = parent.getData();
		boolean newParent = !currentlyContainingAssemblyIds.contains(parentData.getLeft().getId())
				|| deprecationProcessor.isCurrentlyDeprecated(parentData.getLeft());

		parent.getChildren().forEach(child -> {
			boolean newChild = !currentlyContainingAssemblyIds.contains(child.getData().getLeft().getId())
					|| deprecationProcessor.isCurrentlyDeprecated(child.getData().getLeft());

			if (!newParent && !newChild) {
				processTreeNodeRecursive(child, removedAssemblys, addedAssemblys);
			} else {
				// create a new assembly connector for the specific role
				OperationProvidedRole providedRole = child.getData().getLeft()
						.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity()
						.stream().filter(pr -> {
							if (pr instanceof OperationProvidedRole) {
								return ((OperationProvidedRole) pr).getProvidedInterface__OperationProvidedRole()
										.getSignatures__OperationInterface().stream()
										.anyMatch(sig -> sig.getId().equals(
												child.getData().getRight().getDescribedService__SEFF().getId()));
							}
							return false;
						}).map(r -> (OperationProvidedRole) r).findFirst().orElse(null);

				OperationRequiredRole requiredRole = parent.getData().getLeft()
						.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity()
						.stream().filter(rr -> {
							if (rr instanceof OperationRequiredRole) {
								return ((OperationRequiredRole) rr).getRequiredInterface__OperationRequiredRole()
										.getSignatures__OperationInterface().stream()
										.anyMatch(sig -> sig.getId().equals(
												child.getData().getRight().getDescribedService__SEFF().getId()));
							}
							return false;
						}).map(r -> (OperationRequiredRole) r).findFirst().orElse(null);

				if (providedRole != null && requiredRole != null) {
					// remove the old connector
					if (!newChild ^ !newParent) {
						log.info("Search old container.");
						AssemblyConnector conn = system.getConnectors__ComposedStructure().stream().filter(c -> {
							if (c instanceof AssemblyConnector) {
								AssemblyConnector ac = (AssemblyConnector) c;
								return ((ac.getProvidedRole_AssemblyConnector().getId().equals(providedRole.getId())
										&& ac.getProvidingAssemblyContext_AssemblyConnector().getId()
												.equals(child.getData().getLeft().getId())
										&& !newChild)
										|| (ac.getRequiredRole_AssemblyConnector().getId().equals(requiredRole.getId())
												&& ac.getRequiringAssemblyContext_AssemblyConnector().getId()
														.equals(parentData.getLeft().getId())
												&& !newParent));
							}
							return false;
						}).map(c -> (AssemblyConnector) c).findFirst().orElse(null);

						if (conn != null) {
							log.fine("Delete old connector.");
							system.getConnectors__ComposedStructure().remove(conn);

							AssemblyContext removedAssembly = !newChild
									? conn.getRequiringAssemblyContext_AssemblyConnector()
									: conn.getProvidingAssemblyContext_AssemblyConnector();
							AssemblyContext addedAssembly = !newChild
									? conn.getProvidingAssemblyContext_AssemblyConnector()
									: conn.getRequiringAssemblyContext_AssemblyConnector();
							removedAssemblys.add(removedAssembly.getId());
							addedAssemblys.add(addedAssembly.getId());
						}

					} else if (newChild && newParent) {
						addedAssemblys.add(parentData.getLeft().getId());
						addedAssemblys.add(child.getData().getLeft().getId());
					}

					log.fine("Create new connector.");
					PCMSystemUtil.createAssemblyConnector(system, providedRole, child.getData().getLeft(), requiredRole,
							parent.getData().getLeft());
				}

				// recursion
				processTreeNodeRecursive(child, removedAssemblys, addedAssemblys);
			}
		});
	}

	@AllArgsConstructor
	@NoArgsConstructor
	private class AssemblyProvidedRole {
		private AssemblyContext ctx;
		private ProvidedRole role;

		@Override
		public boolean equals(Object o) {
			if (o instanceof AssemblyProvidedRole) {
				return ((AssemblyProvidedRole) o).ctx.getId().equals(ctx.getId())
						&& ((AssemblyProvidedRole) o).role.getId().equals(role.getId());
			} else {
				return false;
			}
		}
	}

}
