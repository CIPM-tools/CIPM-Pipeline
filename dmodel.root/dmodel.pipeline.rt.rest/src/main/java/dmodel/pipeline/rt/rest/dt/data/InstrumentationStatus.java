package dmodel.pipeline.rt.rest.dt.data;

public enum InstrumentationStatus {
	NOT_AVAILABLE(-1), STARTED(5), PREPARED(25), INSTRUMENTATION(40), FINISHED(100), SAVING(85);

	private final int progress;

	private InstrumentationStatus(int progress) {
		this.progress = progress;
	}

	public int getProgress() {
		return progress;
	}

	@Override
	public String toString() {
		return String.valueOf(progress);
	}
}
