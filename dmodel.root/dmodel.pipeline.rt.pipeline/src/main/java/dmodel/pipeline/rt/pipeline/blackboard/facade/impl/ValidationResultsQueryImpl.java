package dmodel.pipeline.rt.pipeline.blackboard.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.core.validation.ValidationSchedulePoint;
import dmodel.pipeline.rt.pipeline.blackboard.facade.IValidationResultsQuery;
import dmodel.pipeline.rt.pipeline.blackboard.validation.ValidationResultContainer;
import dmodel.pipeline.rt.validation.data.ValidationData;

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
