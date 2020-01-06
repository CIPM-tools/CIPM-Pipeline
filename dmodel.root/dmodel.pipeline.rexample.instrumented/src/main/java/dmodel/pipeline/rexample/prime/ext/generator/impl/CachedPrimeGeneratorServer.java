package dmodel.pipeline.rexample.prime.ext.generator.impl;


import dmodel.pipeline.rexample.prime.ext.db.PrimeGeneratorDB;
import dmodel.pipeline.rexample.prime.ext.generator.PrimeGeneratorServer;
import dmodel.pipeline.rexample.prime.ext.service.PrimeGeneratorService;


public class CachedPrimeGeneratorServer implements PrimeGeneratorServer {
    private PrimeGeneratorService generatorService;

    private PrimeGeneratorDB database;

    @Override
    public int[] providePrimes(int upperLimit) {
        if ((database.getCurrentUpperLimit()) < upperLimit) {
            int[] primes = generatorService.calculatePrimes(database.getCurrentUpperLimit(), upperLimit);
            database.storePrimes(primes);
        }
        return database.requestPrimes(upperLimit);
    }
}

