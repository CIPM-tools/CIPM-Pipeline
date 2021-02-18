package cipm.consistency.runtime.pipeline;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import cipm.consistency.runtime.pipeline.core.CorePipelineFunctionalityTest;
import cipm.consistency.runtime.pipeline.vsum.CoreVsumFacadeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CorePipelineFunctionalityTest.class, CoreVsumFacadeTest.class })
public class PipelineTestSuite {

}
