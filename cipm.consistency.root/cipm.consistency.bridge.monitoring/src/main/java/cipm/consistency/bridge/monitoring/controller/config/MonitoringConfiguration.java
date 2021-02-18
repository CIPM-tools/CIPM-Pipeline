package cipm.consistency.bridge.monitoring.controller.config;

public interface MonitoringConfiguration {

	String SERVER_HOSTNAME = "localhost";
	int SERVER_REST_PORT = 8090;
	String SERVER_REST_INM_URL = "/runtime/pipeline/imm";

	boolean LOGARITHMIC_SCALING = false;
	long LOGARITHMIC_SCALING_INTERVAL = 1000; // recovery interval in ms

}
