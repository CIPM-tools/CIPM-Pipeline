package dmodel.runtime.pipeline.blackboard.facade;

import dmodel.base.core.facade.IResettableQueryFacade;
import dmodel.base.core.validation.ValidationSchedulePoint;
import dmodel.runtime.pipeline.validation.data.ValidationData;

public interface IValidationResultsQuery extends IResettableQueryFacade {
	ValidationData get(ValidationSchedulePoint point);
}
