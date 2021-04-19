package storage;

public final class CmdAddIfMin extends ElemCmd {
	public CmdAddIfMin (String[] arguments, Flat elem) { super(arguments, elem); }

	@Override
	public boolean run () {
		Storage storage = Storage.getStorage();
		if (this.element.compareTo(storage.getCurrentMinimum()) < 0) {
			storage.add(this.element);
		}
		return true;
	}
}