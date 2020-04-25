package dmodel.runtime.pipeline.blackboard.facade;

import java.util.Comparator;
import java.util.List;

import dmodel.base.core.validation.ValidationSchedulePoint;
import dmodel.base.shared.pcm.InMemoryPCM;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.runtime.pipeline.validation.data.ValidationData;

public interface IValidationQueryFacade extends Comparator<ValidationData> {

	void process(InMemoryPCM raw, List<PCMContextRecord> data, ValidationSchedulePoint schedulePoint);

}
