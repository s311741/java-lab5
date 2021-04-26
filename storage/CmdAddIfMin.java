package storage;

import java.io.IOException;

/**
 * add_if_min: add an element if it is lesser than the existing minimum (or there is no minimum yet).
 * Input of the element required
 */
public final class CmdAddIfMin extends Cmd {
	public CmdAddIfMin (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		Storage storage = Storage.getStorage();
		try {
			Flat element = Flat.next(this.prompter);
			if (storage.getCurrentMinimum() == null
			 || element.compareTo(storage.getCurrentMinimum()) < 0) {
				storage.add(element);
			}
		} catch (PrompterInputAbortedException e) {
			this.printError("input aborted while entering element");
			return false;
		}
		return true;
	}
}