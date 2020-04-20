package dmodel.pipeline.rt.rest.health.data;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import lombok.Data;

@Data
public class JsonComponentHealthStates {

	private Map<Pair<HealthStateObservedComponent, String>, HealthState> states;

	public JsonComponentHealthStates() {
		this.states = Maps.newHashMap();
	}

}
