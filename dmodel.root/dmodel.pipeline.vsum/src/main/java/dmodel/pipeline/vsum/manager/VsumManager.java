package dmodel.pipeline.vsum.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;

import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.core.ISpecificModelProvider;
import dmodel.pipeline.core.config.ConfigurationContainer;
import dmodel.pipeline.core.health.AbstractHealthStateComponent;
import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.pipeline.vsum.domains.java.JavaCorrespondenceModelImpl;
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
	private IPcmModelProvider pcmModelContainer;

	@Autowired
	private ConfigurationContainer configurationContainer;

	@Autowired
	private ISpecificModelProvider specificModelContainer;

	@Autowired
	private VsumMappingPersistence persistence;

	private InternalVirtualModel vsum;

	@Getter
	private TypeInferringAtomicEChangeFactory atomicFactory;

	@Getter
	private IJavaPCMCorrespondenceModel javaCorrespondences;

	// pure internal
	private List<File> usedFiles;

	public VsumManager() {
		super(HealthStateObservedComponent.VSUM_MANAGER, HealthStateObservedComponent.MODEL_MANAGER);
		this.usedFiles = new ArrayList<>();
	}

	public void executeTransaction(Callable<Void> cb) {
		vsum.executeCommand(cb);
	}

	public void propagateChange(EChange change) {
		this.vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change));

		// persist the mapping
		specificModelContainer
				.swapCorrespondenceModel(persistence.buildStorableCorrespondeces(getCorrespondenceModel()));
	}

	public CorrespondenceModel getCorrespondenceModel() {
		if (vsum != null) {
			return vsum.getCorrespondenceModel();
		}
		return null;
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		if (source == HealthStateObservedComponent.MODEL_MANAGER && state == HealthState.WORKING) {
			this.buildVSUM();
		}
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

		vsum.save();
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
				VURI.getInstance(EMFBridge.getEmfFileUriForFile(provideFile(
						configurationContainer.getModels().getRepositoryPath(), pcmModelContainer.getRepository()))),
				pcmModelContainer.getRepository());

		// 2. load resource environment
		vsum.persistRootElement(
				VURI.getInstance(EMFBridge.getEmfFileUriForFile(provideFile(
						configurationContainer.getModels().getEnvPath(), pcmModelContainer.getResourceEnvironment()))),
				pcmModelContainer.getResourceEnvironment());

		// 3. load runtime environment
		vsum.persistRootElement(
				VURI.getInstance(EMFBridge.getEmfFileUriForFile(
						provideFile(configurationContainer.getModels().getRuntimeEnvironmentPath(),
								specificModelContainer.getRuntimeEnvironment()))),
				specificModelContainer.getRuntimeEnvironment());

		// 4. load instrumentation model
		vsum.persistRootElement(
				VURI.getInstance(EMFBridge.getEmfFileUriForFile(
						provideFile(configurationContainer.getModels().getInstrumentationModelPath(),
								specificModelContainer.getInstrumentation()))),
				specificModelContainer.getInstrumentation());
	}

	private void recoverMapping() {
		// map parents accordingly
		vsum.executeCommand(() -> {
			ReactionsCorrespondenceHelper.addCorrespondence(vsum.getCorrespondenceModel(),
					pcmModelContainer.getRepository(), specificModelContainer.getInstrumentation(), null);

			ReactionsCorrespondenceHelper.addCorrespondence(vsum.getCorrespondenceModel(),
					pcmModelContainer.getResourceEnvironment(), specificModelContainer.getRuntimeEnvironment(), null);

			return null;
		});

		// init java correspondence
		javaCorrespondences = new JavaCorrespondenceModelImpl();

		// TODO reload the mapping from files
		// TODO outsource
	}

	private synchronized void persistVirtual(EObject model, String suffix) {
		// backup resource
		Resource backupResource = model.eResource();

		File temp;
		try {
			temp = File.createTempFile("model_vsum", suffix);
		} catch (IOException e) {
			log.severe("Failed to create temporary files for the VSUM models.");
			return;
		}

		// write the system to the file
		ModelUtil.saveToFile(model, temp);

		// persist
		vsum.persistRootElement(VURI.getInstance(EMFBridge.getEmfFileUriForFile(temp)), model);

		// recover resource
		if (backupResource == null) {
			EcoreUtil.remove(model);
		} else {
			backupResource.getContents().add(model);
			System.out.println(model.eResource() == backupResource);
		}
	}

	private File provideFile(String filePath, EObject model) {
		File temp = new File(filePath);
		if (!temp.getParentFile().exists()) {
			temp.getParentFile().mkdirs();
		}

		// write new TODO: dangerous?
		ModelUtil.saveToFile(model, temp);

		return temp;
	}

}
