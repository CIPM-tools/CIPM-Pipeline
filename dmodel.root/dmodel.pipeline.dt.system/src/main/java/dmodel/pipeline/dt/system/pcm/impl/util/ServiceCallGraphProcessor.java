package dmodel.pipeline.dt.system.pcm.impl.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode;
import lombok.Getter;

public class ServiceCallGraphProcessor {
	private ServiceCallGraph scg;

	@Getter
	private List<ServiceCallGraphNode> entryNodes;

	private Map<String, ServiceCallGraphNode> serviceNodeMapping;

	public ServiceCallGraphProcessor(ServiceCallGraph serviceCallGraph) {
		this.scg = serviceCallGraph;
		this.serviceNodeMapping = Maps.newHashMap();

		searchEntryNodes();
	}

	// TODO refactor
	public List<RepositoryComponent> filterComponents(List<RepositoryComponent> init, RepositoryComponent from,
			OperationInterface iface) {
		if (scg == null) {
			return init;
		}

		Set<RepositoryComponent> outLinks = new HashSet<>();
		if (from == null) {
			entryNodes.forEach(n -> {
				OperationSignature sig = (OperationSignature) n.getSeff().getDescribedService__SEFF();
				if (sig.getInterface__OperationSignature().getId().equals(iface.getId())) {
					outLinks.add(n.getSeff().getBasicComponent_ServiceEffectSpecification());
				}
			});
		} else {
			List<ServiceCallGraphNode> compNodes = scg.getNodes().stream().filter(
					n -> n.getSeff().getBasicComponent_ServiceEffectSpecification().getId().equals(from.getId()))
					.collect(Collectors.toList());

			if (compNodes.size() > 0) {
				compNodes.forEach(n -> {
					if (scg.getOutgoingEdges().containsKey(n)) {
						scg.getOutgoingEdges().get(n).forEach(out -> {
							OperationSignature opSig = (OperationSignature) out.getTo().getSeff()
									.getDescribedService__SEFF();
							if (opSig.getId().equals(iface.getId())) {
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
		this.entryNodes = scg.getNodes().stream()
				.filter(n -> !scg.getIncomingEdges().containsKey(n) || scg.getIncomingEdges().get(n).size() == 0)
				.collect(Collectors.toList());
	}
}
