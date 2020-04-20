package dmodel.pipeline.rt.rest.health.data;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class JsonComponentHealthStateProblemContainer {

	private List<JsonComponentHealthStateProblem> problems;

	public JsonComponentHealthStateProblemContainer() {
		this.problems = Lists.newArrayList();
	}

}
