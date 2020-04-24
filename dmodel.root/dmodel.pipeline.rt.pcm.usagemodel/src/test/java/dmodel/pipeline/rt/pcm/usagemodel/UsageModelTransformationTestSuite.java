package dmodel.pipeline.rt.pcm.usagemodel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dmodel.pipeline.rt.pcm.usagemodel.cocome.CocomeUsageModelDerivationTest;
import dmodel.pipeline.rt.pcm.usagemodel.sub.ExtendedUsageDerivationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CocomeUsageModelDerivationTest.class, ExtendedUsageDerivationTest.class })
public class UsageModelTransformationTestSuite {

}
