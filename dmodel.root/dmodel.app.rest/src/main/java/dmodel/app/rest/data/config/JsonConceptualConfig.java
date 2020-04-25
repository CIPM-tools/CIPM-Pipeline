package dmodel.app.rest.data.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.tuple.Pair;
import org.pcm.headless.api.client.PCMHeadlessClient;

import dmodel.base.core.config.ConfigurationContainer;
import lombok.Data;

@Data
public class JsonConceptualConfig {
	private static final long SIMULATOR_TIMEOUT = 2500;

	public static JsonConceptualConfig from(ConfigurationContainer config) {
		JsonConceptualConfig output = new JsonConceptualConfig();

		output.setSimulatorUrl(config.getVfl().getUrl() + ":" + config.getVfl().getPort() + "/");
		output.setMeasurements(config.getVfl().getMeasurements());
		output.setSimulationTime(config.getVfl().getSimulationTime());
		output.setValidationSplit(config.getVfl().getValidationShare());

		output.setSlidingWindowSize(config.getEntry().getSlidingWindowSize());
		output.setSlidingWindowTrigger(config.getEntry().getSlidingWindowTrigger());

		return output;
	}

	private long slidingWindowSize;
	private long slidingWindowTrigger;

	private String simulatorUrl;
	private long measurements;
	private long simulationTime;
	private float validationSplit;

	public boolean applyTo(ConfigurationContainer config) {
		if (this.validate().isValid()) {
			config.getEntry().setSlidingWindowSize(slidingWindowSize);
			config.getEntry().setSlidingWindowTrigger(slidingWindowTrigger);

			config.getVfl().setMeasurements(measurements);
			config.getVfl().setSimulationTime(simulationTime);
			config.getVfl().setValidationShare(validationSplit);

			Pair<String, Integer> urlSplit = hostPortSplit(simulatorUrl);
			if (urlSplit != null) {
				config.getVfl().setUrl(urlSplit.getLeft());
				config.getVfl().setPort(urlSplit.getRight());
			} else {
				return false;
			}

			return true;
		}
		return false;
	}

	public JsonConceptualValidationResponse validate() {
		JsonConceptualValidationResponse result = new JsonConceptualValidationResponse();

		result.setSlidingWindowSizeValid(slidingWindowSize > 0);
		result.setSlidingWindowTriggerValid(slidingWindowTrigger > 0 && slidingWindowTrigger <= slidingWindowSize);

		result.setSimulatorValid(simulatorUrlValid(simulatorUrl));
		result.setMeasurementsValid(measurements > 0);
		result.setSimulationTimeValid(simulationTime > 0);
		result.setValidationSplitValid(validationSplit >= 0.0 && validationSplit <= 0.5);

		return result;
	}

	private boolean simulatorUrlValid(String url) {
		PCMHeadlessClient client = new PCMHeadlessClient(url);
		return client.isReachable(SIMULATOR_TIMEOUT);
	}

	private Pair<String, Integer> hostPortSplit(String url) {
		try {
			// WORKAROUND: add any scheme to make the resulting URI valid.
			URI uri = new URI(url); // may throw URISyntaxException
			String host = uri.getHost();
			int port = uri.getPort();

			return Pair.of(host, port);

		} catch (URISyntaxException ex) {
			// validation failed
			return null;
		}
	}

}
