package dmodel.pipeline.monitoring.controller.scale;

import java.util.HashMap;
import java.util.Map;

public class LogarithmicScaleController implements IScaleController {

	private Map<String, LogarithmicScaleSubController> scaleControllers;
	private long recoveryInterval;

	public LogarithmicScaleController(long logarithmicRecoveryInterval) {
		this.recoveryInterval = logarithmicRecoveryInterval;
		scaleControllers = new HashMap<String, LogarithmicScaleSubController>();
	}

	@Override
	public boolean shouldLog(String targetId) {
		if (scaleControllers.containsKey(targetId)) {
			return scaleControllers.get(targetId).shouldLog(targetId);
		} else {
			LogarithmicScaleSubController subController = new LogarithmicScaleSubController(recoveryInterval);
			scaleControllers.put(targetId, subController);
			return subController.shouldLog(targetId);
		}
	}

}
