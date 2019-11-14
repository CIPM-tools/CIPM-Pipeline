package dmodel.pipeline.rt.validation.contracts;

import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.rt.validation.data.ValidationState;

public interface IValidationProcessListener {

	public void stateChanged(ValidationState state);

	public void validationFinished(ValidationData data);

}
