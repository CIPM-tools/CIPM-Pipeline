package dmodel.pipeline.instrumentation.mapping.bridge;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import dmodel.pipeline.instrumentation.project.ParsedApplicationProject;
import dmodel.pipeline.vsum.domains.java.IJavaPCMCorrespondenceModel;

public interface IEMFSubMappingImporter {

	public boolean targets(List<EObject> a, List<EObject> b);

	public void process(IJavaPCMCorrespondenceModel container, ParsedApplicationProject project, List<EObject> a,
			List<EObject> b);

}
