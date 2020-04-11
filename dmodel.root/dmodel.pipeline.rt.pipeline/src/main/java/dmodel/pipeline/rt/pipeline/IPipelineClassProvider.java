package dmodel.pipeline.rt.pipeline;

import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;

public interface IPipelineClassProvider<B extends RuntimePipelineBlackboard> {

	public boolean contains(Class<? extends AbstractIterativePipelinePart<B>> transformationClass);

	public AbstractIterativePipelinePart<B> provide(
			Class<? extends AbstractIterativePipelinePart<B>> transformationClass);

}
