package cipm.consistency.tools.evaluation.scenario.data;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class AdaptionScenarioList {

	private List<AdaptionScenario> scenarios = Lists.newArrayList();
	private List<AdaptionScenario> initialScenarios = Lists.newArrayList();

}
