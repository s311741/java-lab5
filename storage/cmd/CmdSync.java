package storage.cmd;

import storage.*;
import storage.server.*;
import storage.client.*;
import java.io.IOException;

/**
 * clear: remove all elements
 */
public final class CmdSync extends NetworkedCmd {
	@Override
	public boolean runOnClient (String[] args, Prompter prompter) {
		return true;
	}

	@Override
	public Response runOnServer () {
		boolean success = StorageServer.getServer().tryDumpToJson();
		return new Response(success);
	}
}
