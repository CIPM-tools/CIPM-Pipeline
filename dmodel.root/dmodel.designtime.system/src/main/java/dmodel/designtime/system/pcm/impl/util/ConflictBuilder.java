package dmodel.designtime.system.pcm.impl.util;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.springframework.stereotype.Service;

import dmodel.designtime.system.pcm.data.AssemblyConflict;
import dmodel.designtime.system.pcm.data.ConnectionConflict;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.SystemProvidedRole;

/**
 * Utility service that can be used to create conflicts.
 * 
 * @author David Monschein
 *
 */
@Service
public class ConflictBuilder {
	private int conflictCounter = 0;

	/**
	 * Creates a new assembly conflict instance.
	 * 
	 * @param target                 the target of the required role/ provided role
	 *                               that needs to be satisfied
	 * @param possibleACtxs          the possible solutions
	 * @param correspondingComponent the corresponding component of the target
	 * @return the created conflict
	 */
	public AssemblyConflict createAssemblyConflict(Xor<AssemblyRequiredRole, SystemProvidedRole> target,
			List<AssemblyContext> possibleACtxs, RepositoryComponent correspondingComponent) {
		AssemblyConflict conflict = new AssemblyConflict(conflictCounter++);
		conflict.setTarget(target);
		conflict.setSolutions(possibleACtxs);
		conflict.setCorrespondingComponent(correspondingComponent);
		return conflict;
	}

	/**
	 * Creates a new connection conflict instance.
	 * 
	 * @param target             the target of the required/ provided role that
	 *                           needs to be satisfied
	 * @param possibleComponents the possible solutions
	 * @return the created conflict
	 */
	public ConnectionConflict createConnectionConflict(Xor<AssemblyRequiredRole, SystemProvidedRole> target,
			List<RepositoryComponent> possibleComponents) {
		ConnectionConflict conflict = new ConnectionConflict(conflictCounter++);
		conflict.setTarget(target);
		conflict.setSolutions(new ArrayList<>(possibleComponents));
		return conflict;
	}

}
