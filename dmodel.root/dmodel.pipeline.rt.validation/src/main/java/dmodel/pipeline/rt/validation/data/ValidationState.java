package dmodel.pipeline.rt.validation.data;

public class ValidationState {

	public static final ValidationState INIT = null;

	private int iteration;

	public int getIteration() {
		return iteration;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

}
