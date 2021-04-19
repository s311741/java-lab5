package storage;

public final class CmdClear extends Cmd {
	public CmdClear (String[] arguments) { super(arguments); }

	@Override
	public boolean run () {
		Storage.getStorage().clear();
		return true;
	}
}
