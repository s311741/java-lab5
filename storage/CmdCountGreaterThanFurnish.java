package storage;

public final class CmdCountGreaterThanFurnish extends Cmd {
	public CmdCountGreaterThanFurnish (String[] arguments) { super(arguments); }

	@Override
	public boolean run () {
		if (arguments.length < 2) {
			return false;
		}

		Furnish threshold;
		try {
			threshold = Enum.valueOf(Furnish.class, arguments[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			return false;
		}

		int answer = 0;
		for (Flat flat: Storage.getStorage()) {
			if (flat.getFurnish().ordinal() > threshold.ordinal()) {
				answer++;
			}
		}

		System.out.println(answer);
		return true;
	}
}
