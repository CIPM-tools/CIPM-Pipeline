package dmodel.designtime.instrumentation.mapping.bridge;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.ecore.EObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;

@Service
public class EMFMappingImporter {
	@Autowired
	private List<IEMFSubMappingImporter> subImporters;

	public EMFMappingImporter() {
	}

	public void importMapping(IJavaPCMCorrespondenceModel container, ParsedApplicationProject currentProject,
			List<Pair<List<EObject>, List<EObject>>> emfCorrespondences) {
		emfCorrespondences.forEach(corr -> {
			for (IEMFSubMappingImporter subImporter : subImporters) {
				if (subImporter.targets(corr.getLeft(), corr.getRight())) {
					subImporter.process(container, currentProject, corr.getLeft(), corr.getRight());
				}
			}
		});
	}
}
