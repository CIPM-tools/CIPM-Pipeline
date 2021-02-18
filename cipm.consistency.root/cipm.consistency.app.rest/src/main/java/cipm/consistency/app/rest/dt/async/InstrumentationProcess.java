package cipm.consistency.app.rest.dt.async;

import cipm.consistency.app.rest.dt.data.InstrumentationStatus;
import cipm.consistency.app.rest.dt.data.JsonInstrumentationConfiguration;
import cipm.consistency.base.shared.util.AbstractObservable;
import cipm.consistency.designtime.instrumentation.manager.InstrumentationManager;

public class InstrumentationProcess extends AbstractObservable<InstrumentationStatus> implements Runnable {

	private InstrumentationManager transformer;
	private JsonInstrumentationConfiguration configuration;

	public InstrumentationProcess(InstrumentationManager transformer, JsonInstrumentationConfiguration configuration) {
		this.configuration = configuration;
		this.transformer = transformer;
	}

	@Override
	public void run() {
		this.flood(InstrumentationStatus.STARTED);

		// instrument
		transformer.instrumentProject(configuration.isExtractMappingFromCode(), configuration.getMetadata());

		this.flood(InstrumentationStatus.FINISHED);
	}

}
