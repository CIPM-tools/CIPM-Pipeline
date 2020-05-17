package dmodel.designtime.instrumentation.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.base.core.config.ConfigurationContainer;
import dmodel.base.core.health.AbstractHealthStateComponent;
import dmodel.base.core.health.HealthState;
import dmodel.base.core.health.HealthStateObservedComponent;
import dmodel.designtime.instrumentation.project.ApplicationProject;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;
import lombok.Getter;

/**
 * Component that manages the Java project under observation.
 * 
 * @author David Monschein
 *
 */
@Component
public class ProjectManager extends AbstractHealthStateComponent {
	@Autowired
	private ConfigurationContainer configuration;

	@Getter
	private ApplicationProject applicationProject;

	@Getter
	private ParsedApplicationProject parsedApplicationProject;

	/**
	 * Creates a new instance of the project manager component.
	 */
	protected ProjectManager() {
		super(HealthStateObservedComponent.PROJECT_MANAGER, HealthStateObservedComponent.CONFIGURATION);
	}

	/**
	 * {@inheritDoc}
	 */
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
