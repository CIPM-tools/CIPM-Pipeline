package dmodel.pipeline.rt.pcm.usagemodel.cluster;

import java.util.HashMap;
import java.util.Map;

import dmodel.pipeline.rt.pcm.usagemodel.data.usage.AbstractUsageElement;

public class SessionClusterMapping {
	private float similarity;

	// bidirectional
	private Map<AbstractUsageElement, AbstractUsageElement> mapping;
	private Map<AbstractUsageElement, AbstractUsageElement> mappingBack;

	public SessionClusterMapping() {
		this.mapping = new HashMap<>();
		this.mappingBack = new HashMap<>();
	}

	public float getSimilarity() {
		return similarity;
	}

	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}

	public boolean has(AbstractUsageElement a) {
		return mapping.containsKey(a) || mappingBack.containsKey(a);
	}

	public int mappedSize() {
		return mapping.size(); // == mappingBack.size()
	}

	public AbstractUsageElement getMapped(AbstractUsageElement a) {
		if (mapping.containsKey(a)) {
			return mapping.get(a);
		} else if (mappingBack.containsKey(a)) {
			return mappingBack.get(a);
		} else {
			return null;
		}
	}

	public void addMapping(AbstractUsageElement a, AbstractUsageElement b) {
		this.mapping.put(a, b);
		this.mappingBack.put(b, a);
	}

}
