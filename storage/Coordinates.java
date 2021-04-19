package storage;

import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONException;

public class Coordinates {
	private float x;
	private Double y;

	public static Coordinates next (Prompter prompt) throws IOException, PrompterInputAbortedException {
		Coordinates result = new Coordinates();
		result.x = (float) (double) prompt.nextDouble("x: ", x -> x <= 132);
		result.y = prompt.nextDouble("y: ", y -> y <= 364);
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
