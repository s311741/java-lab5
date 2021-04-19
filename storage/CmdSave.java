package storage;

import java.io.IOException;

public final class CmdSave extends Cmd {
	public CmdSave (String[] arguments) { super(arguments); }

	@Override
	public boolean run () {
		try {
			Storage.getStorage().dumpToFile();
		} catch (IOException e) {
			System.err.println("The database file is inaccessible");
			return false;
		}
		return true;
	}
}
