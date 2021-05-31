package storage.cmd;

import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * info: print info about the storage
 */
public final class CmdInfo extends NetworkedCmd {
	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		return true;
	}

	@Override
	public Response runOnServer () {
		return new Response(true, StorageServer.getServer().info());
	}
}
