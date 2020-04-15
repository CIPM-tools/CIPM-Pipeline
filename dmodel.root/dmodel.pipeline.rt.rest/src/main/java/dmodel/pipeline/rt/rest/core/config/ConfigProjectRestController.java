package dmodel.pipeline.rt.rest.core.config;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.core.config.ConfigurationContainer;
import dmodel.pipeline.rt.rest.data.config.JsonProjectConfiguration;
import dmodel.pipeline.rt.rest.data.config.ProjectSourceFolderResponse;
import dmodel.pipeline.rt.rest.data.config.ProjectType;
import dmodel.pipeline.rt.rest.data.config.ProjectValidationResponse;

@RestController
public class ConfigProjectRestController {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ConfigurationContainer config;

	@GetMapping("/config/project/get")
	public String getProjectConfig() {
		try {
			return objectMapper.writeValueAsString(JsonProjectConfiguration.from(config));
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	@PostMapping("/config/project/save")
	public String saveProjectConfig(@RequestParam String config) {
		try {
			JsonProjectConfiguration json = objectMapper.readValue(config, JsonProjectConfiguration.class);
			json.flush(this.config);
			return this.config.syncWithFilesystem() ? "{\"success\" : true}" : "{\"success\" : false}";
		} catch (IOException e) {
			return "{\"success\" : false}";
		}
	}

	@PostMapping("/config/project/validate-path")
	public String validatePaths(@RequestParam String path) {
		File fpath = new File(path);
		ProjectValidationResponse resp = validateProjectPath(fpath);

		if (resp.isValid()) {
			resp.setPossibleFolders(collectSubFolders(fpath, null));
		}

		try {
			return objectMapper.writeValueAsString(resp);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	private ProjectValidationResponse validateProjectPath(File fpath) {
		ProjectValidationResponse resp = new ProjectValidationResponse();

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

		return resp;
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
