package dmodel.pipeline.rt.rest.rt.validation.data;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class JsonValidationOverview {

	private List<JsonValidationPointOverview> points;

	public JsonValidationOverview() {
		this.points = Lists.newArrayList();
	}

}
