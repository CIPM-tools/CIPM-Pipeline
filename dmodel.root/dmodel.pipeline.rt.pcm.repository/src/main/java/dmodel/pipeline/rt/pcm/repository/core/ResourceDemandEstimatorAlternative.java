package dmodel.pipeline.rt.pcm.repository.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;
import org.pcm.headless.api.util.PCMUtil;

import com.google.common.collect.Sets;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet;
import dmodel.pipeline.rt.pcm.repository.estimation.IResourceDemandTimeline;
import dmodel.pipeline.rt.pcm.repository.estimation.ResourceDemandTimeline;
import dmodel.pipeline.rt.pcm.repository.estimation.ResourceDemandTimelineInterval;
import dmodel.pipeline.rt.pcm.repository.estimation.TimelineAnalyzer;
import dmodel.pipeline.rt.pcm.repository.model.IResourceDemandEstimator;
import dmodel.pipeline.rt.pcm.repository.tree.TreeNode;
import dmodel.pipeline.rt.pcm.repository.usage.AdvancedPOSIXUsageEstimation;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

public class ResourceDemandEstimatorAlternative implements IResourceDemandEstimator {

	private InMemoryPCM pcm;

	private List<IResourceDemandTimeline> timelines;

	public ResourceDemandEstimatorAlternative(InMemoryPCM pcm) {
		this.pcm = pcm;
		this.timelines = new ArrayList<>();
	}

	@Override
	// TODO performance is not that good, POC only atm
	public void prepare(MonitoringDataSet data) {
		// build service call tree
		List<TreeNode<ServiceCallRecord>> callRoots = new ArrayList<>();
		Map<String, TreeNode<ServiceCallRecord>> idMapping = new HashMap<>();
		Set<String> seenCallerIds = Sets.newHashSet();

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
			AssemblyContext ctx = PCMUtil.getElementById(pcm.getSystem(), AssemblyContext.class, assembly);
			ResourceDemandingSEFF seff = PCMUtil.getElementById(pcm.getRepository(), ResourceDemandingSEFF.class,
					root.data.getServiceId());

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
	public void derive() {
		TimelineAnalyzer analyzer = new TimelineAnalyzer(pcm, TimelineAnalyzer.UnrollStrategy.COMPLETE,
				new AdvancedPOSIXUsageEstimation()); // 20 s
		for (IResourceDemandTimeline tl : this.timelines) {
			analyzer.analyze(tl);
		}
	}

	private Set<String> getDemandingResources(ResourceDemandingSEFF seff) {
		return ModelUtil.getObjects(seff, ParametricResourceDemand.class).stream()
				.filter(demand -> demand.getRequiredResource_ParametricResourceDemand() != null)
				.map(demand -> demand.getRequiredResource_ParametricResourceDemand().getId())
				.collect(Collectors.toSet());
	}

	private ResourceContainer getContainerByAssemblyId(AssemblyContext asCtx) {
		return ModelUtil.getObjects(pcm.getAllocationModel(), AllocationContext.class).stream().filter(ctx -> {
			return ctx.getAssemblyContext_AllocationContext().getId().equals(asCtx.getId());
		}).map(t -> t.getResourceContainer_AllocationContext()).findFirst().orElse(null);
	}

}
