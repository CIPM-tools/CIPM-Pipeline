package dmodel.app.rest.core.config;

import java.io.IOException;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.app.rest.data.config.ModelPathContainer;
import dmodel.app.rest.data.config.ModelPathResponse;
import dmodel.base.core.config.ConfigurationContainer;
import dmodel.base.core.config.ModelConfiguration;
import dmodel.base.core.impl.CentralModelAdminstrator;
import dmodel.base.models.inmodel.InstrumentationModelUtil;
import dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import dmodel.base.shared.JsonUtil;
import dmodel.base.shared.ModelUtil;
import dmodel.base.vsum.facade.ISpecificVsumFacade;
import dmodel.base.vsum.manager.VsumManager.VsumChangeSource;
import tools.vitruv.framework.correspondence.Correspondences;

@RestController
public class ConfigModelsRestController {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ConfigurationContainer config;

	@Autowired
	private CentralModelAdminstrator modelContainer;

	@Autowired
	private ISpecificVsumFacade vsumFacade;

	@GetMapping("/config/models/get")
	public String getModelConfig() {
		try {
			return objectMapper.writeValueAsString(ModelPathContainer.from(config));
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	@PostMapping("/config/models/save")
	public String saveModels(@RequestParam String models) {
		try {
			ModelPathContainer req = objectMapper.readValue(models, ModelPathContainer.class);
			ModelPathResponse val = validateModelPaths(req);

			ModelConfiguration into = config.getModels();
			if (val.isAlloc()) {
				into.setAllocationPath(req.getAlloc());
			}
			if (val.isRepo()) {
				into.setRepositoryPath(req.getRepo());
			}
			if (val.isRes()) {
				into.setEnvPath(req.getRes());
			}
			if (val.isSys()) {
				into.setSystemPath(req.getSys());
			}
			if (val.isUsage()) {
				into.setUsagePath(req.getUsage());
			}
			if (val.isInstrumentation()) {
				into.setInstrumentationModelPath(req.getInstrumentation());
			}
			if (val.isCorrespondences()) {
				into.setCorrespondencePath(req.getCorrespondences());
			}
			if (val.isRuntimeenv()) {
				into.setRuntimeEnvironmentPath(req.getRuntimeenv());
			}
			modelContainer.loadArchitectureModel(into);

			return config.syncWithFilesystem(true) ? "{\"success\" : true}" : "{\"success\" : false}";
		} catch (IOException e) {
			e.printStackTrace();
			return "{\"success\" : false}";
		}
	}

	@PostMapping("/config/models/validate")
	public String validateModels(@RequestParam String models) {
		try {
			ModelPathContainer req = objectMapper.readValue(models, ModelPathContainer.class);
			return objectMapper.writeValueAsString(validateModelPaths(req));
		} catch (IOException e) {
			return null;
		}
	}

	@PostMapping("/config/models/inm/init")
	public String initializeInstrumentationModel() {
		this.enrichInitialInstrumentationModel(modelContainer.getInstrumentation());
		return JsonUtil.wrapAsObject("success", true, false);
	}

	private void enrichInitialInstrumentationModel(InstrumentationModel instrumentationModel) {
		// create an initial instrumentation model
		if (instrumentationModel != null && instrumentationModel.getPoints().size() == 0) {
			InstrumentationModelUtil.enrichInitialInstrumentationModel(instrumentationModel,
					modelContainer.getRepository());
			propagateInitialInstrumentationModelChanges(instrumentationModel);
		}
	}

	private void propagateInitialInstrumentationModelChanges(InstrumentationModel instrumentationModel) {
		instrumentationModel.getPoints().forEach(sip -> {
			vsumFacade.createdObject(sip, VsumChangeSource.INSTRUMENTATION_MODEL);
			sip.getActionInstrumentationPoints().forEach(aip -> {
				vsumFacade.createdObject(aip, VsumChangeSource.INSTRUMENTATION_MODEL);
			});
		});
	}

	private ModelPathResponse validateModelPaths(ModelPathContainer req) {
		ModelPathResponse resp = new ModelPathResponse();
		resp.setAlloc(ModelUtil.validateModelPath(req.getAlloc(), Allocation.class));
		resp.setRepo(ModelUtil.validateModelPath(req.getRepo(), Repository.class));
		resp.setRes(ModelUtil.validateModelPath(req.getRes(), ResourceEnvironment.class));
		resp.setSys(ModelUtil.validateModelPath(req.getSys(), System.class));
		resp.setUsage(ModelUtil.validateModelPath(req.getUsage(), UsageModel.class));
		resp.setCorrespondences(ModelUtil.validateModelPath(req.getCorrespondences(), Correspondences.class));
		resp.setInstrumentation(ModelUtil.validateModelPath(req.getInstrumentation(), InstrumentationModel.class));
		resp.setRuntimeenv(ModelUtil.validateModelPath(req.getRuntimeenv(), RuntimeEnvironmentModel.class));

		return resp;
	}

}
