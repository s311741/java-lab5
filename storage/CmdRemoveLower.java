package storage;

public final class CmdRemoveLower extends ElemCmd {
	public CmdRemoveLower (String[] arguments, Flat elem) { super(arguments, elem); }

	@Override
	public boolean run () {
		Storage storage = Storage.getStorage();
		for (Flat flat: storage) {
			if (flat.compareTo(this.element) < 0
			 && !storage.removeByReference(flat)) {
				return false;
			}
		}
		return true;
	}
}
