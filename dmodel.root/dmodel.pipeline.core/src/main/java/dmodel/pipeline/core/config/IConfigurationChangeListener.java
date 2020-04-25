package dmodel.pipeline.core.config;

/**
 * Interface which describes a listener which should be informed when a certain
 * configuration changed.
 * 
 * @author David Monschein
 *
 * @param <T> the type of configuration that changed.
 */
@FunctionalInterface
public interface IConfigurationChangeListener<T> {

	/**
	 * Callback when the configuration changed.
	 * 
	 * @param target the changed configuration
	 */
	public void configurationChanged(T target);
}
