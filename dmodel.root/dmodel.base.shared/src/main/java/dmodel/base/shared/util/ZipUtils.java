package dmodel.base.shared.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utilities for the interaction with zip-compressed files.
 * 
 * @author David Monschein
 *
 */
public class ZipUtils {

	/**
	 * Unzips a stream of zip-compressed data into a target directory.
	 * 
	 * @param source zip stream
	 * @param target target directory
	 * @throws IOException if the extraction failed
	 */
	public static void unzip(InputStream source, File target) throws IOException {
		final ZipInputStream zipStream = new ZipInputStream(source);
		ZipEntry nextEntry;
		while ((nextEntry = zipStream.getNextEntry()) != null) {
			final String name = nextEntry.getName();
			// only extract files
			if (!name.endsWith("/")) {
				final File nextFile = new File(target, name);

				// create directories
				final File parent = nextFile.getParentFile();
				if (parent != null) {
					parent.mkdirs();
				}

				// write file
				try (OutputStream targetStream = new FileOutputStream(nextFile)) {
					copy(zipStream, targetStream);
				}
			}
		}
	}

	private static void copy(final InputStream source, final OutputStream target) throws IOException {
		final int bufferSize = 4 * 1024;
		final byte[] buffer = new byte[bufferSize];

		int nextCount;
		while ((nextCount = source.read(buffer)) >= 0) {
			target.write(buffer, 0, nextCount);
		}
	}
}