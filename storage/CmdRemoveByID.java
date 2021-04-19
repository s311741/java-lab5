package storage;

public final class CmdRemoveByID extends Cmd {
	public CmdRemoveByID (String[] arguments) { super(arguments); }

	@Override
	public boolean run () {
		if (arguments.length < 2) {
			return false;
		}
		final int id;
		try {
			id = Integer.parseInt(arguments[1]);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return Storage.getStorage().removeByID(id);
	}
}
