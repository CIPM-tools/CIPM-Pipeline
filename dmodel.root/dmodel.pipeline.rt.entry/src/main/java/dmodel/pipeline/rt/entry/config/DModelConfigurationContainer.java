package dmodel.pipeline.rt.entry.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DModelConfigurationContainer {

	@Autowired
	private ProjectConfiguration projectConfiguration;

	public ProjectConfiguration getProjectConfiguration() {
		return projectConfiguration;
	}

	public void setProjectConfiguration(ProjectConfiguration projectConfiguration) {
		this.projectConfiguration = projectConfiguration;
	}

}
