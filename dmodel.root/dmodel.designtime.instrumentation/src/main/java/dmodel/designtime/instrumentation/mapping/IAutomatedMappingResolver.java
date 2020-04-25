package dmodel.designtime.instrumentation.mapping;

import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;

public interface IAutomatedMappingResolver {

	public void resolveMappings(ParsedApplicationProject project, IJavaPCMCorrespondenceModel cpm);

}
