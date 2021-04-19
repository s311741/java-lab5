package storage;

public final class CmdHelp extends Cmd {
	public CmdHelp (String[] arguments) { super(arguments); }

	private static final String HELP_MESSAGE =
		"commands:\n" +
		"help           print this message\n" +
		"exit           exit without saving\n" +
		"show           print out all elements in the collection\n" +
		"save           save the database to file\n" +
		"add            add an element\n" +
		"clear          remove all elements\n" +
		"update_id      update element with specified id\n" +
		"remove_by_id   remove element with specified id\n" +
		"history        print the last commands entered, up to 13\n";

	@Override
	public boolean run () {
		System.out.println(HELP_MESSAGE);
		return this.arguments[0].equals("help");
	}
}
