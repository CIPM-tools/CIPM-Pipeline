package dmodel.pipeline.rt.pipeline;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dmodel.pipeline.rt.pipeline.core.CorePipelineFunctionalityTest;
import dmodel.pipeline.rt.pipeline.vsum.CoreVsumFacadeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CorePipelineFunctionalityTest.class, CoreVsumFacadeTest.class })
public class PipelineTestSuite {

}
