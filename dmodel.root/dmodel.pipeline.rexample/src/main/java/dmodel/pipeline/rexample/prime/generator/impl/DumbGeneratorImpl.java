package dmodel.pipeline.rexample.prime.generator.impl;

import java.util.ArrayList;
import java.util.List;

import dmodel.pipeline.monitoring.util.ManualMapping;
import dmodel.pipeline.rexample.prime.generator.IPrimeGenerator;

public class DumbGeneratorImpl implements IPrimeGenerator {

	@Override
	@ManualMapping("_2nvWUKKQEem6I6QlOar_-g")
	public List<Integer> generatePrimes(int amount) {
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
		return result;
	}

}
