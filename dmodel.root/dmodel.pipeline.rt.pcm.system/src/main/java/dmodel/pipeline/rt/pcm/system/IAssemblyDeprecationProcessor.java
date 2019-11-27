package dmodel.pipeline.rt.pcm.system;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;

public interface IAssemblyDeprecationProcessor {

	public boolean shouldDelete(AssemblyContext ctx);

	public void iterationFinished();

}
