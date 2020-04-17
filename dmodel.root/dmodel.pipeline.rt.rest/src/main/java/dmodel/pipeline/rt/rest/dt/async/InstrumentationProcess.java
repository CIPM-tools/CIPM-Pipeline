package dmodel.pipeline.rt.rest.dt.async;

import dmodel.pipeline.instrumentation.manager.InstrumentationManager;
import dmodel.pipeline.rt.rest.dt.data.InstrumentationStatus;
import dmodel.pipeline.rt.rest.dt.data.JsonInstrumentationConfiguration;
import dmodel.pipeline.shared.util.AbstractObservable;

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
