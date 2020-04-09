package dmodel.pipeline.monitoring;

import dmodel.pipeline.monitoring.controller.ThreadMonitoringController;

public class TestMonitoringController {

	public static void main(String[] args) {
		ThreadMonitoringController controller = ThreadMonitoringController.getInstance();
		long start = controller.getTime();
		for (int i = 0; i < 1000000; i++) {
			Math.sqrt(i);
		}
		controller.enterService("1234", "ABCD");
		controller.enterInternalAction("abc", "cdf");
		controller.exitInternalAction("abc", "cdf");
		controller.exitService("1234");
	}

}
