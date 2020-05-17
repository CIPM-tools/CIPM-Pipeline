package dmodel.designtime.instrumentation.mapping;

import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;

/**
 * Interface that can be used to automatically extract a mapping between source
 * code elements and architecture model elements from the corresponding source
 * code.
 * 
 * @author David Monschein
 *
 */
public interface IAutomatedMappingResolver {

	/**
	 * Resolves the mapping between source code elements and architecture model
	 * elements.
	 * 
	 * @param project the Java project under observation
	 * @param cpm     the correspondence model that should be used to store the
	 *                mappings
	 */
	public void resolveMappings(ParsedApplicationProject project, IJavaPCMCorrespondenceModel cpm);

}
