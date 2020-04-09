package dmodel.pipeline.monitoring.controller;

public class IDFactory {
	private final long creationTimeMillis;
	private long lastTimeMillis;
	private long discriminator;

	public IDFactory() {
		this.creationTimeMillis = System.currentTimeMillis();
		this.lastTimeMillis = creationTimeMillis;
	}

	public synchronized String createId() {
		String id;
		long now = System.currentTimeMillis();

		if (now == lastTimeMillis) {
			++discriminator;
		} else {
			discriminator = 0;
		}

		// creationTimeMillis used to prevent multiple instances of the JVM
		// running on the same host returning clashing IDs.
		// The only way a clash could occur is if the applications started at
		// exactly the same time.
		id = String.format("%d-%d-%d", creationTimeMillis, now, discriminator);
		lastTimeMillis = now;

		return id;
	}
}
