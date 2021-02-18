package cipm.consistency.runtime.pipeline.blackboard.facade;

import java.util.Comparator;
import java.util.List;

import cipm.consistency.base.core.state.ValidationSchedulePoint;
import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;

public interface IValidationQueryFacade extends Comparator<ValidationData> {

	void process(InMemoryPCM raw, List<PCMContextRecord> data, ValidationSchedulePoint schedulePoint);

}
