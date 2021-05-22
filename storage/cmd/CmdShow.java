package storage.cmd;

import storage.*;

/**
 * show: print the collection's elements in a human-readable way
 */
public final class CmdShow extends Cmd {
	public CmdShow (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		Storage storage = Storage.getStorage();

		if (storage.isEmpty()) {
			this.printMessage("no items to show");
		}

		for (Flat flat: storage) {
			System.out.println(flat.toString());
		}
		return true;
	}
}
