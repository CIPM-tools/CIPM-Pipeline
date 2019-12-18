package dmodel.pipeline.shared.pcm.util.deprecation;

import de.uka.ipd.sdq.identifier.Identifier;

public interface IDeprecationProcessor {

	public boolean shouldDelete(Identifier el);

	public boolean isCurrentlyDeprecated(Identifier el);

	public void iterationFinished();

}
