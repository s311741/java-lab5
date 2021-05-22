package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * clear: remove all elements
 */
public final class CmdClear extends Cmd {
	public CmdClear (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		// Storage.getStorage().clear();
		// TODO: request clear
		return true;
	}
}
