package dmodel.pipeline.monitoring.controller.scale;

public class LogarithmicScaleSubController implements IScaleController {
	private static final double NANO_TO_S = 1000000000;
	private static final double MS_TO_S = 1000;
	private static final double BASE = 2;

	private long logarithmicRecoveryIntervalMs;

	private double currentXValue;
	private double currentYValue;
	private double currentIncrement;

	private long lastTimestamp;

	public LogarithmicScaleSubController(long logarithmicRecoveryInterval) {
		this.logarithmicRecoveryIntervalMs = logarithmicRecoveryInterval;

		this.currentXValue = 0;
		this.currentYValue = 0;
		this.currentIncrement = 0;
		this.lastTimestamp = Long.MIN_VALUE;
	}

	@Override
	public boolean shouldLog(String targetId) {
		decrement(lastTimestamp);

		double nextValue = ++currentXValue;
		double currentValue = logBase(nextValue + 1);
		currentIncrement += currentValue - currentYValue;
		currentYValue = currentValue;

		if (currentIncrement >= 1) {
			currentIncrement--;
			return true;
		}

		return false;
	}

	private void decrement(long lastTimestamp2) {
		long currentTime = System.nanoTime();
		if (lastTimestamp2 == Long.MIN_VALUE) {
			lastTimestamp = System.nanoTime();
			return;
		}
		double timeDiff = currentTime - lastTimestamp2;

		// linear equation system with 2 variables
		// logarithmic recovery interval = i
		// -a * -e^x + y = 0
		// => -a + y = currentYValue (1)
		// => a * e^i = y (2)
		// =>

		double iExp = logarithmicRecoveryIntervalMs / MS_TO_S;
		double aAmount = -1d + Math.pow(BASE, iExp);
		double aValue = currentYValue / aAmount;

		double yValue = currentYValue + aValue;

		double nValue = Math.max(0, -aValue * Math.pow(BASE, timeDiff / NANO_TO_S) + yValue);

		currentYValue = nValue;
		currentXValue = Math.pow(BASE, currentYValue) - 1d;
		lastTimestamp = currentTime;
	}

	private double logBase(double val) {
		return Math.log(val) / Math.log(BASE);
	}

}