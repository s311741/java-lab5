package storage.cmd;

import java.io.Serializable;
import storage.*;

public abstract class NetworkedCmd extends Cmd implements Serializable {
	/**
	 * Runs for the command's invocation on the server, after deserialization
	 */
	public abstract Response runOnServer ();
}