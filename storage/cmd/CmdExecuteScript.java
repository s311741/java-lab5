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
	public CmdExecuteScript (String[] a, Prompter p) { super(a, p); }

	private static HashSet<String> calls = new HashSet<String>();

	@Override
	public boolean run () {
		if (this.arguments.length < 2) {
			this.printMessage("no script name given");
			return false;
		}
		final String scriptName = this.arguments[1];
		final boolean topLevel = calls.isEmpty();

		if (!topLevel && calls.contains(scriptName)) {
			this.printMessage("recursion detected in script " + scriptName
			              + ".\nExiting without saving any changes this script might have made");
			System.exit(1);
		}

		// if (topLevel) {
		// 	Storage.getStorage().lockWrites();
		// }
		// TODO: lock writes at client level
		calls.add(scriptName);

		Cmd failedCmd = null;
		boolean success = true;

		try {
			Prompter prompt = new Prompter(new BufferedReader(new FileReader(scriptName)));
			for (Cmd cmd; (cmd = Cmd.next(prompt)) != null; ) {
				if (!cmd.run()) {
					failedCmd = cmd;
					success = false;
					break;
				}
			}
		} catch (IOException e) {
			this.printMessage("couldn\'t access script " + scriptName);
			success = false;
		}

		if (failedCmd != null && topLevel) {
			this.printMessage("script " + scriptName + " aborted: command \""
			                + failedCmd.toString() + "\" failed");
		}

		calls.remove(scriptName);
		if (success && topLevel) {
			// try {
			// 	Storage.getStorage().unlockWrites();
			// } catch (IOException e) {
			// 	this.printMessage("Couldn'\t unlock writes to the database at the end of script "
			// 	                + scriptName);
			// 	success = false;
			// }
			// TODO: unlock writes at client level
		}
		return success;
	}
}
