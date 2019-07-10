package dmodel.pipeline.rt.pcm.usagemodel.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmodel.pipeline.rt.pcm.usagemodel.data.usage.AbstractUsageElement;

public class SessionCluster {
	private List<AbstractUsageElement> elements;
	private int inheritCount;
	private List<Long> arrivalTimes;

	public SessionCluster(List<AbstractUsageElement> elements) {
		this.elements = elements;
		this.inheritCount = 0;
		this.arrivalTimes = new ArrayList<>();
	}

	public void inherit(List<AbstractUsageElement> other, long entryTime, SessionClusterMapping mapping) {
		for (AbstractUsageElement myElement : elements) {
			if (mapping.has(myElement)) {
				myElement.merge(mapping.getMapped(myElement));
			}
		}

		arrivalTimes.add(entryTime);
		inheritCount++;
	}

	public long getInterArrivalTimeAverage() {
		Collections.sort(this.arrivalTimes);

		long sum = 0;
		long current = -1;

		for (long nArrival : this.arrivalTimes) {
			if (current > 0) {
				sum += nArrival - current;
			}
			current = nArrival;
		}

		return sum / this.arrivalTimes.size();
	}

	public List<Long> getArrivalTimes() {
		return arrivalTimes;
	}

	public List<AbstractUsageElement> getElements() {
		return elements;
	}

	public int getInheritCount() {
		return inheritCount;
	}

}
