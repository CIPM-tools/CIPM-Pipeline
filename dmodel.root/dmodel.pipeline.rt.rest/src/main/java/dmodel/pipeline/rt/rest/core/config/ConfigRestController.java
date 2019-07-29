package dmodel.pipeline.rt.rest.core.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.rt.rest.data.config.ProjectSourceFolderResponse;
import dmodel.pipeline.rt.rest.data.config.ProjectType;
import dmodel.pipeline.rt.rest.data.config.ProjectValidationResponse;

@RestController
public class ConfigRestController {
	@Autowired
	private ObjectMapper objectMapper;

	@PostMapping("/config/validate-path")
	public String validate(@RequestParam String path) {
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

	private ProjectSourceFolderResponse collectSubFolders(File basePath, String name) {
		ProjectSourceFolderResponse base = new ProjectSourceFolderResponse();
		base.setName(name);

		System.out.println(name);

		for (File sub : basePath.listFiles()) {
			if (sub.isDirectory()) {
				base.getSubfolders().add(collectSubFolders(sub, sub.getName()));
			}
		}

		return base;
	}

}
