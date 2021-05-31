package storage.server;

import java.io.IOException;
import java.net.*;
import storage.*;
import storage.cmd.*;

public class Main {
	private static boolean mustShutdown = false;
	public static void scheduleShutdown () {
		mustShutdown = true;
	}

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

		ServerCmdReceiver receiver = new ServerCmdReceiver(port);
		while (!mustShutdown) {
			try {
				receiver.processNextPacket();
			} catch (IOException e) {
				System.err.println("I/O error while waiting for packet.");
				e.printStackTrace();
				continue;
			}
		}
	}
}