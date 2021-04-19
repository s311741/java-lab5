package storage;

import java.io.IOException;

public class Coordinates {
	private float x;
	private Double y;

	public static Coordinates next (Prompter prompt) throws IOException, PrompterInputAbortedException {
		Coordinates result = new Coordinates();
		result.x = (float) (double) prompt.nextDouble("x: ", x -> x <= 132);
		result.y = prompt.nextDouble("y: ", y -> y <= 364);
		return result;
	}
}
