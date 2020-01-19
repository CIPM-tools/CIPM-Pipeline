package dmodel.pipeline.rt.pipeline.blackboard;

import java.io.File;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import InstrumentationMetamodel.ActionInstrumentationPoint;
import InstrumentationMetamodel.InstrumentationModel;
import InstrumentationMetamodel.InstrumentationModelFactory;
import InstrumentationMetamodel.InstrumentationType;
import InstrumentationMetamodel.ServiceInstrumentationPoint;
import dmodel.pipeline.evaluation.PerformanceEvaluation;
import dmodel.pipeline.rt.pipeline.blackboard.state.PipelineUIState;
import dmodel.pipeline.rt.pipeline.blackboard.validation.ValidationResultContainer;
import dmodel.pipeline.rt.pipeline.border.RunTimeDesignTimeBorder;
import dmodel.pipeline.rt.validation.ValidationFeedbackComponent;
import dmodel.pipeline.shared.ModelUtil;
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
	private InstrumentationModel instrumentationModel;

	@Autowired
	private DModelConfigurationContainer config;

	@Autowired
	private RunTimeDesignTimeBorder border;

	@Autowired
	private ValidationFeedbackComponent validationFeedbackComponent;

	@Autowired
	private ValidationResultContainer validationResultContainer;

	@Autowired
	private PipelineUIState pipelineState;

	@Autowired
	private PerformanceEvaluation performanceEvaluation;

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

		// create an initial instrumentation model
		InstrumentationModel imm = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();
		for (ResourceDemandingSEFF service : ModelUtil.getObjects(architectureModel.getRepository(),
				ResourceDemandingSEFF.class)) {
			ServiceInstrumentationPoint sip = InstrumentationModelFactory.eINSTANCE.createServiceInstrumentationPoint();
			sip.setActive(true);
			sip.setService(service);
			imm.getPoints().add(sip);

			recursiveBuildImm(service, sip);
		}
		this.instrumentationModel = imm;
	}

	public void receivedMonitoringData() {
		this.lastMonitoringDataReceivedTimestamp = System.currentTimeMillis();
	}

	public void reset() {
		// delete previous validation results
		pipelineState.reset();
		validationResultContainer.reset();
	}

	private void recursiveBuildImm(ResourceDemandingBehaviour service, ServiceInstrumentationPoint sip) {
		for (AbstractAction action : service.getSteps_Behaviour()) {
			if (action instanceof LoopAction) {
				ActionInstrumentationPoint inner = InstrumentationModelFactory.eINSTANCE
						.createActionInstrumentationPoint();
				inner.setType(InstrumentationType.LOOP);
				inner.setAction(action);
				inner.setActive(true);
				sip.getActionInstrumentationPoints().add(inner);

				recursiveBuildImm(((LoopAction) action).getBodyBehaviour_Loop(), sip);
			} else if (action instanceof BranchAction) {
				ActionInstrumentationPoint inner = InstrumentationModelFactory.eINSTANCE
						.createActionInstrumentationPoint();
				inner.setType(InstrumentationType.BRANCH);
				inner.setAction(action);
				inner.setActive(true);
				sip.getActionInstrumentationPoints().add(inner);

				((BranchAction) action).getBranches_Branch().stream().forEach(branch -> {
					recursiveBuildImm(branch.getBranchBehaviour_BranchTransition(), sip);
				});
			} else if (action instanceof InternalAction) {
				ActionInstrumentationPoint inner = InstrumentationModelFactory.eINSTANCE
						.createActionInstrumentationPoint();
				inner.setType(InstrumentationType.INTERNAL);
				inner.setAction(action);
				inner.setActive(true);
				sip.getActionInstrumentationPoints().add(inner);
			}
		}
	}

}
