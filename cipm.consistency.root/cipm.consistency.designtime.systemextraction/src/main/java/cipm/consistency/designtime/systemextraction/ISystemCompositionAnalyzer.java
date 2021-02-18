package cipm.consistency.designtime.systemextraction;

import java.io.File;
import java.util.List;

import cipm.consistency.base.core.facade.pcm.IRepositoryQueryFacade;
import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph;
import cipm.consistency.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import cipm.consistency.designtime.instrumentation.project.ParsedApplicationProject;

/**
 * Interface that can be used to derive a service-call-graph (SCG) from a Java
 * project and the corresponding binary jar files. Furthermore, a repository
 * model and a mapping between repository model and Java source code elements is
 * necessary as input.
 * 
 * @author David Monschein
 *
 */
public interface ISystemCompositionAnalyzer {
	/**
	 * Builds a service-call-graph by performing source-code analysis in combination
	 * with the binaries and an existing repository model.
	 * 
	 * @param parsedApplication the parsed application (JavaParser)
	 * @param binaryJarFiles    the binary JAR files of the application (used for
	 *                          type resolution)
	 * @param repository        the repository model
	 * @param correspondence    the mapping between Java source code elements and
	 *                          repository model elements
	 * @return the resulting SCG
	 */
	public ServiceCallGraph deriveSystemComposition(ParsedApplicationProject parsedApplication,
			List<File> binaryJarFiles, IRepositoryQueryFacade repository, IJavaPCMCorrespondenceModel correspondence);

}