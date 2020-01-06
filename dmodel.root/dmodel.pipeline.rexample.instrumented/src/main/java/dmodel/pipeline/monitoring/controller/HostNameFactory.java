package dmodel.pipeline.monitoring.controller;


import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;
import lombok.extern.java.Log;
import org.apache.commons.codec.digest.DigestUtils;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;


@Log
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
        // TODO check if the id is valid, otherwise use processor
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hw = si.getHardware();
        // Try well known MAC addresses
        try {
            HostNameFactory.CURRENT_HOSTID = Optional.of(DigestUtils.md5Hex(HostNameFactory.getMACAddress()));
        } catch (SocketException | UnknownHostException e) {
            log.warning((("Failed to calculate the Host ID (" + (e.getMessage())) + ")."));
            HostNameFactory.CURRENT_HOSTID = Optional.empty();
        }
    }

    private static byte[] getMACAddress() throws SocketException, UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
        return networkInterface.getHardwareAddress();
    }
}

