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

	static HashSet<String> calls = new HashSet<String>();

	@Override
	public boolean run () {
		if (this.arguments.length < 2) {
			return false;
		}
		final String scriptName = this.arguments[1];
		if (calls.contains(scriptName)) {
			return false;
		}

		boolean success = true;
		calls.add(scriptName);
		try {
			Prompter prompt = new Prompter(new BufferedReader(new FileReader(scriptName)));
			Cmd cmd;
			while ((cmd = Cmd.next(prompt)) != null) {
				if (!cmd.run()) {
					success = false;
					break;
				}
			}
		} catch (IOException e) {
			success = false;
		}

		calls.remove(scriptName);
		return success;
	}
}
