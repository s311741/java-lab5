package storage;

import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONException;
import storage.client.*;

/**
 * Two coordinates, curiously stored in very different data types
 */
public class Coordinates {
	private float x;
	private Double y;

	/**
	 * Ask a Prompter for a Coordinates
	 * @param Prompter the I/O device
	 */
	public static Coordinates next (Prompter prompt) throws PrompterInputAbortedException {
		Coordinates result = new Coordinates();
		result.x = (float) (double) prompt.nextDouble("x (<=132): ", x -> x <= 132);
		result.y = prompt.nextDouble("y (<=364): ", y -> y <= 364);
		return result;
	}

	@Override
	public String toString () {
		return "(" + Float.toString(this.x) + ", " + this.y.toString() + ")";
	}

	public JSONObject toJson () {
		JSONObject jo = new JSONObject();
		jo.put("x", this.x);
		jo.put("y", this.y);
		return jo;
	}

	public static Coordinates fromJson (JSONObject jo) throws JSONException {
		Coordinates result = new Coordinates();
		result.x = jo.getFloat("x");
		result.y = jo.getDouble("y");
		return result;
	}
}
