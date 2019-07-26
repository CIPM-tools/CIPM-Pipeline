package dmodel.pipeline.rexample.prime.manager;


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
        return generator.generatePrimes(amount);
    }
}
