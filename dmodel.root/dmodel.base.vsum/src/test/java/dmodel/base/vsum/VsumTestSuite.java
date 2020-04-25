package dmodel.base.vsum;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import dmodel.base.vsum.integration.BaseVsumIntegrationTest;
import dmodel.base.vsum.persistence.CorrespondencePersistenceTest;
import dmodel.base.vsum.reactions.VsumReactionsTests;

@RunWith(Suite.class)
@SuiteClasses({ BaseVsumIntegrationTest.class, CorrespondencePersistenceTest.class, VsumReactionsTests.class })
public class VsumTestSuite {
}
