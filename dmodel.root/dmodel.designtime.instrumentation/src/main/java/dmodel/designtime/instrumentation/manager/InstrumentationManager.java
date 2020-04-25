package dmodel.designtime.instrumentation.manager;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.base.core.health.AbstractHealthStateComponent;
import dmodel.base.core.health.HealthState;
import dmodel.base.core.health.HealthStateObservedComponent;
import dmodel.base.core.health.HealthStateProblem;
import dmodel.base.core.health.HealthStateProblemSeverity;
import dmodel.base.vsum.manager.VsumManager;
import dmodel.designtime.instrumentation.mapping.IAutomatedMappingResolver;
import dmodel.designtime.instrumentation.project.app.ApplicationProjectTransformer;
import dmodel.designtime.instrumentation.project.app.InstrumentationMetadata;

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

	public InstrumentationManager() {
		super(HealthStateObservedComponent.INSTRUMENTATION_MANAGER, HealthStateObservedComponent.PROJECT_MANAGER,
				HealthStateObservedComponent.VSUM_MANAGER);
	}

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

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// nothing to do here
	}

}
