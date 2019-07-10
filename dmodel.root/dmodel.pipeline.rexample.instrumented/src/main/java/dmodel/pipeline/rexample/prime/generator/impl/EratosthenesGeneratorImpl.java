package dmodel.pipeline.rexample.prime.generator.impl;


import dmodel.pipeline.monitoring.controller.ServiceParameters;
import dmodel.pipeline.monitoring.controller.ThreadMonitoringController;
import dmodel.pipeline.rexample.prime.generator.IPrimeGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EratosthenesGeneratorImpl implements IPrimeGenerator {
    @Override
    public List<Integer> generatePrimes(int amount) {
        ThreadMonitoringController threadMonitoringController = dmodel.pipeline.monitoring.controller.ThreadMonitoringController.getInstance();
        try  {
            ServiceParameters serviceParametersMonitoring = new ServiceParameters();
            serviceParametersMonitoring.addValue("amount", amount);
            threadMonitoringController.enterService("_PlFlUJYHEempGaXtj6ezAw", serviceParametersMonitoring);
            int currentUpperlimit = amount * 10;
            boolean[] res = new boolean[0];
            while ((res.length) < amount) {
                res = berechnePrimzahlen(currentUpperlimit);
                currentUpperlimit *= 10;
            } 
            boolean[] fres = res;
            List<Integer> results = new ArrayList<Integer>();
            for (int k = 0; k < (res.length); k++) {
                if ((results.size()) == amount) {
                    break;
                } else {
                    if (fres[k]) {
                        results.add(k);
                    }
                }
            }
            return results;
        } finally {
            threadMonitoringController.exitService();
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
        return primzahlen;// Feld zurückgeben

    }
}

