package storage.cmd;

import java.util.HashSet;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import storage.*;
import storage.client.*;

/**
 * execute_script: run commands from a file as if from stdin
 * Recursion is (crudely) detected and disallowed
 */
public final class CmdExecuteScript extends Cmd {
	private static HashSet<String> calls = new HashSet<String>();

	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		if (arguments.length < 2) {
			System.err.println(arguments[0] + ": no script name given");
			return false;
		}
		String scriptName = arguments[1];
		boolean topLevel = !calls.isEmpty();

		if (!topLevel && calls.contains(scriptName)) {
			System.err.println(arguments[0] + ": recursion detected in script " + scriptName + ". Exiting");
			System.exit(1);
		}
		calls.add(scriptName);

		String[] failedCmdWords = null;
		boolean success = true;

		try {
			Prompter prompt = new Prompter(new BufferedReader(new FileReader(scriptName)));
			for (String[] cmdWords; (cmdWords = Cmd.nextCmdWords(prompt)) != null; ) {
				if (!StorageClient.getClient().runCommand(cmdWords, prompt)) {
					failedCmdWords = cmdWords;
					success = false;
					break;
				}
			}
		} catch (IOException e) {
			System.err.println(arguments[0] + ": couldn\'t access script " + scriptName);
			success = false;
		}

		if (topLevel && failedCmdWords != null) {
			System.err.println("Script " + scriptName + " aborted: the command "
			                   + String.join(" ", failedCmdWords) + " failed");
		}

		return success;
	}
}
