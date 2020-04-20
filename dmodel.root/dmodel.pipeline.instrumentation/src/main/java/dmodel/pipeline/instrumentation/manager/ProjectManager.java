package dmodel.pipeline.instrumentation.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.core.config.ConfigurationContainer;
import dmodel.pipeline.core.health.AbstractHealthStateComponent;
import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import dmodel.pipeline.instrumentation.project.ApplicationProject;
import dmodel.pipeline.instrumentation.project.ParsedApplicationProject;
import lombok.Getter;

@Component
public class ProjectManager extends AbstractHealthStateComponent {
	@Autowired
	private ConfigurationContainer configuration;

	@Getter
	private ApplicationProject applicationProject;

	@Getter
	private ParsedApplicationProject parsedApplicationProject;

	protected ProjectManager() {
		super(HealthStateObservedComponent.PROJECT_MANAGER, HealthStateObservedComponent.CONFIGURATION);
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		if (source == HealthStateObservedComponent.CONFIGURATION && state == HealthState.WORKING) {
			buildProjectData();
		}
	}

	private void buildProjectData() {
		if (!checkPreconditions()) {
			return;
		}

		this.applicationProject = new ApplicationProject();
		this.applicationProject.setRootPath(configuration.getProject().getRootPath());
		this.applicationProject.setSourceFolders(configuration.getProject().getSourceFolders());

		this.parsedApplicationProject = new ParsedApplicationProject(applicationProject);

		super.updateState();
	}

}
