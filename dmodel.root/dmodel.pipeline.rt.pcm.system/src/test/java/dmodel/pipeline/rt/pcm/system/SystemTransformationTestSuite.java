package dmodel.pipeline.rt.pcm.system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dmodel.pipeline.rt.pcm.system.sub.AdvancedSystemTransformationTest;
import dmodel.pipeline.rt.pcm.system.sub.SimpleSystemTransformationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ SimpleSystemTransformationTest.class, AdvancedSystemTransformationTest.class })
public class SystemTransformationTestSuite {

}
