package dmodel.pipeline.dt.system.pcm.impl.util;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.springframework.stereotype.Service;

import dmodel.pipeline.dt.system.pcm.data.AssemblyConflict;
import dmodel.pipeline.dt.system.pcm.data.ConnectionConflict;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder.SystemProvidedRole;

@Service
public class ConflictBuilder {
	private int conflictCounter = 0;

	public AssemblyConflict createAssemblyConflict(Xor<AssemblyRequiredRole, SystemProvidedRole> target,
			List<AssemblyContext> possibleACtxs, RepositoryComponent correspondingComponent) {
		AssemblyConflict conflict = new AssemblyConflict(conflictCounter++);
		conflict.setTarget(target);
		conflict.setSolutions(possibleACtxs);
		conflict.setCorrespondingComponent(correspondingComponent);
		return conflict;
	}

	public ConnectionConflict createConnectionConflict(Xor<AssemblyRequiredRole, SystemProvidedRole> target,
			List<RepositoryComponent> possibleComponents) {
		ConnectionConflict conflict = new ConnectionConflict(conflictCounter++);
		conflict.setTarget(target);
		conflict.setSolutions(new ArrayList<>(possibleComponents));
		return conflict;
	}

}
