package cipm.consistency.tools.evaluation.load.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DefaultHttpClient {

	private OkHttpClient client;

	public DefaultHttpClient(long timeout) {
		client = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.MILLISECONDS)
				.writeTimeout(timeout, TimeUnit.MILLISECONDS).readTimeout(timeout, TimeUnit.MILLISECONDS).build();
	}

	public boolean isReachable(String url) {
		Request request = new Request.Builder().url(url).build();

		boolean reach;
		try (Response response = client.newCall(request).execute()) {
			reach = response.isSuccessful();
		} catch (IOException e) {
			reach = false;
		}
		return reach;
	}

	public String getRequest(String url, Map<String, String> parameter) {
		HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
		if (parameter != null && parameter.size() > 0) {
			for (Map.Entry<String, String> param : parameter.entrySet()) {
				httpBuilder.addQueryParameter(param.getKey(), param.getValue());
			}
		}

		Request request = new Request.Builder().url(httpBuilder.build()).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		} catch (Exception e) {
			return null;
		}
	}

}
