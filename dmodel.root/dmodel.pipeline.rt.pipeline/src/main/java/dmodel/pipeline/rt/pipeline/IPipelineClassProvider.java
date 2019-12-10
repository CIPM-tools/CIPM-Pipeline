package dmodel.pipeline.rt.pipeline;

public interface IPipelineClassProvider<B> {

	public boolean contains(Class<? extends AbstractIterativePipelinePart<B>> transformationClass);

	public AbstractIterativePipelinePart<B> provide(
			Class<? extends AbstractIterativePipelinePart<B>> transformationClass);

}
