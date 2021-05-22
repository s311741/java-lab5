package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * add: add an element
 * Input of the element required
 */
public final class CmdAdd extends Cmd {
	public CmdAdd (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		// try {
		// 	Flat element = Flat.next(this.prompter);
		// 	return StorageClient.getClient().add(element);

		// 	return true;
		// } catch (PrompterInputAbortedException e) {
		// 	this.printMessage("input aborted while entering element");
		// 	return false;
		// }
		// TODO: just request addition

		return true;
	}
}