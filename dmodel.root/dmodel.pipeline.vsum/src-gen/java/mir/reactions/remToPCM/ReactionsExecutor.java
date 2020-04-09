package mir.reactions.remToPCM;

import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsExecutor;
import tools.vitruv.extensions.dslsruntime.reactions.RoutinesFacadesProvider;
import tools.vitruv.extensions.dslsruntime.reactions.structure.ReactionsImportPath;

@SuppressWarnings("all")
class ReactionsExecutor extends AbstractReactionsExecutor {
  public ReactionsExecutor() {
    super(new dmodel.pipeline.vsum.domains.RuntimeEnvironmentDomainProvider().getDomain(), 
    	new dmodel.pipeline.vsum.domains.ExtendedPcmDomainProvider().getDomain());
  }
  
  protected RoutinesFacadesProvider createRoutinesFacadesProvider() {
    return new mir.routines.remToPCM.RoutinesFacadesProvider();
  }
  
  protected void setup() {
    this.addReaction(new mir.reactions.remToPCM.CreateHostNameCorrespondenceReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("remToPCM"))));
    this.addReaction(new mir.reactions.remToPCM.DeleteHostCorrespondenceReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("remToPCM"))));
    this.addReaction(new mir.reactions.remToPCM.CreateHostLinkCorrespondenceReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("remToPCM"))));
    this.addReaction(new mir.reactions.remToPCM.DeleteHostLinkCorrespondenceReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("remToPCM"))));
  }
}
