package dmodel.pipeline.monitoring.util;


import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.shared.structure.Tree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class MonitoringDataUtil {
    // this function needs to be structure insensitive because it is not guaranteed
    // that the service calls are ordered
    // O(2n)
    public static List<Tree<ServiceCallRecord>> buildServiceCallTree(List<ServiceCallRecord> records) {
        List<Tree<ServiceCallRecord>> callTree = new ArrayList<>();
        Map<ServiceCallRecord, Tree.TreeNode<ServiceCallRecord>> nodeMapping = new HashMap<>();
        Map<String, ServiceCallRecord> idMapping = new HashMap<>();
        // create mapping due to structure insensitivity
        records.stream().forEach(( scr) -> {
            // create treenode
            Tree.TreeNode<ServiceCallRecord> nTreeNode = new Tree.TreeNode<>();
            nTreeNode.setData(scr);
            nodeMapping.put(scr, nTreeNode);
            // create id mapping
            idMapping.put(scr.getServiceExecutionId(), scr);
        });
        // TODO problem if we have a trace which is not complete in the monitoring data
        // TODO this should be fixed
        records.stream().forEach(( scr) -> {
            if (!(idMapping.containsKey(scr.getCallerServiceExecutionId()))) {
                // get own
                Tree.TreeNode<ServiceCallRecord> parent = nodeMapping.get(scr);
                // it is a root call
                Tree<ServiceCallRecord> nTree = new Tree<>(parent);
                // add root
                callTree.add(nTree);
            } else {
                // resolve parent
                ServiceCallRecord parent = idMapping.get(scr.getCallerServiceExecutionId());
                Tree.TreeNode<ServiceCallRecord> parentNode = nodeMapping.get(parent);
                // get myself
                Tree.TreeNode<ServiceCallRecord> childNode = nodeMapping.get(scr);
                // add it
                parentNode.addChildren(childNode);
            }
        });
        return callTree;
    }
}

