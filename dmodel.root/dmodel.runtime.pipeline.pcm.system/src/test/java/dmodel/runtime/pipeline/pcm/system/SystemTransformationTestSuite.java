package dmodel.runtime.pipeline.pcm.system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dmodel.runtime.pipeline.pcm.system.sub.AdvancedSystemTransformationTest;
import dmodel.runtime.pipeline.pcm.system.sub.SimpleSystemTransformationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ SimpleSystemTransformationTest.class, AdvancedSystemTransformationTest.class })
public class SystemTransformationTestSuite {

}
