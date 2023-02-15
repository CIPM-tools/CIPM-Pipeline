package tools.vitruv.domains.provider.im;

import cipm.consistency.domains.im.InstrumentationModelDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;

public class InstrumentationModelDomainProvider implements VitruvDomainProvider<InstrumentationModelDomain> {
	private static InstrumentationModelDomain instance;

	@Override
	public InstrumentationModelDomain getDomain() {
		if (instance == null) {
			instance = new InstrumentationModelDomain();
		}
		return instance;
	}

}
