package storage.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
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

		Thread commandLineInputThread = new Thread(Main::commandLineInput);

		DatabaseConnection db = new DatabaseConnection();

		if (!StorageServer.getServer().connect(db)
		 || !UserServer.getServer().connect(db)) {
			System.exit(1);
		}

		System.err.println("Ready...");

		commandLineInputThread.start();

		ServerCmdReceiver receiver = new ServerCmdReceiver(port);
		while (true) {
			try {
				receiver.processNextPacket();
			} catch (IOException e) {
				System.err.println("I/O error while receiving packet:");
				e.printStackTrace();
				continue;
			}
		}
	}

	private static void commandLineInput () {
		Prompter prompt = new Prompter(new BufferedReader(new InputStreamReader(System.in)),
			                               new OutputStreamWriter(System.out));
			try {
				for (String line; (line = prompt.nextLine("")) != null; ) {
					if (line.equals("shutdown")) {
						System.out.println("Shutting down.");
						break;
					} else {
						System.err.println("Server accepts the command \"shutdown\". "
						                 + "Other commands are only accepted from clients");
					}
				}
			} catch (PrompterInputAbortedException e) { }

			System.exit(0);
	}
}