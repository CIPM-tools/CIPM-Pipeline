package dmodel.pipeline.rt.pipeline.blackboard;

import java.io.File;

import org.springframework.stereotype.Service;

import dmodel.pipeline.dt.mmmodel.MeasurementModel;
import dmodel.pipeline.dt.mmmodel.MmmodelFactory;
import dmodel.pipeline.shared.config.ModelConfiguration;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.LocalFilesystemPCM;

@Service
public class RuntimePipelineBlackboard {

	private MeasurementModel measurementModel;
	private InMemoryPCM architectureModel;
	private LocalFilesystemPCM filesystemPCM;

	public RuntimePipelineBlackboard() {
		this.reset();
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
		filesystemPCM.setAllocationModelFile(new File(config.getAllocationPath()));
		filesystemPCM.setRepositoryFile(new File(config.getRepositoryPath()));
		filesystemPCM.setResourceEnvironmentFile(new File(config.getEnvPath()));
		filesystemPCM.setSystemFile(new File(config.getSystemPath()));
		filesystemPCM.setUsageModelFile(new File(config.getUsagePath()));

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

}
