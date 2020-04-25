package dmodel.designtime.instrumentation.project.app;

import lombok.Data;

@Data
public class InstrumentationMetadata {

	private String outputPath;
	private String hostName;
	private int restPort;
	private String inmRestPath;

	private boolean logarithmicScaling;
	private long logarithmicRecoveryInterval;

}
