package dmodel.pipeline.shared.util;

public class StackedRunnable implements Runnable {
	private Runnable[] runnables;

	public StackedRunnable(Runnable... runnables) {
		this.runnables = runnables;
	}

	@Override
	public void run() {
		for (Runnable r : runnables) {
			r.run();
		}
	}

}
