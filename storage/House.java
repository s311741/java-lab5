package storage;

import java.io.IOException;

public class House {
	private String name;
	private Integer year;
	private int numberOfFlatsOnFloor;
	private Long numberOfLifts;

	private boolean invariant () {
		return year > 0 && year < 699
		    && numberOfFlatsOnFloor > 0
		    && numberOfLifts != null && numberOfLifts > 0;
	}

	public static House next (Prompter prompt) throws IOException, PrompterInputAbortedException {
		House house = new House();
		house.name = prompt.nextLine("name: ");
		house.year = (int) (long) prompt.nextLong("year: ", n -> n > 0 && n <= 699);
		house.numberOfFlatsOnFloor = (int) (long) prompt.nextLong("flats per floor: ", n -> n > 0);
		house.numberOfLifts = prompt.nextLong("number of lifts: ", n -> n > 0);
		return house;
	}
}
