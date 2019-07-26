package dmodel.pipeline.monitoring.controller;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import org.apache.commons.codec.digest.DigestUtils;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;


public class HostNameFactory {
    private static Optional<String> CURRENT_HOSTID = Optional.empty();

    private static Optional<String> CURRENT_HOSTNAME = Optional.empty();

    public static final synchronized String generateHostName() {
        if (!(HostNameFactory.CURRENT_HOSTNAME.isPresent())) {
            // build it
            HostNameFactory.buildHostname();
        }
        return HostNameFactory.CURRENT_HOSTNAME.get();
    }

    private static void buildHostname() {
        try {
            HostNameFactory.CURRENT_HOSTNAME = Optional.of(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            HostNameFactory.CURRENT_HOSTNAME = Optional.of("<not set>");
        }
    }

    public static final synchronized String generateHostId() {
        if (!(HostNameFactory.CURRENT_HOSTID.isPresent())) {
            // build it
            HostNameFactory.buildHostId();
        }
        return HostNameFactory.CURRENT_HOSTID.get();
    }

    private static void buildHostId() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hw = si.getHardware();
        // Try well known MAC addresses
        NetworkIF[] nifs = hw.getNetworkIFs();
        String macAddrConcat = "";
        for (NetworkIF nif : nifs) {
            macAddrConcat += nif.getMacaddr();
        }
        HostNameFactory.CURRENT_HOSTID = Optional.of(DigestUtils.md5Hex(macAddrConcat));
    }
}

