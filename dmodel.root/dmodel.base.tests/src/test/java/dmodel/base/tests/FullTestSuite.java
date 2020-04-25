package dmodel.base.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dmodel.base.vsum.VsumTestSuite;
import dmodel.designtime.system.DesignTimeSystemExtractionTestSuite;
import dmodel.runtime.pipeline.PipelineTestSuite;
import dmodel.runtime.pipeline.entry.MultipleClientTest;
import dmodel.runtime.pipeline.pcm.repository.RepositoryTransformationTestSuite;
import dmodel.runtime.pipeline.pcm.resourceenv.ResourceEnvironmentTransformationTestSuite;
import dmodel.runtime.pipeline.pcm.system.SystemTransformationTestSuite;
import dmodel.runtime.pipeline.pcm.usagemodel.UsageModelTransformationTestSuite;
import dmodel.runtime.pipeline.validation.tests.ValidationProcessorTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ VsumTestSuite.class, ValidationProcessorTestSuite.class, SystemTransformationTestSuite.class,
		DesignTimeSystemExtractionTestSuite.class, PipelineTestSuite.class,
		ResourceEnvironmentTransformationTestSuite.class, RepositoryTransformationTestSuite.class,
		UsageModelTransformationTestSuite.class, MultipleClientTest.class })
public class FullTestSuite {
}
