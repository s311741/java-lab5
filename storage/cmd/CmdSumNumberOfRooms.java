package storage.cmd;

import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * sum_of_number_of_rooms: print the sum of "numberOfRooms" parameter for all elements
 */
public final class CmdSumNumberOfRooms extends NetworkedCmd {
	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		return true;
	}

	public Response runOnServer () {
		long answer = 0;
		for (Flat flat: StorageServer.getServer()) {
			answer += flat.getNumberOfRooms();
		}
		return new Response(true, ""+ answer);
	}
}
