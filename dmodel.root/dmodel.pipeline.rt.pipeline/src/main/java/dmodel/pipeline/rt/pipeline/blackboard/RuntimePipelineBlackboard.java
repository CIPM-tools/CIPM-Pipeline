package dmodel.pipeline.rt.pipeline.blackboard;

import java.io.File;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dmodel.pipeline.dt.mmmodel.MeasurementModel;
import dmodel.pipeline.dt.mmmodel.MmmodelFactory;
import dmodel.pipeline.shared.config.ModelConfiguration;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.LocalFilesystemPCM;

@Service
public class RuntimePipelineBlackboard {
	private static final long CONSIDER_APPLICATION_RUNNING_BUFFER = 60000;

	private MeasurementModel measurementModel;
	private InMemoryPCM architectureModel;
	private LocalFilesystemPCM filesystemPCM;

	private boolean applicationRunning = false;
	private long lastMonitoringDataReceivedTimestamp = 0;

	public RuntimePipelineBlackboard() {
		this.reset();
	}

	@Scheduled(initialDelay = 1000 * 60, fixedRate = 1000 * 60)
	public void refreshApplicationRunning() {
		if (System.currentTimeMillis() - lastMonitoringDataReceivedTimestamp < CONSIDER_APPLICATION_RUNNING_BUFFER) {
			this.applicationRunning = true;
		} else {
			this.applicationRunning = false;
		}
	}

	@Scheduled(initialDelay = 1000 * 60 * 2, fixedRate = 1000 * 60 * 2)
	public void syncModelsFS() {
		architectureModel.saveToFilesystem(filesystemPCM);
	}

	public MeasurementModel getMeasurementModel() {
		return measurementModel;
	}

	public void setMeasurementModel(MeasurementModel measurementModel) {
		this.measurementModel = measurementModel;
	}

	public void reset() {
		measurementModel = MmmodelFactory.eINSTANCE.createMeasurementModel();
	}

	public void loadArchitectureModel(ModelConfiguration config) {
		filesystemPCM = new LocalFilesystemPCM();
		filesystemPCM.setAllocationModelFile(
				config.getAllocationPath().length() > 0 ? new File(config.getAllocationPath()) : null);
		filesystemPCM.setRepositoryFile(
				config.getRepositoryPath().length() > 0 ? new File(config.getRepositoryPath()) : null);
		filesystemPCM
				.setResourceEnvironmentFile(config.getEnvPath().length() > 0 ? new File(config.getEnvPath()) : null);
		filesystemPCM.setSystemFile(config.getSystemPath().length() > 0 ? new File(config.getSystemPath()) : null);
		filesystemPCM.setUsageModelFile(config.getUsagePath().length() > 0 ? new File(config.getUsagePath()) : null);

		architectureModel = InMemoryPCM.createFromFilesystem(filesystemPCM);
	}

	public InMemoryPCM getArchitectureModel() {
		return architectureModel;
	}

	public void setArchitectureModel(InMemoryPCM architectureModel) {
		this.architectureModel = architectureModel;
	}

	public LocalFilesystemPCM getFilesystemPCM() {
		return filesystemPCM;
	}

	public void setFilesystemPCM(LocalFilesystemPCM filesystemPCM) {
		this.filesystemPCM = filesystemPCM;
	}

	public void receivedMonitoringData() {
		this.lastMonitoringDataReceivedTimestamp = System.currentTimeMillis();
	}

	public boolean isApplicationRunning() {
		return applicationRunning;
	}

	public void setApplicationRunning(boolean is) {
		this.applicationRunning = is;
	}

}
