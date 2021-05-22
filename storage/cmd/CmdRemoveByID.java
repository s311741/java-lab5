package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * remove_by_id: remove an element with given ID, if any
 * ID must be given as an argument
 */
public final class CmdRemoveByID extends Cmd {
	public CmdRemoveByID (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		if (arguments.length < 2) {
			this.printMessage("no ID given");
			return false;
		}
		final int id;
		try {
			id = Integer.parseInt(arguments[1]);
		} catch (IllegalArgumentException e) {
			this.printMessage("invalid ID given");
			return false;
		}
		boolean success = true;

		// boolean success = Storage.getStorage().removeByID(id);
		// if (!success) {
		// 	this.printMessage("failed to remove element from storage");
		// }
		// TODO: request removal

		return success;
	}
}
