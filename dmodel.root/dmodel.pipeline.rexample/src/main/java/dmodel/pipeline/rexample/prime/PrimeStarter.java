package dmodel.pipeline.rexample.prime;

import dmodel.pipeline.rexample.prime.generator.impl.DumbGeneratorImpl;
import dmodel.pipeline.rexample.prime.manager.PrimeManagerImpl;

public class PrimeStarter {

	public static void main(String[] args) {
		PrimeManagerImpl primeManager = new PrimeManagerImpl();
		// primeManager.setGenerator(new EratosthenesGeneratorImpl());
		primeManager.setGenerator(new DumbGeneratorImpl());

		for (int i = 500; i < 1000; i++) {
			for (int k : primeManager.generatePrimes(i)) {
				System.out.println(k);
			}
		}

		System.out.println("Still running.");

	}

}
