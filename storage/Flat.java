package storage;

import java.util.Date;
import java.io.BufferedReader;
import java.io.Writer;
import java.io.IOException;
import java.io.Serializable;
import storage.client.*;
import java.sql.*;

/**
 * The subject matter of the collection, a representation of some real estate.
 */
public final class Flat implements Comparable<Flat>, Serializable {
	private Integer id = null;
	private String name;
	private String creatorName = null;
	private Coordinates coordinates;
	private Date creationDate;
	private Long area;
	private Long numberOfRooms;
	private Furnish furnish;
	private View view;
	private Transport transport;
	private House house;

	/**
	 * Ask a Prompter for a Flat
	 * @param Prompter the I/O device
	 */
	public static Flat next (Prompter prompt) throws PrompterInputAbortedException {
		Flat result = new Flat();

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

	public Integer getID () { return this.id; }
	public String getCreatorName () { return this.creatorName; }
	public String getName () { return this.name; }
	/**
	 * State of furnishment
	 */
	public Furnish getFurnish () { return this.furnish; }
	public Long getNumberOfRooms () { return this.numberOfRooms; }
	/**
	 * The house object (which this flat owns, not the other way around)
	 */
	public House getHouse () { return this.house; }

	public Flat setID (Integer id) {
		if (this.id != null) {
			throw new Error("Tried to set the ID of an element which already has one");
		}
		return this.forceUpdateID(id);
	}
	public Flat forceUpdateID (Integer id) {
		if (id == null) {
			throw new Error("Tried to set the ID of an element to null");
		}
		this.id = id;
		return this;
	}

	public Flat setCreatorName (String name) {
		if (this.creatorName != null) {
			throw new Error("Tries to set a creator name for an element which already has one");
		}
		return this.forceUpdateCreatorName(name);
	}
	public Flat forceUpdateCreatorName (String name) {
		if (name == null) {
			throw new Error("Tried to set the creator name of an element to null");
		}
		this.creatorName = name;
		return this;
	}

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
		       "\ncreated at: " + this.creationDate.toString() +
		       "\ncreated by: " + this.creatorName + "\n";
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

	public static Flat fromSQLResult (ResultSet s, int i) throws SQLException {
		Flat flat = new Flat();

		flat.id = s.getInt(++i);
		flat.name = s.getString(++i);

		flat.coordinates = Coordinates.fromSQLResult(s, i);
		i += 2;

		flat.creationDate = new Date(s.getLong(++i));
		flat.area = s.getLong(++i);
		flat.numberOfRooms = s.getLong(++i);

		String str;
		if ((str = s.getString(++i)) != null) {
			flat.furnish = Enum.valueOf(Furnish.class, str);
		}
		flat.view = Enum.valueOf(View.class, s.getString(++i));
		if ((str = s.getString(++i)) != null) {
			flat.transport = Enum.valueOf(Transport.class, str);
		}

		flat.house = House.fromSQLResult(s, i);
		i += 4;

		return flat;
	}

	public PreparedStatement prepareStatement (Connection conn, String tableName) throws SQLException {
		final String tableFields = "(name,creator_name,coord_x,coord_y,created_unixtime,area,num_rooms,"
		                          + "furnish,view,transport,house_name,"
		                          + "house_year,house_num_flats,house_num_lifts)";
		final String questionMarks = "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		final String query = "INSERT INTO " + tableName + tableFields + " VALUES " + questionMarks;

		PreparedStatement s = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		int i = 0;
		s.setString(++i, this.name);
		s.setString(++i, this.creatorName);
		s.setFloat (++i, this.coordinates.getX());
		s.setDouble(++i, this.coordinates.getY());
		s.setLong  (++i, this.creationDate.getTime());
		s.setLong  (++i, this.area);
		s.setLong  (++i, this.numberOfRooms);
		s.setObject(++i, this.furnish, java.sql.Types.OTHER);
		s.setObject(++i, this.view, java.sql.Types.OTHER);
		s.setObject(++i, this.transport, java.sql.Types.OTHER);
		s.setString(++i, this.house.getName());
		s.setInt   (++i, this.house.getYear());
		s.setInt   (++i, this.house.getNumberOfFlatsOnFloor());
		s.setLong  (++i, this.house.getNumberOfLifts());
		return s;
	}
}
