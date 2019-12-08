package dmodel.pipeline.rt.validation.contracts;

import dmodel.pipeline.rt.validation.data.ValidationResults;
import dmodel.pipeline.rt.validation.data.ValidationState;

public interface IValidationProcessListener {

	public void stateChanged(ValidationState state);

	public void validationFinished(ValidationResults data);

}
