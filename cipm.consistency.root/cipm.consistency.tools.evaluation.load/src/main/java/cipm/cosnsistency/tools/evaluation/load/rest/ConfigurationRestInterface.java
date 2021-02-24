package cipm.cosnsistency.tools.evaluation.load.rest;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.jmeter.engine.JMeterEngineException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import cipm.cosnsistency.tools.evaluation.load.jmeter.JMeterController;
import cipm.cosnsistency.tools.evaluation.load.util.DefaultHttpClient;

@RestController("/")
public class ConfigurationRestInterface implements InitializingBean {
	@Autowired
	private JMeterController jmeterController;

	@Value("${loadFile:null}")
	private String loadFile;
	
	@Value("${checkUrl:null}")
	private String checkUrl;
	
	private DefaultHttpClient http;
	private ScheduledExecutorService checkReachabilityScheduler;
	
	public ConfigurationRestInterface() {
		this.http = new DefaultHttpClient(2000);
		this.checkReachabilityScheduler = Executors.newSingleThreadScheduledExecutor();
	}

	@GetMapping("stop")
	public String stopLoad() {
		this.jmeterController.stopTest(true);
		return "true";
	}

	@GetMapping("start")
	public String startLoad(@RequestParam("file") String file) {
		try {
			return String.valueOf(jmeterController.startTest(file));
		} catch (IOException | JMeterEngineException e) {
			return "false";
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (loadFile != null && checkUrl == null) {
			this.jmeterController.startTest(loadFile);
		} else if (loadFile != null && checkUrl != null) {
			checkReachabilityScheduler.schedule(() -> checkReachability(), 20, TimeUnit.SECONDS);
		}
	}
	
	private void checkReachability() {
		if (this.http.isReachable(checkUrl)) {
			try {
				this.jmeterController.startTest(loadFile);
			} catch (IOException | JMeterEngineException e) {
				checkReachabilityScheduler.schedule(() -> checkReachability(), 20, TimeUnit.SECONDS);
			}
		} else {
			checkReachabilityScheduler.schedule(() -> checkReachability(), 20, TimeUnit.SECONDS);
		}
	}

}
