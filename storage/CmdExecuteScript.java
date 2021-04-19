package storage;

import java.util.HashSet;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class CmdExecuteScript extends Cmd {
	public CmdExecuteScript (String[] arguments) { super(arguments); }

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

		calls.add(scriptName);
		try {
			Prompter prompt = new Prompter(new BufferedReader(new FileReader(scriptName)));
			Cmd cmd;
			while ((cmd = Cmd.next(prompt)) != null) {
				if (!cmd.run()) {
					calls.remove(scriptName);
					return false;
				}
			}
		} catch (IOException e) {
			return false;
		} finally {
			calls.remove(scriptName);
		}

		return true;
	}
}
