package dmodel.pipeline.monitoring.controller.scale;

public class NoDecelerationScaleController implements IScaleController {

	@Override
	public boolean shouldLog(String targetId) {
		return true;
	}

}
