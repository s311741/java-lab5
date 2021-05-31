package storage.cmd;

import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * remove_by_id: remove an element with given ID, if any
 * ID must be given as an argument
 */
public final class CmdRemoveByID extends NetworkedCmd {
	private Integer id;

	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		if (arguments.length < 2) {
			System.err.println(arguments[0] + ": no ID given");
			return false;
		}
		try {
			this.id = Integer.parseInt(arguments[1]);
		} catch (IllegalArgumentException e) {
			System.err.println(arguments[0] + ": invalid ID given");
			return false;
		}
		return true;
	}

	@Override
	public Response runOnServer () {
		return new Response(StorageServer.getServer().removeByID(this.id));
	}
}
