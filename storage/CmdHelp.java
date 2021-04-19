package storage;

public final class CmdHelp extends Cmd {
	public CmdHelp (String[] arguments) { super(arguments); }

	private static final String HELP_MESSAGE =
		"commands:\n" +
		"help      print this message\n" +
		"exit      exit without saving\n" +
		"add       add an element\n" +
		"clear     remove all elements \n" +
		"history   print the last commands entered, up to 13\n";

	@Override
	public boolean run () {
		System.out.println(HELP_MESSAGE);
		return this.arguments[0].equals("help");
	}
}
