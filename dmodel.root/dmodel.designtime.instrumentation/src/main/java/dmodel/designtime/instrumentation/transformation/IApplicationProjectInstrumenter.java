package dmodel.designtime.instrumentation.transformation;

import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;

public interface IApplicationProjectInstrumenter {

	public void transform(ParsedApplicationProject pap, IJavaPCMCorrespondenceModel correspondence);

}
