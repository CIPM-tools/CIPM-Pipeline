package cipm.consistency.runtime.pipeline.entry.core;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import cipm.consistency.runtime.pipeline.AbstractIterativePipelinePart;
import cipm.consistency.runtime.pipeline.IPipelineClassProvider;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;

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
