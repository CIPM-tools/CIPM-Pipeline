package dmodel.pipeline.rt.pcm.usagemodel.cocome;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pcm.usagemodel.transformation.UsageDataDerivation;
import dmodel.pipeline.rt.pipeline.AbstractTransformationTest;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.structure.Tree;
import kieker.analysis.exception.AnalysisConfigurationException;

public class CocomeUsageModelDerivationTest extends AbstractTransformationTest {
	protected UsageDataDerivation derivation;

	@Before
	public void initTransformation() {
		derivation = new UsageDataDerivation();
	}

	@Override
	protected void loadPCMModels() {
		blackboard.getArchitectureModel()
				.setRepository(ModelUtil.readFromResource(
						CocomeUsageModelDerivationTest.class.getResource("/cocome/models/cocome.repository"),
						Repository.class));

		blackboard.getArchitectureModel()
				.setSystem(ModelUtil.readFromResource(
						CocomeUsageModelDerivationTest.class.getResource("/cocome/models/cocome.system"),
						org.palladiosimulator.pcm.system.System.class));

		blackboard.getArchitectureModel()
				.setResourceEnvironmentModel(ModelUtil.readFromResource(
						CocomeUsageModelDerivationTest.class.getResource("/cocome/models/cocome.resourceenvironment"),
						ResourceEnvironment.class));

		blackboard.getArchitectureModel()
				.setAllocationModel(ModelUtil.readFromResource(
						CocomeUsageModelDerivationTest.class.getResource("/cocome/models/cocome.allocation"),
						Allocation.class));
	}

	@Test
	public void noMonitoringDataTest() throws IllegalStateException, AnalysisConfigurationException {
		List<PCMContextRecord> recs = MonitoringDataUtil.getMonitoringDataFromFiles("cocome/monitoring/");
		List<ServiceCallRecord> srecs = recs.stream().filter(f -> f instanceof ServiceCallRecord)
				.map(ServiceCallRecord.class::cast).collect(Collectors.toList());
		List<Tree<ServiceCallRecord>> records = MonitoringDataUtil.buildServiceCallTree(srecs);

		derivation.deriveUsageData(records, blackboard.getArchitectureModel(), null);

		ModelUtil.saveToFile(blackboard.getArchitectureModel().getUsageModel(), "output.usagemodel");
	}

}
