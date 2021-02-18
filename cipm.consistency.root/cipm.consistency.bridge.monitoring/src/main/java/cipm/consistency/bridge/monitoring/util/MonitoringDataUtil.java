package cipm.consistency.bridge.monitoring.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.base.shared.structure.Tree.TreeNode;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import kieker.analysis.AnalysisController;
import kieker.analysis.IAnalysisController;
import kieker.analysis.exception.AnalysisConfigurationException;
import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.analysis.plugin.filter.forward.CountingFilter;
import kieker.analysis.plugin.reader.filesystem.FSReader;
import kieker.common.configuration.Configuration;

public class MonitoringDataUtil {

	// this function needs to be structure insensitive because it is not guaranteed
	// that the service calls are ordered
	// O(2n)
	public static List<Tree<ServiceCallRecord>> buildServiceCallTree(List<ServiceCallRecord> records) {
		List<Tree<ServiceCallRecord>> callTree = new ArrayList<>();
		Map<ServiceCallRecord, TreeNode<ServiceCallRecord>> nodeMapping = new HashMap<>();
		Map<String, ServiceCallRecord> idMapping = new HashMap<>();

		// create mapping due to structure insensitivity
		records.stream().forEach(scr -> {
			// create treenode
			TreeNode<ServiceCallRecord> nTreeNode = new TreeNode<>();
			nTreeNode.setData(scr);
			nodeMapping.put(scr, nTreeNode);

			// create id mapping
			idMapping.put(scr.getServiceExecutionId(), scr);
		});

		records.stream().forEach(scr -> {
			if (!idMapping.containsKey(scr.getCallerServiceExecutionId())) {
				// get own
				TreeNode<ServiceCallRecord> parent = nodeMapping.get(scr);

				// it is a root call
				Tree<ServiceCallRecord> nTree = new Tree<>(parent);

				// add root
				callTree.add(nTree);
			} else {
				// resolve parent
				ServiceCallRecord parent = idMapping.get(scr.getCallerServiceExecutionId());
				TreeNode<ServiceCallRecord> parentNode = nodeMapping.get(parent);

				// get myself
				TreeNode<ServiceCallRecord> childNode = nodeMapping.get(scr);

				// add it
				parentNode.addChildren(childNode);
			}
		});

		return callTree;
	}

	public static List<PCMContextRecord> getMonitoringDataFromFiles(String directory)
			throws IllegalStateException, AnalysisConfigurationException {
		// Create Kieker Analysis instance
		final IAnalysisController analysisInstance = new AnalysisController();

		// Set file system monitoring log input directory for our analysis
		final Configuration fsReaderConfig = new Configuration();
		fsReaderConfig.setProperty(FSReader.CONFIG_PROPERTY_NAME_INPUTDIRS, directory);
		final FSReader reader = new FSReader(fsReaderConfig, analysisInstance);

		final InternalFilter sink = new InternalFilter(new Configuration(), analysisInstance);

		analysisInstance.connect(reader, FSReader.OUTPUT_PORT_NAME_RECORDS, sink,
				CountingFilter.INPUT_PORT_NAME_EVENTS);

		// Start reading all records.
		analysisInstance.run();

		return sink.recs;
	}

	private static class InternalFilter extends AbstractFilterPlugin {
		private List<PCMContextRecord> recs;

		private static final String INPUT_PORT_NAME_EVENTS = "inputEvents";

		public InternalFilter(Configuration configuration, IAnalysisController analysisInstance) {
			super(configuration, analysisInstance);
			recs = Lists.newArrayList();
		}

		@Override
		public Configuration getCurrentConfiguration() {
			return configuration;
		}

		@InputPort(name = INPUT_PORT_NAME_EVENTS, description = "Input pcm records.", eventTypes = {
				PCMContextRecord.class })
		public final void inputEvent(final PCMContextRecord record) {
			recs.add(record);
		}
	}

}
