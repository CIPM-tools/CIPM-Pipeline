package dmodel.base.vsum.domains;

import dmodel.base.models.runtimeenvironment.REModel.REModelPackage;
import dmodel.base.vsum.manager.VsumConstants;
import tools.vitruv.domains.emf.builder.VitruviusEmfBuilderApplicator;
import tools.vitruv.framework.domains.AbstractTuidAwareVitruvDomain;
import tools.vitruv.framework.domains.VitruviusProjectBuilderApplicator;
import tools.vitruv.framework.tuid.AttributeTuidCalculatorAndResolver;

public class RuntimeEnvironmentDomain extends AbstractTuidAwareVitruvDomain {
	public RuntimeEnvironmentDomain() {
		super("REM", REModelPackage.eINSTANCE,
				new AttributeTuidCalculatorAndResolver(REModelPackage.eINSTANCE.getNsURI(), "id", "hostID",
						"entityName"),
				VsumConstants.RUNTIMEENVIRONMENT_SUFFIX.substring(1));
	}

	@Override
	public VitruviusProjectBuilderApplicator getBuilderApplicator() {
		return new VitruviusEmfBuilderApplicator();
	}

	@Override
	public boolean isUserVisible() {
		return false;
	}

	@Override
	public boolean supportsUuids() {
		return true;
	}

}
