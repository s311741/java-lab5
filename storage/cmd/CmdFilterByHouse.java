package storage.cmd;

import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * filter_by_house: output elements whose "house" parameter is equal to the given house
 * Input of the house element required
 */
public final class CmdFilterByHouse extends NetworkedCmd {
	private House reference;

	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		try {
			prompter.pushPrefix("reference house ");
			this.reference = House.next(prompter);
		} catch (PrompterInputAbortedException e) {
			System.err.println(arguments[0] + ": input aborted while entering element");
			return false;
		} finally {
			prompter.popPrefix();
		}
		return true;
	}

	@Override
	public Response runOnServer () {
		for (Flat flat: StorageServer.getServer()) {
			if (this.reference.equals(flat.getHouse())) {
				// add to response??
			}
		}
		return new Response(true);
	}
}
