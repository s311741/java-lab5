package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * exit: exit the program
 */
public final class CmdExit extends Cmd {
	public CmdExit (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		System.exit(0);
		return true;
	}
}
