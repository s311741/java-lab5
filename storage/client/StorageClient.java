package storage.client;

import java.io.IOException;
import java.net.*;
import storage.*;

public class StorageClient {
	DatagramSocket socket;
	SocketAddress address;

	private static StorageClient instance = null;

	private StorageClient (SocketAddress addr) {
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			System.err.println("Cannot create socket");
			System.exit(1);
		}
		this.address = addr;

		// Try to connect to the server
	}

	public static StorageClient getClient () {
		if (instance == null) {
			System.err.println("Tried to access storage client before initialization");
			System.exit(1);
		}
		return instance;
	}

	public static void initialize (SocketAddress addr) {
		if (instance != null) {
			System.err.println("Tried to initialize storage client twice");
			System.exit(1);
		}
		instance = new StorageClient(addr);
	}

	public void shutdownServer () {
		this.sendRequest(new Request(Request.Type.SHUTDOWN));
	}

	private void sendRequest (Request request) {
		try {
			byte[] buffer = request.serialize();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.address);
			this.socket.send(packet);
		} catch (IOException e) {
			System.err.println("I/O exception while sending request:\n" + e.getMessage());
		}
	}
}
