package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * logout: remove user credentials
 */
public final class CmdUnsetLogin extends Cmd {
	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		if (!StorageClient.getClient().removeLoginData()) {
			System.err.println("Couldn\'t unset login data: there was none");
			return false;
		}
		return true;
	}
}
