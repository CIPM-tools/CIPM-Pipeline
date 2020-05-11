package dmodel.runtime.pipeline.validation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.beust.jcommander.internal.Lists;

import dmodel.base.core.IPcmModelProvider;
import dmodel.base.shared.ModelUtil;
import dmodel.base.vsum.VsumManagerTestBase;
import dmodel.runtime.pipeline.validation.data.ValidationData;

@RunWith(SpringRunner.class)
@Import(ValidationProcessorTestBase.TestContextConfiguration.class)
public class ValidationProcessorTest extends VsumManagerTestBase {
	@Autowired
	private ValidationFeedbackComponent feedback;

	@Autowired
	private IPcmModelProvider pcmProvider;

	@Test
	public void checkNotNull() {
		assertNotNull(feedback);
	}

	@Test
	public void testSimulation() {
		for (int i = 0; i < 5; i++) {
			ValidationData data = feedback.process(pcmProvider.getRaw(), Lists.newArrayList(), "TestAnalysis");
			assertNotNull(data);
			assertTrue(data.getValidationPoints().size() > 2);
		}
		feedback.clearSimulationData();
	}

	@Before
	public void loadPCMModels() {
		super.setSpecific(null, null, null);
		super.setPcm(
				ModelUtil.readFromResource(ValidationProcessorTest.class.getResource("/models/cocome.repository"),
						Repository.class),
				ModelUtil.readFromResource(ValidationProcessorTest.class.getResource("/models/cocome.system"),
						System.class),
				ModelUtil.readFromResource(
						ValidationProcessorTest.class.getResource("/models/cocome.resourceenvironment"),
						ResourceEnvironment.class),
				ModelUtil.readFromResource(ValidationProcessorTest.class.getResource("/models/cocome.allocation"),
						Allocation.class),
				ModelUtil.readFromResource(ValidationProcessorTest.class.getResource("/models/cocome.usagemodel"),
						UsageModel.class));
	}

}
