package cipm.consistency.runtime.pipeline.instrumentation.predicate;

import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;

public interface ValidationPredicate {

	boolean satisfied(ValidationPoint validationPoint);

}
