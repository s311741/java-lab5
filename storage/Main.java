package storage;

import java.io.Writer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class Main {
	public static void main (String[] args) {
		Storage.getStorage().tryPopulateFromFile();

		if (args.length > 0) {
			// automated mode
			String[] cmdArgs = { "execute_script", args[0] };
			Cmd cmd = new CmdExecuteScript(cmdArgs,
			                               new Prompter(new BufferedReader(new InputStreamReader(System.in))));
			if (!cmd.run()) {
				System.exit(1);
			}
		} else {
			// interactive mode
			Cmd cmd;
			Prompter prompter = new Prompter(new BufferedReader(new InputStreamReader(System.in)),
			                                 new OutputStreamWriter(System.out));
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
}

