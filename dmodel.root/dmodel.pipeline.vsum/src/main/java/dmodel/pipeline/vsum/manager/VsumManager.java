package dmodel.pipeline.vsum.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;

import dmodel.pipeline.core.CentralModelAdminstrator;
import dmodel.pipeline.shared.health.AbstractHealthStateComponent;
import dmodel.pipeline.shared.health.HealthStateObservedComponents;
import dmodel.pipeline.vsum.mapping.VsumMappingPersistence;
import lombok.Getter;
import lombok.extern.java.Log;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.extensions.dslsruntime.reactions.helper.ReactionsCorrespondenceHelper;
import tools.vitruv.framework.change.description.VitruviusChangeFactory;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.TypeInferringAtomicEChangeFactory;
import tools.vitruv.framework.change.echange.TypeInferringUnresolvingAtomicEChangeFactory;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;
import tools.vitruv.framework.userinteraction.UserInteractionFactory;
import tools.vitruv.framework.util.bridges.EMFBridge;
import tools.vitruv.framework.util.datatypes.VURI;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.framework.vsum.VirtualModelConfiguration;
import tools.vitruv.framework.vsum.VirtualModelImpl;

@Component
@Log
public class VsumManager extends AbstractHealthStateComponent {
	@Autowired
	private List<VitruvDomainProvider<? extends VitruvDomain>> domainProviders;

	@Autowired
	private List<AbstractReactionsChangePropagationSpecification> reactions;

	@Autowired
	private CentralModelAdminstrator modelContainer;

	@Autowired
	private VsumMappingPersistence persistence;

	private InternalVirtualModel vsum;

	@Getter
	private TypeInferringAtomicEChangeFactory atomicFactory;

	// pure internal
	private List<File> usedFiles;

	public VsumManager() {
		super(HealthStateObservedComponents.VSUM_MANAGER, HealthStateObservedComponents.CONFIGURATION);
		this.usedFiles = new ArrayList<>();
	}

	public void propagateChange(EChange change) {
		this.vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change));

		// persist the mapping
		modelContainer.swapCorrespondenceModel(persistence.buildStorableCorrespondeces(getCorrespondenceModel()));
	}

	public CorrespondenceModel getCorrespondenceModel() {
		if (vsum != null) {
			return vsum.getCorrespondenceModel();
		}
		return null;
	}

	@PostConstruct
	private void buildVSUM() {
		if (!super.checkPreconditions()) {
			return;
		}

		tearDownVSUM();

		VirtualModelConfiguration vsumConfig = new VirtualModelConfiguration();

		for (VitruvDomainProvider<? extends VitruvDomain> provider : domainProviders) {
			vsumConfig.addMetamodel(provider.getDomain());
		}
		for (AbstractReactionsChangePropagationSpecification reaction : reactions) {
			vsumConfig.addChangePropagationSpecification(reaction);
		}

		File vsumFolder = Files.createTempDir();
		vsumFolder.deleteOnExit();
		usedFiles.add(vsumFolder);

		this.vsum = new VirtualModelImpl(vsumFolder, UserInteractionFactory.instance.createDummyUserInteractor(),
				vsumConfig);

		// load
		loadModels();
		recoverMapping();
		finalizeVsum();

		super.updateState();
	}

	private void finalizeVsum() {
		atomicFactory = new TypeInferringUnresolvingAtomicEChangeFactory(vsum.getUuidGeneratorAndResolver());
	}

	private void tearDownVSUM() {
		if (vsum != null) {
			log.info("Tear down current VSUM.");
			try {
				FileUtils.deleteDirectory(vsum.getFolder());
			} catch (IOException e) {
				log.warning("Could not tear down VSUM successfully.");
			}

			usedFiles.forEach(f -> {
				if (f.exists()) {
					if (f.isDirectory()) {
						try {
							FileUtils.deleteDirectory(f);
						} catch (IOException e) {
							log.warning("Could not remove all temporary used files.");
						}
					} else {
						f.delete();
					}
				}
			});
		}
	}

	private void loadModels() {
		// 1. load repository
		vsum.persistRootElement(
				VURI.getInstance(EMFBridge.getEmfFileUriForFile(provideTempFile(VsumConstants.REPOSITORY_SUFFIX))),
				modelContainer.getRepository());

		// 2. load resource environment
		vsum.persistRootElement(
				VURI.getInstance(
						EMFBridge.getEmfFileUriForFile(provideTempFile(VsumConstants.RESOURCEENVIRONMENT_SUFFIX))),
				modelContainer.getResourceEnvironment());

		// 3. load runtime environment
		vsum.persistRootElement(
				VURI.getInstance(
						EMFBridge.getEmfFileUriForFile(provideTempFile(VsumConstants.RUNTIMEENVIRONMENT_SUFFIX))),
				modelContainer.getRuntimeEnvironment());

		// 4. load instrumentation model
		vsum.persistRootElement(
				VURI.getInstance(EMFBridge.getEmfFileUriForFile(provideTempFile(VsumConstants.INSTRUMENTATION_SUFFIX))),
				modelContainer.getInstrumentation());
	}

	private void recoverMapping() {
		// map parents accordingly
		ReactionsCorrespondenceHelper.addCorrespondence(vsum.getCorrespondenceModel(), modelContainer.getRepository(),
				modelContainer.getInstrumentation(), null);

		ReactionsCorrespondenceHelper.addCorrespondence(vsum.getCorrespondenceModel(),
				modelContainer.getResourceEnvironment(), modelContainer.getRuntimeEnvironment(), null);

		// TODO reload the mapping from files
		// TODO outsource
	}

	private File provideTempFile(String suffix) {
		File temp;
		try {
			temp = File.createTempFile(VsumConstants.MODEL_FILE_PREFIX, suffix);
		} catch (IOException e) {
			return null;
		}
		temp.deleteOnExit();
		this.usedFiles.add(temp);
		return temp;
	}

}
