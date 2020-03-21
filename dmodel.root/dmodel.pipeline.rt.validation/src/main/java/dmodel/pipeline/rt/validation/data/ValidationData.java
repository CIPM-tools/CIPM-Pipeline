package dmodel.pipeline.rt.validation.data;

import java.util.List;

import lombok.Data;

@Data
public class ValidationData {

	private List<ValidationPoint> validationPoints;

	public boolean isEmpty() {
		return validationPoints == null || validationPoints.isEmpty();
	}

}
