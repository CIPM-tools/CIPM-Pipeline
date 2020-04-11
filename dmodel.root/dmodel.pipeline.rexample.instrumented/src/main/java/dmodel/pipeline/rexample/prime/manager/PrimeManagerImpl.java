package dmodel.pipeline.rexample.prime.manager;

import java.util.List;
import dmodel.pipeline.monitoring.util.ManualMapping;
import dmodel.pipeline.rexample.prime.generator.IPrimeGenerator;
import dmodel.pipeline.monitoring.controller.ThreadMonitoringController;
import dmodel.pipeline.monitoring.controller.ServiceParameters;
import java.util.concurrent.atomic.AtomicInteger;

public class PrimeManagerImpl implements IPrimeManager {

    private IPrimeGenerator generator;

    public IPrimeGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(IPrimeGenerator generator) {
        this.generator = generator;
    }

    @Override
    @ManualMapping("_2RDcwKMhEemdKJpkeqfUZw")
    public List<Integer> generatePrimes(int amount) {
        ThreadMonitoringController threadMonitoringController = ThreadMonitoringController.getInstance();
        ServiceParameters monitoringServiceParameters = new ServiceParameters();
        monitoringServiceParameters.addValue("amount", amount);
        threadMonitoringController.enterService("_2RDcwKMhEemdKJpkeqfUZw", this, monitoringServiceParameters);
        try {
            threadMonitoringController.setExternalCallId("_TvvgkB3-EeqqfJt2XRLLdA");
            // @CALL EXTERNAL_CALL{_TvvgkB3-EeqqfJt2XRLLdA}
            return generator.generatePrimes(amount);
        } finally {
            threadMonitoringController.exitService("_2RDcwKMhEemdKJpkeqfUZw");
        }
    }
}
