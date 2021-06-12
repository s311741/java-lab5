package storage.cmd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.util.HashMap;

import storage.*;
import storage.client.*;

/**
 * The command class. A subclass exists for each possible command, and an object is created for each invocation
 */
public abstract class Cmd {
	public Cmd () { }

	/**
	 * Runs on the command's invocation on the client side
	 * @return true iff the client's check has been successful
	 * @param arguments The arguments of the command, the 0th one being the command name itself
	 * @param prompter The prompter from which this command was run (the command may ask further info)
	 */
	public abstract boolean runOnClient (String[] arguments, Prompter prompter);


	private static final HashMap<String, Class<? extends Cmd>> cmdsByName = new HashMap();
	static {
		cmdsByName.put("add", CmdAdd.class);
		cmdsByName.put("add_if_min", CmdAddIfMin.class);
		cmdsByName.put("clear", CmdClear.class);
		cmdsByName.put("count_greater_than_furnish", CmdCountGreaterThanFurnish.class);
		cmdsByName.put("execute_script", CmdExecuteScript.class);
		cmdsByName.put("exit", CmdExit.class);
		cmdsByName.put("filter_by_house", CmdFilterByHouse.class);
		cmdsByName.put("help", CmdHelp.class);
		cmdsByName.put("history", CmdHistory.class);
		cmdsByName.put("info", CmdInfo.class);
		cmdsByName.put("login_set", CmdSetLogin.class);
		cmdsByName.put("login_unset", CmdUnsetLogin.class);
		cmdsByName.put("register", CmdRegister.class);
		cmdsByName.put("remove_by_id", CmdRemoveByID.class);
		cmdsByName.put("remove_lower", CmdRemoveLower.class);
		cmdsByName.put("show", CmdShow.class);
		cmdsByName.put("sum_of_number_of_rooms", CmdSumNumberOfRooms.class);
		cmdsByName.put("update_id", CmdUpdateID.class);
	}

	public static String[] nextCmdWords (Prompter prompt) throws IOException {
		final String line;
		try {
			line = prompt.nextLine("> ");
		} catch (PrompterInputAbortedException e) {
			return null;
		}
		return line.split("\\s+");
	}

	private static final Class[] CMD_CTOR_ARGS = { };

	public static Cmd getCommandFromWords (String[] words, Prompter prompt) {
		final String cmdName = words[0];
		final Class<? extends Cmd> cmdClass = cmdsByName.getOrDefault(cmdName, CmdHelp.class);
		final Constructor ctor;
		final Cmd cmd;
		try {
			ctor = cmdClass.getConstructor(CMD_CTOR_ARGS);
		} catch (NoSuchMethodException e) {
			return null;
		}

		try {
			cmd = (Cmd) ctor.newInstance((Object[]) CMD_CTOR_ARGS);
		}
		catch (InstantiationException e) { return null; }
		catch (IllegalAccessException e) { return null; }
		catch (InvocationTargetException e) { return null; }

		return cmd;
	}
}
