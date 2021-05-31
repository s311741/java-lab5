package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * exit: exit the client
 */
public final class CmdExit extends Cmd {
	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		System.exit(0);
		return true;
	}
}
