package dmodel.pipeline.rt.rest.data.config;

import java.util.List;

import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.config.ProjectConfiguration;

public class JsonProjectConfiguration {

	private String projectPath;
	private List<String> sourceFolders;
	private String instrumentedPath;
	private String correspondencePath;

	public static JsonProjectConfiguration from(DModelConfigurationContainer conf) {
		ProjectConfiguration pconf = conf.getProject();

		JsonProjectConfiguration out = new JsonProjectConfiguration();
		out.setProjectPath(pconf.getRootPath());
		out.setSourceFolders(pconf.getSourceFolders());
		out.setInstrumentedPath(pconf.getInstrumentedPath());

		return out;
	}

	public void flush(DModelConfigurationContainer conf) {
		conf.getProject().setInstrumentedPath(instrumentedPath);
		conf.getProject().setRootPath(projectPath);
		conf.getProject().setSourceFolders(sourceFolders);
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

	public String getCorrespondencePath() {
		return correspondencePath;
	}

	public void setCorrespondencePath(String correspondencePath) {
		this.correspondencePath = correspondencePath;
	}

}
