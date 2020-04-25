package dmodel.runtime.pipeline.validation.contracts;

import dmodel.runtime.pipeline.validation.data.ValidationData;
import dmodel.runtime.pipeline.validation.data.ValidationState;

public interface IValidationProcessListener {

	public void stateChanged(ValidationState state);

	public void validationFinished(ValidationData data);

}
