package dmodel.pipeline.rt.pcm.usagemodel.cocome;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;

import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pcm.usagemodel.transformation.UsageDataDerivation;
import dmodel.pipeline.rt.pipeline.AbstractPipelineTestBase;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.structure.Tree;
import kieker.analysis.exception.AnalysisConfigurationException;

public class CocomeUsageModelDerivationTest extends AbstractPipelineTestBase {
	protected UsageDataDerivation derivation;

	@Before
	public void initTransformation() {
		derivation = new UsageDataDerivation();
	}

	@Override
	protected void loadPCMModels() {
		blackboard.getArchitectureModel()
				.setRepository(ModelUtil.readFromFile("test-data/cocome/models/cocome.repository", Repository.class));

		blackboard.getArchitectureModel()
				.setSystem(ModelUtil.readFromFile("test-data/cocome/models/cocome.system", System.class));

		blackboard.getArchitectureModel().setResourceEnvironmentModel(ModelUtil
				.readFromFile("test-data/cocome/models/cocome.resourceenvironment", ResourceEnvironment.class));

		blackboard.getArchitectureModel().setAllocationModel(
				ModelUtil.readFromFile("test-data/cocome/models/cocome.allocation", Allocation.class));
	}

	@Test
	public void basicCoCoMETest() throws IllegalStateException, AnalysisConfigurationException {
		List<PCMContextRecord> recs = MonitoringDataUtil.getMonitoringDataFromFiles("test-data/cocome/monitoring/");
		List<ServiceCallRecord> srecs = recs.stream().filter(f -> f instanceof ServiceCallRecord)
				.map(ServiceCallRecord.class::cast).collect(Collectors.toList());
		List<Tree<ServiceCallRecord>> records = MonitoringDataUtil.buildServiceCallTree(srecs);

		derivation.deriveUsageData(records, blackboard.getArchitectureModel(), null);

		ModelUtil.saveToFile(blackboard.getArchitectureModel().getUsageModel(), "output.usagemodel");
	}

}
