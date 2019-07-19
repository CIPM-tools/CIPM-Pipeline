package dmodel.pipeline.dt.system.pcm;

import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.RequiredRole;

public interface IAssemblySelectionStrategy {

	/**
	 * Select an assembly context from possible ones based on a role which requires
	 * a plugged in assembly.
	 * 
	 * @param poss    possible already existing assembly contexts
	 * @param reqRole role which requires a specific interface provided by the
	 *                assembly(s)
	 * @return the selected assembly from the list or null if a new one should be
	 *         created
	 */
	public AssemblyContext selectAssembly(List<AssemblyContext> poss, RequiredRole reqRole);

}
