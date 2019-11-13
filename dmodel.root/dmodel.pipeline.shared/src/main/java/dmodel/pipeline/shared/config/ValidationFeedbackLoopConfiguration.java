package dmodel.pipeline.shared.config;

import lombok.Data;

@Data
public class ValidationFeedbackLoopConfiguration {
	private String url;
	private int port;
}
