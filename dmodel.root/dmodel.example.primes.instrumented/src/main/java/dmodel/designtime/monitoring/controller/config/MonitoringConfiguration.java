package dmodel.designtime.monitoring.controller.config;

public interface MonitoringConfiguration {

    String SERVER_HOSTNAME = "localhost";

    String SERVER_REST_INM_URL = "/runtime/pipeline/imm";

    int SERVER_REST_PORT = 8090;

    boolean LOGARITHMIC_SCALING = true;

    long LOGARITHMIC_SCALING_INTERVAL = 1000;
}
