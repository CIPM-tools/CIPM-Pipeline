package cipm.consistency.runtime.pipeline.instrumentation.predicate.impl;

import cipm.consistency.runtime.pipeline.instrumentation.predicate.ValidationPredicate;
import cipm.consistency.runtime.pipeline.instrumentation.predicate.config.ELogicalOperator;
import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;

public class StackedValidationPredicate implements ValidationPredicate {
	private ValidationPredicate[] predicates;
	private ELogicalOperator operator;

	public StackedValidationPredicate(ELogicalOperator operator, ValidationPredicate... predicates) {
		this.predicates = predicates;
		this.operator = operator;
	}

	@Override
	public boolean satisfied(ValidationPoint validationPoint) {
		for (ValidationPredicate pred : predicates) {
			if (ELogicalOperator.AND == operator && pred != null && !pred.satisfied(validationPoint)) {
				return false;
			} else if (operator == ELogicalOperator.OR && pred != null && pred.satisfied(validationPoint)) {
				return true;
			}
		}
		return ELogicalOperator.AND == operator ? true : false;
	}

}
