package dmodel.pipeline.dt.system.pcm;

import java.util.List;

import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RequiredRole;

public interface IConflictResolver {

	public ProvidedRole resolveConflict(RequiredRole required, List<ProvidedRole> poss);

}
