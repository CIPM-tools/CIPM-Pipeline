package dmodel.pipeline.shared.correspondence;

import java.io.File;

import dmodel.pipeline.shared.ModelUtil;
import tools.vitruv.dsls.reactions.meta.correspondence.reactions.ReactionsPackage;
import tools.vitruv.framework.correspondence.CorrespondencePackage;
import tools.vitruv.framework.correspondence.Correspondences;

public class CorrespondenceUtil {

	public static Correspondences loadCorrespondenceModel(File file) {
		return ModelUtil.readFromFile(file.getAbsolutePath(), Correspondences.class);
	}

	public static void initVitruv() {
		CorrespondencePackage.eINSTANCE.eClass();
		ReactionsPackage.eINSTANCE.eClass();
	}

}
