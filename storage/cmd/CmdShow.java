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
		StringBuilder sb = StorageServer.getServer().stream()
				.sorted((a, b) -> (a.getName().compareTo(b.getName())))
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
		return new Response(true, sb.length() != 0 ? sb.toString() : "No items to show");
	}
}
