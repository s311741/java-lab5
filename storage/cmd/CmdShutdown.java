package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * shutdown: shutdown the server
 */
public final class CmdShutdown extends Cmd {
	public CmdShutdown (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		StorageClient.getClient().shutdownServer();
		return true;
	}
}