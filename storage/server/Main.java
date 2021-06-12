package storage.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.net.*;
import storage.*;
import storage.cmd.*;

public class Main {
	private static int port = 0;
	private static String host = null;
	private static String databaseName = null;

	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println("Must specify: port, host, database");
			System.exit(1);
		}
		try {
			port = Integer.parseInt(args[0]);
			host = args[1];
			databaseName = args[2];
		} catch (NumberFormatException e) {
			System.err.println("Invalid port specified");
			System.exit(1);
		}

		Prompter prompt = new Prompter(new BufferedReader(new InputStreamReader(System.in)),
		                               new OutputStreamWriter(System.out));

		DatabaseConnection db = null;
		try {
			String username = prompt.nextLine("Database username: ");
			String password = prompt.nextLine("Database password: ", s -> true);
			db = new DatabaseConnection(username, password, host, databaseName);
		} catch (PrompterInputAbortedException e) {
			System.exit(0);
		}

		if (!StorageServer.getServer().connect(db)
		 || !UserServer.getServer().connect(db)) {
			System.exit(1);
		}

		System.err.println("Ready...");
		// Thread commandLineInputThread = new Thread(Main::commandLineInput);
		Thread work = new Thread(Main::workerThread);
		work.start();

		try {
			for (String line; (line = prompt.nextLine("")) != null; ) {
				if (line.equals("shutdown")) {
					System.out.println("Shutting down.");
					break;
				} else if (line.equals("dropquit")) {
					StorageServer.getServer().forceDropTable();
					UserServer.getServer().forceDropTable();
					break;
				} else {
					System.err.println("Server accepts the commands \'shutdown\', \'dropquit\'. "
					                 + "Other commands are only accepted from clients");
				}
			}
		} catch (PrompterInputAbortedException e) { }
		System.exit(0);
	}

	private static void workerThread () {
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
}