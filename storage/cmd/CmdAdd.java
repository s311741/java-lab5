package storage.cmd;

import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * add: add an element
 * Input of the element required
 */
public final class CmdAdd extends NetworkedCmd {
	private Flat element;

	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		try {
			this.element = Flat.next(prompter);
		} catch (PrompterInputAbortedException e) {
			System.err.println(arguments[0] + ": input aborted while entering elements");
			return false;
		}
		return true;
	}

	@Override
	public Response runOnServer () {
		return new Response(StorageServer.getServer().add(this.element));
	}
}