package mir.routines.pcmToREM;

import java.io.IOException;
import mir.routines.pcmToREM.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class DeleteCorrespondenceContainerRoutine extends AbstractRepairRoutineRealization {
  private DeleteCorrespondenceContainerRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final ResourceContainer container, final RuntimeResourceContainer corr) {
      return container;
    }
    
    public EObject getElement2(final ResourceContainer container, final RuntimeResourceContainer corr) {
      return corr;
    }
    
    public EObject getCorrepondenceSourceCorr(final ResourceContainer container) {
      return container;
    }
  }
  
  public DeleteCorrespondenceContainerRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final ResourceContainer container) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.pcmToREM.DeleteCorrespondenceContainerRoutine.ActionUserExecution(getExecutionState(), this);
    this.container = container;
  }
  
  private ResourceContainer container;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine DeleteCorrespondenceContainerRoutine with input:");
    getLogger().debug("   container: " + this.container);
    
    cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer corr = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceCorr(container), // correspondence source supplier
    	cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer.class,
    	(cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (corr == null) {
    	return false;
    }
    registerObjectUnderModification(corr);
    removeCorrespondenceBetween(userExecution.getElement1(container, corr), userExecution.getElement2(container, corr), "");
    
    postprocessElements();
    
    return true;
  }
}
