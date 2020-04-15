package dmodel.pipeline.rt.rest.data.config;

import java.util.List;

import dmodel.pipeline.core.config.ConfigurationContainer;
import dmodel.pipeline.core.config.ProjectConfiguration;
import lombok.Data;

@Data
public class JsonProjectConfiguration {

	private String projectPath;
	private List<String> sourceFolders;
	private String instrumentedPath;

	public static JsonProjectConfiguration from(ConfigurationContainer conf) {
		ProjectConfiguration pconf = conf.getProject();

		JsonProjectConfiguration out = new JsonProjectConfiguration();
		out.setProjectPath(pconf.getRootPath());
		out.setSourceFolders(pconf.getSourceFolders());
		out.setInstrumentedPath(pconf.getInstrumentedPath());

		return out;
	}

	public void flush(ConfigurationContainer conf) {
		conf.getProject().setInstrumentedPath(instrumentedPath);
		conf.getProject().setRootPath(projectPath);
		conf.getProject().setSourceFolders(sourceFolders);
	}

}
