package storage;

public final class CmdUpdateID extends ElemCmd {
	public CmdUpdateID (String[] arguments, Flat elem) { super(arguments, elem); }

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

		this.element.setID(id);
		Storage storage = Storage.getStorage();
		return storage.removeByID(id) && storage.add(this.element);
	}
}
