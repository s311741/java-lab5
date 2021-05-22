package storage.client;

import java.io.Writer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

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

		if (sep == 0) {
			// No filename given before command; ask for one
			try {
				do {
					storageFilename = prompt.nextLine("Database file: ");
				} while (!Storage.getStorage().setFile(storageFilename));
			} catch (PrompterInputAbortedException e) {
				return;
			}
		} else {
			if (!Storage.getStorage().setFile(args[0])) {
				System.exit(1);
			}
		}

		Storage.getStorage().tryPopulateFromFile();

		if (sep < args.length-1) {
			// There is a command in the argument list; run it and quit
			int numCmdWords = args.length-sep-1;
			String[] cmdWords = new String[numCmdWords];
			for (int i = 0; i < numCmdWords; i++) {
				cmdWords[i] = args[sep+1+i];
			}
			System.exit(Cmd.getCommandFromWords(cmdWords, prompt).run() ? 0 : 1);
		}

		// Interactive mode
		try {
			for (Cmd cmd; (cmd = Cmd.next(prompt)) != null; ) {
				if (!cmd.run()) {
					System.err.println("The command failed");
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
