package dmodel.runtime.pipeline.pcm.repository.tests;

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

import dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import dmodel.base.shared.ModelUtil;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.designtime.monitoring.util.MonitoringDataUtil;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipeline.data.PCMPartionedMonitoringData;
import dmodel.runtime.pipeline.data.PartitionedMonitoringData;
import dmodel.runtime.pipeline.pcm.repository.AbstractRepositoryTransformationTestBase;
import dmodel.runtime.pipeline.pcm.repository.RepositoryDerivation;
import dmodel.runtime.pipeline.pcm.repository.RepositoryStoexChanges;
import dmodel.runtime.pipeline.validation.data.ValidationData;
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

		RepositoryStoexChanges changes = transformation.calibrateRepository(pcmData, blackboard.getPcmQuery(),
				new ValidationData(), Sets.newHashSet());
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
