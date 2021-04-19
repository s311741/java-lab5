package storage;

import java.util.LinkedHashSet;

public final class Storage {
	private static Storage instance;

	private LinkedHashSet<Flat> set = new LinkedHashSet();

	public static Storage getInstance () {
		return instance;
	}

	public boolean add (Flat element) {
		return false;
	}
}