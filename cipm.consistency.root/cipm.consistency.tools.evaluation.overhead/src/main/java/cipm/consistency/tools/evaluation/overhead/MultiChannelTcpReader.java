package cipm.consistency.tools.evaluation.overhead;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

import kieker.analysis.plugin.reader.tcp.util.AbstractTcpReader;
import kieker.analysis.plugin.reader.util.IRecordReceivedListener;
import lombok.extern.java.Log;

/**
 * This is an extension for {@link AbstractTcpReader} of Kieker which accepts
 * multiple connections at the same time.
 */
@Log
public class MultiChannelTcpReader implements Runnable {
	private static final int CONNECTION_CLOSED_BY_CLIENT = -1;

	private final int port;
	private final int bufferCapacity;
	private volatile boolean terminated;

	private ExecutorService executorService;
	private IRecordReceivedListener listener;

	/**
	 * Constructs a new TCP reader.
	 *
	 * @param port           on which to listen for requests
	 * @param bufferCapacity of the used read buffer
	 * @param logger         for notification to users and developers
	 */
	public MultiChannelTcpReader(final int port, final int bufferCapacity, IRecordReceivedListener listener,
			final ExecutorService clientProcessExecutorService) {
		super();
		this.port = port;
		this.bufferCapacity = bufferCapacity;
		this.executorService = clientProcessExecutorService;
		this.listener = listener;
	}

	@Override
	public final void run() {
		ServerSocketChannel serversocket = null;
		try {
			serversocket = ServerSocketChannel.open();
			serversocket.socket().bind(new InetSocketAddress(this.port));

			do {
				log.fine("Listening on port " + this.port);

				final SocketChannel socketChannel = serversocket.accept();
				asyncProcessSocketChannel(socketChannel);
			} while (!this.terminated);
		} catch (final IOException ex) {
			log.log(Level.WARNING, "Error while reading.", ex);
		} finally {
			if (null != serversocket) {
				try {
					serversocket.close();
				} catch (final IOException e) {
					log.log(Level.WARNING, "Failed to close TCP connection.", e);
				}
			}
		}
	}

	private void asyncProcessSocketChannel(SocketChannel socketChannel) {
		executorService.submit(new SingleChannelReader(socketChannel));
	}

	/**
	 * Gracefully terminates this TCP reader.
	 */
	public void terminate() {
		this.terminated = true;
	}

	public int getPort() {
		return this.port;
	}

	private class SingleChannelReader implements Runnable {
		private SocketChannel channel;
		private SingleSocketRecordReader recordReader;

		private SingleChannelReader(SocketChannel socket) {
			recordReader = new SingleSocketRecordReader(listener);
			channel = socket;
		}

		@Override
		public void run() {
			try {
				final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferCapacity);
				while ((channel.read(buffer) != CONNECTION_CLOSED_BY_CLIENT) && !terminated) {
					this.process(buffer);
				}
			} catch (IOException e) {
				log.log(Level.WARNING, e.getMessage(), e);
			} finally {
				try {
					channel.close();
				} catch (IOException e) {
					log.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}

		private void process(final ByteBuffer buffer) {
			buffer.flip();
			try {
				while (buffer.hasRemaining()) {
					buffer.mark();
					final boolean success = this.onBufferReceived(buffer);
					if (!success) {
						buffer.reset();
						buffer.compact();
						return;
					}
				}
				buffer.clear();
			} catch (final BufferUnderflowException ex) {
				log.log(Level.WARNING, "Unexpected buffer underflow. Resetting and compacting buffer.", ex);
				buffer.reset();
				buffer.compact();
			}
		}

		/**
		 * @param buffer to be read from
		 * @return
		 *         <ul>
		 *         <li><code>true</code> when there were enough bytes to perform the
		 *         read operation
		 *         <li><code>false</code> otherwise. In this case, the buffer is reset,
		 *         compacted, and filled with new content.
		 */
		protected boolean onBufferReceived(final ByteBuffer buffer) {
			return recordReader.onBufferReceived(buffer);
		}
	}
}