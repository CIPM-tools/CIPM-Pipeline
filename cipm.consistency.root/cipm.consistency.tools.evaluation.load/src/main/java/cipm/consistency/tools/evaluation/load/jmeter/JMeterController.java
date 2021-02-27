package cipm.consistency.tools.evaluation.load.jmeter;

import java.io.File;
import java.io.IOException;

import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JMeterController implements InitializingBean {
	@Value("${jmeterPath}")
	private String jmeterPath;

	private StandardJMeterEngine engine;
	private File testsFolder = new File("jmxTests");

	public boolean startTest(String filename) throws IOException, JMeterEngineException {
		// Load existing .jmx Test Plan
		File in = new File(testsFolder, filename);
		if (!in.exists()) {
			return false;
		}
		HashTree testPlanTree = SaveService.loadTree(in);

		// Run JMeter Test
		engine.configure(testPlanTree);
		engine.runTest();
		return true;
	}

	public void stopTest(boolean now) {
		engine.stopTest(now);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		engine = new StandardJMeterEngine();

		// Jmeter base path
		File jmeterBase = new File(jmeterPath);

		// Initialize Properties, logging, locale, etc.
		JMeterUtils.loadJMeterProperties(new File(jmeterBase, "bin/jmeter.properties").getAbsolutePath());
		JMeterUtils.setJMeterHome(jmeterBase.getAbsolutePath());
		JMeterUtils.initLocale();

		// Initialize JMeter SaveService
		SaveService.loadProperties();
	}

}
