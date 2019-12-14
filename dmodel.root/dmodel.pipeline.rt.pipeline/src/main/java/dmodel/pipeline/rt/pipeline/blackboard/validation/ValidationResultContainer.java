package dmodel.pipeline.rt.pipeline.blackboard.validation;

import org.springframework.stereotype.Service;

import dmodel.pipeline.rt.validation.data.ValidationData;
import lombok.Data;

@Service
@Data
public class ValidationResultContainer {
	private ValidationData preValidationResults;

	private ValidationData afterUsageModelResults;
	private ValidationData afterRepositoryResults;

	private ValidationData finalResults;

	public void reset() {
		preValidationResults = afterUsageModelResults = afterRepositoryResults = finalResults = null;
	}

}
