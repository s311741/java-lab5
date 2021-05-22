package storage.server;

import java.io.IOException;
import java.net.*;
import storage.Request;

public class Main {
	public static void main (String[] args) {
		if (args.length < 1) {
			System.err.println("Must specify port");
			System.exit(1);
		}
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("Invalid port specified");
			System.exit(1);
		}

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.err.println("!!!");
			System.exit(1);
		}

		byte[] buffer = new byte[Request.MAX_PACKET_LENGTH];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		while (true) {
			System.err.println("Waiting for command...");

			Request request = null;
			try {
				socket.receive(packet);
				int length = packet.getLength();
				request = Request.deserialize(buffer, length);
			} catch (IOException e) {
				System.err.println("I/O exception while waiting for command:\n" + e.getMessage());
				continue;
			}

			switch (request.type) {
			case SHUTDOWN:
				System.exit(0);
			}
		}
	}
}