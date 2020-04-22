package dmodel.pipeline.vsum.domains;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModelPackage;
import dmodel.pipeline.vsum.manager.VsumConstants;
import tools.vitruv.domains.emf.builder.VitruviusEmfBuilderApplicator;
import tools.vitruv.framework.domains.AbstractTuidAwareVitruvDomain;
import tools.vitruv.framework.domains.VitruviusProjectBuilderApplicator;
import tools.vitruv.framework.tuid.AttributeTuidCalculatorAndResolver;

public class InstrumentationModelDomain extends AbstractTuidAwareVitruvDomain {
	public InstrumentationModelDomain() {
		super("IMM", InstrumentationModelPackage.eINSTANCE,
				new AttributeTuidCalculatorAndResolver(InstrumentationModelPackage.eINSTANCE.getNsURI(), "id",
						"entityName"),
				VsumConstants.INSTRUMENTATION_SUFFIX.substring(1));
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
