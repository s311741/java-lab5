package storage;

/**
 *
 */
public abstract class ElemCmd extends Cmd {
	Flat element;

	public ElemCmd (String[] args, Flat elem) {
		super(args);
		this.element = elem;
	}

	public ElemCmd (String[] args) {
		this(args, null);
	}
}