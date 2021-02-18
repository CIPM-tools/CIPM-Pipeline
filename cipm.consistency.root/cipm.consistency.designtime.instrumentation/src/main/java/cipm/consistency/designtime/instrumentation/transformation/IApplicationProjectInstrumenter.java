package cipm.consistency.designtime.instrumentation.transformation;

import cipm.consistency.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import cipm.consistency.designtime.instrumentation.project.ParsedApplicationProject;

public interface IApplicationProjectInstrumenter {

	public void transform(ParsedApplicationProject pap, IJavaPCMCorrespondenceModel correspondence);

}
