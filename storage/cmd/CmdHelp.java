package storage.cmd;

import storage.*;
import storage.client.*;

/**
 * help: print out a help message
 * Success status depends on whether the command was actually invoked as "help" (success),
 * or whether this invocation is a result of the user entering an unknown command (failure)
 */
public final class CmdHelp extends Cmd {
	public CmdHelp (String[] a, Prompter p) { super(a, p); }

	private static final String HELP_MESSAGE =
		"commands:\n" +
		"help           print this message\n" +
		"exit           exit without saving\n" +
		"info           show info about the database\n" +
		"show           print out all elements in the collection\n" +
		"save           save the database to file\n" +
		"add            add an element\n" +
		"clear          remove all elements\n" +
		"update_id      update element with specified id\n" +
		"remove_by_id   remove element with specified id\n" +
		"add_if_min     add element if it is lesser than the existing minimum\n" +
		"remove_lower   remove all elements which are lesser than given\n" +
		"sum_number_of_rooms\n" +
		"               print the sum of number of rooms in the collection\n" +
		"count_greater_than_furnish\n" +
		"               count elements whose furnish value is greater than given\n" +
		"filter_by_house\n" +
		"               print those elements whose house equals to given\n" +
		"history        print the last commands entered, up to 13\n" +
		"execute_script execute commands from a file as if from stdin\n" +
		"shutdown       shutdown remote server";

	@Override
	public boolean run () {
		System.out.println(HELP_MESSAGE);
		return this.arguments[0].equals("help");
	}
}
