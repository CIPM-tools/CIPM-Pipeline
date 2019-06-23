package dmodel.pipeline.shared.correspondence;

import java.io.File;

import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.Correspondences;

public class CorrespondenceTest {

	public static void main(String[] argv) {
		CorrespondenceUtil.initVitruv();

		Correspondences model = CorrespondenceUtil
				.loadCorrespondenceModel(new File("correspondence/Correspondences.correspondence"));

		for (Correspondence corr : model.getCorrespondences()) {
			System.out.println("TEST");
			System.out.println(corr.getATuids());
			System.out.println(corr.getBTuids());
		}
	}

}
