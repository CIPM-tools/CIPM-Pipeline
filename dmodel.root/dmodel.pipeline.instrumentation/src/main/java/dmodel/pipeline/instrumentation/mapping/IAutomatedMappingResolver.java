package dmodel.pipeline.instrumentation.mapping;

import dmodel.pipeline.instrumentation.project.ParsedApplicationProject;
import dmodel.pipeline.vsum.domains.java.IJavaPCMCorrespondenceModel;

public interface IAutomatedMappingResolver {

	public void resolveMappings(ParsedApplicationProject project, IJavaPCMCorrespondenceModel cpm);

}
