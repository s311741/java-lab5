package storage;

/**
 * update_id: replace the element with given ID with a new one
 * Input of the element required, ID is given as an argument
 */
public final class CmdUpdateID extends Cmd {
	public CmdUpdateID (String[] a, Prompter p) { super(a, p); }

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

		Flat element;
		try {
			element = Flat.next(this.prompter, id);
		} catch (PrompterInputAbortedException e)  {
			this.printMessage("input aborted while entering element");
			return false;
		}
		Storage storage = Storage.getStorage();
		boolean success = storage.removeByID(id);
		if (!success) {
			this.printMessage("failed to remove old element with id" + id);
			return false;
		}
		success = storage.add(element);
		if (!success) {
			this.printMessage("failed to add new element with id " + id);
			return false;
		}
		return success;
	}
}
