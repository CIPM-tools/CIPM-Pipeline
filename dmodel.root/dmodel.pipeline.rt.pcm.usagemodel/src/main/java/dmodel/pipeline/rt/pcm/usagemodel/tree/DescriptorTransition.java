package dmodel.pipeline.rt.pcm.usagemodel.tree;

public class DescriptorTransition<T> {
	private T call;
	private float probability;

	public DescriptorTransition(T call, float probability) {
		this.call = call;
		this.probability = probability;
	}

	public T getCall() {
		return call;
	}

	public void setCall(T call) {
		this.call = call;
	}

	public float getProbability() {
		return probability;
	}

	public void setProbability(float probability) {
		this.probability = probability;
	}

}
