package dmodel.pipeline.rt.pipeline;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;

@RunWith(SpringRunner.class)
public class BaseSpringTestConfiguration {

	@TestConfiguration
	static class TestContextConfiguration {
		@Bean
		public RuntimePipelineBlackboard blackboard() {
			return new RuntimePipelineBlackboard();
		}

		@Bean
		public DModelConfigurationContainer configuration() {
			return new DModelConfigurationContainer();
		}
	}

	@Test
	public void basicTest() {
		assertTrue(true);
	}

}
