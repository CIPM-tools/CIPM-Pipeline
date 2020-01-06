package dmodel.pipeline.rexample.prime.ext.db;


public interface PrimeGeneratorDB {
    int getCurrentUpperLimit();

    int[] requestPrimes(int upperLimit);

    void storePrimes(int[] primes);
}

