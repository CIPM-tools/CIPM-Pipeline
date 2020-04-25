package dmodel.app.rest.rt.validation.data;

import lombok.Data;

@Data
public class JsonValidationPointOverview {

	private int validationPointCount;
	private boolean empty;

	private double validationImprovementScore;
	private boolean visPresent;

	private String validationDescription;
	private String validationDescriptionEnum;

}
