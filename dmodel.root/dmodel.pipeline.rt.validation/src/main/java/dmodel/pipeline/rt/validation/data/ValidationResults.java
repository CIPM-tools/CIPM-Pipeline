package dmodel.pipeline.rt.validation.data;

import java.util.List;

import lombok.Data;

@Data
public class ValidationResults {

	private List<ValidationMetric> metrics;

}
