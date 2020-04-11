package dmodel.pipeline.vsum;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.core.config.ConfigurationContainer;
import dmodel.pipeline.core.config.ProjectConfiguration;
import dmodel.pipeline.core.health.HealthStateManager;
import dmodel.pipeline.shared.correspondence.CorrespondenceUtil;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import dmodel.pipeline.vsum.domains.ExtendedPcmDomainProvider;
import dmodel.pipeline.vsum.domains.InstrumentationModelDomainProvider;
import dmodel.pipeline.vsum.domains.RuntimeEnvironmentDomainProvider;
import mir.reactions.pcmToREM.PcmToREMChangePropagationSpecification;
import mir.reactions.remToPCM.RemToPCMChangePropagationSpecification;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;

@RunWith(SpringRunner.class)
public class VsumManagerTestBase {

	@BeforeClass
	public static void initBefore() {
		PCMUtils.loadPCMModels();
		CorrespondenceUtil.initVitruv();
	}

	@TestConfiguration
	public static class VsumManagerTestConfiguration {
		@Bean
		public HealthStateManager healthManager() {
			return new HealthStateManager();
		}

		@Bean
		public ConfigurationContainer configuration() {
			ConfigurationContainer container = new ConfigurationContainer();
			container.setProject(new ProjectConfiguration());

			return container;
		}

		// DOMAINS
		@Bean
		public VitruvDomainProvider<? extends VitruvDomain> pcmDomain() {
			return new ExtendedPcmDomainProvider();
		}

		@Bean
		public VitruvDomainProvider<? extends VitruvDomain> remDomain() {
			return new RuntimeEnvironmentDomainProvider();
		}

		@Bean
		public VitruvDomainProvider<? extends VitruvDomain> immDomain() {
			return new InstrumentationModelDomainProvider();
		}

		// PROPAGATION SPECS
		@Bean
		public AbstractReactionsChangePropagationSpecification remToResEnv() {
			return new PcmToREMChangePropagationSpecification();
		}

		@Bean
		public AbstractReactionsChangePropagationSpecification resEnvToREM() {
			return new RemToPCMChangePropagationSpecification();
		}
	}

}
