package storage;

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
}
