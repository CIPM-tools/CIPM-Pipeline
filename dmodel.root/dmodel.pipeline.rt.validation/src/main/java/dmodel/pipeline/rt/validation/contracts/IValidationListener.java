package dmodel.pipeline.rt.validation.contracts;

import dmodel.pipeline.rt.validation.data.ValidationState;

public interface IValidationListener {

	public void stateChanged(ValidationState state);

}
