package dmodel.pipeline.rt.pcm.repository.tests;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Sets;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pcm.repository.AbstractRepositoryTransformationTestBase;
import dmodel.pipeline.rt.pcm.repository.RepositoryDerivation;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.data.PCMPartionedMonitoringData;
import dmodel.pipeline.rt.pipeline.data.PartitionedMonitoringData;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.ModelUtil;
import kieker.analysis.exception.AnalysisConfigurationException;
import tools.vitruv.framework.correspondence.Correspondences;

@RunWith(SpringRunner.class)
@Import(AbstractRepositoryTransformationTestBase.RepositoryTransformationTestConfiguration.class)
public class SimpleRepositoryTransformationTest extends AbstractRepositoryTransformationTestBase {
	@Autowired
	private RepositoryDerivation transformation;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Test
	public void test() throws IllegalStateException, AnalysisConfigurationException {
		List<PCMContextRecord> dataset = MonitoringDataUtil
				.getMonitoringDataFromFiles("test-data/repository/monitoring");
		PartitionedMonitoringData<PCMContextRecord> pcmData = new PCMPartionedMonitoringData(dataset, 0.1f);

		transformation.calibrateRepository(pcmData, blackboard.getPcmQuery(), new ValidationData(), Sets.newHashSet());
	}

	@Before
	public void loadModels() {
		setSpecific(
				ModelUtil.readFromResource(
						SimpleRepositoryTransformationTest.class.getResource("/models/prime_generator.rem"),
						RuntimeEnvironmentModel.class),
				ModelUtil.readFromResource(
						SimpleRepositoryTransformationTest.class.getResource("/models/prime_generator.imm"),
						InstrumentationModel.class),
				ModelUtil.readFromResource(
						SimpleRepositoryTransformationTest.class.getResource("/models/prime_generator.correspondences"),
						Correspondences.class));
		setPcm(ModelUtil.readFromResource(
				SimpleRepositoryTransformationTest.class.getResource("/models/prime_generator.repository"),
				Repository.class),
				ModelUtil.readFromResource(
						SimpleRepositoryTransformationTest.class.getResource("/models/prime_generator.system"),
						org.palladiosimulator.pcm.system.System.class),
				ModelUtil.readFromResource(SimpleRepositoryTransformationTest.class
						.getResource("/models/prime_generator.resourceenvironment"), ResourceEnvironment.class),
				ModelUtil.readFromResource(
						SimpleRepositoryTransformationTest.class.getResource("/models/prime_generator.allocation"),
						Allocation.class),
				null);

		this.reloadVsum();
	}

}
