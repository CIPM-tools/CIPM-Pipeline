package dmodel.pipeline.rexample.prime.generator.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dmodel.pipeline.rexample.prime.generator.IPrimeGenerator;

public class EratosthenesGeneratorImpl implements IPrimeGenerator {

	@Override
	public List<Integer> generatePrimes(int amount) {
		int currentUpperlimit = amount * 10;
		boolean[] res = new boolean[0];
		while (res.length < amount) {
			res = berechnePrimzahlen(currentUpperlimit);
			currentUpperlimit *= 10;
		}

		boolean[] fres = res;

		return IntStream.range(0, res.length).filter(i -> fres[i]).limit(amount).mapToObj(i -> i)
				.collect(Collectors.toList());
	}

	private boolean[] berechnePrimzahlen(int obergrenze) {
		boolean[] primzahlen = new boolean[obergrenze + 1];
		Arrays.fill(primzahlen, Boolean.TRUE);
		primzahlen[0] = false;
		primzahlen[1] = false;
		for (int i = 2; i <= obergrenze; i++) {
			int momentanerWert = i;
			if (primzahlen[momentanerWert]) {
				momentanerWert += i;
				while (momentanerWert <= obergrenze) {
					primzahlen[momentanerWert] = false;
					momentanerWert += i;
				}
			}
		}
		return primzahlen; // Feld zurückgeben
	}

}
