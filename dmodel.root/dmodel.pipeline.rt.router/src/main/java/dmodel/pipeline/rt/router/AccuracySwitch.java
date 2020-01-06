package dmodel.pipeline.rt.router;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.repository.RepositoryDerivation;
import dmodel.pipeline.rt.pcm.usagemodel.transformation.UsageDataDerivation;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.blackboard.state.EPipelineTransformation;
import dmodel.pipeline.rt.pipeline.blackboard.state.ETransformationState;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.rt.validation.data.ValidationMetricValue;
import dmodel.pipeline.rt.validation.data.ValidationPoint;
import dmodel.pipeline.rt.validation.data.metric.ValidationMetricType;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
import lombok.extern.java.Log;

@Log
@Service
// TODO refactoring
public class AccuracySwitch extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	private ExecutorService executorService;

	@Autowired
	private UsageDataDerivation usageDataTransformation;

	@Autowired
	private RepositoryDerivation repositoryTransformation;

	public AccuracySwitch() {
		executorService = Executors.newFixedThreadPool(2);
	}

	@InputPorts({ @InputPort(PortIDs.T_SC_ROUTER), @InputPort(PortIDs.T_RAW_ROUTER),
			@InputPort(PortIDs.T_SYSTEM_ROUTER) })
	@OutputPorts(@OutputPort(to = FinalValidationTask.class, async = false, id = PortIDs.T_FINAL_VALIDATION))
	public void accuracyRouter(List<Tree<ServiceCallRecord>> entryCalls, List<PCMContextRecord> rawMonitoringData) {
		log.info("Running usage model and repository derivation.");

		// create deep copies
		InMemoryPCM copyForUsage = getBlackboard().getArchitectureModel().copyDeep();
		InMemoryPCM copyForRepository = getBlackboard().getArchitectureModel().copyDeep();
		copyForRepository.clearListeners();
		copyForUsage.clearListeners();

		// 1. invoke the transformations
		CountDownLatch waitLatch = new CountDownLatch(2);

		// 1.1 submit usage derivation
		executorService.submit(() -> {
			getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_USAGEMODEL1,
					ETransformationState.RUNNING);
			usageDataTransformation.deriveUsageData(entryCalls, copyForUsage,
					getBlackboard().getValidationResultContainer().getPreValidationResults());
			waitLatch.countDown();
			getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_USAGEMODEL1,
					ETransformationState.FINISHED);
		});
		// 1.2. submit repository derivation
		executorService.submit(() -> {
			getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_REPOSITORY1,
					ETransformationState.RUNNING);
			repositoryTransformation.calibrateRepository(rawMonitoringData, copyForRepository,
					getBlackboard().getBorder().getRuntimeMapping(),
					getBlackboard().getValidationResultContainer().getPreValidationResults());
			waitLatch.countDown();
			getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_REPOSITORY1,
					ETransformationState.FINISHED);
		});

		// 2. wait for the transformations to finish
		try {
			waitLatch.await();
		} catch (InterruptedException e) {
			log.warning("Waiting for the subtransformations has been interrupted.");
			return;
		}

		// 3. simulate the resulting models
		// TODO this could be parallelized
		getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_VALIDATION22,
				ETransformationState.RUNNING);
		ValidationData pathRepository = getBlackboard().getValidationFeedbackComponent().process(copyForRepository,
				getBlackboard().getBorder().getRuntimeMapping(), rawMonitoringData, "ValidateRepo");
		getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_VALIDATION22,
				ETransformationState.FINISHED);

		getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_VALIDATION21,
				ETransformationState.RUNNING);
		ValidationData pathUsageModel = getBlackboard().getValidationFeedbackComponent().process(copyForUsage,
				getBlackboard().getBorder().getRuntimeMapping(), rawMonitoringData, "ValidateUsage");
		getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_VALIDATION21,
				ETransformationState.FINISHED);

		// add them to the blackboard
		getBlackboard().getValidationResultContainer().setAfterRepositoryResults(pathRepository);
		getBlackboard().getValidationResultContainer().setAfterUsageModelResults(pathUsageModel);

		// 3.1. check which one is better
		double sum = 0;
		Map<Triple<String, String, ValidationMetricType>, ValidationMetricValue> mappingA = Maps.newHashMap();
		if (pathRepository != null && pathUsageModel != null) {
			pathRepository.getValidationPoints().forEach(m -> {
				m.getMetricValues().forEach(metricValue -> {
					mappingA.put(Triple.of(m.getId(), m.getMetricDescription().getId(), metricValue.type()),
							metricValue);
				});
			});
			for (ValidationPoint validationPoint : pathUsageModel.getValidationPoints()) {
				for (ValidationMetricValue val : validationPoint.getMetricValues()) {
					Triple<String, String, ValidationMetricType> query = Triple.of(validationPoint.getId(),
							validationPoint.getMetricDescription().getId(), val.type());
					if (mappingA.containsKey(query)) {
						sum += mappingA.get(query).compare(val);
					}
				}
			}
		}

		// 4. execute one final transformation on the better model
		if (sum >= 0) {
			getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_USAGEMODEL2,
					ETransformationState.RUNNING);

			// repository was better
			usageDataTransformation.deriveUsageData(entryCalls, copyForRepository, pathRepository);

			getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_USAGEMODEL2,
					ETransformationState.FINISHED);
		} else {
			getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_REPOSITORY2,
					ETransformationState.RUNNING);

			// usagemodel was better
			repositoryTransformation.calibrateRepository(rawMonitoringData, copyForUsage,
					getBlackboard().getBorder().getRuntimeMapping(), pathUsageModel);

			getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_REPOSITORY2,
					ETransformationState.FINISHED);
		}

		// 5. set it as final
		getBlackboard().getArchitectureModel().clearListeners();
		if (sum >= 0) {
			// TODO debug
			getBlackboard().setArchitectureModel(copyForRepository);
			copyForRepository.syncWithFilesystem(getBlackboard().getFilesystemPCM());
		} else {
			getBlackboard().setArchitectureModel(copyForUsage);
			copyForUsage.syncWithFilesystem(getBlackboard().getFilesystemPCM());
		}
	}

}
