package storage;

import java.util.LinkedHashSet;

public final class Storage {
	private static Storage instance = null;

	private LinkedHashSet<Flat> set = new LinkedHashSet<Flat>();

	public static Storage getStorage () {
		if (instance == null) {
			instance = new Storage();
		}
		return instance;
	}

	public boolean add (Flat element) {
		return this.set.add(element);
	}

	public void clear () {
		this.set.clear();
	}
}