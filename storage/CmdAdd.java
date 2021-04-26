package storage;

/**
 * add: add an element
 * Input of the element required
 */
public final class CmdAdd extends Cmd {
	public CmdAdd (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		try {
			Flat element = Flat.next(this.prompter);
			return Storage.getStorage().add(element);
		} catch (PrompterInputAbortedException e) {
			this.printMessage("input aborted while entering element");
			return false;
		}
	}
}