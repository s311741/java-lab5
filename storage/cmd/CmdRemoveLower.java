package storage.cmd;

import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * remove_lower: remove all elements which evaluate lower than given
 * Input of the element required
 */
public final class CmdRemoveLower extends NetworkedCmd {
	private Flat element;

	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		try {
			this.element = Flat.next(prompter);
		} catch (PrompterInputAbortedException e) {
			System.err.println(arguments[0] + ": input aborted while entering element");
			return false;
		}
		return true;
	}

	@Override
	public Response runOnServer () {
		boolean success = true;
		StorageServer server = StorageServer.getServer();
		for (Flat flat: server) {
			if (flat.compareTo(this.element) < 0 && !server.removeByReference(flat)) {
				success = false;
			}
		}
		return new Response(success);
	}
}
