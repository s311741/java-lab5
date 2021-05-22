package storage.cmd;

import storage.*;

/**
 * remove_lower: remove all elements which evaluate lower than given
 * Input of the element required
 */
public final class CmdRemoveLower extends Cmd {
	public CmdRemoveLower (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		Flat element;
		try {
			element = Flat.next(this.prompter);
		} catch (PrompterInputAbortedException e) {
			this.printMessage("input aborted while entering house");
			return false;
		}

		Storage storage = Storage.getStorage();
		for (Flat flat: storage) {
			if (flat.compareTo(element) < 0 && !storage.removeByReference(flat)) {
				this.printMessage("failed to remove element with id " + flat.getID());
				return false;
			}
		}
		return true;
	}
}
