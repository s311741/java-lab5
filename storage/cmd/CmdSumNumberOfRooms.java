package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * sum_of_number_of_rooms: print the sum of "numberOfRooms" parameter for all elements
 */
public final class CmdSumNumberOfRooms extends Cmd {
	public CmdSumNumberOfRooms (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		long answer = 0;

		// for (Flat flat: Storage.getStorage()) {
		// 	answer += flat.getNumberOfRooms();
		// }
		// TODO: move this logic to server

		System.out.println(answer);
		return true;
	}
}
