package dmodel.pipeline.shared.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DModelConfigurationContainer {
	// project config
	private ProjectConfiguration project;

	// model config
	private ModelConfiguration models;

	// processing md config
	private MonitoringDataEntryConfiguration entry;

	// vfl
	private ValidationFeedbackLoopConfiguration vfl;

	// path of the file
	private File fileBackedPath;

	// create writer
	private ObjectMapper writer = new ObjectMapper(new YAMLFactory());

	public ProjectConfiguration getProject() {
		return project;
	}

	public void setProject(ProjectConfiguration project) {
		this.project = project;
	}

	public ModelConfiguration getModels() {
		return models;
	}

	public void setModels(ModelConfiguration models) {
		this.models = models;
	}

	public MonitoringDataEntryConfiguration getEntry() {
		return entry;
	}

	public void setEntry(MonitoringDataEntryConfiguration entry) {
		this.entry = entry;
	}

	public ValidationFeedbackLoopConfiguration getVfl() {
		return vfl;
	}

	public void setVfl(ValidationFeedbackLoopConfiguration vfl) {
		this.vfl = vfl;
	}

	public File getFileBackedPath() {
		return fileBackedPath;
	}

	public void setFileBackedPath(File fileBackedPath) {
		this.fileBackedPath = fileBackedPath;
	}

	public boolean syncWithFilesystem() {
		writer.findAndRegisterModules();
		try {
			writer.writeValue(fileBackedPath, this);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
