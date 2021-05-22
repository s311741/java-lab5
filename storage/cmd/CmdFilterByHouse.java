package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * filter_by_house: output elements whose "house" parameter is equal to the given house
 * Input of the house element required
 */
public final class CmdFilterByHouse extends Cmd {
	public CmdFilterByHouse (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		final House reference;
		try {
			this.prompter.pushPrefix("reference house ");
			reference = House.next(this.prompter);
		} catch (PrompterInputAbortedException e) {
			this.printMessage("input aborted while entering element");
			return false;
		} finally {
			this.prompter.popPrefix();
		}

		// for (Flat flat: Storage.getStorage()) {
		// 	if (reference.equals(flat.getHouse())) {
		// 		System.out.println(flat.toString());
		// 	}
		// }
		// TODO: move this logic to server

		return true;
	}
}
