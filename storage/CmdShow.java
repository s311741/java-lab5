package storage;

public final class CmdShow extends Cmd {
	public CmdShow (String[] arguments) { super(arguments); }

	@Override
	public boolean run () {
		for (Flat flat: Storage.getStorage()) {
			System.out.println(flat.toString());
		}
		return true;
	}
}
