package storage;

import java.io.Writer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class Main {
	public static void main (String[] args) {
		Prompter prompter = new Prompter(new BufferedReader(new InputStreamReader(System.in)),
		                                 new OutputStreamWriter(System.out));

		String storageFilename;
		if (args.length < 1) {
			try {
				do {
					storageFilename = prompter.nextLine("Database file: ");
				} while (!Storage.getStorage().setFile(storageFilename));
			} catch (PrompterInputAbortedException e) {
				return;
			}
		} else if (args.length == 1) {
			if (!Storage.getStorage().setFile(args[0])) {
				System.exit(1);
			}
		} else {
			System.err.println("Too many arguments");
			System.exit(1);
		}

		Storage.getStorage().tryPopulateFromFile();

		Cmd cmd;

		try {
			while ((cmd = Cmd.next(prompter)) != null) {
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

