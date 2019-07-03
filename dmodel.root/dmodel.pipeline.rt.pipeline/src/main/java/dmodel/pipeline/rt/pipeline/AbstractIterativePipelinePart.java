package dmodel.pipeline.rt.pipeline;

public abstract class AbstractIterativePipelinePart<B> {
	private B blackboard;

	public B getBlackboard() {
		return blackboard;
	}

	public void setBlackboard(B blackboard) {
		this.blackboard = blackboard;
	}
}
