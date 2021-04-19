package storage;

import java.io.Writer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class Main {
	public static void main (String[] args) {
		final Prompter prompter = new Prompter(
				new BufferedReader(new InputStreamReader(System.in)),
				new OutputStreamWriter(System.out));
		Cmd cmd;

		Storage.getStorage().tryPopulateFromFile();

		try {
			while ((cmd = Cmd.next(prompter)) != null) {
				cmd.run();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return;
		}
	}
}

