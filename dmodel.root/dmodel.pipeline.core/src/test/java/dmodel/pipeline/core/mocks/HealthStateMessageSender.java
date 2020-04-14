package dmodel.pipeline.core.mocks;

import dmodel.pipeline.core.health.AbstractHealthStateComponent;
import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateObservedComponent;

public class HealthStateMessageSender extends AbstractHealthStateComponent {

	public HealthStateMessageSender() {
		super(null);
	}

	public void sendMessage(HealthStateObservedComponent from, HealthStateObservedComponent to) {
		this.component = from;
		this.removeAllProblems();
		this.updateState();
		this.sendStateMessage(to);
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// nothing
	}

}
