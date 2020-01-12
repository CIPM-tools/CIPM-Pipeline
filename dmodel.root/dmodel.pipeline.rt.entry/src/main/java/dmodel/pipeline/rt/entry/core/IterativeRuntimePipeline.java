package dmodel.pipeline.rt.entry.core;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import dmodel.pipeline.rt.pipeline.AbstractIterativePipeline;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pcm.LocalFilesystemPCM;
import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.writer.filesystem.AsciiFileWriter;
import lombok.extern.java.Log;

@Component
@Log
public class IterativeRuntimePipeline extends
		AbstractIterativePipeline<List<IMonitoringRecord>, RuntimePipelineBlackboard> implements InitializingBean {
	private static final boolean EVALUATION = true;
	private static final String EVALUATION_PATH = "evaluation/";

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private ApplicationContext applicationContext;

	private int currentIteration = 0;

	public IterativeRuntimePipeline() {
		super();
	}

	@Override
	public void initBlackboard() {
		blackboard.reset();
		blackboard.getValidationFeedbackComponent().clearSimulationData();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.blackboard = blackboard;
		this.buildPipeline(IterativePipelineEntryPoint.class, new SpringContextClassProvider(applicationContext));

		if (EVALUATION) {
			File evaluationBasePath = new File(EVALUATION_PATH);
			evaluationBasePath.delete();
		}
	}

	@Override
	protected void onIterationFinished(List<IMonitoringRecord> monitoring) {
		log.info("Finished execution of the pipeline.");

		// EVALUATION STUFF
		if (EVALUATION) {
			currentIteration++;

			File evaluationBasePath = new File(EVALUATION_PATH);
			File monitoringPath = new File(evaluationBasePath, "monitoring");
			File modelPath = new File(evaluationBasePath, "models");

			if (!monitoringPath.exists()) {
				monitoringPath.mkdirs();
			}
			if (!modelPath.exists()) {
				modelPath.mkdirs();
			}

			// save models
			File repositoryFile = new File(modelPath, "repository_" + currentIteration + ".repository");
			File systemFile = new File(modelPath, "system_" + currentIteration + ".system");
			File resourceEnvFile = new File(modelPath, "resourceenv_" + currentIteration + ".resourceenvironment");
			File allocationFile = new File(modelPath, "allocation_" + currentIteration + ".allocation");
			File usageFile = new File(modelPath, "usage_" + currentIteration + ".usagemodel");

			LocalFilesystemPCM fileSys = new LocalFilesystemPCM();
			fileSys.setAllocationModelFile(allocationFile);
			fileSys.setRepositoryFile(repositoryFile);
			fileSys.setResourceEnvironmentFile(resourceEnvFile);
			fileSys.setSystemFile(systemFile);
			fileSys.setUsageModelFile(usageFile);

			blackboard.getArchitectureModel().saveToFilesystem(fileSys);

			// save monitoring data
			saveMonitoringData(monitoring, monitoringPath);
		}
	}

	private void saveMonitoringData(List<IMonitoringRecord> monitoring, File file) {
		final Configuration configuration = ConfigurationFactory.createDefaultConfiguration();
		configuration.setProperty(ConfigurationFactory.METADATA, "false");
		configuration.setProperty(ConfigurationFactory.AUTO_SET_LOGGINGTSTAMP, "true");
		configuration.setProperty(ConfigurationFactory.WRITER_CLASSNAME, AsciiFileWriter.class.getName());
		// configuration.setProperty(WriterController.RECORD_QUEUE_SIZE, "5");
		configuration.setProperty(AsciiFileWriter.CONFIG_FLUSH, "true");
		configuration.setProperty(ConfigurationFactory.TIMER_CLASSNAME, "kieker.monitoring.timer.SystemMilliTimer");
		configuration.setProperty(AsciiFileWriter.CONFIG_PATH, file.getAbsolutePath());
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXENTRIESINFILE, 2000 * 1000 * 100);

		MonitoringController monitoringController = MonitoringController.createInstance(configuration);
		monitoring.forEach(rec -> {
			monitoringController.newMonitoringRecord(rec);
		});

		monitoringController.terminateMonitoring();
	}

}
