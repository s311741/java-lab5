package storage;

/**
 * show: print the collection's elements in a human-readable way
 */
public final class CmdShow extends Cmd {
	public CmdShow (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		for (Flat flat: Storage.getStorage()) {
			System.out.println(flat.toString());
		}
		return true;
	}
}
