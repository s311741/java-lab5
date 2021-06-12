package storage.cmd;

import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * info: print info about the storage
 */
public final class CmdRegister extends NetworkedCmd {
	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		return true;
	}

	@Override
	public Response runOnServer () {
		return new Response(UserServer.getServer().registerUser(this.login));
	}
}
