package dmodel.runtime.pipeline.pcm.repository.estimation;

import java.util.List;

import dmodel.designtime.monitoring.records.ResponseTimeRecord;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.runtime.pipeline.pcm.repository.MonitoringDataSet;
import dmodel.runtime.pipeline.pcm.repository.tree.TreeNode;

public class ResourceDemandTimelineInterval {

	private TreeNode<AbstractTimelineObject> root;

	public ResourceDemandTimelineInterval(TreeNode<ServiceCallRecord> callGraph, MonitoringDataSet data,
			String resourceId) {
		root = buildRecursive(callGraph, null, data, resourceId);
	}

	public TreeNode<AbstractTimelineObject> getRoot() {
		return root;
	}

	private TreeNode<AbstractTimelineObject> buildRecursive(TreeNode<ServiceCallRecord> node,
			TreeNode<AbstractTimelineObject> parent, MonitoringDataSet data, String resourceId) {

		TreeNode<AbstractTimelineObject> root = buildNode(node.data);
		root.parent = parent;
		enrichNode(root, node.data, data, resourceId);

		for (TreeNode<ServiceCallRecord> child : node.children) {
			root.children.add(buildRecursive(child, root, data, resourceId));
		}

		return root;
	}

	private void enrichNode(TreeNode<AbstractTimelineObject> root, ServiceCallRecord data, MonitoringDataSet data2,
			String resourceId) {
		List<ResponseTimeRecord> records = data2.getResponseTimes().getResponseTimes(data.getServiceExecutionId());
		if (records != null) {
			records.forEach(r -> {
				if (r != null && r.getResourceId().equals(resourceId)) {
					TreeNode<AbstractTimelineObject> temp = buildNode(r, data.getServiceId());
					temp.parent = root;
					root.children.add(temp);
				}
			});
		}
	}

	private TreeNode<AbstractTimelineObject> buildNode(ResponseTimeRecord r, String serviceId) {
		return new TreeNode<AbstractTimelineObject>(new InternalActionTimelineObject(r, serviceId));
	}

	private TreeNode<AbstractTimelineObject> buildNode(ServiceCallRecord data) {
		return new TreeNode<AbstractTimelineObject>(new ServiceCallTimelineObject(data));
	}

}
