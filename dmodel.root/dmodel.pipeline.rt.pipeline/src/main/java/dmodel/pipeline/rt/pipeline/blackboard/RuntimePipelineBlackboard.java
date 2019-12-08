package dmodel.pipeline.rt.pipeline.blackboard;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dmodel.pipeline.rt.pipeline.blackboard.validation.ValidationResultContainer;
import dmodel.pipeline.rt.pipeline.border.RunTimeDesignTimeBorder;
import dmodel.pipeline.rt.validation.ValidationFeedbackComponent;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.config.ModelConfiguration;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.LocalFilesystemPCM;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Data
@NoArgsConstructor
public class RuntimePipelineBlackboard {
	private static final long CONSIDER_APPLICATION_RUNNING_BUFFER = 60000;

	private InMemoryPCM architectureModel;
	private LocalFilesystemPCM filesystemPCM;

	@Autowired
	private DModelConfigurationContainer config;

	@Autowired
	private RunTimeDesignTimeBorder border;

	@Autowired
	private ValidationFeedbackComponent validationFeedbackComponent;

	@Autowired
	private ValidationResultContainer validationResultContainer;

	private boolean applicationRunning = false;
	private long lastMonitoringDataReceivedTimestamp = 0;

	@Scheduled(initialDelay = 1000 * 60, fixedRate = 1000 * 60)
	public void refreshApplicationRunning() {
		if (System.currentTimeMillis() - lastMonitoringDataReceivedTimestamp < CONSIDER_APPLICATION_RUNNING_BUFFER) {
			this.applicationRunning = true;
		} else {
			this.applicationRunning = false;
		}
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

		// clear the old listeners (memory leak)
		if (architectureModel != null) {
			architectureModel.clearListeners();
		}
		architectureModel = InMemoryPCM.createFromFilesystemSynced(filesystemPCM);
	}

	public void receivedMonitoringData() {
		this.lastMonitoringDataReceivedTimestamp = System.currentTimeMillis();
	}

	public void reset() {
		// delete previous validation results
		validationResultContainer.reset();
	}

}
