package tools.vitruv.domains.provider.pcm;

import cipm.consistency.domains.pcm.ExtendedPcmDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;

public class ExtendedPcmDomainProvider implements VitruvDomainProvider<ExtendedPcmDomain> {
	private static ExtendedPcmDomain domain;

	@Override
	public ExtendedPcmDomain getDomain() {
		if (domain == null) {
			domain = new ExtendedPcmDomain();
		}
		return domain;
	}

}
