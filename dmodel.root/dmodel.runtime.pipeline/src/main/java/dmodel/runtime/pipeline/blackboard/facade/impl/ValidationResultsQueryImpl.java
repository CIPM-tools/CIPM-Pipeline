package dmodel.runtime.pipeline.blackboard.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.base.core.validation.ValidationSchedulePoint;
import dmodel.runtime.pipeline.blackboard.facade.IValidationResultsQuery;
import dmodel.runtime.pipeline.blackboard.validation.ValidationResultContainer;
import dmodel.runtime.pipeline.validation.data.ValidationData;

@Component
public class ValidationResultsQueryImpl implements IValidationResultsQuery {
	@Autowired
	private ValidationResultContainer validationResults;

	@Override
	public void reset(boolean hard) {
		if (hard) {
			validationResults.reset();
		}
	}

	@Override
	public ValidationData get(ValidationSchedulePoint point) {
		return validationResults.getData(point);
	}

}
