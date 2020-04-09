package dmodel.pipeline.rt.pipeline.blackboard.facade;

import dmodel.pipeline.core.facade.IResettableQueryFacade;
import dmodel.pipeline.core.validation.ValidationSchedulePoint;
import dmodel.pipeline.rt.validation.data.ValidationData;

public interface IValidationResultsQuery extends IResettableQueryFacade {
	ValidationData get(ValidationSchedulePoint point);
}
