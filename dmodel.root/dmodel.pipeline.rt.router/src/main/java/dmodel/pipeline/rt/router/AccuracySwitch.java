package dmodel.pipeline.rt.router;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.records.ServiceContextRecord;
import dmodel.pipeline.rt.pcm.repository.RepositoryDerivation;
import dmodel.pipeline.rt.pcm.usagemodel.transformation.UsageDataDerivation;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.validation.data.ValidationMetric;
import dmodel.pipeline.rt.validation.data.ValidationMetricType;
import dmodel.pipeline.rt.validation.data.ValidationMetricValue;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
import lombok.extern.java.Log;

@Log
@Service
// TODO refactoring
public class AccuracySwitch extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	private ScheduledExecutorService executorService;

	@Autowired
	private UsageDataDerivation usageDataTransformation;

	@Autowired
	private RepositoryDerivation repositoryTransformation;

	public AccuracySwitch() {
		executorService = Executors.newScheduledThreadPool(2);
	}

	@InputPorts({ @InputPort(PortIDs.T_SC_ROUTER), @InputPort(PortIDs.T_RAW_ROUTER),
			@InputPort(PortIDs.T_SYSTEM_ROUTER) })
	public void accuracyRouter(List<Tree<ServiceCallRecord>> entryCalls, List<ServiceContextRecord> rawMonitoringData) {
		log.info("Running usage model and repository derivation.");

		InMemoryPCM copyForUsage = getBlackboard().getArchitectureModel().copyReference();
		InMemoryPCM copyForRepository = getBlackboard().getArchitectureModel().copyReference();

		// we assume here that these two transformations only change one model part
		// for the usage transformation only the usage model is duplicated and for the
		// repository only the repo
		copyForUsage.setUsageModel(EcoreUtil.copy(copyForUsage.getUsageModel()));
		copyForRepository.setRepository(EcoreUtil.copy(copyForRepository.getRepository()));

		// 1. invoke the transformations
		CountDownLatch waitLatch = new CountDownLatch(2);

		// 1.1 submit usage derivation
		executorService.submit(() -> {
			usageDataTransformation.deriveUsageData(entryCalls, copyForUsage,
					getBlackboard().getValidationResultContainer().getPreValidationResults());
			waitLatch.countDown();
		});
		// 1.2. submit repository derivation
		executorService.submit(() -> {
			repositoryTransformation.calibrateRepository(entryCalls, copyForRepository,
					getBlackboard().getValidationResultContainer().getPreValidationResults());
			waitLatch.countDown();
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
		List<ValidationMetric> pathRepository = getBlackboard().getValidationFeedbackComponent()
				.process(copyForRepository, rawMonitoringData, "ValidateRepo");
		List<ValidationMetric> pathUsageModel = getBlackboard().getValidationFeedbackComponent().process(copyForUsage,
				rawMonitoringData, "ValidateUsage");

		// add them to the blackboard
		getBlackboard().getValidationResultContainer().setAfterRepositoryResults(pathRepository);
		getBlackboard().getValidationResultContainer().setAfterUsageModelResults(pathUsageModel);

		// 3.1. check which one is better
		double sum = 0;
		Map<Pair<List<String>, ValidationMetricType>, ValidationMetricValue> mappingA = Maps.newHashMap();
		pathRepository.forEach(m -> {
			mappingA.put(Pair.of(m.getTargetIds(), m.getValue().type()), m.getValue());
		});
		for (ValidationMetric metric : pathUsageModel) {
			Pair<List<String>, ValidationMetricType> query = Pair.of(metric.getTargetIds(), metric.getValue().type());
			if (mappingA.containsKey(query)) {
				sum += mappingA.get(query).compare(metric.getValue());
			}
		}

		// 4. execute one final transformation on the better model
		if (sum >= 0) {
			copyForRepository.saveToFilesystem(getBlackboard().getFilesystemPCM());
			// repository was better
			usageDataTransformation.deriveUsageData(entryCalls, copyForRepository, pathRepository);
		} else {
			copyForUsage.saveToFilesystem(getBlackboard().getFilesystemPCM());
			// usagemodel was better
			usageDataTransformation.deriveUsageData(entryCalls, copyForUsage, pathUsageModel);
		}

		// 5. set it as final
		if (sum >= 0) {
			getBlackboard().setArchitectureModel(copyForRepository);
		} else {
			getBlackboard().setArchitectureModel(copyForUsage);
		}

		// save finally
		getBlackboard().getArchitectureModel().saveToFilesystem(getBlackboard().getFilesystemPCM());
	}

}
