package cipm.consistency.base.core.mocks;

import cipm.consistency.base.core.health.AbstractHealthStateComponent;
import cipm.consistency.base.core.health.HealthState;
import cipm.consistency.base.core.health.HealthStateObservedComponent;

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
