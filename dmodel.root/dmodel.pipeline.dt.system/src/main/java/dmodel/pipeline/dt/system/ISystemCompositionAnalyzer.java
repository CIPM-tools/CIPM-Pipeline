package dmodel.pipeline.dt.system;

import java.io.File;
import java.util.List;

import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.instrumentation.project.ParsedApplicationProject;
import dmodel.pipeline.vsum.domains.java.IJavaPCMCorrespondenceModel;

public interface ISystemCompositionAnalyzer {
	public ServiceCallGraph deriveSystemComposition(ParsedApplicationProject parsedApplication,
			List<File> binaryJarFiles, IRepositoryQueryFacade repository, IJavaPCMCorrespondenceModel correspondence);

}