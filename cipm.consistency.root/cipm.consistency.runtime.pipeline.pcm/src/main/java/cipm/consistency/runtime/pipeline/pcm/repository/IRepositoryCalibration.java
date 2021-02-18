package cipm.consistency.runtime.pipeline.pcm.repository;

import java.util.List;
import java.util.Set;

import cipm.consistency.base.core.facade.IPCMQueryFacade;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.pcm.repository.calibration.RepositoryStoexChanges;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;

public interface IRepositoryCalibration {

	public RepositoryStoexChanges calibrateRepository(List<PCMContextRecord> data, IPCMQueryFacade pcm,
			ValidationData validation, Set<String> fineGraindInstrumentedServiceIds);

	public void reset();

}
