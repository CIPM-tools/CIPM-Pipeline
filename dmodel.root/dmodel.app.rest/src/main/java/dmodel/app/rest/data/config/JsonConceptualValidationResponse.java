package dmodel.app.rest.data.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class JsonConceptualValidationResponse {

	private boolean slidingWindowSizeValid;
	private boolean slidingWindowTriggerValid;

	private boolean simulatorValid;
	private boolean measurementsValid;
	private boolean simulationTimeValid;
	private boolean validationSplitValid;

	@JsonIgnore
	public boolean isValid() {
		return slidingWindowSizeValid && slidingWindowTriggerValid && simulatorValid && measurementsValid
				&& simulationTimeValid && validationSplitValid;
	}

}
