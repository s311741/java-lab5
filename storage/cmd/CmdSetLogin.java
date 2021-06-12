package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * login: provide user credentials
 */
public final class CmdSetLogin extends Cmd {
	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		try {
			String name = prompter.nextLine("User name: ");
			String password = prompter.nextLine("Password: ");
			StorageClient.getClient().setLoginData(name, password);
		} catch (PrompterInputAbortedException e) {
			System.err.println("set_login: Aborted input");
			return false;
		}
		return true;
	}
}
