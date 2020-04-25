package dmodel.app.rest.dt.data;

import dmodel.designtime.instrumentation.project.app.InstrumentationMetadata;
import lombok.Data;

@Data
public class JsonInstrumentationConfiguration {

	private boolean extractMappingFromCode;
	private InstrumentationMetadata metadata;

}
