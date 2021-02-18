package cipm.cosnsistency.tools.evaluation.load.rest;

import java.io.IOException;

import org.apache.jmeter.engine.JMeterEngineException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cipm.cosnsistency.tools.evaluation.load.jmeter.JMeterController;

@RestController("/")
public class ConfigurationRestInterface implements InitializingBean {
	@Autowired
	private JMeterController jmeterController;

	@Value("${loadFile:null}")
	private String loadFile;

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
		if (loadFile != null) {
			this.jmeterController.startTest(loadFile);
		}
	}

}
