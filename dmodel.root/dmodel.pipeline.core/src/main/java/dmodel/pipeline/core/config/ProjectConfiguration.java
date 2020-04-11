package dmodel.pipeline.core.config;

import java.io.File;
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

	public boolean isValid() {
		if (rootPath == null || sourceFolders == null || instrumentedPath == null) {
			return false;
		}

		File rootPathFile = new File(rootPath);
		if (!rootPathFile.exists() || !rootPathFile.isDirectory()) {
			return false;
		}

		for (String sourceFolder : sourceFolders) {
			File srcFolder = new File(rootPathFile, sourceFolder);
			if (!srcFolder.exists() || !srcFolder.isDirectory()) {
				return false;
			}
		}

		return true;
	}

}
