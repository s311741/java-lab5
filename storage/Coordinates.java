package storage;

public class Coordinates {
	private float x;
	private Double y;

	private boolean invariant () {
		return x <= 132
		    && y != null && y <= 364;
	}
}
