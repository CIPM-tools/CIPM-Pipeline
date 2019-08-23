package dmodel.pipeline.rexample.prime.manager;


import dmodel.pipeline.monitoring.controller.ServiceParameters;
import dmodel.pipeline.monitoring.controller.ThreadMonitoringController;
import dmodel.pipeline.rexample.prime.generator.IPrimeGenerator;
import java.util.List;


public class PrimeManagerImpl implements IPrimeManager {
    private IPrimeGenerator generator;

    public IPrimeGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(IPrimeGenerator generator) {
        this.generator = generator;
    }

    @Override
    public List<Integer> generatePrimes(int amount) {
        ThreadMonitoringController threadMonitoringController = dmodel.pipeline.monitoring.controller.ThreadMonitoringController.getInstance();
        try  {
            ServiceParameters serviceParametersMonitoring = new ServiceParameters();
            serviceParametersMonitoring.addValue("amount", amount);
            threadMonitoringController.enterService("_2RDcwKMhEemdKJpkeqfUZw", this, serviceParametersMonitoring);
            return generator.generatePrimes(amount);
        } finally {
            threadMonitoringController.exitService();
        }
    }
}

