package storage.cmd;

import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * update_id: replace the element with given ID with a new one
 * Input of the element required, ID is given as an argument
 */
public final class CmdUpdateID extends NetworkedCmd {
	private Flat element;

	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		if (arguments.length < 2) {
			System.err.println(arguments[0] + ": no ID given");
			return false;
		}
		int id;
		try {
			id = Integer.parseInt(arguments[1]);
		} catch (IllegalArgumentException e) {
			System.err.println(arguments[0] + ": invalid ID given");
			return false;
		}
		try {
			this.element = Flat.next(prompter).setID(id);
		} catch (PrompterInputAbortedException e) {
			System.err.println(arguments[0] + ": input aborted while entering element");
			return false;
		}
		return true;
	}

	@Override
	public Response runOnServer () {
		StorageServer server = StorageServer.getServer();
		boolean success = server.removeByID(this.element.getID());
		if (!success) {
			return new Response(false, "Failed to remove old element with id " + this.element.getID());
		}
		success = server.add(element);
		if (!success) {
			return new Response(false, "Failed to insert the new element with id " + this.element.getID());
		}
		return new Response(true);
	}
}
