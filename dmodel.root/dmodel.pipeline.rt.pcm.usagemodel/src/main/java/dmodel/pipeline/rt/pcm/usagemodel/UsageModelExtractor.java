package dmodel.pipeline.rt.pcm.usagemodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import dmodel.pipeline.dt.mmmodel.MmmodelFactory;
import dmodel.pipeline.dt.mmmodel.UsageData;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.cluster.SessionCluster;
import dmodel.pipeline.rt.pcm.usagemodel.cluster.UsageSessionClusterer;
import dmodel.pipeline.rt.pcm.usagemodel.session.UsageSession;
import dmodel.pipeline.shared.structure.Tree;

public class UsageModelExtractor {
	private UsageModel model;

	private Map<String, UsageSession> sessions;
	private UsageSessionClusterer clusterer;

	public UsageModelExtractor(Repository repository, UsageModel initial) {
		this.model = initial;

		this.sessions = new HashMap<>();
		this.clusterer = new UsageSessionClusterer();
	}

	// TODO also inherit timings between the actions in the cluster
	public UsageData extractUserGroups(List<Tree<ServiceCallRecord>> callTrees, float similaryThres,
			int minInheritCount) {
		// return obj
		UsageData retData = MmmodelFactory.eINSTANCE.createUsageData();

		// collect service calls
		callTrees.forEach(call -> {
			ServiceCallRecord entryCall = call.getRoot().getData();
			if (!sessions.containsKey(entryCall.getSessionId())) {
				sessions.put(entryCall.getSessionId(), new UsageSession(entryCall.getEntryTime()));
			}
			sessions.get(entryCall.getSessionId()).update(entryCall);
		});

		// cluster
		List<UsageSession> rawSession = sessions.entrySet().stream().map(entry -> entry.getValue())
				.collect(Collectors.toList());
		List<SessionCluster> clusters = this.clusterer.clusterSessions(rawSession, similaryThres);

		// build scenarios
		return clusters.stream().filter(cluster -> cluster.getInheritCount() >= minInheritCount)
				.collect(Collectors.toList());
	}

	public void set(UsageModel current) {
		this.model = current;
	}

	public UsageModel get() {
		return this.model;
	}

}
