package dmodel.designtime.system.scg;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.base.core.ICallGraphProvider;
import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.core.health.AbstractHealthStateComponent;
import dmodel.base.core.health.HealthState;
import dmodel.base.core.health.HealthStateObservedComponent;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.base.vsum.manager.VsumManager;
import dmodel.designtime.instrumentation.manager.ProjectManager;
import dmodel.designtime.system.ISystemCompositionAnalyzer;

@Component
public class ServiceCallGraphBuilder extends AbstractHealthStateComponent implements ICallGraphProvider {

	@Autowired
	private IRepositoryQueryFacade repository;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private VsumManager vsumManager;

	@Autowired
	private ISystemCompositionAnalyzer compositionAnalyzer;

	private ServiceCallGraph currentServiceCallGraph;

	protected ServiceCallGraphBuilder() {
		super(HealthStateObservedComponent.SCG_BUILDER, HealthStateObservedComponent.MODEL_MANAGER,
				HealthStateObservedComponent.PROJECT_MANAGER);
	}

	public ServiceCallGraph buildServiceCallGraph(List<File> jarFiles) {
		try {
			this.currentServiceCallGraph = compositionAnalyzer.deriveSystemComposition(
					projectManager.getParsedApplicationProject(), jarFiles, repository,
					vsumManager.getJavaCorrespondences());
			super.removeAllProblems();
		} catch (Exception e) {
			super.reportError(
					"Service-Call-Graph extraction failed to due to a faulty configuration of the project or the binary JAR files.");
		}
		super.updateState();

		return this.currentServiceCallGraph;
	}

	@Override
	public ServiceCallGraph provideCallGraph() {
		return this.currentServiceCallGraph;
	}

	@Override
	public boolean callGraphPresent() {
		return this.currentServiceCallGraph != null;
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// nothing to do here
	}

}
