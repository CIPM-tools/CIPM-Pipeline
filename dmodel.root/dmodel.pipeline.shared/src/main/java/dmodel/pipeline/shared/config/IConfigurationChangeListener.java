package dmodel.pipeline.shared.config;

@FunctionalInterface
public interface IConfigurationChangeListener<T> {
	public void configurationChanged(T target);
}
