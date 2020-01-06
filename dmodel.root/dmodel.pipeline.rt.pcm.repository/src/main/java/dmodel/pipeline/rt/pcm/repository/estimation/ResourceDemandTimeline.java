package dmodel.pipeline.rt.pcm.repository.estimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import dmodel.pipeline.rt.pcm.repository.model.ResourceDemandModel;

// FOR ALL SERVICES
public class ResourceDemandTimeline implements IResourceDemandTimeline {

	private String resourceId;
	private String containerId;

	private TreeMap<Long, ResourceDemandTimelineInterval> intervals;
	private TreeMap<Long, Double> utilizations;

	public ResourceDemandTimeline(String containerId, String resourceId) {
		this.resourceId = resourceId;
		this.containerId = containerId;

		this.intervals = new TreeMap<>();
		this.utilizations = new TreeMap<>();
	}

	@Override
	public void addUtilization(long time, double value) {
		this.utilizations.put(time, value);
	}

	@Override
	public void addInterval(ResourceDemandTimelineInterval ival, long time) {
		this.intervals.put(time, ival);
	}

	@Override
	public Set<Entry<Long, Double>> getAllUtilizations() {
		return this.utilizations.entrySet();
	}

	@Override
	public List<Entry<Long, ResourceDemandTimelineInterval>> getIntersectingIntervals(long start, long end,
			long maxDuration) {
		List<Entry<Long, ResourceDemandTimelineInterval>> res = new ArrayList<>();
		Entry<Long, ResourceDemandTimelineInterval> current = this.nextInterval(start - maxDuration - 1);
		while (current != null && current.getKey() <= end) {
			if (current.getKey() + current.getValue().getRoot().data.getDuration() >= start) {
				res.add(current);
			}
			current = this.nextInterval(current.getKey() + 1);
		}
		return res;
	}

	@Override
	public Entry<Long, ResourceDemandTimelineInterval> nextInterval(long time) {
		return intervals.ceilingEntry(time);
	}

	@Override
	public long maxDuration() {
		return intervals.entrySet().stream().mapToLong(i -> i.getValue().getRoot().data.getDuration()).max().orElse(0);
	}

	@Override
	public long lowestIntervalEntry() {
		return intervals.firstKey();
	}

	@Override
	public long highestIntervalEntry() {
		return intervals.lastKey();
	}

	@Override
	public double beforeUtilization(long time) {
		Entry<Long, Double> fl = utilizations.floorEntry(time);
		if (fl != null) {
			return fl.getValue();
		}
		return Double.NaN;
	}

	@Override
	public double afterUtilization(long time) {
		Entry<Long, Double> cl = utilizations.ceilingEntry(time);
		if (cl != null) {
			return cl.getValue();
		}
		return Double.NaN;
	}

	@Override
	public Pair<Double, Long> nearestUtilization(long time) {
		Entry<Long, Double> fl = utilizations.floorEntry(time);
		Entry<Long, Double> cl = utilizations.ceilingEntry(time);

		if (fl == null && cl == null) {
			return null;
		}
		if (fl == null) {
			return Pair.of(cl.getValue(), Math.abs(time - cl.getKey()));
		} else if (cl == null) {
			return Pair.of(fl.getValue(), Math.abs(time - fl.getKey()));
		}

		if (Math.abs(time - fl.getKey()) > Math.abs(time - cl.getKey())) {
			return Pair.of(cl.getValue(), Math.abs(time - cl.getKey()));
		} else {
			return Pair.of(fl.getValue(), Math.abs(time - fl.getKey()));
		}
	}

	public Map<String, ResourceDemandModel> deriveModels() {
		Map<String, ResourceDemandModel> rets = new HashMap<>();

		return rets;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

}
