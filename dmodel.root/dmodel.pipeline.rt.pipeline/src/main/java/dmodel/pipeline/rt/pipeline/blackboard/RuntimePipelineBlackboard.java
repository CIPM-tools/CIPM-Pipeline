package dmodel.pipeline.rt.pipeline.blackboard;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dmodel.pipeline.dt.mmmodel.MeasurementModel;
import dmodel.pipeline.dt.mmmodel.MmmodelFactory;
import dmodel.pipeline.models.mapping.MappingFactory;
import dmodel.pipeline.models.mapping.MappingPackage;
import dmodel.pipeline.models.mapping.PalladioRuntimeMapping;
import dmodel.pipeline.shared.FileBackedModelUtil;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.config.ModelConfiguration;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.LocalFilesystemPCM;
import dmodel.pipeline.shared.structure.DirectedGraph;

@Service
public class RuntimePipelineBlackboard implements InitializingBean {
	private static final long CONSIDER_APPLICATION_RUNNING_BUFFER = 60000;
	private static final String RT_MAPPING_PATH = "models" + File.separator + "rt_mapping.corr";

	private MeasurementModel measurementModel;

	private InMemoryPCM architectureModel;
	private LocalFilesystemPCM filesystemPCM;
	private PalladioRuntimeMapping runtimeMapping;

	private DirectedGraph<String, Integer> serviceCallGraph;

	@Autowired
	private DModelConfigurationContainer config;

	private boolean applicationRunning = false;
	private long lastMonitoringDataReceivedTimestamp = 0;

	private File currentRuntimeMappingPath;

	public RuntimePipelineBlackboard() {
		this.reset();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// load mapping package
		MappingPackage.eINSTANCE.eClass();

		config.getProject().getListeners().add(d -> {
			refreshRuntimeMappingPath();
		});
		refreshRuntimeMappingPath();
	}

	private void refreshRuntimeMappingPath() {
		File rtMappingFile = new File(new File(config.getProject().getRootPath()), RT_MAPPING_PATH);
		try {
			if (currentRuntimeMappingPath != null
					&& rtMappingFile.getCanonicalPath().equals(currentRuntimeMappingPath.getCanonicalPath())) {
				return;
			}
		} catch (IOException e) {
			return;
		}

		this.runtimeMapping = FileBackedModelUtil.synchronize(this.runtimeMapping, rtMappingFile,
				PalladioRuntimeMapping.class, null, v -> {
					return MappingFactory.eINSTANCE.createPalladioRuntimeMapping();
				});
		currentRuntimeMappingPath = rtMappingFile;
	}

	@Scheduled(initialDelay = 1000 * 60, fixedRate = 1000 * 60)
	public void refreshApplicationRunning() {
		if (System.currentTimeMillis() - lastMonitoringDataReceivedTimestamp < CONSIDER_APPLICATION_RUNNING_BUFFER) {
			this.applicationRunning = true;
		} else {
			this.applicationRunning = false;
		}
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

		// clear the old listeners (memory leak)
		if (architectureModel != null) {
			architectureModel.clearListeners();
		}
		architectureModel = InMemoryPCM.createFromFilesystemSynced(filesystemPCM);
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

	public DirectedGraph<String, Integer> getServiceCallGraph() {
		return serviceCallGraph;
	}

	public void setServiceCallGraph(DirectedGraph<String, Integer> serviceCallGraph) {
		this.serviceCallGraph = serviceCallGraph;
	}

	public PalladioRuntimeMapping getRuntimeMapping() {
		return runtimeMapping;
	}

	public void setRuntimeMapping(PalladioRuntimeMapping runtimeMapping) {
		this.runtimeMapping = runtimeMapping;
	}

}
