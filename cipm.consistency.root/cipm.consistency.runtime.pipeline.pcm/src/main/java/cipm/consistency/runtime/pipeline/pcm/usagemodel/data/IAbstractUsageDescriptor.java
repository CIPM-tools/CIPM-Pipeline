package cipm.consistency.runtime.pipeline.pcm.usagemodel.data;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;

public interface IAbstractUsageDescriptor extends IPCMAnalogue<AbstractUserAction> {
	public boolean matches(IAbstractUsageDescriptor other);

	public void merge(IAbstractUsageDescriptor other);
}
