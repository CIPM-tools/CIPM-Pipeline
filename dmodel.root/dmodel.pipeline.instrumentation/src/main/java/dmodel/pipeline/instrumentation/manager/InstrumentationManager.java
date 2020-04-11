package dmodel.pipeline.instrumentation.manager;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.core.health.AbstractHealthStateComponent;
import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import dmodel.pipeline.core.health.HealthStateProblem;
import dmodel.pipeline.core.health.HealthStateProblemSeverity;
import dmodel.pipeline.instrumentation.mapping.AutomatedMappingResolverImpl;
import dmodel.pipeline.instrumentation.project.app.ApplicationProjectTransformer;
import dmodel.pipeline.instrumentation.project.app.InstrumentationMetadata;
import dmodel.pipeline.vsum.manager.VsumManager;

@Component
public class InstrumentationManager extends AbstractHealthStateComponent {
	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private AutomatedMappingResolverImpl mappingResolver;

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
