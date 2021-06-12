package storage;

import java.io.IOException;
import java.io.Serializable;
import storage.client.*;
import java.sql.*;

/**
 * Two coordinates, curiously stored in very different data types
 */
public class Coordinates implements Serializable {
	private float x;
	private Double y;

	/**
	 * Ask a Prompter for a Coordinates
	 * @param Prompter the I/O device
	 */
	public static Coordinates next (Prompter prompt) throws PrompterInputAbortedException {
		Coordinates crd = new Coordinates();
		crd.x = (float) (double) prompt.nextDouble("x (<=132): ", _x -> _x <= 132);
		crd.y = prompt.nextDouble("y (<=364): ", _y -> _y <= 364);
		return crd;
	}

	public static Coordinates fromSQLResult (ResultSet s, int i) throws SQLException {
		Coordinates crd = new Coordinates();
		crd.x = s.getFloat(++i);
		crd.y = s.getDouble(++i);
		return crd;
	}

	@Override
	public String toString () {
		return "(" + Float.toString(this.x) + ", " + this.y.toString() + ")";
	}

	public float getX () { return this.x; }
	public double getY () { return this.y; }
}
