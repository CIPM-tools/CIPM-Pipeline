package dmodel.pipeline.rt.imm.transformation.predicate;

import dmodel.pipeline.rt.validation.data.ValidationPoint;

public interface ValidationPredicate {

	boolean satisfied(ValidationPoint validationPoint);

}
