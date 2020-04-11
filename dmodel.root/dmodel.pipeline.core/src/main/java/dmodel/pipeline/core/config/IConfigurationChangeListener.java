package dmodel.pipeline.core.config;

@FunctionalInterface
public interface IConfigurationChangeListener<T> {
	public void configurationChanged(T target);
}
