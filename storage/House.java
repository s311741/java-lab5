package storage;

import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONException;

import storage.client.*;

/**
 * A house in which a Flat can be
 */
public class House {
	private String name;
	private Integer year;
	private int numberOfFlatsOnFloor;
	private Long numberOfLifts;

	/**
	 * Ask a Prompter for a House
	 * @param prompt the I/O device
	 */
	public static House next (Prompter prompt) throws PrompterInputAbortedException {
		House result = new House();
		result.name = prompt.nextLine("name: ");
		result.year = (int) (long) prompt.nextLong("year: ", n -> n > 0 && n <= 699);
		result.numberOfFlatsOnFloor = (int) (long) prompt.nextLong("flats per floor: ", n -> n > 0);
		result.numberOfLifts = prompt.nextLong("number of lifts: ", n -> n > 0);
		return result;
	}

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

	public JSONObject toJson () {
		JSONObject jo = new JSONObject();
		jo.put("name", this.name);
		jo.put("year", this.year);
		jo.put("numberOfFlatsOnFloor", this.numberOfFlatsOnFloor);
		jo.put("numberOfLifts", this.numberOfLifts);
		return jo;
	}

	public static House fromJson (JSONObject jo) throws JSONException {
		House result = new House();

		result.name = jo.getString("name");
		result.year = jo.getInt("year");
		result.numberOfFlatsOnFloor = jo.getInt("numberOfFlatsOnFloor");
		result.numberOfLifts = jo.getLong("numberOfLifts");

		return result;
	}
}
