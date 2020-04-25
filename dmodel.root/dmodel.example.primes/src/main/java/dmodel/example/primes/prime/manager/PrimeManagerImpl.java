package dmodel.example.primes.prime.manager;

import java.util.List;

import dmodel.designtime.monitoring.util.ManualMapping;
import dmodel.example.primes.prime.generator.IPrimeGenerator;

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
		// @CALL EXTERNAL_CALL{_TvvgkB3-EeqqfJt2XRLLdA}
		return generator.generatePrimes(amount);
	}

}
