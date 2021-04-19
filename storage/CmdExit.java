package storage;

public final class CmdExit extends Cmd {
	public CmdExit (String[] arguments) { super(arguments); }

	@Override
	public boolean run () {
		System.exit(0);
		return true;
	}
}
