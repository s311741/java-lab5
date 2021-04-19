package storage;

/**
 * info: print info about the storage
 */
public final class CmdInfo extends Cmd {
	public CmdInfo (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		System.out.println(Storage.getStorage().info());
		return true;
	}
}
