package dmodel.pipeline.monitoring.controller;

import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

public class HostNameFactory {

	private static Optional<String> CURRENT_HOSTNAME = Optional.empty();

	public static synchronized final String generateHostname() {
		if (!CURRENT_HOSTNAME.isPresent()) {
			// build it
			buildHostname();
		}
		return CURRENT_HOSTNAME.get();
	}

	private static void buildHostname() {
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hw = si.getHardware();

		// Try well known MAC addresses
		NetworkIF[] nifs = hw.getNetworkIFs();
		String macAddrConcat = "";
		for (NetworkIF nif : nifs) {
			macAddrConcat += nif.getMacaddr();
		}
		CURRENT_HOSTNAME = Optional.of(DigestUtils.md5Hex(macAddrConcat));
	}

}
