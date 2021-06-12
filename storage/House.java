package storage;

import java.io.IOException;
import java.io.Serializable;
import storage.client.*;
import java.sql.*;

/**
 * A house in which a Flat can be
 */
public class House implements Serializable {
	private String name;
	private Integer year;
	private int numberOfFlatsOnFloor;
	private Long numberOfLifts;

	/**
	 * Ask a Prompter for a House
	 * @param prompt the I/O device
	 */
	public static House next (Prompter prompt) throws PrompterInputAbortedException {
		House house = new House();
		house.name = prompt.nextLine("name: ");
		house.year = (int) (long) prompt.nextLong("year: ", n -> n > 0 && n <= 699);
		house.numberOfFlatsOnFloor = (int) (long) prompt.nextLong("flats per floor: ", n -> n > 0);
		house.numberOfLifts = prompt.nextLong("number of lifts: ", n -> n > 0);
		return house;
	}

	public static House fromSQLResult (ResultSet s, int i) throws SQLException {
		House house = new House();
		house.name = s.getString(++i);
		house.year = s.getInt(++i);
		house.numberOfFlatsOnFloor = s.getInt(++i);
		house.numberOfLifts = s.getLong(++i);
		return house;
	}

	public String getName () { return this.name; }
	public int getYear () { return this.year; }
	public int getNumberOfFlatsOnFloor () { return this.numberOfFlatsOnFloor; }
	public long getNumberOfLifts () { return this.numberOfLifts; }

	@Override
	public String toString () {
		return "(House, name: " + this.name +
		     ", year: " + this.year.toString() +
		     ", flats per floor: " + Integer.toString(this.numberOfFlatsOnFloor) +
		     ", lifts: " + this.numberOfLifts.toString() + ")";
	}

	@Override
	public boolean equals (Object other) {
		if (other.getClass() != this.getClass()) {
			return false;
		}
		House cast = (House) other;
		return this.name.equals(cast.name) && this.year.equals(cast.year)
		    && this.numberOfFlatsOnFloor == cast.numberOfFlatsOnFloor
		    && this.numberOfLifts.equals(cast.numberOfLifts);
	}
}
