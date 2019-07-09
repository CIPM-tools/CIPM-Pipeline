package dmodel.pipeline.rexample.prime.generator.impl;


import dmodel.pipeline.rexample.prime.generator.IPrimeGenerator;
import java.util.ArrayList;
import java.util.List;


public class DumbGeneratorImpl implements IPrimeGenerator {
    // TEST
    @Override
    public List<Integer> generatePrimes(int amount) {
        // TEST
        List<Integer> result = new ArrayList<Integer>();
        int k = 2;
        while ((result.size()) < amount) {
            boolean is = true;
            for (int j = 2; j < (Math.sqrt(k)); j++) {
                if ((k % j) == 0) {
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

