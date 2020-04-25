package dmodel.app.rest.dt.async;

import dmodel.app.rest.dt.data.InstrumentationStatus;
import dmodel.app.rest.dt.data.JsonInstrumentationConfiguration;
import dmodel.base.shared.util.AbstractObservable;
import dmodel.designtime.instrumentation.manager.InstrumentationManager;

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
