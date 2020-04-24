package dmodel.pipeline.vsum;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import dmodel.pipeline.vsum.integration.BaseVsumIntegrationTest;
import dmodel.pipeline.vsum.persistence.CorrespondencePersistenceTest;
import dmodel.pipeline.vsum.reactions.VsumReactionsTests;

@RunWith(Suite.class)
@SuiteClasses({ BaseVsumIntegrationTest.class, CorrespondencePersistenceTest.class, VsumReactionsTests.class })
public class VsumTestSuite {
}
