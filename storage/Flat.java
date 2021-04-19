package storage;

import java.util.Date;
import java.io.BufferedReader;
import java.io.Writer;
import java.io.IOException;

public class Flat {
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

	private boolean invariant () {
		return id != null && id > 0
		    && name != null && !name.isEmpty()
		    && creationDate != null
		    && area <= 926
		    && numberOfRooms != null && numberOfRooms > 0;
	}

	static int nextID = 1;

	public static Flat next (Prompter prompt) throws IOException, PrompterInputAbortedException {
		Flat result = new Flat();
		if (nextID == Integer.MAX_VALUE) {
			return null;
		}

		result.id = nextID++;
		result.name = prompt.nextLine("name (nonempty): ", s -> !s.isEmpty());
		result.area = prompt.nextLong("area (> 0): ", n -> n > 0);
		result.numberOfRooms = prompt.nextLong("number of rooms (> 0): ", n -> n > 0);
		result.furnish = prompt.nextEnum("furnish: ", Furnish.class, f -> true);

		return result;
	}
}
