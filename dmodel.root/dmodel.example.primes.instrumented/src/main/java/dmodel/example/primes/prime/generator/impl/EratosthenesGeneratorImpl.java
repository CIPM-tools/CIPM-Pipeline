package dmodel.example.primes.prime.generator.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import dmodel.designtime.monitoring.util.ManualMapping;
import dmodel.example.primes.prime.generator.IPrimeGenerator;
import dmodel.designtime.monitoring.controller.ThreadMonitoringController;
import dmodel.designtime.monitoring.controller.ServiceParameters;
import java.util.concurrent.atomic.AtomicInteger;

public class EratosthenesGeneratorImpl implements IPrimeGenerator {

    @Override
    @ManualMapping("_PlFlUJYHEempGaXtj6ezAw")
    public List<Integer> generatePrimes(int amount) {
        ThreadMonitoringController threadMonitoringController = ThreadMonitoringController.getInstance();
        ServiceParameters monitoringServiceParameters = new ServiceParameters();
        monitoringServiceParameters.addValue("amount", amount);
        threadMonitoringController.enterService("_PlFlUJYHEempGaXtj6ezAw", this, monitoringServiceParameters);
        try {
            int currentUpperlimit = amount * 10;
            boolean[] res = new boolean[0];
            while (res.length < amount) {
                res = berechnePrimzahlen(currentUpperlimit);
                currentUpperlimit *= 10;
            }
            boolean[] fres = res;
            List<Integer> results = new ArrayList<Integer>();
            for (int k = 0; k < res.length; k++) {
                if (results.size() == amount) {
                    break;
                } else {
                    if (fres[k]) {
                        results.add(k);
                    }
                }
            }
            return results;
        } finally {
            threadMonitoringController.exitService("_PlFlUJYHEempGaXtj6ezAw");
        }
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
        // Feld zurückgeben
        return primzahlen;
    }
}
