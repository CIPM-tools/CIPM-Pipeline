package cipm.consistency.runtime.pipeline.entry.receiver;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.base.shared.structure.Tree.TreeNode;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ResponseTimeRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.util.MonitoringDataUtil;
import kieker.analysis.exception.AnalysisConfigurationException;

public class PlainServiceCallGraphVisualizer {

	private static final long NANO_TO_MS = 1000000L;

	public static void main(String[] args) throws IllegalStateException, AnalysisConfigurationException {
		List<PCMContextRecord> records = MonitoringDataUtil.getMonitoringDataFromFiles(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git2/dModel/dmodel.root/dmodel.runtime.pipeline.pcm.system/test-resources/monitoring/kieker-20200729-112157-3327798188707-UTC--KIEKER");

		List<Tree<ServiceCallRecord>> trees = MonitoringDataUtil
				.buildServiceCallTree(records.stream().filter(r -> r instanceof ServiceCallRecord)
						.map(ServiceCallRecord.class::cast).collect(Collectors.toList()));
		List<ResponseTimeRecord> responseTimes = records.stream().filter(r -> r instanceof ResponseTimeRecord)
				.map(ResponseTimeRecord.class::cast).collect(Collectors.toList());

		String targetServiceId = "_xliLoDVXEeqPG_FgW3bi6Q";
		for (Tree<ServiceCallRecord> tree : trees) {
			if (tree.getRoot().getData().getServiceId().equals(targetServiceId)) {
				visualizeTree(tree.getRoot(), responseTimes, 0);
			}
		}
	}

	private static void visualizeTree(TreeNode<ServiceCallRecord> n, List<ResponseTimeRecord> records, int space) {
		String spacedString = " ".repeat(space);
		System.out.println(spacedString + "-" + dataToString(n.getData(), getCoverage(n, records)));
		for (TreeNode<ServiceCallRecord> node : n.getChildren()) {
			visualizeTree(node, records, space + 4);
		}
	}

	private static double getCoverage(TreeNode<ServiceCallRecord> data, List<ResponseTimeRecord> recs) {
		double durationWhole = (data.getData().getExitTime() - data.getData().getEntryTime()) / NANO_TO_MS;
		double durationChilds = 0.0d;

		for (TreeNode<ServiceCallRecord> child : data.getChildren()) {
			durationChilds += ((child.getData().getExitTime() - child.getData().getEntryTime())) / NANO_TO_MS;
		}

		for (ResponseTimeRecord rec : recs) {
			if (rec.getServiceExecutionId().equals(data.getData().getServiceExecutionId())) {
				durationChilds += (rec.getStopTime() - rec.getStartTime()) / NANO_TO_MS;
			}
		}

		return (100d / durationWhole) * durationChilds;
	}

	private static String dataToString(ServiceCallRecord data, double coverage) {
		if (coverage >= 101 && data.getServiceId().equals("_lR_nUDVgEeqPG_FgW3bi6Q")) {
			JOptionPane.showMessageDialog(null, String.valueOf(coverage));
		}

		return "|" + data.getExternalCallId() + "| " + data.getServiceId() + " [" + data.getHostName() + ","
				+ data.getHostId() + "]" + " - " + String.valueOf((data.getExitTime() - data.getEntryTime()) / 1000000L)
				+ "ms" + "- {" + data.getParameters() + "}" + " - Coverage: "
				+ String.valueOf(Math.round(coverage * 100) / 100) + " %";
	}

}
