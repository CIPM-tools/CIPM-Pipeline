package dmodel.pipeline.rt.validation.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationMetric {

	private List<String> targetIds;
	private ValidationMetricValue value;

}
