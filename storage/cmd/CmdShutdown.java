package storage.cmd;

import storage.*;
import storage.client.Prompter;
import storage.server.*;

/**
 * shutdown: shutdown the server
 */
public final class CmdShutdown extends NetworkedCmd {
	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		return true;
	}

	@Override
	public Response runOnServer () {
		Main.scheduleShutdown();
		return new Response(true);
	}
}
