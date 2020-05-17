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
import dmodel.base.core.impl.CentralModelAdminstrator;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.base.vsum.manager.VsumManager;
import dmodel.designtime.instrumentation.manager.ProjectManager;
import dmodel.designtime.system.ISystemCompositionAnalyzer;

/**
 * The component which is responsible for building service-call-graphs (SCG). It
 * depends on the model manager (see {@link CentralModelAdminstrator}) and the
 * project manager (see {@link ProjectManager}).
 * 
 * @author David Monschein
 *
 */
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

	/**
	 * Creates a new instance of the component with the already mentioned components
	 * as dependencies.
	 */
	protected ServiceCallGraphBuilder() {
		super(HealthStateObservedComponent.SCG_BUILDER, HealthStateObservedComponent.MODEL_MANAGER,
				HealthStateObservedComponent.PROJECT_MANAGER);
	}

	/**
	 * Builds a service-call-graph (SCG) by means of the JAR binary files and the
	 * dependencies.
	 * 
	 * @param jarFiles the binary JAR files of the application under observation
	 *                 (they are needed for the type resolution)
	 * @return the generated SCG
	 */
	public ServiceCallGraph buildServiceCallGraph(List<File> jarFiles) {
		try {
			this.currentServiceCallGraph = compositionAnalyzer.deriveSystemComposition(
					projectManager.getParsedApplicationProject(), jarFiles, repository,
					vsumManager.getJavaCorrespondences());
			super.removeAllProblems();
		} catch (Exception e) {
			e.printStackTrace();
			super.reportError(
					"Service-Call-Graph extraction failed to due to a faulty configuration of the project or the binary JAR files.");
		}
		super.updateState();

		return this.currentServiceCallGraph;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceCallGraph provideCallGraph() {
		return this.currentServiceCallGraph;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean callGraphPresent() {
		return this.currentServiceCallGraph != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// nothing to do here
	}

}
