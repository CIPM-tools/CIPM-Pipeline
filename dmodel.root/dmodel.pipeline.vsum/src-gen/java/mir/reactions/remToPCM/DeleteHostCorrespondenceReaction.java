package mir.reactions.remToPCM;

import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer;
import mir.routines.remToPCM.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.eobject.DeleteEObject;

@SuppressWarnings("all")
public class DeleteHostCorrespondenceReaction extends AbstractReactionRealization {
  private DeleteEObject<RuntimeResourceContainer> deleteChange;
  
  private int currentlyMatchedChange;
  
  public DeleteHostCorrespondenceReaction(final RoutinesFacade routinesFacade) {
    super(routinesFacade);
  }
  
  public void executeReaction(final EChange change) {
    if (!checkPrecondition(change)) {
    	return;
    }
    dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer affectedEObject = deleteChange.getAffectedEObject();
    				
    getLogger().trace("Passed complete precondition check of Reaction " + this.getClass().getName());
    				
    mir.reactions.remToPCM.DeleteHostCorrespondenceReaction.ActionUserExecution userExecution = new mir.reactions.remToPCM.DeleteHostCorrespondenceReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(deleteChange, affectedEObject, this.getRoutinesFacade());
    
    resetChanges();
  }
  
  private boolean matchDeleteChange(final EChange change) {
    if (change instanceof DeleteEObject<?>) {
    	DeleteEObject<dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer> _localTypedChange = (DeleteEObject<dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer)) {
    		return false;
    	}
    	this.deleteChange = (DeleteEObject<dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer>) change;
    	return true;
    }
    
    return false;
  }
  
  private void resetChanges() {
    deleteChange = null;
    currentlyMatchedChange = 0;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (currentlyMatchedChange == 0) {
    	if (!matchDeleteChange(change)) {
    		resetChanges();
    		return false;
    	} else {
    		currentlyMatchedChange++;
    	}
    }
    
    return true;
  }
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final DeleteEObject deleteChange, final RuntimeResourceContainer affectedEObject, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.syncHostDeletion(affectedEObject);
    }
  }
}
