package dmodel.pipeline.rt.pipeline.blackboard.validation;

import java.util.List;

import org.springframework.stereotype.Service;

import dmodel.pipeline.rt.validation.data.ValidationMetric;
import lombok.Data;

@Service
@Data
public class ValidationResultContainer {

	private List<ValidationMetric> preValidationResults;

	private List<ValidationMetric> afterUsageModelResults;
	private List<ValidationMetric> afterRepositoryResults;

	private List<ValidationMetric> finalResults;

	public void reset() {
		preValidationResults = afterUsageModelResults = afterRepositoryResults = finalResults = null;
	}

}
