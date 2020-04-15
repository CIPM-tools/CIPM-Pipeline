package dmodel.pipeline.rt.router;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.core.state.EPipelineTransformation;
import dmodel.pipeline.core.state.ETransformationState;
import dmodel.pipeline.core.validation.ValidationSchedulePoint;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.repository.RepositoryDerivation;
import dmodel.pipeline.rt.pcm.repository.RepositoryStoexChanges;
import dmodel.pipeline.rt.pcm.usagemodel.transformation.UsageDataDerivation;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.data.PartitionedMonitoringData;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
import lombok.extern.java.Log;

@Log
@Service
public class AccuracySwitch extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	// sub transformations
	@Autowired
	private UsageDataDerivation usageDataTransformation;

	@Autowired
	private RepositoryDerivation repositoryTransformation;

	// needed internals
	private ExecutorService executorService;
	private CountDownLatch waitLatch;
	private InMemoryPCM copyForUsage;
	private InMemoryPCM copyForRepository;
	private Set<String> fineGrainedInstrumentedServices;

	private ValidationData validationRepository;
	private ValidationData validationUsage;

	private RepositoryStoexChanges extractedStoexChanges;
	private List<UsageScenario> extractedUsageScenarios;

	public AccuracySwitch() {
		super(null, null);
		executorService = Executors.newFixedThreadPool(2);
	}

	@InputPorts({ @InputPort(PortIDs.T_SC_ROUTER), @InputPort(PortIDs.T_RAW_ROUTER),
			@InputPort(PortIDs.T_SYSTEM_ROUTER) })
	@OutputPorts(@OutputPort(to = FinalValidationTask.class, async = false, id = PortIDs.T_FINAL_VALIDATION))
	public void accuracyRouter(List<Tree<ServiceCallRecord>> entryCalls,
			PartitionedMonitoringData<PCMContextRecord> rawMonitoringData) {
		log.info("Running usage model and repository derivation.");

		// create deep copies
		createDeepCopies();

		// 1. invoke the transformations
		waitLatch = new CountDownLatch(2);

		// 1.0. get services that are instrumented fine grained
		fineGrainedInstrumentedServices = getFineGrainedInstrumentedServices();

		// 1.1 submit usage derivation
		submitUsageDataTransformation(entryCalls, ExecutionMeasuringPoint.T_USAGE_1,
				EPipelineTransformation.T_USAGEMODEL1, ValidationSchedulePoint.PRE_PIPELINE);

		// 1.2. submit repository derivation
		submitRepositoryTransformation(rawMonitoringData, ExecutionMeasuringPoint.T_DEMAND_CALIBRATION_1,
				EPipelineTransformation.T_REPOSITORY1, ValidationSchedulePoint.PRE_PIPELINE);

		// 2. wait for the transformations to finish
		try {
			waitLatch.await();
		} catch (InterruptedException e) {
			log.warning("Waiting for the subtransformations has been interrupted.");
			return;
		}

		// 2.1 apply the changes
		extractedStoexChanges.apply(copyForRepository.getRepository());
		applyUsageScenarios(extractedUsageScenarios, copyForUsage.getUsageModel());

		// 3. simulate the resulting models
		simulateResultingModels(rawMonitoringData);
		validationRepository = getBlackboard().getValidationResultsQuery().get(ValidationSchedulePoint.AFTER_T_REPO);
		validationUsage = getBlackboard().getValidationResultsQuery().get(ValidationSchedulePoint.AFTER_T_USAGE);

		// 3.1. check which one is better
		getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_CROSS_VALIDATION);
		int crossValidationResult = getBlackboard().getValidationQuery().compare(validationRepository, validationUsage);
		getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_CROSS_VALIDATION);

		// 4. execute one final transformation on the better model
		waitLatch = new CountDownLatch(1);
		if (crossValidationResult >= 0) {
			log.info("Selected repository path.");
			getBlackboard().getQuery().trackPath(true);

			submitUsageDataTransformation(entryCalls, ExecutionMeasuringPoint.T_USAGE_2,
					EPipelineTransformation.T_USAGEMODEL2, ValidationSchedulePoint.AFTER_T_REPO);
		} else {
			log.info("Selected usage path.");
			getBlackboard().getQuery().trackPath(false);

			submitRepositoryTransformation(rawMonitoringData, ExecutionMeasuringPoint.T_DEMAND_CALIBRATION_2,
					EPipelineTransformation.T_REPOSITORY2, ValidationSchedulePoint.AFTER_T_USAGE);
		}

		// 4.1. wait for transformation to finish
		try {
			waitLatch.await();
		} catch (InterruptedException e) {
			log.warning("Waiting for the subtransformations has been interrupted.");
			return;
		}

		// 5. apply to current model
		extractedStoexChanges.apply(getBlackboard().getPcmQuery().getRaw().getRepository());
		applyUsageScenarios(extractedUsageScenarios, getBlackboard().getPcmQuery().getRaw().getUsageModel());

		// 6. reset caches (soft) because the may invalid now
		getBlackboard().reset(false);

		// evaluation
		getBlackboard().getQuery().trackUsageScenarios(getBlackboard().getPcmQuery().getUsage().getScnearioCount());
	}

	private void simulateResultingModels(PartitionedMonitoringData<PCMContextRecord> rawMonitoringData) {
		simulateResultingModel(copyForRepository, rawMonitoringData, ValidationSchedulePoint.AFTER_T_REPO,
				ExecutionMeasuringPoint.T_VALIDATION_2, EPipelineTransformation.T_VALIDATION22);

		simulateResultingModel(copyForUsage, rawMonitoringData, ValidationSchedulePoint.AFTER_T_USAGE,
				ExecutionMeasuringPoint.T_VALIDATION_3, EPipelineTransformation.T_VALIDATION21);
	}

	private void simulateResultingModel(InMemoryPCM pcm, PartitionedMonitoringData<PCMContextRecord> monitoring,
			ValidationSchedulePoint valPoint, ExecutionMeasuringPoint execPoint, EPipelineTransformation trans) {
		getBlackboard().getQuery().track(execPoint);
		getBlackboard().getQuery().updateState(trans, ETransformationState.RUNNING);

		getBlackboard().getValidationQuery().process(copyForRepository, monitoring.getValidationData(), valPoint);

		getBlackboard().getQuery().updateState(trans, ETransformationState.FINISHED);
		getBlackboard().getQuery().track(execPoint);
	}

	private void createDeepCopies() {
		getBlackboard().getPcmQuery().getRaw().clearListeners();
		copyForUsage = getBlackboard().getPcmQuery().getDeepCopy();
		copyForRepository = getBlackboard().getPcmQuery().getDeepCopy();
		copyForUsage.clearListeners();
		copyForRepository.clearListeners();
	}

	private Set<String> getFineGrainedInstrumentedServices() {
		return getBlackboard().getInmQuery().getFineGrainedInstrumentedServices().stream()
				.map(sip -> sip.getService().getId()).collect(Collectors.toSet());
	}

	private void submitRepositoryTransformation(PartitionedMonitoringData<PCMContextRecord> rawMonitoringData,
			ExecutionMeasuringPoint measuringPoint, EPipelineTransformation transformation,
			ValidationSchedulePoint reference) {
		executorService.submit(() -> {
			getBlackboard().getQuery().track(measuringPoint);
			getBlackboard().getQuery().updateState(transformation, ETransformationState.RUNNING);

			extractedStoexChanges = repositoryTransformation.calibrateRepository(rawMonitoringData,
					getBlackboard().getPcmQuery(), getBlackboard().getValidationResultsQuery().get(reference),
					fineGrainedInstrumentedServices);

			getBlackboard().getQuery().updateState(transformation, ETransformationState.FINISHED);
			getBlackboard().getQuery().track(measuringPoint);

			waitLatch.countDown();
		});
	}

	private void submitUsageDataTransformation(List<Tree<ServiceCallRecord>> entryCalls,
			ExecutionMeasuringPoint measuringPoint, EPipelineTransformation transformation,
			ValidationSchedulePoint reference) {
		executorService.submit(() -> {
			getBlackboard().getQuery().track(measuringPoint);
			getBlackboard().getQuery().updateState(transformation, ETransformationState.RUNNING);

			extractedUsageScenarios = usageDataTransformation.deriveUsageData(entryCalls, getBlackboard().getPcmQuery(),
					getBlackboard().getValidationResultsQuery().get(reference));

			getBlackboard().getQuery().track(measuringPoint);
			getBlackboard().getQuery().updateState(transformation, ETransformationState.RUNNING);

			waitLatch.countDown();
		});
	}

	private void applyUsageScenarios(List<UsageScenario> scenarios, UsageModel usage) {
		if (scenarios != null && scenarios.size() > 0) {
			usage.getUsageScenario_UsageModel().clear();
			usage.getUsageScenario_UsageModel().addAll(scenarios);
		}
	}

}
