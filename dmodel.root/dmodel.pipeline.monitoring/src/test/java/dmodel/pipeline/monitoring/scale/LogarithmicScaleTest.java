package dmodel.pipeline.monitoring.scale;

import org.junit.Test;

import dmodel.pipeline.monitoring.controller.scale.LogarithmicScaleController;

public class LogarithmicScaleTest {

	@Test
	public void testScaling() {
		LogarithmicScaleController scaler = new LogarithmicScaleController(1000);
		int counts = 0;
		for (int i = 0; i < 1000000; i++) {
			if (scaler.shouldLog("a")) {
				counts++;
			}
		}

		System.out.println(counts);
	}

}
