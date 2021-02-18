package cipm.consistency.base.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Configuration of the project under observation.
 * 
 * @author David Monschein
 *
 */
@Data
public class ProjectConfiguration {
	/**
	 * Listeners for this configuration part. They get informed when this
	 * configuration changes.
	 */
	@JsonIgnore
	private List<IConfigurationChangeListener<ProjectConfiguration>> listeners;

	/**
	 * Root path of the application under observation.
	 */
	@Setter(AccessLevel.NONE)
	private String rootPath;

	/**
	 * Folders that contain Java source code files relative to the base path.
	 */
	@Setter(AccessLevel.NONE)
	private List<String> sourceFolders;

	/**
	 * Creates a new project configuration.
	 */
	public ProjectConfiguration() {
		this.listeners = new ArrayList<>();
	}

	/**
	 * Sets the current root path and informs all listeners.
	 * 
	 * @param rootPath the new root path
	 */
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
		this.listeners.forEach(l -> l.configurationChanged(this));
	}

	/**
	 * Sets the current source folders and informs all listeners.
	 * 
	 * @param sourceFolders the source folders relative to the base path
	 */
	public void setSourceFolders(List<String> sourceFolders) {
		this.sourceFolders = sourceFolders;
		this.listeners.forEach(l -> l.configurationChanged(this));
	}

	/**
	 * Determines whether the project configuration is valid.
	 * 
	 * @return true if the root path and the source folders are valid, false
	 *         otherwise
	 */
	@JsonIgnore
	public boolean isValid() {
		if (rootPath == null || sourceFolders == null) {
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
