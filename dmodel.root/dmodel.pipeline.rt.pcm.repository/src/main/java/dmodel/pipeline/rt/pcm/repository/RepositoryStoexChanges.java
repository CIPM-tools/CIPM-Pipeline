package dmodel.pipeline.rt.pcm.repository;

import java.util.Map;

import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.AbstractInternalControlFlowAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.GuardedBranchTransition;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;

import com.google.common.collect.Maps;

import dmodel.pipeline.shared.ModelUtil;

public class RepositoryStoexChanges {

	private Map<String, PCMRandomVariable> seffStoexChanges;

	public RepositoryStoexChanges() {
		this.seffStoexChanges = Maps.newHashMap();
	}

	public void inherit(RepositoryStoexChanges other) {
		other.seffStoexChanges.entrySet().forEach(e -> seffStoexChanges.put(e.getKey(), e.getValue()));
	}

	public void put(String id, PCMRandomVariable nStoex) {
		seffStoexChanges.put(id, nStoex);
	}

	public void clear() {
		seffStoexChanges.clear();
	}

	public void apply(Repository repository) {
		ModelUtil.getObjects(repository, AbstractInternalControlFlowAction.class).stream().forEach(action -> {
			if (seffStoexChanges.containsKey(action.getId())) {
				PCMRandomVariable nStoex = seffStoexChanges.get(action.getId());
				if (action instanceof LoopAction) {
					((LoopAction) action).setIterationCount_LoopAction(nStoex);
				} else if (action instanceof GuardedBranchTransition) {
					((GuardedBranchTransition) action).setBranchCondition_GuardedBranchTransition(nStoex);
				} else if (action instanceof InternalAction) {
					if (action.getResourceDemand_Action().size() == 1) {
						action.getResourceDemand_Action().get(0).setSpecification_ParametericResourceDemand(nStoex);
					}
				} else if (action instanceof ExternalCallAction) {
					// TODO
				}
			}
		});
	}

	public int size() {
		return seffStoexChanges.size();
	}

}
