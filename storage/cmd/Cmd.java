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
	protected final String[] arguments;
	protected final Prompter prompter;

	/**
	 * @param args The arguments of the command, the 0th one being the command name itself
	 * @param prompter The prompter from which this command was run (the command may ask further info)
	 */
	public Cmd (String[] args, Prompter prompter) {
		this.arguments = args;
		this.prompter = prompter;
	}

	/**
	 * This method is run on the object that is created for the invocation
	 * @return true iff the invocation was successful
	 */
	public abstract boolean run ();

	/**
	 * Helper to print error messages while giving the command name
	 */
	public void printMessage (String message) {
		System.err.println(this.arguments[0] + ": " + message);
	}

	@Override
	public String toString () {
		return String.join(" ", this.arguments);
	}

	private static final HashMap<String, Class<? extends Cmd>> cmdsByName = new HashMap();
	static {
		cmdsByName.put("help", CmdHelp.class);
		cmdsByName.put("add", CmdAdd.class);
		cmdsByName.put("history", CmdHistory.class);
		cmdsByName.put("exit", CmdExit.class);
		cmdsByName.put("clear", CmdClear.class);
		cmdsByName.put("show", CmdShow.class);
		cmdsByName.put("save", CmdSave.class);
		cmdsByName.put("execute_script", CmdExecuteScript.class);
		cmdsByName.put("info", CmdInfo.class);
		cmdsByName.put("update_id", CmdUpdateID.class);
		cmdsByName.put("remove_by_id", CmdRemoveByID.class);
		cmdsByName.put("add_if_min", CmdAddIfMin.class);
		cmdsByName.put("remove_lower", CmdRemoveLower.class);
		cmdsByName.put("count_greater_than_furnish", CmdCountGreaterThanFurnish.class);
		cmdsByName.put("sum_of_number_of_rooms", CmdSumNumberOfRooms.class);
		cmdsByName.put("filter_by_house", CmdFilterByHouse.class);
		cmdsByName.put("shutdown", CmdShutdown.class);
	}

	private static final Class[] CMD_CTOR_ARGS = { String[].class, Prompter.class };

	/**
	 * Get the next command from Prompter input
	 */
	public static Cmd next (Prompter prompt) throws IOException {
		final String line;
		try {
			line = prompt.nextLine("> ");
		} catch (PrompterInputAbortedException e) {
			return null;
		}

		final String[] words = line.split("\\s+");
		CmdHistory.pushEntry(words[0]);
		return getCommandFromWords(words, prompt);
	}

	public static Cmd getCommandFromWords (String[] words, Prompter prompt) {
		final String cmdName = words[0];
		final Class<? extends Cmd> cmdClass = cmdsByName.getOrDefault(cmdName,CmdHelp.class);
		final Constructor ctor;
		final Cmd cmd;
		try {
			ctor = cmdClass.getConstructor(CMD_CTOR_ARGS);
		} catch (NoSuchMethodException e) {
			return null;
		}

		final Object[] instanceArgs = new Object[]{ words, prompt };

		try {
			cmd = (Cmd) ctor.newInstance(instanceArgs);
		}
		catch (InstantiationException e) { return null; }
		catch (IllegalAccessException e) { return null; }
		catch (InvocationTargetException e) { return null; }

		return cmd;
	}
}
