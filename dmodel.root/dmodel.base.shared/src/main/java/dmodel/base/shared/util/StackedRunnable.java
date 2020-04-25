package dmodel.base.shared.util;

public class StackedRunnable implements Runnable {
	private Runnable[] runnables;
	private boolean debug;

	public StackedRunnable(boolean debug, Runnable... runnables) {
		this.runnables = runnables;
		this.debug = debug;
	}

	@Override
	public void run() {
		for (Runnable r : runnables) {
			if (debug) {
				try {
					r.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				r.run();
			}
		}
	}

}
