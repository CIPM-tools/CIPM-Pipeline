package dmodel.example.primes.prime.generator.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import dmodel.designtime.monitoring.util.ManualMapping;
import dmodel.example.primes.prime.generator.IPrimeGenerator;

public class DumbGeneratorImpl implements IPrimeGenerator {

	@Override
	@ManualMapping("_2nvWUKKQEem6I6QlOar_-g")
	public List<Integer> generatePrimes(int amount) {
		// @START INTERNAL_ACTION{_jg0MoB3-EeqqfJt2XRLLdA}
		List<Integer> result = new ArrayList<Integer>();
		int k = 2;
		while (result.size() < amount) {
			boolean is = true;
			for (int j = 2; j < Math.sqrt(k); j++) {
				if (k % j == 0) {
					is = false;
					break;
				}
			}
			if (is) {
				result.add(k);
			}

			k++;
		}

		IntStream.of(50).forEach(test -> {
			System.out.println("TEST");
		});

		// @END INTERNAL_ACTION{_jg0MoB3-EeqqfJt2XRLLdA}

		return result;
	}

}
