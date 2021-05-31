package storage.cmd;

import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * show: print the collection's elements in a human-readable way
 */
public final class CmdShow extends NetworkedCmd {
	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		return true;
	}

	@Override
	public Response runOnServer () {
		StorageServer server = StorageServer.getServer();
		if (server.isEmpty()) {
			return new Response(true, "No items to show");
		}

		StringBuilder sb = new StringBuilder();
		for (Flat flat: server) {
			sb.append(flat.toString());
			sb.append('\n');
		}

		return new Response(true, sb.toString());
	}
}
