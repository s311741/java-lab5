package storage.cmd;

import java.util.Iterator;
import storage.*;
import storage.client.*;

/**
 * history: print out the last entered commands within this session, to a maximum of 13,
 * latest first. Implemented with a circular buffer
 */
public final class CmdHistory extends Cmd {
	@Override
	public boolean runOnClient (String[] arguments, Prompter prompter) {
		StorageClient.getClient().printHistory();
		return true;
	}
}
