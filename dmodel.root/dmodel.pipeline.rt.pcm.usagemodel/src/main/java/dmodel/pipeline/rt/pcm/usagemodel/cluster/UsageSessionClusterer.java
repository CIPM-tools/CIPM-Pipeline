package dmodel.pipeline.rt.pcm.usagemodel.cluster;

import java.util.ArrayList;
import java.util.List;

import dmodel.pipeline.rt.pcm.usagemodel.data.usage.AbstractUsageElement;
import dmodel.pipeline.rt.pcm.usagemodel.session.UsageSession;

// TODO shorten methods
public class UsageSessionClusterer {

	public List<SessionCluster> clusterSessions(List<UsageSession> sessions, float similarityThreshold) {
		List<SessionCluster> currentClusters = new ArrayList<>();

		for (UsageSession session : sessions) {
			List<AbstractUsageElement> converted = session.compress();

			SessionCluster bestMatching = null;
			SessionClusterMapping bestMapping = null;

			for (SessionCluster current : currentClusters) {
				SessionClusterMapping mapping = determineSimilarity(converted, current.getElements());
				boolean isBestMatch = bestMapping == null || bestMapping.getSimilarity() < mapping.getSimilarity();
				if (mapping.getSimilarity() >= similarityThreshold && isBestMatch) {
					bestMatching = current;
					bestMapping = mapping;
				}
			}

			if (bestMatching != null) {
				bestMatching.inherit(converted, session.getEntryTime(), bestMapping);
			} else {
				currentClusters.add(new SessionCluster(converted));
			}
		}

		return currentClusters;
	}

	private SessionClusterMapping determineSimilarity(List<AbstractUsageElement> usageA,
			List<AbstractUsageElement> usageB) {
		final SessionClusterMapping clusterMapping = new SessionClusterMapping();

		AbstractUsageElement a0 = null;
		AbstractUsageElement a1 = null;
		for (AbstractUsageElement a2 : usageA) {
			if (a0 != null || a1 != null) {
				processMapping(getPossibleMappings(a0, a1, a2, usageB), a1, clusterMapping);
			}

			// merge all
			a0 = a1;
			a1 = a2;
		}
		processMapping(getPossibleMappings(a0, a1, null, usageB), a1, clusterMapping);

		// calculate similarity
		float similarity = clusterMapping.mappedSize() / (float) Math.max(usageA.size(), usageB.size());
		clusterMapping.setSimilarity(similarity);

		return clusterMapping;
	}

	private void processMapping(List<AbstractUsageElement> els, final AbstractUsageElement current,
			final SessionClusterMapping mapping) {
		els.stream().filter(mapEl -> {
			return !mapping.has(mapEl);
		}).findFirst().ifPresent(mp -> mapping.addMapping(current, mp));
	}

	private List<AbstractUsageElement> getPossibleMappings(AbstractUsageElement before, AbstractUsageElement element,
			AbstractUsageElement after, List<AbstractUsageElement> possible) {
		List<AbstractUsageElement> matches = new ArrayList<>();

		AbstractUsageElement a0 = null;
		AbstractUsageElement a1 = null;
		for (AbstractUsageElement a2 : possible) {
			boolean matching = matchesProxy(a0, before) && matchesProxy(a1, element) && matchesProxy(a2, after);
			if (matching) {
				matches.add(a1);
			}

			// merge all
			a0 = a1;
			a1 = a2;
		}

		// we need to this afterwards otherwise we dont recognize the last element
		boolean matching = matchesProxy(a0, before) && matchesProxy(a1, element) && matchesProxy(null, after);
		if (matching) {
			matches.add(a1);
		}

		return matches;
	}

	private boolean matchesProxy(AbstractUsageElement a, AbstractUsageElement b) {
		if (a == null && b == null) {
			return true;
		} else if (a == null) {
			return false;
		} else if (b == null) {
			return false;
		} else {
			return a.matches(b);
		}
	}

}
