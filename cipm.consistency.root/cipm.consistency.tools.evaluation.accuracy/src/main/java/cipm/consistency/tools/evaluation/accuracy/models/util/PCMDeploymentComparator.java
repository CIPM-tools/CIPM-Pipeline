package cipm.consistency.tools.evaluation.accuracy.models.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import lombok.extern.java.Log;

@Service
@Log
public class PCMDeploymentComparator {

	// JACCARD COEFFICIENT
	public double compareAllocations(Allocation actual, Allocation expected) {
		Map<ResourceContainer, ResourceContainer> actualToExpectedMapping = Maps.newHashMap();

		int matches = 0;
		int comparisons = 0;

		for (AllocationContext ac1 : actual.getAllocationContexts_Allocation()) {
			boolean any = expected.getAllocationContexts_Allocation().stream()
					.anyMatch(ac2 -> allocationContextEqual(ac1, ac2, actualToExpectedMapping));

			if (any) {
				matches++;
			} else {
				log.info("Could not find corresponding allocation context for AC with ID '" + ac1.getId() + "'.");
			}
		}

		comparisons = expected.getAllocationContexts_Allocation().size()
				+ actual.getAllocationContexts_Allocation().size() - matches;

		// check links
		Set<String> usedContainerIds = actual.getAllocationContexts_Allocation().stream()
				.map(ac -> ac.getResourceContainer_AllocationContext().getId()).collect(Collectors.toSet());
		int linkmatches = 0;
		int linkcomparisons = 0;
		for (LinkingResource lr1 : actual.getTargetResourceEnvironment_Allocation()
				.getLinkingResources__ResourceEnvironment()) {
			boolean isConsidered = lr1.getConnectedResourceContainers_LinkingResource().stream()
					.allMatch(rc -> usedContainerIds.contains(rc.getId()));
			if (isConsidered) {
				boolean any = expected.getTargetResourceEnvironment_Allocation()
						.getLinkingResources__ResourceEnvironment().stream()
						.anyMatch(lr2 -> linkEqual(actualToExpectedMapping, lr1, lr2));
				if (any) {
					linkmatches++;
				} else {
					log.info("Could not find corresponding link for link for ID '" + lr1.getId() + "'.");
				}
				linkcomparisons++;
			}
		}

		return (double) (linkmatches + matches) / (double) (linkcomparisons + comparisons);
	}

	private boolean linkEqual(Map<ResourceContainer, ResourceContainer> actualToExpectedMapping, LinkingResource lr1,
			LinkingResource lr2) {
		if (lr1.getConnectedResourceContainers_LinkingResource().size() == lr2
				.getConnectedResourceContainers_LinkingResource().size()) {
			for (ResourceContainer rci : lr1.getConnectedResourceContainers_LinkingResource()) {
				if (!actualToExpectedMapping.containsKey(rci)) {
					return false;
				}
				String mapped = actualToExpectedMapping.get(rci).getId();
				if (!lr2.getConnectedResourceContainers_LinkingResource().stream().anyMatch(am -> {
					return am.getId().equals(mapped);
				})) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	private boolean allocationContextEqual(AllocationContext ac1, AllocationContext ac2,
			Map<ResourceContainer, ResourceContainer> actualToExpectedMapping) {
		return ac1.getAssemblyContext_AllocationContext().getEncapsulatedComponent__AssemblyContext().getId()
				.equals(ac2.getAssemblyContext_AllocationContext().getEncapsulatedComponent__AssemblyContext().getId())
				&& containerEqual(ac1.getResourceContainer_AllocationContext(), ac1.getAllocation_AllocationContext(),
						ac2.getResourceContainer_AllocationContext(), ac2.getAllocation_AllocationContext(),
						actualToExpectedMapping);
	}

	private boolean containerEqual(ResourceContainer container1, Allocation allocation1, ResourceContainer container2,
			Allocation allocation2, Map<ResourceContainer, ResourceContainer> actualToExpectedMapping) {
		if (actualToExpectedMapping.containsKey(container1)) {
			return actualToExpectedMapping.get(container1) == container2;
		} else if (actualToExpectedMapping.containsKey(container2)) {
			return actualToExpectedMapping.get(container2) == container1;
		}

		Map<String, Integer> acMapping1 = new HashMap<>();
		Map<String, Integer> acMapping2 = new HashMap<>();

		allocation1.getAllocationContexts_Allocation().stream().forEach(ac -> {
			if (ac.getResourceContainer_AllocationContext().getId().equals(container1.getId())) {
				incrementOrAdd(acMapping1,
						ac.getAssemblyContext_AllocationContext().getEncapsulatedComponent__AssemblyContext().getId(),
						1);
			}
		});

		allocation2.getAllocationContexts_Allocation().stream().forEach(ac -> {
			if (ac.getResourceContainer_AllocationContext().getId().equals(container2.getId())) {
				incrementOrAdd(acMapping2,
						ac.getAssemblyContext_AllocationContext().getEncapsulatedComponent__AssemblyContext().getId(),
						1);
			}
		});

		if (acMapping1.size() == acMapping2.size()) {
			boolean deploymentEqual = acMapping1.entrySet().stream().allMatch(ent -> {
				return ent.getValue() == acMapping2.get(ent.getKey());
			});

			if (deploymentEqual && !actualToExpectedMapping.containsKey(container1)) {
				actualToExpectedMapping.put(container1, container2);
				return true;
			}
		}

		return false;
	}

	private void incrementOrAdd(Map<String, Integer> map, String key, int add) {
		if (map.containsKey(key)) {
			map.put(key, map.get(key) + add);
		} else {
			map.put(key, add);
		}
	}

}
