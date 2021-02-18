package cipm.consistency.runtime.pipeline.blackboard.facade;

import cipm.consistency.base.core.facade.IResettableQueryFacade;
import cipm.consistency.base.core.state.ValidationSchedulePoint;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;

public interface IValidationResultsQuery extends IResettableQueryFacade {
	ValidationData get(ValidationSchedulePoint point);
}
