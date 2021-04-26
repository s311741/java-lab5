package storage;

/**
 * remove_by_id: remove an element with given ID, if any
 * ID must be given as an argument
 */
public final class CmdRemoveByID extends Cmd {
	public CmdRemoveByID (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		if (arguments.length < 2) {
			this.printError("no ID given");
			return false;
		}
		final int id;
		try {
			id = Integer.parseInt(arguments[1]);
		} catch (IllegalArgumentException e) {
			this.printError("invalid ID given");
			return false;
		}
		boolean success = Storage.getStorage().removeByID(id);
		if (!success) {
			this.printError("failed to remove element from storage");
		}
		return success;
	}
}
