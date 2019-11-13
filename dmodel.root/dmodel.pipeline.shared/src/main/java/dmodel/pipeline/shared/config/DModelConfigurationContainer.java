package dmodel.pipeline.shared.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
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
