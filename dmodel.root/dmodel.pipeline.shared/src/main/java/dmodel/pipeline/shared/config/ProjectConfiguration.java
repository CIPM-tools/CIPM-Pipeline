package dmodel.pipeline.shared.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class ProjectConfiguration {
	@JsonIgnore
	private List<IConfigurationChangeListener<ProjectConfiguration>> listeners;

	@Setter(AccessLevel.NONE)
	private String rootPath;

	@Setter(AccessLevel.NONE)
	private List<String> sourceFolders;

	@Setter(AccessLevel.NONE)
	private String instrumentedPath;

	@Setter(AccessLevel.NONE)
	private String correspondencePath;

	public ProjectConfiguration() {
		this.listeners = new ArrayList<>();
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
		this.listeners.forEach(l -> l.configurationChanged(this));
	}

	public void setSourceFolders(List<String> sourceFolders) {
		this.sourceFolders = sourceFolders;
		this.listeners.forEach(l -> l.configurationChanged(this));
	}

	public void setInstrumentedPath(String instrumentedPath) {
		this.instrumentedPath = instrumentedPath;
		this.listeners.forEach(l -> l.configurationChanged(this));
	}

	public void setCorrespondencePath(String correspondencePath) {
		this.correspondencePath = correspondencePath;
		this.listeners.forEach(l -> l.configurationChanged(this));
	}

}
