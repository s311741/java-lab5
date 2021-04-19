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
			return false;
		}
		final int id;
		try {
			id = Integer.parseInt(arguments[1]);
		} catch (IllegalArgumentException e) {
			return false;
		}

		Flat element;
		try {
			element = Flat.next(this.prompter, id);
		} catch (PrompterInputAbortedException e)  {
			return false;
		}
		Storage storage = Storage.getStorage();
		return storage.removeByID(id) && storage.add(element);
	}
}
