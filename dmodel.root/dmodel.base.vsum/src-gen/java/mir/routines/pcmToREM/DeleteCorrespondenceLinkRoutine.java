package mir.routines.pcmToREM;

import java.io.IOException;
import mir.routines.pcmToREM.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;

import dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class DeleteCorrespondenceLinkRoutine extends AbstractRepairRoutineRealization {
  private DeleteCorrespondenceLinkRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final LinkingResource link, final RuntimeResourceContainerConnection corr) {
      return link;
    }
    
    public EObject getElement2(final LinkingResource link, final RuntimeResourceContainerConnection corr) {
      return corr;
    }
    
    public EObject getCorrepondenceSourceCorr(final LinkingResource link) {
      return link;
    }
  }
  
  public DeleteCorrespondenceLinkRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final LinkingResource link) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.pcmToREM.DeleteCorrespondenceLinkRoutine.ActionUserExecution(getExecutionState(), this);
    this.link = link;
  }
  
  private LinkingResource link;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine DeleteCorrespondenceLinkRoutine with input:");
    getLogger().debug("   link: " + this.link);
    
    dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection corr = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceCorr(link), // correspondence source supplier
    	dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection.class,
    	(dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (corr == null) {
    	return false;
    }
    registerObjectUnderModification(corr);
    removeCorrespondenceBetween(userExecution.getElement1(link, corr), userExecution.getElement2(link, corr), "");
    
    postprocessElements();
    
    return true;
  }
}
