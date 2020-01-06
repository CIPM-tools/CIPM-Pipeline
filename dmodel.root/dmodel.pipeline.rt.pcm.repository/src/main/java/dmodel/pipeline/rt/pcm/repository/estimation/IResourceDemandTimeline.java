package dmodel.pipeline.rt.pcm.repository.estimation;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public interface IResourceDemandTimeline {

	public void addUtilization(long time, double value);

	public void addInterval(ResourceDemandTimelineInterval ival, long time);

	Pair<Double, Long> nearestUtilization(long time);

	Entry<Long, ResourceDemandTimelineInterval> nextInterval(long time);

	long lowestIntervalEntry();

	long highestIntervalEntry();

	double beforeUtilization(long time);

	double afterUtilization(long time);

	Set<Entry<Long, Double>> getAllUtilizations();

	List<Entry<Long, ResourceDemandTimelineInterval>> getIntersectingIntervals(long start, long end, long maxDuration);

	public long maxDuration();

}
