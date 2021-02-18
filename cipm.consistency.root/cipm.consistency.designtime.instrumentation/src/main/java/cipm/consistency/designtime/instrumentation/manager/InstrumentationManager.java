package cipm.consistency.designtime.instrumentation.manager;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cipm.consistency.base.core.health.AbstractHealthStateComponent;
import cipm.consistency.base.core.health.HealthState;
import cipm.consistency.base.core.health.HealthStateObservedComponent;
import cipm.consistency.base.core.health.HealthStateProblem;
import cipm.consistency.base.core.health.HealthStateProblemSeverity;
import cipm.consistency.base.vsum.manager.VsumManager;
import cipm.consistency.designtime.instrumentation.mapping.IAutomatedMappingResolver;
import cipm.consistency.designtime.instrumentation.project.app.ApplicationProjectTransformer;
import cipm.consistency.designtime.instrumentation.project.app.InstrumentationMetadata;

/**
 * Component that is responsible for instrumenting the application under
 * observation. It does not perform the instrumentation by itself, rather it is
 * delegated to subcomponents.
 * 
 * @author David Monschein
 *
 */
@Component
public class InstrumentationManager extends AbstractHealthStateComponent {
	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private IAutomatedMappingResolver mappingResolver;

	@Autowired
	private VsumManager vsumManager;

	@Autowired
	private ApplicationProjectTransformer transformer;

	/**
	 * Creates a new instrumentation manager component.
	 */
	public InstrumentationManager() {
		super(HealthStateObservedComponent.INSTRUMENTATION_MANAGER, HealthStateObservedComponent.PROJECT_MANAGER,
				HealthStateObservedComponent.VSUM_MANAGER);
	}

	/**
	 * Instruments the project under observation.
	 * 
	 * @param extractMappingFromCode whether the mapping between source code
	 *                               elements and architecture model elements should
	 *                               be extracted from comments in the code before
	 *                               the application is instrumented
	 * @param metadata               configuration data for the instrumentation
	 *                               process, such as output path
	 */
	public void instrumentProject(boolean extractMappingFromCode, InstrumentationMetadata metadata) {
		if (extractMappingFromCode) {
			mappingResolver.resolveMappings(projectManager.getParsedApplicationProject(),
					vsumManager.getJavaCorrespondences());
		}

		// instrumentation
		try {
			transformer.performInstrumentation(projectManager.getApplicationProject(), metadata,
					vsumManager.getJavaCorrespondences());
			super.removeAllProblems();
			super.updateState();
		} catch (IOException e) {
			super.reportProblem(HealthStateProblem.builder()
					.description("Could not instrument the project due to an I/O exception ('" + e.getMessage() + "').")
					.severity(HealthStateProblemSeverity.WARNING).build());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// nothing to do here
	}

}
