package dmodel.pipeline.vsum.domains;

import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import de.uka.ipd.sdq.identifier.IdentifierPackage;
import tools.vitruv.domains.emf.builder.VitruviusEmfBuilderApplicator;
import tools.vitruv.domains.pcm.PcmNamespace;
import tools.vitruv.framework.domains.AbstractTuidAwareVitruvDomain;
import tools.vitruv.framework.domains.VitruviusProjectBuilderApplicator;
import tools.vitruv.framework.tuid.AttributeTuidCalculatorAndResolver;
import tools.vitruv.framework.tuid.TuidCalculatorAndResolver;

public class ExtendedPcmDomain extends AbstractTuidAwareVitruvDomain {
	private static final String METAMODEL_NAME = "PCMExtended";

	private static final String RESOURCEENVIRONMENT_FILE_EXTENSION = "resourceenvironment";
	private static final String ALLOCATION_FILE_EXTENSION = "allocation";
	private static final String USAGEMODEL_FILE_EXTENSION = "usagemodel";

	public ExtendedPcmDomain() {
		super(METAMODEL_NAME, PcmNamespace.ROOT_PACKAGE, generateTuidCalculator(), generateExtensionList());
	}

	private static String[] generateExtensionList() {
		return new String[] { PcmNamespace.REPOSITORY_FILE_EXTENSION, PcmNamespace.SYSTEM_FILE_EXTENSION,
				RESOURCEENVIRONMENT_FILE_EXTENSION, ALLOCATION_FILE_EXTENSION, USAGEMODEL_FILE_EXTENSION };
	}

	private static TuidCalculatorAndResolver generateTuidCalculator() {
		return new AttributeTuidCalculatorAndResolver(PcmNamespace.METAMODEL_NAMESPACE,
				IdentifierPackage.Literals.IDENTIFIER__ID.getName(),
				RepositoryPackage.Literals.PARAMETER__PARAMETER_NAME.getName(),
				EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME.getName(),
				RepositoryPackage.Literals.PRIMITIVE_DATA_TYPE__TYPE.getName());
	}

	@Override
	public VitruviusProjectBuilderApplicator getBuilderApplicator() {
		return new VitruviusEmfBuilderApplicator();
	}

}
