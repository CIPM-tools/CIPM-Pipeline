package dmodel.pipeline.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dmodel.pipeline.dt.system.DesignTimeSystemExtractionTestSuite;
import dmodel.pipeline.rt.pcm.repository.RepositoryTransformationTestSuite;
import dmodel.pipeline.rt.pcm.resourceenv.ResourceEnvironmentTransformationTestSuite;
import dmodel.pipeline.rt.pcm.system.SystemTransformationTestSuite;
import dmodel.pipeline.rt.pipeline.PipelineTestSuite;
import dmodel.pipeline.rt.validation.tests.ValidationProcessorTestSuite;
import dmodel.pipeline.vsum.VsumTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ VsumTestSuite.class, ValidationProcessorTestSuite.class, SystemTransformationTestSuite.class,
		DesignTimeSystemExtractionTestSuite.class, PipelineTestSuite.class,
		ResourceEnvironmentTransformationTestSuite.class, RepositoryTransformationTestSuite.class })
public class FullTestSuite {
}
