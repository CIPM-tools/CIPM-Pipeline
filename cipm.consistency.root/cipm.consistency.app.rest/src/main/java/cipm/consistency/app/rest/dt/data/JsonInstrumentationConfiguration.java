package cipm.consistency.app.rest.dt.data;

import cipm.consistency.designtime.instrumentation.project.app.InstrumentationMetadata;
import lombok.Data;

@Data
public class JsonInstrumentationConfiguration {

	private boolean extractMappingFromCode;
	private InstrumentationMetadata metadata;

}
