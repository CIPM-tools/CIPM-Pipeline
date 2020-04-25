package dmodel.designtime.system.pcm.impl.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

import com.google.common.collect.Lists;

import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphNode;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.SystemProvidedRole;
import lombok.Getter;

public class ServiceCallGraphProcessor {
	private ServiceCallGraph scg;

	@Getter
	private List<ServiceCallGraphNode> entryNodes;

	public ServiceCallGraphProcessor(ServiceCallGraph serviceCallGraph) {
		this.scg = serviceCallGraph;

		searchEntryNodes();
	}

	// TODO refactor
	public List<RepositoryComponent> filterComponents(List<RepositoryComponent> init, RepositoryComponent from,
			Xor<AssemblyRequiredRole, SystemProvidedRole> target) {
		if (scg == null || !target.anyPresent()) {
			return init;
		}

		OperationInterface iface = target.leftPresent()
				? target.getLeft().getRole().getRequiredInterface__OperationRequiredRole()
				: target.getRight().getRole().getProvidedInterface__OperationProvidedRole();

		Set<RepositoryComponent> outLinks = new HashSet<>();
		if (target.rightPresent()) {
			entryNodes.forEach(n -> {
				OperationSignature sig = (OperationSignature) n.getSeff().getDescribedService__SEFF();
				if (sig.getInterface__OperationSignature().getId().equals(iface.getId())) {
					outLinks.add(n.getSeff().getBasicComponent_ServiceEffectSpecification());
				}
			});
		} else if (target.leftPresent()) {
			List<ServiceCallGraphNode> compNodes = scg.getNodes().stream().filter(
					n -> n.getSeff().getBasicComponent_ServiceEffectSpecification().getId().equals(from.getId()))
					.collect(Collectors.toList());

			if (compNodes.size() > 0) {
				compNodes.forEach(n -> {
					if (scg.getOutgoingEdges().containsKey(n)) {
						scg.getOutgoingEdges().get(n).forEach(out -> {
							OperationSignature opSig = (OperationSignature) out.getTo().getSeff()
									.getDescribedService__SEFF();
							if (opSig.getId().equals(iface.getId()) && out.getExternalCall().getRole_ExternalService()
									.getId().equals(target.getLeft().getRole().getId())) {
								outLinks.add(out.getTo().getSeff().getBasicComponent_ServiceEffectSpecification());
							}
						});
					}
				});
			}
		}

		List<RepositoryComponent> finalComps = Lists.newArrayList();
		init.forEach(i -> {
			if (outLinks.contains(i)) {
				finalComps.add(i);
			}
		});

		if (finalComps.size() == 0) {
			// warning
			return init;
		}

		return finalComps;
	}

	private void searchEntryNodes() {
		if (scg != null) {
			this.entryNodes = scg.getNodes().stream()
					.filter(n -> !scg.getIncomingEdges().containsKey(n) || scg.getIncomingEdges().get(n).size() == 0)
					.collect(Collectors.toList());
		}
	}
}
