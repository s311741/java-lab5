package storage;

import java.util.HashSet;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
			this.printError("no script name given");
			return false;
		}
		final String scriptName = this.arguments[1];

		boolean success = true;
		CmdSave saveAfter = null;

		if (calls.contains(scriptName)) {
			this.printError("recursion detected in script " + scriptName
			              + ".\nExiting without saving any changes this script might have made");
			System.exit(1);
		}
		calls.add(scriptName);

		try {
			Prompter prompt = new Prompter(new BufferedReader(new FileReader(scriptName)));
			for (Cmd cmd; (cmd = Cmd.next(prompt)) != null; ) {
				if (cmd instanceof CmdSave) {
					saveAfter = (CmdSave) cmd;
				} else if (!cmd.run()) {
					this.printError("recursion detected");
					success = false;
					break;
				}
			}
		} catch (IOException e) {
			this.printError("couldn\'t access script " + scriptName);
			success = false;
		}

		calls.remove(scriptName);
		if (success && saveAfter != null && calls.isEmpty()) {
			saveAfter.run();
		}

		return success;
	}
}
