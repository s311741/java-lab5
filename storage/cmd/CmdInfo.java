package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * info: print info about the storage
 */
public final class CmdInfo extends Cmd {
	public CmdInfo (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		// System.out.println(Storage.getStorage().info());
		// TODO: make a request for info
		return true;
	}
}
