package dmodel.pipeline.instrumentation.transformation;

import dmodel.pipeline.instrumentation.project.ParsedApplicationProject;
import dmodel.pipeline.vsum.domains.java.IJavaPCMCorrespondenceModel;

public interface IApplicationProjectInstrumenter {

	public void transform(ParsedApplicationProject pap, IJavaPCMCorrespondenceModel correspondence);

}
