package dmodel.designtime.system;

import java.io.File;
import java.util.List;

import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;

public interface ISystemCompositionAnalyzer {
	public ServiceCallGraph deriveSystemComposition(ParsedApplicationProject parsedApplication,
			List<File> binaryJarFiles, IRepositoryQueryFacade repository, IJavaPCMCorrespondenceModel correspondence);

}