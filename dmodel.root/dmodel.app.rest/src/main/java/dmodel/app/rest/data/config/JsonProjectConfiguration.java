package dmodel.app.rest.data.config;

import java.util.List;

import dmodel.base.core.config.ConfigurationContainer;
import dmodel.base.core.config.ProjectConfiguration;
import lombok.Data;

@Data
public class JsonProjectConfiguration {

	private String projectPath;
	private List<String> sourceFolders;

	public static JsonProjectConfiguration from(ConfigurationContainer conf) {
		ProjectConfiguration pconf = conf.getProject();

		JsonProjectConfiguration out = new JsonProjectConfiguration();
		out.setProjectPath(pconf.getRootPath());
		out.setSourceFolders(pconf.getSourceFolders());

		return out;
	}

	public void flush(ConfigurationContainer conf) {
		conf.getProject().setRootPath(projectPath);
		conf.getProject().setSourceFolders(sourceFolders);
	}

}
