package storage.cmd;

import java.io.IOException;
import storage.*;
import storage.client.*;

/**
 * add_if_min: add an element if it is lesser than the existing minimum (or there is no minimum yet).
 * Input of the element required
 */
public final class CmdAddIfMin extends Cmd {
	public CmdAddIfMin (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		// Storage storage = Storage.getStorage();
		try {
			Flat element = Flat.next(this.prompter);
			// if (storage.getCurrentMinimum() == null
			//  || element.compareTo(storage.getCurrentMinimum()) < 0) {
			// 	storage.add(element);
			// }
			// TODO: move minimum-checking logic to server
		} catch (PrompterInputAbortedException e) {
			this.printMessage("input aborted while entering element");
			return false;
		}
		return true;
	}
}