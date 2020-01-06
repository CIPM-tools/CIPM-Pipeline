package dmodel.pipeline.rt.pcm.repository.usage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmodel.pipeline.rt.pcm.repository.estimation.AbstractTimelineObject;

public class AdvancedPOSIXUsageEstimation implements IUsageEstimation {

	@Override
	public Map<AbstractTimelineObject, Double> splitUpUsage(double usage, long startInterval, long stopInterval,
			List<AbstractTimelineObject> objects) {
		Map<AbstractTimelineObject, Double> output = new HashMap<>();

		long intervalSum = 0;
		for (AbstractTimelineObject o : objects) {
			long d = Math.min(stopInterval, o.getEnd()) - Math.max(startInterval, o.getStart());
			intervalSum += d > 0 ? d : 0;
		}

		for (AbstractTimelineObject obj : objects) {
			long d = Math.min(stopInterval, obj.getEnd()) - Math.max(startInterval, obj.getStart());

			if (d > 0) {
				output.put(obj, usage * ((double) d / (double) intervalSum));
			} else {
				output.put(obj, 0.0d);
			}
		}

		return output;
	}

}
