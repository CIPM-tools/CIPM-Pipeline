package dmodel.pipeline.rt.pcm.repository;

import java.util.List;

import org.springframework.stereotype.Service;

import dmodel.pipeline.models.mapping.PalladioRuntimeMapping;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.pcm.repository.branch.impl.BranchEstimationImpl;
import dmodel.pipeline.rt.pcm.repository.core.ResourceDemandEstimatorAlternative;
import dmodel.pipeline.rt.pcm.repository.loop.impl.LoopEstimationImpl;
import dmodel.pipeline.rt.pcm.repository.model.IResourceDemandEstimator;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import lombok.extern.java.Log;

@Service
@Log
public class RepositoryDerivation {
	private final LoopEstimationImpl loopEstimation;
	private final BranchEstimationImpl branchEstimation;

	public RepositoryDerivation() {
		this.loopEstimation = new LoopEstimationImpl();
		this.branchEstimation = new BranchEstimationImpl();
	}

	public void calibrateRepository(List<PCMContextRecord> data, InMemoryPCM pcm, PalladioRuntimeMapping mapping,
			ValidationData validation) {

		try {
			MonitoringDataSet monitoringDataSet = new MonitoringDataSet(data, mapping, pcm.getAllocationModel(),
					pcm.getRepository());

			IResourceDemandEstimator estimation = new ResourceDemandEstimatorAlternative(pcm);
			estimation.prepare(monitoringDataSet);
			estimation.derive();

			log.info("Finished calibration of internal actions.");
			log.info("Finished repository calibration.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
