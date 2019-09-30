package dmodel.pipeline.shared.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProjectConfiguration {
	@JsonIgnore
	private List<IConfigurationChangeListener<ProjectConfiguration>> listeners;

	private String rootPath;
	private List<String> sourceFolders;
	private String instrumentedPath;

	private String correspondencePath;

	public ProjectConfiguration() {
		this.listeners = new ArrayList<>();
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
		this.listeners.forEach(l -> l.configurationChanged(this));
	}

	public List<String> getSourceFolders() {
		return sourceFolders;
	}

	public void setSourceFolders(List<String> sourceFolders) {
		this.sourceFolders = sourceFolders;
		this.listeners.forEach(l -> l.configurationChanged(this));
	}

	public String getInstrumentedPath() {
		return instrumentedPath;
	}

	public void setInstrumentedPath(String instrumentedPath) {
		this.instrumentedPath = instrumentedPath;
		this.listeners.forEach(l -> l.configurationChanged(this));
	}

	public String getCorrespondencePath() {
		return correspondencePath;
	}

	public void setCorrespondencePath(String correspondencePath) {
		this.correspondencePath = correspondencePath;
		this.listeners.forEach(l -> l.configurationChanged(this));
	}

	public List<IConfigurationChangeListener<ProjectConfiguration>> getListeners() {
		return listeners;
	}

	public void setListeners(List<IConfigurationChangeListener<ProjectConfiguration>> listeners) {
		this.listeners = listeners;
	}

}
