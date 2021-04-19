package storage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.util.HashMap;

public abstract class Cmd {
	final String[] arguments;
	public Cmd (String[] args) { this.arguments = args; }
	public abstract boolean run ();

	private static HashMap<String, Class<? extends Cmd>> cmdsByName = new HashMap();

	static {
		cmdsByName.put("help", CmdHelp.class);
		cmdsByName.put("add", CmdAdd.class);
		cmdsByName.put("history", CmdHistory.class);
		cmdsByName.put("exit", CmdExit.class);
		cmdsByName.put("clear", CmdClear.class);
		cmdsByName.put("show", CmdShow.class);
		cmdsByName.put("save", CmdSave.class);
	}

	private static final Class[] CMD_CTOR_ARGS_NOELEM = { String[].class };
	private static final Class[] CMD_CTOR_ARGS_ELEM = { String[].class, Flat.class };

	public static Cmd next (Prompter prompt) throws IOException {
		final String line;
		try {
			line = prompt.nextLine("> ");
		} catch (PrompterInputAbortedException e) {
			return null;
		}

		final String[] words = line.split("\\s+");
		final String cmdName = words[0];
		final Class<? extends Cmd> cmdClass = cmdsByName.getOrDefault(cmdName, CmdHelp.class);
		final boolean acceptsElement = ElemCmd.class.isAssignableFrom(cmdClass);

		final Constructor ctor;
		final Class[] ctorArgs = acceptsElement
				? CMD_CTOR_ARGS_ELEM
				: CMD_CTOR_ARGS_NOELEM;

		final Cmd cmd;
		try {
			ctor = cmdClass.getConstructor(ctorArgs);
		} catch (NoSuchMethodException e) {
			return null;
		}

		final Object[] instanceArgs;
		if (acceptsElement) {
			Flat flat;
			try {
				flat = Flat.next(prompt);
			} catch (PrompterInputAbortedException e) {
				return null;
			}
			instanceArgs = new Object[]{ words, flat };
		} else {
			instanceArgs = new Object[]{ words };
		}

		try {
			cmd = (Cmd) ctor.newInstance(instanceArgs);
		}
		catch (InstantiationException e) { return null; }
		catch (IllegalAccessException e) { return null; }
		catch (InvocationTargetException e) { return null; }

		CmdHistory.pushEntry(cmdName);
		return cmd;
	}
}
