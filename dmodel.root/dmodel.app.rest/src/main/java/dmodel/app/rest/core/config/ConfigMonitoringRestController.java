package dmodel.app.rest.core.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.base.core.config.ConfigurationContainer;
import dmodel.base.core.config.PredicateRuleConfiguration;
import dmodel.base.shared.JsonUtil;
import dmodel.runtime.pipeline.inm.transformation.InstrumentationModelTransformation;
import dmodel.runtime.pipeline.inm.transformation.predicate.ValidationPredicate;
import dmodel.runtime.pipeline.inm.transformation.predicate.config.ELogicalOperator;
import dmodel.runtime.pipeline.inm.transformation.predicate.config.ENumericalComparator;
import dmodel.runtime.pipeline.inm.transformation.predicate.impl.DoubleMetricValidationPredicate;
import dmodel.runtime.pipeline.inm.transformation.predicate.impl.StackedValidationPredicate;
import dmodel.runtime.pipeline.validation.data.metric.ValidationMetricType;
import lombok.extern.java.Log;

@RestController
@Log
public class ConfigMonitoringRestController {
	private static final Map<String, ValidationMetricType> predicateMetricMapping = new HashMap<String, ValidationMetricType>() {
		/**
		 * Generated Serial UID.
		 */
		private static final long serialVersionUID = 1634402137017012232L;
		{
			put("kstest", ValidationMetricType.KS_TEST);
			put("avg_rel", ValidationMetricType.AVG_DISTANCE_REL);
			put("avg_absolute", ValidationMetricType.AVG_DISTANCE_ABS);
			put("wasserstein", ValidationMetricType.WASSERSTEIN);
			put("median_absolute", ValidationMetricType.MEDIAN_DISTANCE);
		}
	};

	@Autowired
	private InstrumentationModelTransformation inmTransformation;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ConfigurationContainer configurationContainer;

	@PostMapping("/config/monitoring/predicate/save")
	public String savePredicate(@RequestParam String predicate) {
		try {
			PredicateRuleConfiguration parsedPredicate = objectMapper.readValue(predicate,
					PredicateRuleConfiguration.class);
			ValidationPredicate javaPredicate = toJavaPredicate(parsedPredicate);

			inmTransformation.setValidationPredicate(javaPredicate);
			configurationContainer.setValidationPredicates(parsedPredicate);

			return JsonUtil.wrapAsObject("success", configurationContainer.syncWithFilesystem(false), false);
		} catch (IOException e) {
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

	@GetMapping("/config/monitoring/predicate/get")
	public String getPredicate() {
		try {
			return objectMapper.writeValueAsString(configurationContainer.getValidationPredicates());
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

	private ValidationPredicate toJavaPredicate(PredicateRuleConfiguration config) {
		if (config.getCondition() != null) {
			// it is stacked
			ValidationPredicate[] predicates = new ValidationPredicate[config.getRules().size()];

			for (int i = 0; i < config.getRules().size(); i++) {
				predicates[i] = toJavaPredicate(config.getRules().get(i));
			}

			return new StackedValidationPredicate(ELogicalOperator.fromString(config.getCondition()), predicates);
		} else {
			return buildSimpleJavaPredicate(config);
		}
	}

	private ValidationPredicate buildSimpleJavaPredicate(PredicateRuleConfiguration config) {
		ENumericalComparator comparator = ENumericalComparator.fromString(config.getOperator());
		if (comparator != null && predicateMetricMapping.containsKey(config.getId())) {
			return new DoubleMetricValidationPredicate(predicateMetricMapping.get(config.getOperator()), comparator,
					config.getValue());
		} else {
			log.warning("Metric of type '" + config.getId() + "' or comparator '" + comparator.getName()
					+ "' is not supported yet for the predicate generation.");
		}

		return null;
	}

}
