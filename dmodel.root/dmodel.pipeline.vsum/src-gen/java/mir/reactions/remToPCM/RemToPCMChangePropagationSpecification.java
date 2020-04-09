package mir.reactions.remToPCM;

import org.springframework.stereotype.Service;

import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;

@SuppressWarnings("all")
@Service
public class RemToPCMChangePropagationSpecification extends AbstractReactionsChangePropagationSpecification
		implements ChangePropagationSpecification {
	public RemToPCMChangePropagationSpecification() {
		super(new dmodel.pipeline.vsum.domains.RuntimeEnvironmentDomainProvider().getDomain(),
				new dmodel.pipeline.vsum.domains.ExtendedPcmDomainProvider().getDomain());
	}

	protected void setup() {
		this.addChangeMainprocessor(new mir.reactions.remToPCM.ReactionsExecutor());
	}
}
