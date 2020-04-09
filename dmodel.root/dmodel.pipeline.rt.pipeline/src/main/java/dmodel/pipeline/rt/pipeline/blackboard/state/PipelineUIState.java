package dmodel.pipeline.rt.pipeline.blackboard.state;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import dmodel.pipeline.core.state.EPipelineTransformation;
import dmodel.pipeline.core.state.ETransformationState;
import lombok.Data;

@Service
@Data
// only represents an abstract view on the status of the pipeline and not in-depth!
public class PipelineUIState implements InitializingBean {

	private Map<EPipelineTransformation, ETransformationState> mapping = new HashMap<>();
	private boolean running;

	public void reset() {
		for (EPipelineTransformation transformation : EPipelineTransformation.values()) {
			mapping.put(transformation, ETransformationState.NOT_REACHED);
			running = false;
		}
	}

	public synchronized void updateState(EPipelineTransformation trans, ETransformationState state) {
		if (trans == EPipelineTransformation.PRE_VALIDATION && state == ETransformationState.RUNNING) {
			this.setRunning(true);
		}
		mapping.put(trans, state);
		if (trans == EPipelineTransformation.T_VALIDATION3 && state == ETransformationState.FINISHED) {
			this.setRunning(false);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		reset();
	}

}
