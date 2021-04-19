package storage;

public final class CmdAdd extends ElemCmd {
	public CmdAdd (String[] arguments, Flat elem) { super(arguments, elem); }

	@Override
	public boolean run () {
		return Storage.getStorage().add(this.element);
	}
}