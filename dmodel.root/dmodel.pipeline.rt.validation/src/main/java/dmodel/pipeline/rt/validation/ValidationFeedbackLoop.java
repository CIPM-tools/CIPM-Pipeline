package dmodel.pipeline.rt.validation;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.pcm.headless.api.client.PCMHeadlessClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.monitoring.MonitoringDataContainer;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import lombok.extern.java.Log;

@Component
@Log
public class ValidationFeedbackLoop implements IValidationProcessor, InitializingBean {
	private static final long TIMEOUT_VFL = 5000;

	@Autowired
	private DModelConfigurationContainer config;

	private PCMHeadlessClient client;

	private ScheduledExecutorService executorService;

	public ValidationFeedbackLoop() {
	}

	@Override
	public void process(InMemoryPCM instance, MonitoringDataContainer monitoringData,
			List<IValidationProcessor> processors) {
		// TODO
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		config.getVfl().registerChangeListener(v -> {
			updatePCMHeadlessClient();
		});
		updatePCMHeadlessClient();

		executorService = Executors.newScheduledThreadPool(config.getVfl().getConcurrentSimulations());
	}

	private void updatePCMHeadlessClient() {
		client = new PCMHeadlessClient(buildBackendUrl(config.getVfl().getUrl(), config.getVfl().getPort()));

		// check if it is available
		if (client.isReachable(TIMEOUT_VFL)) {
			log.info("Backend for VFL is reachable.");
		} else {
			log.warning("Backend for VFL seems to be offline.");
		}
	}

	private String buildBackendUrl(String url, int port) {
		StringBuilder res = new StringBuilder();
		if (!url.startsWith("http://")) {
			res.append("http://");
		}
		res.append(url);
		res.append(":");
		res.append(port);
		res.append("/");

		return res.toString();
	}
}