package cipm.consistency.tools.evaluation.scenario.data.pipeline;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
// only represents an abstract view on the status of the pipeline and not in-depth!
public class PipelineUIState {

	private Map<EPipelineTransformation, ETransformationState> mapping = new HashMap<>();
	private boolean running;

}
