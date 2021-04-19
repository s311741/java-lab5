package storage;

import java.util.Date;
import java.io.BufferedReader;
import java.io.Writer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import org.json.JSONException;

public final class Flat {
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

	public static Flat next (Prompter prompt) throws IOException, PrompterInputAbortedException {
		Flat result = new Flat();

		result.id = Storage.getStorage().getNextId();
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

		prompt.pushPrefix("house ");
		result.house = House.next(prompt);
		prompt.popPrefix();

		return result;
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
		       "\nhouse: " + this.house.toString() + "\n";
	}

	public JSONObject toJson () {
		JSONObject jo = new JSONObject();
		jo.put("id", this.id);
		jo.put("creationDate", this.creationDate);
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

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
			result.creationDate = sdf.parse(jo.getString("creationDate"));
		} catch (ParseException e) {
			return null;
		}

		result.coordinates = Coordinates.fromJson(jo.getJSONObject("coordinates"));
		result.area = jo.getLong("area");
		result.numberOfRooms = jo.getLong("numberOfRooms");

		try {
			result.furnish = Enum.valueOf(Furnish.class, jo.getString("furnish").toUpperCase());
		} catch (JSONException e) {
			result.furnish = null;
		} catch (IllegalArgumentException e) {
			return null;
		}

		try {
			result.view = Enum.valueOf(View.class, jo.getString("view").toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}

		try {
			result.transport = Enum.valueOf(Transport.class, jo.getString("transport").toUpperCase());
		} catch (JSONException e) {
			result.transport = null;
		} catch (IllegalArgumentException e) {
			return null;
		}

		result.house = House.fromJson(jo.getJSONObject("house"));

		return result;
	}
}
