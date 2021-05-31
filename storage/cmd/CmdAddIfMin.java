package storage.cmd;

import java.io.IOException;
import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * add_if_min: add an element if it is lesser than the existing minimum (or there is no minimum yet).
 * Input of the element required
 */
public final class CmdAddIfMin extends NetworkedCmd {
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
		StorageServer server = StorageServer.getServer();

		Flat minimum = server.getCurrentMinimum();
		if (minimum == null || this.element.compareTo(minimum) < 0) {
			server.add(element);
		}

		return new Response(true);
	}
}