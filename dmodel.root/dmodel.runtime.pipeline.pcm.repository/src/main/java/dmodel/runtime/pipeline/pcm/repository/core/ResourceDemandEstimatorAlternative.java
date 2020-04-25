package dmodel.runtime.pipeline.pcm.repository.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;

import dmodel.base.core.facade.IPCMQueryFacade;
import dmodel.base.shared.ModelUtil;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.runtime.pipeline.pcm.repository.MonitoringDataSet;
import dmodel.runtime.pipeline.pcm.repository.RepositoryStoexChanges;
import dmodel.runtime.pipeline.pcm.repository.estimation.IResourceDemandTimeline;
import dmodel.runtime.pipeline.pcm.repository.estimation.ResourceDemandTimeline;
import dmodel.runtime.pipeline.pcm.repository.estimation.ResourceDemandTimelineInterval;
import dmodel.runtime.pipeline.pcm.repository.estimation.TimelineAnalyzer;
import dmodel.runtime.pipeline.pcm.repository.model.IResourceDemandEstimator;
import dmodel.runtime.pipeline.pcm.repository.tree.TreeNode;
import dmodel.runtime.pipeline.pcm.repository.usage.AdvancedPOSIXUsageEstimation;

public class ResourceDemandEstimatorAlternative implements IResourceDemandEstimator {

	private IPCMQueryFacade pcm;

	private List<IResourceDemandTimeline> timelines;

	public ResourceDemandEstimatorAlternative(IPCMQueryFacade pcm) {
		this.pcm = pcm;
		this.timelines = new ArrayList<>();
	}

	@Override
	// TODO performance is not that good, POC only atm
	public void prepare(MonitoringDataSet data) {
		// build service call tree
		List<TreeNode<ServiceCallRecord>> callRoots = new ArrayList<>();
		Map<String, TreeNode<ServiceCallRecord>> idMapping = new HashMap<>();

		// sort
		List<ServiceCallRecord> sortedServiceCalls = data.getServiceCalls().getServiceCalls().stream()
				.sorted((a, b) -> {
					if (a.getEntryTime() < b.getEntryTime()) {
						return -1;
					} else if (a.getEntryTime() > b.getEntryTime()) {
						return 1;
					} else {
						return 0;
					}
				}).collect(Collectors.toList());

		// get all service calls
		for (ServiceCallRecord call : sortedServiceCalls) {
			TreeNode<ServiceCallRecord> root = new TreeNode<>(call);
			idMapping.put(call.getServiceExecutionId(), root);

			if (call.getCallerServiceExecutionId().equals("<not set>")) {
				callRoots.add(root);
			}
		}

		for (ServiceCallRecord call : sortedServiceCalls) {
			if (!call.getCallerServiceExecutionId().equals("<not set>")) {
				// get parent
				TreeNode<ServiceCallRecord> parent = idMapping.get(call.getCallerServiceExecutionId());
				TreeNode<ServiceCallRecord> cNode = idMapping.get(call.getServiceExecutionId());

				if (parent != null) {
					cNode.parent = parent;
					parent.children.add(cNode);
				}
			}
		}

		// timeline mapping
		Map<Pair<String, String>, IResourceDemandTimeline> timelineMapping = new HashMap<>();

		// process all roots
		for (TreeNode<ServiceCallRecord> root : callRoots) {
			String assembly = data.getAssemblyContextId(root.data);
			AssemblyContext ctx = pcm.getSystem().getAssemblyById(assembly);
			ResourceDemandingSEFF seff = pcm.getRepository().getServiceById(root.data.getServiceId());

			ResourceContainer container = getContainerByAssemblyId(ctx);

			Set<String> resourceIds = getDemandingResources(seff);
			for (String resourceId : resourceIds) {
				Pair<String, String> tempPair = Pair.of(container.getId(), resourceId);
				if (!timelineMapping.containsKey(tempPair)) {
					timelineMapping.put(tempPair, new ResourceDemandTimeline(container.getId(), resourceId));
				}
				timelineMapping.get(tempPair).addInterval(new ResourceDemandTimelineInterval(root, data, resourceId),
						root.data.getEntryTime());
			}

		}

		// process all cpu rates
		timelineMapping.entrySet().forEach(entry -> {
			String containerId = entry.getKey().getLeft();
			String resourceId = entry.getKey().getRight();

			IResourceDemandTimeline timeline = entry.getValue();

			Map<String, SortedMap<Long, Double>> resourceUtils = data.getResourceUtilizations()
					.getContainerUtilization(containerId);
			if (resourceUtils != null) {
				resourceUtils.entrySet().stream().filter(util -> util.getKey().equals(resourceId)).forEach(util -> {
					util.getValue().entrySet().forEach(et -> timeline.addUtilization(et.getKey(), et.getValue()));
				});
			}
		});

		// timelines
		this.timelines = timelineMapping.entrySet().stream().map(entry -> entry.getValue())
				.collect(Collectors.toList());

	}

	@Override
	public RepositoryStoexChanges derive(Map<String, Double> currentValidationAdjustment) {
		RepositoryStoexChanges result = new RepositoryStoexChanges();
		TimelineAnalyzer analyzer = new TimelineAnalyzer(pcm, TimelineAnalyzer.UnrollStrategy.COMPLETE,
				new AdvancedPOSIXUsageEstimation(), currentValidationAdjustment); // 20 s
		for (IResourceDemandTimeline tl : this.timelines) {
			result.inherit(analyzer.analyze(tl));
		}
		return result;
	}

	private Set<String> getDemandingResources(ResourceDemandingSEFF seff) {
		return ModelUtil.getObjects(seff, ParametricResourceDemand.class).stream()
				.filter(demand -> demand.getRequiredResource_ParametricResourceDemand() != null)
				.map(demand -> demand.getRequiredResource_ParametricResourceDemand().getId())
				.collect(Collectors.toSet());
	}

	private ResourceContainer getContainerByAssemblyId(AssemblyContext asCtx) {
		return pcm.getAllocation().getContainerByAssembly(asCtx);
	}

}
