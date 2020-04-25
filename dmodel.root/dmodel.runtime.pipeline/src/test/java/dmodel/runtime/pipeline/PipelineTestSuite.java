package dmodel.runtime.pipeline;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dmodel.runtime.pipeline.core.CorePipelineFunctionalityTest;
import dmodel.runtime.pipeline.vsum.CoreVsumFacadeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CorePipelineFunctionalityTest.class, CoreVsumFacadeTest.class })
public class PipelineTestSuite {

}
