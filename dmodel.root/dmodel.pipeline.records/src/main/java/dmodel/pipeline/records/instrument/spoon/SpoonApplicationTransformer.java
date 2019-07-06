package dmodel.pipeline.records.instrument.spoon;

import dmodel.pipeline.records.instrument.ApplicationProject;
import dmodel.pipeline.records.instrument.IApplicationInstrumenter;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonApplicationTransformer implements IApplicationInstrumenter {

	public CtModel createModel(ApplicationProject project) {
		Launcher spoon = new Launcher();

		// load all sources
		for (String srcFolder : project.getSourceFolders()) {
			spoon.addInputResource(project.getRootPath() + srcFolder);
		}
		spoon.buildModel();

		// iterate over
		CtModel model = spoon.getModel();

		return model;
	}

	@Override
	public void instrumentApplication(ApplicationProject project, String outputPath) {
		Launcher spoon = new Launcher();

		// load all sources
		for (String srcFolder : project.getSourceFolders()) {
			spoon.addInputResource(project.getRootPath() + srcFolder);
		}
		spoon.buildModel();

		// iterate over
		CtModel model = spoon.getModel();
		model.filterChildren(new TypeFilter<CtClass<?>>(CtClass.class)).forEach(c -> {
		});

	}

}
