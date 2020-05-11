package dmodel.base.vsum;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.base.core.AbstractCoreTest;
import dmodel.base.core.health.HealthStateObservedComponent;
import dmodel.base.core.mocks.HealthStateMessageSender;
import dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage;
import dmodel.base.models.runtimeenvironment.REModel.REModelPackage;
import dmodel.base.shared.pcm.util.PCMUtils;
import dmodel.base.shared.vitruv.VitruviusUtil;
import dmodel.base.vsum.domains.ExtendedPcmDomainProvider;
import dmodel.base.vsum.domains.InstrumentationModelDomainProvider;
import dmodel.base.vsum.domains.RuntimeEnvironmentDomainProvider;
import dmodel.base.vsum.facade.CentralVsumFacade;
import dmodel.base.vsum.manager.VsumManager;
import dmodel.base.vsum.mapping.VsumMappingController;
import dmodel.base.vsum.mapping.VsumMappingPersistence;
import mir.reactions.pcmToREM.PcmToREMChangePropagationSpecification;
import mir.reactions.remToPCM.RemToPCMChangePropagationSpecification;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;

@RunWith(SpringRunner.class)
public abstract class VsumManagerTestBase extends AbstractCoreTest {

	@BeforeClass
	public static void initBefore() {
		PCMUtils.loadPCMModels();
		VitruviusUtil.initVitruv();
		InstrumentationModelPackage.eINSTANCE.eClass();
		REModelPackage.eINSTANCE.eClass();
	}

	@TestConfiguration
	public static class VsumManagerTestConfiguration extends AbstractCoreTest.CoreContextConfiguration {
		@Bean
		public VsumManager vsumManager() {
			return new VsumManager();
		}

		@Bean
		public CentralVsumFacade vsumFacade() {
			return new CentralVsumFacade();
		}

		@Bean
		public VsumMappingPersistence vsumPersistence() {
			return new VsumMappingPersistence();
		}

		@Bean
		public VsumMappingController vsumMappingController() {
			return new VsumMappingController();
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

	@Autowired
	private HealthStateMessageSender healthStateMsgSender;

	protected void reloadVsum() {
		healthStateMsgSender.sendMessage(HealthStateObservedComponent.MODEL_MANAGER,
				HealthStateObservedComponent.VSUM_MANAGER); // => this triggers the vsum startup
	}

	@Before
	public void startup() {
		setSpecific(null, null, null); // empty models 2
		setPcm(null, null, null, null, null); // empty models

		this.reloadVsum();
	}

}
