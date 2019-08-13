package dmodel.pipeline.rt.rest.core.config;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.ecore.EObject;
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

import dmodel.pipeline.rt.entry.config.DModelConfigurationContainer;
import dmodel.pipeline.rt.rest.data.config.JsonConfiguration;
import dmodel.pipeline.rt.rest.data.config.ModelPathRequest;
import dmodel.pipeline.rt.rest.data.config.ModelPathResponse;
import dmodel.pipeline.rt.rest.data.config.ProjectSourceFolderResponse;
import dmodel.pipeline.rt.rest.data.config.ProjectType;
import dmodel.pipeline.rt.rest.data.config.ProjectValidationResponse;
import dmodel.pipeline.shared.ModelUtil;

@RestController
public class ConfigRestController {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DModelConfigurationContainer config;

	@GetMapping("/config/get")
	public String getConfig() {
		try {
			return objectMapper.writeValueAsString(JsonConfiguration.from(config));
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	@PostMapping("/config/save/project")
	public String saveConfig(@RequestParam String config) {
		try {
			JsonConfiguration json = objectMapper.readValue(config, JsonConfiguration.class);
			json.flush(this.config);
			return "{\"success\" : true}";
		} catch (IOException e) {
			return "{\"success\" : false}";
		}
	}

	@PostMapping("/config/validate-models")
	public String validateModels(@RequestParam String models) {
		ModelPathResponse resp = new ModelPathResponse();
		try {
			ModelPathRequest req = objectMapper.readValue(models, ModelPathRequest.class);
			resp.setAlloc(validateModelPath(req.getAlloc(), Allocation.class));
			resp.setRepo(validateModelPath(req.getRepo(), Repository.class));
			resp.setRes(validateModelPath(req.getRes(), ResourceEnvironment.class));
			resp.setSys(validateModelPath(req.getSys(), System.class));
			resp.setUsage(validateModelPath(req.getUsage(), UsageModel.class));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return objectMapper.writeValueAsString(resp);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	@PostMapping("/config/validate-path")
	public String validatePaths(@RequestParam String path) {
		ProjectValidationResponse resp = new ProjectValidationResponse();

		File fpath = new File(path);
		if (fpath.exists() && fpath.isDirectory()) {
			File buildGradle = new File(fpath, "build.gradle");
			File settingsGradle = new File(fpath, "settings.gradle");

			File pomMaven = new File(fpath, "pom.xml");

			File normalSourceFolder = new File(fpath, "src");

			if (buildGradle.exists() || settingsGradle.exists()) {
				resp.setValid(true);
				resp.setType(ProjectType.GRADLE);
			} else if (pomMaven.exists()) {
				resp.setValid(true);
				resp.setType(ProjectType.MAVEN);
			} else if (normalSourceFolder.exists() && normalSourceFolder.isDirectory()) {
				resp.setValid(true);
				resp.setType(ProjectType.PLAIN_JAVA);
			} else {
				resp.setValid(false);
				resp.setType(ProjectType.UNKNOWN);
			}
		} else {
			resp.setValid(false);
			resp.setType(ProjectType.UNKNOWN);
		}
		resp.setTypeAsText(resp.getType().getName());

		if (resp.isValid()) {
			resp.setPossibleFolders(collectSubFolders(fpath, null));
		}

		try {
			return objectMapper.writeValueAsString(resp);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	private boolean validateModelPath(String path, Class<? extends EObject> type) {
		if (path == null || path.isEmpty())
			return true;
		File file = new File(path);
		if (file.exists()) {
			try {
				EObject res = ModelUtil.readFromFile(path, type);
				if (res != null && type.isInstance(res)) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	private ProjectSourceFolderResponse collectSubFolders(File basePath, String name) {
		ProjectSourceFolderResponse base = new ProjectSourceFolderResponse();
		base.setName(name);

		for (File sub : basePath.listFiles()) {
			if (sub.isDirectory()) {
				base.getSubfolders().add(collectSubFolders(sub, sub.getName()));
			}
		}

		return base;
	}

}
