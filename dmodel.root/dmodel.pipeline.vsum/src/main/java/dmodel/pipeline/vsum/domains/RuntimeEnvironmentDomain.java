package dmodel.pipeline.vsum.domains;

import dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage;
import dmodel.pipeline.vsum.manager.VsumConstants;
import tools.vitruv.domains.emf.builder.VitruviusEmfBuilderApplicator;
import tools.vitruv.framework.domains.AbstractTuidAwareVitruvDomain;
import tools.vitruv.framework.domains.VitruviusProjectBuilderApplicator;
import tools.vitruv.framework.tuid.AttributeTuidCalculatorAndResolver;

public class RuntimeEnvironmentDomain extends AbstractTuidAwareVitruvDomain {
	public RuntimeEnvironmentDomain() {
		super("REM", REModelPackage.eINSTANCE,
				new AttributeTuidCalculatorAndResolver(REModelPackage.eINSTANCE.getNsURI(), "id", "hostID",
						"entityName"),
				VsumConstants.INSTRUMENTATION_SUFFIX);
	}

	@Override
	public VitruviusProjectBuilderApplicator getBuilderApplicator() {
		return new VitruviusEmfBuilderApplicator();
	}

	@Override
	public boolean isUserVisible() {
		return false;
	}

}
