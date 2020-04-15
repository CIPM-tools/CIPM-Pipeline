package dmodel.pipeline.rt.pcm.usagemodel.cocome;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pcm.usagemodel.AbstractBaseUsageModelDerivationTest;
import dmodel.pipeline.rt.pcm.usagemodel.transformation.UsageDataDerivation;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.structure.Tree;
import kieker.analysis.exception.AnalysisConfigurationException;

@RunWith(SpringRunner.class)
@Import(AbstractBaseUsageModelDerivationTest.UsageTransformationTestConfiguration.class)
public class CocomeUsageModelDerivationTest extends AbstractBaseUsageModelDerivationTest {
	protected UsageDataDerivation derivation;

	@Override
	protected void loadModels() {
		super.setSpecific(null, null, null);
		super.setPcm(
				ModelUtil.readFromFile(new File("test-data/cocome/models/cocome.repository").getAbsolutePath(),
						Repository.class),
				ModelUtil.readFromFile(new File("test-data/cocome/models/cocome.system").getAbsolutePath(),
						System.class),
				ModelUtil.readFromFile(new File("test-data/cocome/models/cocome.resourceenvironment").getAbsolutePath(),
						ResourceEnvironment.class),
				ModelUtil.readFromFile(new File("test-data/cocome/models/cocome.allocation").getAbsolutePath(),
						Allocation.class),
				null);
	}

	@Test
	public void basicCoCoMETest() throws IllegalStateException, AnalysisConfigurationException {
		List<PCMContextRecord> recs = MonitoringDataUtil.getMonitoringDataFromFiles("test-data/cocome/monitoring/");
		List<ServiceCallRecord> srecs = recs.stream().filter(f -> f instanceof ServiceCallRecord)
				.map(ServiceCallRecord.class::cast).collect(Collectors.toList());
		List<Tree<ServiceCallRecord>> records = MonitoringDataUtil.buildServiceCallTree(srecs);

		super.deriveUsageData(records);

		ModelUtil.saveToFile(pcm.getUsage(), "output.usagemodel");
	}

}
