package cipm.consistency.base.core.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class PredicateRuleConfiguration {

	private String condition;

	private String id;
	private String operator;
	private double value;

	private List<PredicateRuleConfiguration> rules;

}
