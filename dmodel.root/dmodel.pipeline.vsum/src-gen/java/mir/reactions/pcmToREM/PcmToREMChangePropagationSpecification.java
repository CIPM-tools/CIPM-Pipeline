package mir.reactions.pcmToREM;

import org.springframework.stereotype.Service;

import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;

@SuppressWarnings("all")
@Service
public class PcmToREMChangePropagationSpecification extends AbstractReactionsChangePropagationSpecification
		implements ChangePropagationSpecification {
	public PcmToREMChangePropagationSpecification() {
		super(new dmodel.pipeline.vsum.domains.ExtendedPcmDomainProvider().getDomain(),
				new dmodel.pipeline.vsum.domains.RuntimeEnvironmentDomainProvider().getDomain());
	}

	protected void setup() {
		this.addChangeMainprocessor(new mir.reactions.pcmToREM.ReactionsExecutor());
	}
}
