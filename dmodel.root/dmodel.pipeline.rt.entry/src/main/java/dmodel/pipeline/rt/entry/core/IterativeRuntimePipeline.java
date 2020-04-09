package dmodel.pipeline.rt.entry.core;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import dmodel.pipeline.core.facade.IPCMQueryFacade;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipeline;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.data.PartitionedMonitoringData;
import dmodel.pipeline.shared.pcm.LocalFilesystemPCM;
import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.writer.filesystem.AsciiFileWriter;
import lombok.extern.java.Log;

@Component
@Log
public class IterativeRuntimePipeline
		extends AbstractIterativePipeline<PartitionedMonitoringData<IMonitoringRecord>, RuntimePipelineBlackboard>
		implements InitializingBean {
	private static final boolean EVALUATION = true;

	private static final String EVALUATION_BASE_PATH = "evaluation";
	private static final String EVALUATION_MODELS_BASE_PATH = "models";
	private static final String EVALUATION_MONITORING_BASE_PATH = "monitoring";

	private static final String EVALUATION_PATH_MONITORING_ALL = "all/";
	private static final String EVALUATION_PATH_MONITORING_TRAINING = "training/";
	private static final String EVALUATION_PATH_MONITORING_VALIDATION = "validation/";

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private IPCMQueryFacade pcmQuery;

	@Autowired
	private ApplicationContext applicationContext;

	private List<IterativeRuntimePipelineListener> listeners;

	private int currentIteration = 0;

	public IterativeRuntimePipeline() {
		super();
		this.listeners = Lists.newArrayList();
	}

	public void addPipelineListener(IterativeRuntimePipelineListener listener) {
		listeners.add(listener);
	}

	public void removePipelineListener(IterativeRuntimePipelineListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void initBlackboard() {
		blackboard.reset();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.blackboard = blackboard;
		this.buildPipeline(IterativePipelineEntryPoint.class, new SpringContextClassProvider(applicationContext));

		if (EVALUATION) {
			File evaluationBasePath = new File(EVALUATION_BASE_PATH);
			evaluationBasePath.delete();
		}
	}

	@Override
	protected void onIterationFinished(PartitionedMonitoringData<IMonitoringRecord> monitoring) {
		blackboard.getQuery().trackEndPipelineExecution();
		log.info("Finished execution of the pipeline.");

		// listeners
		listeners.forEach(list -> list.iterationFinished());

		// EVALUATION STUFF
		if (EVALUATION) {
			currentIteration++;

			File evaluationBasePath = new File(EVALUATION_BASE_PATH);
			File monitoringPath = new File(evaluationBasePath, EVALUATION_MONITORING_BASE_PATH);
			File modelPath = new File(evaluationBasePath, EVALUATION_MODELS_BASE_PATH);

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

			pcmQuery.getDeepCopy().saveToFilesystem(fileSys);

			// save monitoring data
			saveMonitoringData(monitoring.getAllData(), new File(monitoringPath, EVALUATION_PATH_MONITORING_ALL));
			saveMonitoringData(monitoring.getTrainingData(),
					new File(monitoringPath, EVALUATION_PATH_MONITORING_TRAINING));
			saveMonitoringData(monitoring.getValidationData(),
					new File(monitoringPath, EVALUATION_PATH_MONITORING_VALIDATION));
		}
	}

	private void saveMonitoringData(List<IMonitoringRecord> monitoring, File file) {
		if (!file.exists()) {
			file.mkdirs();
		}

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
