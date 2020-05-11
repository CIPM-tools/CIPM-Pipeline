package dmodel.designtime.monitoring.controller;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import lombok.extern.java.Log;

@Log
public class HostNameFactory {

	private static Optional<String> CURRENT_HOSTID = Optional.empty();
	private static Optional<String> CURRENT_HOSTNAME = Optional.empty();

	public static synchronized final String generateHostName() {
		if (!CURRENT_HOSTNAME.isPresent()) {
			// build it
			buildHostname();
		}
		return CURRENT_HOSTNAME.get();
	}

	private static void buildHostname() {
		try {
			CURRENT_HOSTNAME = Optional.of(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			CURRENT_HOSTNAME = Optional.of("<not set>");
		}
	}

	public static synchronized final String generateHostId() {
		if (!CURRENT_HOSTID.isPresent()) {
			// build it
			buildHostId();
		}
		return CURRENT_HOSTID.get();
	}

	private static void buildHostId() {
		// Use well known MAC addresses
		try {
			CURRENT_HOSTID = Optional.of(DigestUtils.md5Hex(getMACAddress()));
		} catch (SocketException | UnknownHostException e) {
			log.warning("Failed to calculate the Host ID (" + e.getMessage() + ").");
			CURRENT_HOSTID = Optional.empty();
		}
	}

	private static byte[] getMACAddress() throws SocketException, UnknownHostException {
		InetAddress address = InetAddress.getLocalHost();
		NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);

		return networkInterface.getHardwareAddress();
	}

}
