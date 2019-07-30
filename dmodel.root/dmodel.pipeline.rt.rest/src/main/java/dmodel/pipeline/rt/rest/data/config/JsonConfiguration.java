package dmodel.pipeline.rt.rest.data.config;

import java.util.List;

import dmodel.pipeline.rt.entry.config.DModelConfigurationContainer;
import dmodel.pipeline.rt.entry.config.ProjectConfiguration;

public class JsonConfiguration {

	private String projectPath;
	private List<String> sourceFolders;
	private String instrumentedPath;

	public static JsonConfiguration from(DModelConfigurationContainer conf) {
		ProjectConfiguration pconf = conf.getProjectConfiguration();

		JsonConfiguration out = new JsonConfiguration();
		out.setProjectPath(pconf.getRootPath());
		out.setSourceFolders(pconf.getSourceFolders());
		out.setInstrumentedPath(pconf.getInstrumentedPath());

		return out;
	}

	public void flush(DModelConfigurationContainer conf) {
		conf.getProjectConfiguration().setInstrumentedPath(instrumentedPath);
		conf.getProjectConfiguration().setRootPath(projectPath);
		conf.getProjectConfiguration().setSourceFolders(sourceFolders);
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public List<String> getSourceFolders() {
		return sourceFolders;
	}

	public void setSourceFolders(List<String> sourceFolders) {
		this.sourceFolders = sourceFolders;
	}

	public String getInstrumentedPath() {
		return instrumentedPath;
	}

	public void setInstrumentedPath(String instrumentedPath) {
		this.instrumentedPath = instrumentedPath;
	}

}
