package cipm.consistency.designtime.instrumentation.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cipm.consistency.base.core.config.ConfigurationContainer;
import cipm.consistency.base.core.health.AbstractHealthStateComponent;
import cipm.consistency.base.core.health.HealthState;
import cipm.consistency.base.core.health.HealthStateObservedComponent;
import cipm.consistency.designtime.instrumentation.project.ApplicationProject;
import cipm.consistency.designtime.instrumentation.project.ParsedApplicationProject;
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
