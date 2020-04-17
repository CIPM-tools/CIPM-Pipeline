package dmodel.pipeline.rt.rest.dt.data;

import dmodel.pipeline.instrumentation.project.app.InstrumentationMetadata;
import lombok.Data;

@Data
public class JsonInstrumentationConfiguration {

	private boolean extractMappingFromCode;
	private InstrumentationMetadata metadata;

}
