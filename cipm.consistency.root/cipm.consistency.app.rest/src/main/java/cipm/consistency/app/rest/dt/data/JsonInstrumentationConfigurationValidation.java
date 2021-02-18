package cipm.consistency.app.rest.dt.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import lombok.Data;

@Data
public class JsonInstrumentationConfigurationValidation {
	private static final int URL_TIMEOUT = 2000;

	private boolean pathValid;
	private boolean urlValid;
	private boolean logarithmicValid;

	public boolean isValid() {
		return pathValid && urlValid && logarithmicValid;
	}

	public static JsonInstrumentationConfigurationValidation from(JsonInstrumentationConfiguration config) {
		JsonInstrumentationConfigurationValidation resp = new JsonInstrumentationConfigurationValidation();

		resp.setPathValid(
				config.getMetadata().getOutputPath() != null && config.getMetadata().getOutputPath().length() > 0
						&& isValidPath(config.getMetadata().getOutputPath()));
		resp.setLogarithmicValid(!config.getMetadata().isLogarithmicScaling()
				|| config.getMetadata().getLogarithmicRecoveryInterval() > 0);

		String buildUrl = "http://" + config.getMetadata().getHostName() + ":" + config.getMetadata().getRestPort()
				+ config.getMetadata().getInmRestPath();
		resp.setUrlValid(pingURL(buildUrl, URL_TIMEOUT));

		return resp;
	}

	private static boolean isValidPath(String path) {
		try {
			Paths.get(path);
		} catch (InvalidPathException | NullPointerException ex) {
			return false;
		}
		return true;
	}

	private static boolean pingURL(String url, int timeout) {
		url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			return (200 <= responseCode && responseCode <= 399);
		} catch (IOException exception) {
			return false;
		}
	}

}
