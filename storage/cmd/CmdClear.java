package storage.cmd;

import storage.*;
import storage.server.*;
import storage.client.*;

/**
 * clear: remove all elements
 */
public final class CmdClear extends NetworkedCmd {
	@Override
	public boolean runOnClient (String[] args, Prompter prompter) {
		return true;
	}

	@Override
	public Response runOnServer () {
		StorageServer.getServer().clear();
		return new Response(true);
	}
}
