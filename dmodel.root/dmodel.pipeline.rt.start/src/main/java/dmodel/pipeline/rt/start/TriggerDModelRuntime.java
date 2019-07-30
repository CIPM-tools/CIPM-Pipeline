package dmodel.pipeline.rt.start;

import org.springframework.boot.SpringApplication;

public class TriggerDModelRuntime {

	public static void main(String[] args) {
		System.setProperty("dmodel_home", args[0]);

		SpringApplication.run(DModelRuntimeStarter.class, args);
	}

}
