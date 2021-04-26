package storage;

/**
 * count_greater_than_furnish: output all elements whose "furnish" parameter is greater than given
 * Threshold is given as an argument (string enum value)
 */
public final class CmdCountGreaterThanFurnish extends Cmd {
	public CmdCountGreaterThanFurnish (String[] a, Prompter p) { super(a, p); }

	@Override
	public boolean run () {
		if (arguments.length < 2) {
			this.printMessage("no furnish level specified");
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
			Furnish furnish = flat.getFurnish();
			if (furnish != null && furnish.ordinal() != threshold.ordinal()) {
				answer++;
			}
		}

		System.out.println(answer);
		return true;
	}
}
