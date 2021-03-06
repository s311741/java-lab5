package storage.cmd;

import storage.*;
import storage.client.*;
import storage.server.*;

/**
 * count_greater_than_furnish: output all elements whose "furnish" parameter is greater than given
 * Threshold is given as an argument (string enum value)
 */
public final class CmdCountGreaterThanFurnish extends NetworkedCmd {
	Furnish threshold;

	@Override
	public boolean runOnClient (String[] arguments, Prompter prompt) {
		if (arguments.length < 2) {
			System.err.println(arguments[0] + ": no furnish level specified");
			return false;
		}
		try {
			this.threshold = Enum.valueOf(Furnish.class, arguments[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			System.err.println(arguments[0] + ": invalid furnish level specified");
			return false;
		}
		return true;
	}

	@Override
	public Response runOnServer () {
		int answer = (int) StorageServer.getServer().stream()
				.filter(flat -> (flat.getFurnish().compareTo(this.threshold) > 0))
				.count();
		return new Response(true, ""+ answer);
	}
}
