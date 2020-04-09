package dmodel.pipeline.rt.pcm.repository.estimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.seff.InternalAction;

import dmodel.pipeline.core.facade.IPCMQueryFacade;
import dmodel.pipeline.monitoring.util.ServiceParametersWrapper;
import dmodel.pipeline.rt.pcm.repository.RepositoryStoexChanges;
import dmodel.pipeline.rt.pcm.repository.regression.ParametricLinearRegression;
import dmodel.pipeline.rt.pcm.repository.tree.TreeNode;
import dmodel.pipeline.rt.pcm.repository.usage.IUsageEstimation;

// TODO refactor
public class TimelineAnalyzer implements ITimelineAnalysis {
	private static final Logger LOG = Logger.getLogger(TimelineAnalyzer.class);

	private IPCMQueryFacade pcm;
	private UnrollStrategy strategy;
	private IUsageEstimation usageEstimation;

	private Map<String, Double> adjustmentFactors;

	public TimelineAnalyzer(IPCMQueryFacade pcm, UnrollStrategy strategy, IUsageEstimation estimationStrategy,
			Map<String, Double> adjustmentFactors) {
		this.pcm = pcm;
		this.strategy = strategy;
		this.usageEstimation = estimationStrategy;
		this.adjustmentFactors = adjustmentFactors;
	}

	@Override
	public RepositoryStoexChanges analyze(IResourceDemandTimeline timeline) {
		LOG.info("Starting timeline analysis.");
		// get max duration
		long maxDuration = timeline.maxDuration();

		// calculate baseline (system, ...)
		LOG.info("Calculate baseline.");
		// double utilBaseline = calculateBaseline(timeline, maxDuration);
		double utilBaseline = 0.01;

		// saving data structures
		Map<InternalActionTimelineObject, Double> usageAccumulator = new HashMap<>();
		Map<InternalActionTimelineObject, ServiceCallTimelineObject> serviceCallParentMapping = new HashMap<>();

		// iterate over cpu intervals
		LOG.info("Iterate over CPU intervals.");

		Entry<Long, Double> before = null;
		for (Entry<Long, Double> entry : timeline.getAllUtilizations()) {
			// new iteration
			if (before != null) {
				// effectively final start & end
				long outerIntervalStart = before.getKey();
				long outerIntervalEnd = entry.getKey();

				// intersecting service calls
				List<Entry<Long, ResourceDemandTimelineInterval>> intersecting = timeline
						.getIntersectingIntervals(outerIntervalStart, outerIntervalEnd, maxDuration);

				if (intersecting.size() > 0) {
					// normalized util
					double utilizationNormalized = ((before.getValue() + entry.getValue()) / 2d) - utilBaseline;

					// scale it up to 1
					utilizationNormalized *= 1d / (1d - utilBaseline);

					if (utilizationNormalized > 0.0) {
						// unroll it
						// for (Entry<Long, ResourceDemandTimelineInterval> intersection : intersecting)
						// {
						// unrollIntervalWithModel(intersection.getValue());
						// }

						// get intersecting ias
						List<TreeNode<AbstractTimelineObject>> allIss = new ArrayList<>();
						for (Entry<Long, ResourceDemandTimelineInterval> intersection : intersecting) {
							allIss.addAll(getIntersectingInternalActions(intersection.getValue().getRoot(),
									before.getKey(), entry.getKey()));
						}

						// sort intersecting ias
						allIss.sort((a, b) -> {
							return Long.compare(a.data.getStart(), b.data.getStart());
						});

						// iterate
						if (allIss.size() > 0) {
							TreeMap<Long, TreeNode<AbstractTimelineObject>> currentActive = new TreeMap<>();
							int currentIndex = 0;

							// start (0 iteration)
							long currentTime = Math.max(allIss.get(0).data.getStart(), before.getKey());
							TreeNode<AbstractTimelineObject> currentObj = allIss.get(currentIndex++);
							currentActive.put(currentObj.data.getEnd(), currentObj);
							// end of the whole interval
							long intervalEnd = entry.getKey();

							while (currentTime < intervalEnd) {
								// start of interval
								long intervalStart = currentTime;

								// start of the next interval
								long nextStart = allIss.size() > currentIndex
										? Math.max(before.getKey(), allIss.get(currentIndex).data.getStart())
										: Long.MAX_VALUE;

								// end of the first interval
								long currentEnd = currentActive.size() > 0 ? currentActive.firstKey() : Long.MAX_VALUE;

								// get lowest of all
								long nextTime = NumberUtils.min(nextStart, currentEnd, intervalEnd);
								long timeDiff = nextTime - currentTime;

								if (timeDiff > 0) {
									// split up the usage
									// get services at that point of time
									List<Entry<Long, ResourceDemandTimelineInterval>> services = timeline
											.getIntersectingIntervals(currentTime, nextTime, maxDuration);
									List<TreeNode<AbstractTimelineObject>> rootCalls = services.stream()
											.map(et -> et.getValue().getRoot()).collect(Collectors.toList());

									// build cpu distribution tree
									Map<AbstractTimelineObject, Double> usageMapping = topDownUsageSplit(rootCalls,
											utilizationNormalized, before.getKey(), entry.getKey());

									// distribute on internal actions
									if (currentActive.size() > 0) {
										// create buckets with parents
										Map<AbstractTimelineObject, List<AbstractTimelineObject>> parentMapping = new HashMap<>();
										currentActive.entrySet().forEach(ca -> {
											if (!parentMapping.containsKey(ca.getValue().parent.data)) {
												parentMapping.put(ca.getValue().parent.data, new ArrayList<>());
											}
											parentMapping.get(ca.getValue().parent.data).add(ca.getValue().data);

											// help to identify parents
											if (!serviceCallParentMapping.containsKey(ca.getValue().data)) {
												serviceCallParentMapping.put(
														(InternalActionTimelineObject) ca.getValue().data,
														(ServiceCallTimelineObject) ca.getValue().parent.data);
											}
										});

										// split up for each
										for (Entry<AbstractTimelineObject, List<AbstractTimelineObject>> et : parentMapping
												.entrySet()) {
											// get usage for this service
											AbstractTimelineObject sObj = et.getKey();
											if (!usageMapping.containsKey(sObj)) {
												continue;
											}

											double val = usageMapping.get(sObj);

											// split up the usage for all childs
											Map<AbstractTimelineObject, Double> lastSplit = usageEstimation
													.splitUpUsage(val, intervalStart, nextStart, et.getValue());

											// iterate over all ias
											lastSplit.entrySet().forEach(iet -> {
												InternalActionTimelineObject tlo = (InternalActionTimelineObject) iet
														.getKey();

												long sliceStart = Math.max(tlo.getStart(), intervalStart);
												long sliceEnd = Math.min(tlo.getEnd(), nextTime);
												double sliceUsage = iet.getValue();

												// calculate accumulation part
												double scaledUsage = (sliceUsage * ((double) (sliceEnd - sliceStart)))
														/ (1000000); // nano to ms
												// TODO nano to ms?

												if (!usageAccumulator.containsKey(tlo)) {
													usageAccumulator.put(tlo, scaledUsage);
												} else {
													usageAccumulator.put(tlo, usageAccumulator.get(tlo) + scaledUsage);
												}
											});
										}
									}
								}

								// diff
								if (nextTime == currentEnd) {
									currentActive.remove(currentActive.firstKey());
								}
								if (nextTime == nextStart) {
									currentObj = allIss.get(currentIndex++);
									currentActive.put(currentObj.data.getEnd(), currentObj);
								}

								// update time
								currentTime = nextTime;
							}
						}
					}
				}
			}

			before = entry;
		}
		LOG.info("Finished interval analysis.");

		// read our accumulator
		LOG.info("Reading the values.");
		Map<String, List<Pair<ServiceParametersWrapper, Double>>> iaDemands = new HashMap<>();
		Map<String, String> internalActionServiceIdMapping = new HashMap<>();
		// aggregate up to internal actions
		usageAccumulator.entrySet().forEach(et -> {
			String iaId = et.getKey().getInternalActionId();
			if (!iaDemands.containsKey(iaId)) {
				iaDemands.put(iaId, new ArrayList<>());
				internalActionServiceIdMapping.put(iaId, et.getKey().getServiceId());
			}

			// get parent & parameters
			ServiceCallTimelineObject parentCall = serviceCallParentMapping.get(et.getKey());
			iaDemands.get(iaId).add(Pair.of(parentCall.getParameters(), et.getValue()));
		});

		// calculate stoex
		LOG.info("Performing linear regressions.");
		Map<String, PCMRandomVariable> stoexMapping = new HashMap<>();
		for (Entry<String, List<Pair<ServiceParametersWrapper, Double>>> demandEntry : iaDemands.entrySet()) {
			String belongingServiceId = internalActionServiceIdMapping.get(demandEntry.getKey());
			double adjustmentFactor;
			if (!adjustmentFactors.containsKey(belongingServiceId)) {
				adjustmentFactor = 0;
			} else {
				adjustmentFactor = adjustmentFactors.get(belongingServiceId);
			}

			ParametricLinearRegression regression = new ParametricLinearRegression(demandEntry.getValue(), 1, 0.5f,
					adjustmentFactor + 1.0d);
			PCMRandomVariable derived = regression.deriveStoex(null);
			stoexMapping.put(demandEntry.getKey(), derived);
			LOG.info(derived.getSpecification());
		}

		// write back to model
		RepositoryStoexChanges changes = new RepositoryStoexChanges();
		stoexMapping.entrySet().forEach(wb -> {
			InternalAction action = pcm.getRepository().getInternalAction(wb.getKey());
			LOG.info("Adjusted internal action with id '" + wb.getKey() + "'.");
			LOG.info("-> " + action.getEntityName());
			if (action != null) {
				changes.put(action.getId(), wb.getValue());
			}
		});

		return changes;
	}

	private Map<AbstractTimelineObject, Double> topDownUsageSplit(List<TreeNode<AbstractTimelineObject>> rootCalls,
			double utilizationNormalized, long start, long end) {
		Map<AbstractTimelineObject, Double> mappedDown = new HashMap<>();

		// first
		List<AbstractTimelineObject> all = rootCalls.stream().filter(t -> t.data instanceof ServiceCallTimelineObject)
				.map(t -> t.data).collect(Collectors.toList());
		mappedDown.putAll(usageEstimation.splitUpUsage(utilizationNormalized, start, end, all));

		// recursive childs
		rootCalls.stream().filter(t -> t.data instanceof ServiceCallTimelineObject).forEach(node -> {
			double myUsage = mappedDown.get(node.data);
			long myStart = Math.max(node.data.getStart(), start);
			long myEnd = NumberUtils.max(myStart, Math.min(node.data.getEnd(), end));

			mappedDown.putAll(topDownUsageSplit(node.children, myUsage, myStart, myEnd));
		});

		return mappedDown;
	}

	private List<TreeNode<AbstractTimelineObject>> getIntersectingInternalActions(TreeNode<AbstractTimelineObject> obj,
			long start, long end) {
		List<TreeNode<AbstractTimelineObject>> output = new ArrayList<>();

		obj.children.forEach(child -> {
			if (child.data instanceof ServiceCallTimelineObject) {
				output.addAll(getIntersectingInternalActions(child, start, end));
			} else if (child.data instanceof InternalActionTimelineObject) {
				if (child.data.getStart() <= end && child.data.getEnd() >= start) {
					// hit
					output.add(child);
				}
			}
		});

		return output;
	}

	private double calculateBaseline(IResourceDemandTimeline timeline, long maxDuration) {
		double sum = 0;
		int num = 0;

		Entry<Long, Double> before = null;
		for (Entry<Long, Double> entry : timeline.getAllUtilizations()) {
			if (before != null) {
				if (timeline.getIntersectingIntervals(before.getKey(), entry.getKey(), maxDuration).size() == 0) {
					if (!Double.isNaN(before.getValue()) && !Double.isNaN(entry.getValue())) {
						num++;
						sum += (before.getValue() + entry.getValue()) / 2d;
					}
				}
			}

			before = entry;
		}

		return num == 0 ? 0 : sum / num;
	}

	public enum UnrollStrategy {
		PARTIALLY, COMPLETE;
	}

}
