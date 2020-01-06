package dmodel.pipeline.rt.pcm.repository.usage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmodel.pipeline.rt.pcm.repository.estimation.AbstractTimelineObject;

public class SimplePOSIXUsageEstimation implements IUsageEstimation {

	@Override
	public Map<AbstractTimelineObject, Double> splitUpUsage(double usage, long startInterval, long stopInterval,
			List<AbstractTimelineObject> objects) {
		Map<AbstractTimelineObject, Double> output = new HashMap<>();

		double sum = objects.stream().mapToLong(t -> t.getDuration()).sum();
		objects.stream().forEach(e -> {
			output.put(e, usage * (e.getDuration() / sum));
		});

		return output;
	}

}
