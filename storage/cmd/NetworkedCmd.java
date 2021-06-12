package storage.cmd;

import java.io.Serializable;
import storage.*;

public abstract class NetworkedCmd extends Cmd implements Serializable {
	protected UserCredentials login = null;

	public final NetworkedCmd setLogin (UserCredentials login) {
		if (this.login != null) {
			System.err.println("Tried to re-set a networked command\'s login data");
			System.exit(1);
		}
		this.login = login;
		return this;
	}

	public final UserCredentials getLogin () {
		return this.login;
	}

	/**
	 * Runs for the command's invocation on the server, after deserialization
	 */
	public abstract Response runOnServer ();
}