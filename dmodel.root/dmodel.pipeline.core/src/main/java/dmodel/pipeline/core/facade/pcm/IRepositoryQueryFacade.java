package dmodel.pipeline.core.facade.pcm;

import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import de.uka.ipd.sdq.identifier.Identifier;
import dmodel.pipeline.core.facade.IResettableQueryFacade;

public interface IRepositoryQueryFacade extends IResettableQueryFacade {

	public <T extends Identifier> T getElementById(String id, Class<T> type);

	default public ResourceDemandingSEFF getServiceById(String id) {
		return this.getElementById(id, ResourceDemandingSEFF.class);
	}

	default public InternalAction getInternalAction(String key) {
		return this.getElementById(key, InternalAction.class);
	}

	default public OperationInterface getOperationInterface(String id) {
		return this.getElementById(id, OperationInterface.class);
	}

	default public AbstractBranchTransition getBranchTransition(String id) {
		return this.getElementById(id, AbstractBranchTransition.class);
	}

}
