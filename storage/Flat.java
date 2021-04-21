package storage;

import java.util.Date;
import java.io.BufferedReader;
import java.io.Writer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * The subject matter of the collection, a representation of some real estate.
 */
public final class Flat implements Comparable<Flat> {
	private Integer id;
	private String name;
	private Coordinates coordinates;
	private Date creationDate;
	private Long area;
	private Long numberOfRooms;
	private Furnish furnish;
	private View view;
	private Transport transport;
	private House house;

	/**
	 * Ask a Prompter for a Flat, with the id auto-generated
	 * @param Prompter the I/O device
	 */
	public static Flat next (Prompter prompt) throws PrompterInputAbortedException {
		return next(prompt, Storage.getStorage().nextID());
	}

	/**
	 * Ask a Prompter for a Flat, with the id given
	 * @param Prompter the I/O device
	 * @param id the predefined id
	 */
	public static Flat next (Prompter prompt, int id) throws PrompterInputAbortedException {
		Flat result = new Flat();

		result.id = id;
		result.creationDate = new Date();

		result.name = prompt.nextLine("name (nonempty): ", s -> !s.isEmpty());

		prompt.pushPrefix("coordinate ");
		result.coordinates = Coordinates.next(prompt);
		prompt.popPrefix();

		result.area = prompt.nextLong("area (> 0): ", n -> n > 0);
		result.numberOfRooms = prompt.nextLong("number of rooms (> 0): ", n -> n > 0);
		result.furnish = prompt.nextEnum("furnish: ", Furnish.class, true);
		result.view = prompt.nextEnum("view: ", View.class, false);
		result.transport = prompt.nextEnum("transport: ", Transport.class, true);

		try {
			prompt.pushPrefix("house ");
			result.house = House.next(prompt);
		} catch (PrompterInputAbortedException e) {
			prompt.popPrefix();
			throw e;
		}

		prompt.popPrefix();
		return result;
	}

	/**
	 * Internal ID within the collection
	 */
	public Integer getID () { return this.id; }
	/**
	 * State of furnishment
	 */
	public Furnish getFurnish () { return this.furnish; }
	public Long getNumberOfRooms () { return this.numberOfRooms; }
	/**
	 * The house object (which this flat owns, not the other way around)
	 */
	public House getHouse () { return this.house; }

	@Override
	public String toString () {
		return "id: " + this.id.toString() +
		       "\nname: " + this.name +
		       "\ncoordinates: " + this.coordinates.toString() +
		       "\narea: " + this.area.toString() +
		       "\nnumber of rooms: " + this.numberOfRooms.toString() +
		       (this.furnish == null ? "" : "\nfurnish: " + this.furnish.toString()) +
		       "\nview: " + this.view.toString() +
		       (this.transport == null ? "" : "\ntransport: " + this.transport.toString()) +
		       "\nhouse: " + this.house.toString() +
		       "\ncreated at: " + this.creationDate.toString() + "\n";
	}

	public JSONObject toJson () {
		JSONObject jo = new JSONObject();
		jo.put("id", this.id);
		jo.put("creationDate", this.creationDate.getTime());
		jo.put("name", this.name);
		jo.put("coordinates", this.coordinates.toJson());
		jo.put("area", this.area);
		jo.put("numberOfRooms", this.numberOfRooms);
		if (this.furnish != null) {
			jo.put("furnish", this.furnish);
		}
		jo.put("view", this.view);
		if (this.transport != null) {
			jo.put("transport", this.transport);
		}
		jo.put("house", this.house.toJson());
		return jo;
	}

	public static Flat fromJson (JSONObject jo) throws JSONException {
		Flat result = new Flat();

		result.id = jo.getInt("id");
		result.name = jo.getString("name");
		result.creationDate = new Date(jo.getLong("creationDate"));
		result.coordinates = Coordinates.fromJson(jo.getJSONObject("coordinates"));
		result.area = jo.getLong("area");
		result.numberOfRooms = jo.getLong("numberOfRooms");

		try {
			try {
				result.furnish = Enum.valueOf(Furnish.class, jo.getString("furnish").toUpperCase());
			} catch (JSONException e) {
				result.furnish = null;
			}
			result.view = Enum.valueOf(View.class, jo.getString("view").toUpperCase());
			try {
				result.transport = Enum.valueOf(Transport.class, jo.getString("transport").toUpperCase());
			} catch (JSONException e) {
				result.transport = null;
			}
		} catch (IllegalArgumentException e) {
			System.err.println("Failed to parse a enum value in element with id " + result.id.toString());
			return null;
		}

		result.house = House.fromJson(jo.getJSONObject("house"));

		return result;
	}

	/**
	 * Compares the flats by, in order of importance:
	 * - area
	 * - number of rooms
	 * - furnishment
	 * - transportation availability
	 * If those match, neither is considered better, and 0 is returned,
	 * even though they may not be equal
	 */
	@Override
	public int compareTo (Flat other) {
		if (!this.area.equals(other.area)) {
			return (int) (this.area - other.area);
		}
		if (!this.numberOfRooms.equals(other.numberOfRooms)) {
			return (int) (this.numberOfRooms - other.numberOfRooms);
		}
		if (this.furnish != other.furnish) {
			return this.furnish.ordinal() - other.furnish.ordinal();
		}
		if (this.transport != other.transport) {
			return this.transport.ordinal() - other.transport.ordinal();
		}
		return 0;
	}
}
