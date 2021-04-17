package cipm.consistency.tools.evaluation.accuracy.test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ExternalCallAction;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.base.shared.structure.Tree.TreeNode;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.util.MonitoringDataUtil;
import kieker.analysis.exception.AnalysisConfigurationException;

public class TreeTest {

	public static void main(String[] args) throws IllegalStateException, AnalysisConfigurationException {
		PCMUtils.loadPCMModels();

		List<PCMContextRecord> recs = MonitoringDataUtil.getMonitoringDataFromFiles("kieker");
		List<Tree<ServiceCallRecord>> trees = MonitoringDataUtil
				.buildServiceCallTree(recs.stream().filter(f -> f instanceof ServiceCallRecord)
						.map(ServiceCallRecord.class::cast).collect(Collectors.toList()));

		for (Tree<ServiceCallRecord> tree : trees) {
			recursivePrint(tree.getRoot(), 0);
		}

		List<PCMContextRecord> recs2 = MonitoringDataUtil.getMonitoringDataFromFiles("kieker");
		List<Tree<ServiceCallRecord>> trees2 = MonitoringDataUtil
				.buildServiceCallTree(recs2.stream().filter(f -> f instanceof ServiceCallRecord)
						.map(ServiceCallRecord.class::cast).collect(Collectors.toList()));

		Repository repo = ModelUtil.readFromFile(
				"/Users/david/Desktop/Dynamic Approach/Final Version/Commit Implementation/git_push/CIPM-Pipeline/cipm.consistency.root/cipm.consistency.tools.evaluation/teastore_models/teastore.repository",
				Repository.class);
		Map<String, Long> seenRoles = Maps.newHashMap();
		long lowestEntry = Long.MAX_VALUE;
		for (ServiceCallRecord r : recs2.stream().filter(r -> r instanceof ServiceCallRecord)
				.map(ServiceCallRecord.class::cast).collect(Collectors.toList())) {
			if (!r.getExternalCallId().equals("<not set>")) {
				lowestEntry = Math.min(lowestEntry, r.getEntryTime());
				ExternalCallAction ca = PCMUtils.getElementById(repo, ExternalCallAction.class, r.getExternalCallId());
				seenRoles.put(ca.getRole_ExternalService().getEntityName(), r.getEntryTime() - lowestEntry);
			}
		}

		System.out.println(seenRoles);

		Set<String> graph = Sets.newHashSet();
		for (Tree<ServiceCallRecord> tree : trees) {
			processTreeRecursive(null, tree.getRoot(), graph);
		}
		for (String g : graph) {
			System.out.println(g);
		}
	}

	private static void processTreeRecursive(TreeNode<ServiceCallRecord> parent, TreeNode<ServiceCallRecord> child,
			Set<String> graph) {
		if (parent != null) {
			graph.add(parent.getData().getServiceId() + " [" + parent.getData().getHostId() + "] -> "
					+ child.getData().getServiceId() + " [" + child.getData().getHostId() + "] | "
					+ child.getData().getExternalCallId());
		}

		for (TreeNode<ServiceCallRecord> nch : child.getChildren()) {
			processTreeRecursive(child, nch, graph);
		}
	}

	private static void recursivePrint(TreeNode<ServiceCallRecord> tree, int i) {
		String indent = "";
		for (int k = 0; k < i; k++) {
			indent += " ";
		}

		System.out.println(indent + "- " + tree.getData().getServiceId() + " [" + tree.getData().getHostName() + "]"
				+ " | " + tree.getData().getExternalCallId());
		for (TreeNode<ServiceCallRecord> child : tree.getChildren()) {
			recursivePrint(child, i + 4);
		}
	}

}
