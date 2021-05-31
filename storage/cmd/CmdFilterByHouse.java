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
		final StringBuilder sb = new StringBuilder();

		for (Flat flat: StorageServer.getServer()) {
			if (this.reference.equals(flat.getHouse())) {
				sb.append(flat.toString());
			}
		}

		return new Response(true, sb.length() != 0 ? sb.toString() : "No items to show");
	}
}
