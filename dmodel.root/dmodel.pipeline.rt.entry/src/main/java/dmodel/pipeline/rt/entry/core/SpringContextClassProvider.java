package dmodel.pipeline.rt.entry.core;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.IPipelineClassProvider;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;

public class SpringContextClassProvider implements IPipelineClassProvider<RuntimePipelineBlackboard> {

	private ApplicationContext ctx;

	public SpringContextClassProvider(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public boolean contains(
			Class<? extends AbstractIterativePipelinePart<RuntimePipelineBlackboard>> transformationClass) {
		try {
			ctx.getBean(transformationClass);
		} catch (NoSuchBeanDefinitionException e) {
			return false;
		}
		return true;
	}

	@Override
	public AbstractIterativePipelinePart<RuntimePipelineBlackboard> provide(
			Class<? extends AbstractIterativePipelinePart<RuntimePipelineBlackboard>> transformationClass) {
		return ctx.getBean(transformationClass);
	}

}
