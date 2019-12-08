package dmodel.pipeline.rt.validation.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationMetric {

	private String targetId;
	private ValidationMetricValue value;

}
