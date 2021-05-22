package storage.cmd;

import java.io.IOException;
import storage.*;
import storage.client.*;

/**
 * save: dump the current state of the collection to the file
 */
public final class CmdSave extends Cmd {
	public CmdSave (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		// try {
		// 	Storage.getStorage().dumpToJson();
		// } catch (IOException e) {
		// 	this.printMessage("the database file is inaccessible");
		// 	return false;
		// }
		// TODO: send the server a request to save
		return true;
	}
}
