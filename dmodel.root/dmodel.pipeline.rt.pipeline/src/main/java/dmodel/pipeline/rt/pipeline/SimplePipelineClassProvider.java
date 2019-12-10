package dmodel.pipeline.rt.pipeline;

public class SimplePipelineClassProvider<B> implements IPipelineClassProvider<B> {

	@Override
	public boolean contains(Class<? extends AbstractIterativePipelinePart<B>> transformationClass) {
		return false;
	}

	@Override
	public AbstractIterativePipelinePart<B> provide(
			Class<? extends AbstractIterativePipelinePart<B>> transformationClass) {
		// never called
		return null;
	}

}
