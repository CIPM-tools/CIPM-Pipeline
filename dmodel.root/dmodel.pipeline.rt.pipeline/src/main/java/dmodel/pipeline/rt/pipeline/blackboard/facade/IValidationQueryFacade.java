package dmodel.pipeline.rt.pipeline.blackboard.facade;

import java.util.Comparator;
import java.util.List;

import dmodel.pipeline.core.validation.ValidationSchedulePoint;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

public interface IValidationQueryFacade extends Comparator<ValidationData> {

	void process(InMemoryPCM raw, List<PCMContextRecord> data, ValidationSchedulePoint schedulePoint);

}
