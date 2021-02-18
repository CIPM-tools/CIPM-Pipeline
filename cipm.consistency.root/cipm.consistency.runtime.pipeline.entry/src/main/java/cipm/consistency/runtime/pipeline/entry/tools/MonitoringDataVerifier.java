package cipm.consistency.runtime.pipeline.entry.tools;

import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.pcm.headless.api.util.ModelUtil;

import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.base.shared.structure.Tree.TreeNode;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.util.MonitoringDataUtil;
import kieker.analysis.exception.AnalysisConfigurationException;

public class MonitoringDataVerifier {

	public static void main(String[] args) throws IllegalStateException, AnalysisConfigurationException {
		String path = "/Users/david/Desktop/Dynamic Approach/Implementation/git2/dModel/dmodel.root/dmodel.runtime.pipeline.entry/received/kieker-20200622-132135-17380831977563-UTC--KIEKER";
		String repo_path = "/Users/david/Desktop/Dynamic Approach/Implementation/git2/dModel/dmodel.root/dmodel.base.evaluation/models/teastore/models_init/teastore.repository";

		List<PCMContextRecord> records = MonitoringDataUtil.getMonitoringDataFromFiles(path);

		List<Tree<ServiceCallRecord>> trees = MonitoringDataUtil
				.buildServiceCallTree(records.stream().filter(r -> r instanceof ServiceCallRecord)
						.map(ServiceCallRecord.class::cast).collect(Collectors.toList()));

		PCMUtils.loadPCMModels();
		Repository pcm = ModelUtil.readFromFile(repo_path, Repository.class);

		verifyTrees(trees, pcm);
		verifyRecords(records, pcm);

	}

	private static void verifyRecords(List<PCMContextRecord> records, Repository pcm) {
		// TODO Auto-generated method stub

	}

	private static void verifyTrees(List<Tree<ServiceCallRecord>> trees, Repository pcm) {
		for (Tree<ServiceCallRecord> tree : trees) {
			verifyTreeNode(tree.getRoot(), null, pcm);
		}
	}

	private static void verifyTreeNode(TreeNode<ServiceCallRecord> node, TreeNode<ServiceCallRecord> parent,
			Repository repository) {
		ResourceDemandingSEFF service = PCMUtils.getElementById(repository, ResourceDemandingSEFF.class,
				node.getData().getServiceId());
		if (service == null) {
			System.out
					.println("Service with ID: '" + node.getData().getServiceId() + "' can not be found in the model.");
		} else {
			// check external
			if (parent != null) {
				ResourceDemandingSEFF parentService = PCMUtils.getElementById(repository, ResourceDemandingSEFF.class,
						parent.getData().getServiceId());
				ExternalCallAction innerExternal = PCMUtils.getElementById(parentService, ExternalCallAction.class,
						node.getData().getExternalCallId());

				if (innerExternal == null) {
					System.out.println("External call with ID: '" + node.getData().getExternalCallId()
							+ "' could not be found in the service with ID '" + parent.getData().getServiceId() + "'.");
				} else {
					Signature sig1 = innerExternal.getCalledService_ExternalService();
					Signature sig2 = service.getDescribedService__SEFF();

					if (!sig1.getId().equals(sig2.getId())) {
						System.out.println("Signature is not valid for external call with ID: '"
								+ node.getData().getExternalCallId() + "' within the service with ID '"
								+ parent.getData().getServiceId() + "'.");
					}
				}
			}
		}

		for (TreeNode<ServiceCallRecord> child : node.getChildren()) {
			verifyTreeNode(child, node, repository);
		}
	}

}
