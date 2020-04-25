package dmodel.runtime.pipeline.inm.transformation.predicate;

import dmodel.runtime.pipeline.validation.data.ValidationPoint;

public interface ValidationPredicate {

	boolean satisfied(ValidationPoint validationPoint);

}
