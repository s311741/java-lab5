package storage.client;

import java.io.Writer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.net.*;

import storage.*;
import storage.cmd.Cmd;

public class Main {
	public static void main (String[] args) {
		Prompter prompt = new Prompter(new BufferedReader(new InputStreamReader(System.in)),
		                               new OutputStreamWriter(System.out));

		String storageFilename;

		int sep = args.length;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("+")) {
				sep = i;
				break;
			}
		}

		SocketAddress socketAddress = null;
		if (sep == 0) {
			// No IP given before command: ask for one
			try {
				boolean success = false;
				do {
					String host = prompt.nextLine("Host address: ");
					int port = (int) (long) prompt.nextLong("Port: ", l -> (l > 0 && l < 65536));
					try {
						socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
						success = true;
					} catch (UnknownHostException e) {
						System.err.println("Failed to resolve the host");
					}
				} while (!success);
			} catch (PrompterInputAbortedException e) {
				return;
			}
		} else {
			// Set IP to whatever was in the parameter
			String hostPort = args[0];
			int colon = hostPort.indexOf(':');
			if (colon == -1) {
				System.err.println("Must specify host address as host:port");
				System.exit(1);
			}
			String host = hostPort.substring(0, colon);
			try {
				int port = Integer.parseInt(hostPort.substring(colon + 1));

				socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
			} catch (UnknownHostException e) {
				System.out.println("Failed to resolve the host");
				System.exit(1);
			} catch (NumberFormatException e) {
				System.out.println("Invalid port number");
				System.exit(1);
			}
		}

		StorageClient.initialize(socketAddress);
		StorageClient client = StorageClient.getClient();

		if (sep < args.length-1) {
			// There is a command in the argument list; run it and quit
			int numCmdWords = args.length-sep-1;
			String[] cmdWords = new String[numCmdWords];
			for (int i = 0; i < numCmdWords; i++) {
				cmdWords[i] = args[sep+1+i];
			}
			System.exit(client.runCommand(cmdWords, prompt) ? 0 : 1);
		}

		// Interactive mode
		try {
			for (String[] cmdWords; (cmdWords = Cmd.nextCmdWords(prompt)) != null; ) {
				if (!client.runCommand(cmdWords, prompt)) {
					System.err.println("The command failed");
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
