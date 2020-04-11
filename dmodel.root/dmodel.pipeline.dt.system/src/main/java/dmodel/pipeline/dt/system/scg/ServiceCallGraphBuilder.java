package dmodel.pipeline.dt.system.scg;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.pipeline.core.health.AbstractHealthStateComponent;
import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.system.ISystemCompositionAnalyzer;
import dmodel.pipeline.instrumentation.manager.ProjectManager;
import dmodel.pipeline.vsum.manager.VsumManager;
import lombok.Getter;

@Component
public class ServiceCallGraphBuilder extends AbstractHealthStateComponent {

	@Autowired
	private IRepositoryQueryFacade repository;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private VsumManager vsumManager;

	@Autowired
	private ISystemCompositionAnalyzer compositionAnalyzer;

	@Getter
	private ServiceCallGraph currentServiceCallGraph;

	protected ServiceCallGraphBuilder() {
		super(HealthStateObservedComponent.SCG_BUILDER, HealthStateObservedComponent.MODEL_MANAGER,
				HealthStateObservedComponent.PROJECT_MANAGER);
	}

	public ServiceCallGraph buildServiceCallGraph(List<File> jarFiles) {
		this.currentServiceCallGraph = compositionAnalyzer.deriveSystemComposition(
				projectManager.getParsedApplicationProject(), jarFiles, repository,
				vsumManager.getJavaCorrespondences());
		return this.currentServiceCallGraph;
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// nothing to do here
	}

}
