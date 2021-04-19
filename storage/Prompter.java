package storage;

import java.io.BufferedReader;
import java.io.Writer;
import java.io.IOException;

public final class Prompter {
	private BufferedReader reader;
	private Writer writer;

	public Prompter (BufferedReader reader, Writer writer) {
		this.reader = reader;
		this.writer = writer;
	}

	public Prompter (BufferedReader reader) {
		this(reader, null);
	}

	@FunctionalInterface interface PromptLineCallback { boolean valid (String line); }
	@FunctionalInterface interface PromptLongCallback { boolean valid (Long n); }
	@FunctionalInterface interface PromptEnumCallback<E extends Enum> { boolean valid (E en); }

	public String nextLine (String prompt)
			throws IOException, PrompterInputAbortedException
	{
		return this.nextLine(prompt, (String line) -> !line.isEmpty());
	}

	public String nextLine (String prompt, PromptLineCallback cb)
			throws IOException, PrompterInputAbortedException
	{
		String line;

		if (this.writer == null) {
			// non-interactive mode
			line = this.reader.readLine();
			return cb.valid(line) ? line : null;
		} else {
			// interactive mode
			do {
				this.writer.write(prompt);
				this.writer.flush();
				line = this.reader.readLine();
				if (line == null) {
					throw new PrompterInputAbortedException();
				}
				line.trim();
			} while (!cb.valid(line));
			return line;
		}
	}

	public long nextLong (String prompt)
			throws IOException, PrompterInputAbortedException
	{
		return this.nextLong(prompt, (Long l) -> true);
	}

	public Long nextLong (String prompt, PromptLongCallback cb)
			throws IOException, PrompterInputAbortedException
	{
		String line = this.nextLine(prompt, (String l) -> {
			try {
				return cb.valid(Long.parseLong(l));
			} catch(NumberFormatException e) {
				return false;
			}
		});
		if (line == null) {
			return null;
		}
		return Long.parseLong(line);
	}

	public <E extends Enum<E>> E nextEnum (String prompt, Class<E> enumClass, PromptEnumCallback<E> cb)
			throws IOException, PrompterInputAbortedException
	{
		String line = this.nextLine(prompt, (String l) -> {
			try {
				return cb.valid(Enum.valueOf(enumClass, l.toUpperCase()));
			} catch (IllegalArgumentException e) {
				return false;
			}
		});
		if (line == null) {
			return null;
		}
		return null;
	}
}