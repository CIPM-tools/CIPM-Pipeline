package dmodel.designtime.instrumentation.mapping.bridge;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;

public interface IEMFSubMappingImporter {

	public boolean targets(List<EObject> a, List<EObject> b);

	public void process(IJavaPCMCorrespondenceModel container, ParsedApplicationProject project, List<EObject> a,
			List<EObject> b);

}
